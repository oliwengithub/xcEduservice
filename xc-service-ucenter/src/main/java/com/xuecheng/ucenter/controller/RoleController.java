package com.xuecheng.ucenter.controller;

import com.xuecheng.api.ucenter.RoleControllerApi;
import com.xuecheng.framework.domain.ucenter.XcRole;
import com.xuecheng.framework.domain.ucenter.ext.RoleExt;
import com.xuecheng.framework.domain.ucenter.response.RoleResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.ucenter.service.XcRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 *
 * @author: olw
 * @date: 2020/10/28 21:11
 * @description:  菜单
 */
@RestController
@RequestMapping("/ucenter/role")
public class RoleController implements RoleControllerApi {

    @Autowired
    XcRoleService roleService;

    //@PreAuthorize("hasAuthority('find_all_role')")
    @Override
    @GetMapping("/all")
    public QueryResponseResult findAllRole () {
        return roleService.findAll();
    }

    //@PreAuthorize("hasAuthority('add_role')")
    @Override
    @PostMapping("/add")
    public ResponseResult add (@RequestBody RoleExt roleExt) {
        return roleService.add(roleExt);
    }

    //@PreAuthorize("hasAuthority('get_role')")
    @Override
    @GetMapping("/get/{roleId}")
    public RoleResult getRoleInfo (@PathVariable("roleId") String roleId) {
        return roleService.findById(roleId);
    }

    //@PreAuthorize("hasAuthority('eidt_role')")
    @Override
    @PutMapping("/edit")
    public ResponseResult edit (@RequestBody RoleExt roleExt) {
        return roleService.edit(roleExt);
    }

    //@PreAuthorize("hasAuthority('update_role')")
    @Override
    @PutMapping("/update")
    public ResponseResult updateStatus (@RequestBody XcRole xcRole) {
        return roleService.updateStatus(xcRole);
    }

}
