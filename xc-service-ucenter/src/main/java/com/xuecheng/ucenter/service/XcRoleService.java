package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcPermission;
import com.xuecheng.framework.domain.ucenter.XcRole;
import com.xuecheng.framework.domain.ucenter.ext.RoleExt;
import com.xuecheng.framework.domain.ucenter.response.RoleCode;
import com.xuecheng.framework.domain.ucenter.response.RoleResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.ucenter.dao.XcPermissionRepository;
import com.xuecheng.ucenter.dao.XcRoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 *
 * @author: olw
 * @date: 2020/10/21 18:49
 * @description:  角色业务层
 */
@Service
public class XcRoleService {


    @Autowired
    XcRoleRepository xcRoleRepository;

    @Autowired
    XcPermissionRepository xcPermissionRepository;

    /**
     * 获取所有角色列表
     * @author: olw
     * @Date: 2020/11/19 19:26
     * @param
     * @returns: com.xuecheng.framework.model.response.QueryResponseResult
    */
    public QueryResponseResult findAll (XcRole xcRole) {
        if( xcRole== null) {
            xcRole = new XcRole();
        }
        // 角色数量不多直接查出所有，不做分页处理
        // 构建查询条件
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        if (StringUtils.isNotEmpty(xcRole.getRoleName())) {
            exampleMatcher.withMatcher("roleName", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        if (StringUtils.isNotEmpty(xcRole.getStatus())) {
            exampleMatcher.withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact());
        }
        Example<XcRole> example = Example.of(xcRole, exampleMatcher);
        List<XcRole> all = xcRoleRepository.findAll(example);
        QueryResult<XcRole> queryResult = new  QueryResult();
        queryResult.setList(all);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    public ResponseResult add (RoleExt roleExt) {
        // 选查询角色名称 或角色编码是否存在
        XcRole xcRole = this.checkRoleIsExist(roleExt.getRoleName(), roleExt.getRoleCode());
        if (xcRole != null) {
            ExceptionCast.cast(RoleCode.ROLE_CODE_EXIST);
        }
        xcRole = new XcRole();
        BeanUtils.copyProperties(roleExt, xcRole);
        // 角色转态默认为 1 开启 0 表示禁用
        xcRole.setStatus("1");
        Date date = new Date();
        xcRole.setCreateTime(date);
        xcRole.setUpdateTime(date);
        XcRole save = xcRoleRepository.save(xcRole);
        this.batchUpdateRolePermission(save.getId(), roleExt.getMenuIds());
        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 更新角色状态
     * @author: olw
     * @Date: 2020/11/19 21:18
     * @param xcRole
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult updateStatus (XcRole xcRole) {
        String status = xcRole.getStatus();
        RoleResult roleResult = this.findById(xcRole.getId());
        if (roleResult.isSuccess()) {
            BeanUtils.copyProperties(xcRole, roleResult.getRoleExt());
            xcRole.setStatus(status);
            return this.update(xcRole);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    public ResponseResult edit (RoleExt roleExt) {
        RoleResult roleResult = this.findById(roleExt.getId());
        if (roleResult.isSuccess()) {
            this.batchUpdateRolePermission(roleExt.getId(), roleExt.getMenuIds());
            XcRole xcRole = new XcRole();
            BeanUtils.copyProperties(roleExt, xcRole);
            return this.update(xcRole);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    public RoleResult findById (String roleId) {
        Optional<XcRole> optional = xcRoleRepository.findById(roleId);
        if (optional.isPresent()) {
            // 获取角色包含的资源菜单id列表
            List<XcPermission> xcPermissions = xcPermissionRepository.findXcPermissionByRoleId(roleId);
            String menuIds = xcPermissions.stream().map(p -> p.getMenuId()).collect(Collectors.joining(","));
            RoleExt roleExt = new RoleExt();
            BeanUtils.copyProperties(optional.get(), roleExt);
            roleExt.setMenuIds(menuIds);
            return new RoleResult(CommonCode.SUCCESS, roleExt);
        }
        return new RoleResult(RoleCode.ROLE_NOT_EXIST, null);
    }

    /**
     * 批量更新角色菜单
     * @author: olw
     * @Date: 2020/11/19 19:47
     * @param roleId
     * @param menuIds
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    @Transactional(rollbackFor = Exception.class, propagation=REQUIRED, readOnly = false)
    @Modifying
    public ResponseResult batchUpdateRolePermission (String roleId, String menuIds) {
        String[] strings = new String[]{};
        if(StringUtils.isNotEmpty(menuIds)) {
         strings = menuIds.split(",");
        }
        List<String> menuIdStrings = Arrays.asList(strings);
        // 获取角色拥有的所有菜单
        List<XcPermission> permissionList = xcPermissionRepository.findXcPermissionByRoleId(roleId);
        // 取出数据库已经存在的菜单 map(p -> strings.stream().anyMatch(s -> s.equals(p.getMenuId())) ? p : null).collect(Collectors.toList())
        List<XcPermission> permissionExist = permissionList.stream().filter(p -> menuIdStrings.contains(p.getMenuId())).collect(Collectors.toList());
        // 1、过滤出需要添加的菜单.map(s -> permissionExist.st ream().anyMatch(p -> p.getMenuId().equals(s)) ? null : new XcPermission() {{
        //            setRoleId(roleId);
        //            setMenuId(s);
        //            setCreateTime(new Date());
        //        }}).collect(Collectors.toList());
        List<String> menuIdsAdd = menuIdStrings.stream().filter(s -> !permissionExist.stream().map(XcPermission::getMenuId).collect(Collectors.toList()).contains(s)).collect(Collectors.toList());

        List<XcPermission> permissionsAdd = menuIdsAdd.stream().map(s ->{
            XcPermission xcPermission = new XcPermission();
            xcPermission.setRoleId(roleId);
            xcPermission.setMenuId(s);
            xcPermission.setCreateTime(new Date());
            return xcPermission;
        }).collect(Collectors.toList());
        //2、过滤出需要删除的菜单.map(p -> permissionExist.stream().anyMatch(e -> e.getId().equals(p.getId())) ? null : p).collect(Collectors.toList());
        List<XcPermission> permissionsDel = permissionList.stream().filter(p -> !permissionExist.stream().map(XcPermission::getId).collect(Collectors.toList()).contains(p.getId())).collect(Collectors.toList());
        xcPermissionRepository.saveAll(permissionsAdd);
        xcPermissionRepository.deleteInBatch(permissionsDel);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据角色名称或角色code查询角色是否已经存在
     * @author: olw
     * @Date: 2020/11/19 19:31
     * @param roleName
     * @param roleCode
     * @returns: com.xuecheng.framework.domain.ucenter.XcRole
    */
    private XcRole checkRoleIsExist (String roleName, String roleCode) {
        return xcRoleRepository.findByRoleNameOrRoleCode(roleName, roleCode);
    }

    private ResponseResult update (XcRole xcRole) {
        xcRole.setUpdateTime(new Date());
        xcRoleRepository.save(xcRole);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
