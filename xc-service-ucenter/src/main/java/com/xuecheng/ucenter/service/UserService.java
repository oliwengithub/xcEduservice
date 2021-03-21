package com.xuecheng.ucenter.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.ucenter.*;
import com.xuecheng.framework.domain.ucenter.ext.UserInfo;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.domain.ucenter.request.UserListRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.UcenterCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.BCryptUtil;
import com.xuecheng.ucenter.client.SendFeignClient;
import com.xuecheng.ucenter.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AUTH;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Retention;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author: olw
 * @date: 2020/10/18 17:12
 * @description:  用户信息业务层
 */
@Service
public class UserService {


    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcTeacherRepository xcTeacherRepository;

    @Autowired
    private XcUserRoleRepository xcUserRoleRepository;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SendFeignClient sendFeignClient;

    /**
     * 获取用户信息
     * @author: olw
     * @Date: 2020/10/18 17:11
     * @param username
     * @returns: com.xuecheng.framework.domain.ucenter.ext.XcUserExt
    */
    public XcUserExt getUserExt (String username) {
        // 获取用户信息
        XcUser xcUser = findXcUserByUsername(username);
        if (xcUser == null) {
            // 这里不做异常处理 直接返回null
            // 因为认证服务通过远程调用获取用户信息进行比对
            // 异常信息在认证服务统一处理
            /* ExceptionCast.cast(UcenterCode.UCENTER_ACCOUNT_NOTEXISTS);*/
            return null;
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        String userId = xcUser.getId();
        // 获取用户组织信息（公司）
        XcCompanyUser xcCompanyUser = findByUserId(userId);
        if (xcCompanyUser != null) {
            xcUserExt.setCompanyId(xcCompanyUser.getCompanyId());
        }
        // 获取用户的权限
        List<XcMenu> menuList = menuMapper.findMenuByUserId(userId);
        if (menuList != null && menuList.size() > 0) {
            xcUserExt.setPermissions(menuList);
        }
        // 获取用户角色
        List<XcUserRole> xcUserRoleList= xcUserRoleRepository.findXcUserRoleByUserId(userId);
        if (xcUserRoleList != null && xcUserRoleList.size() > 0) {

            String roles = xcUserRoleList.stream().map(xcUserRole -> String.valueOf(xcUserRole.getRoleId())).collect(Collectors.joining(","));
            xcUserExt.setRoleId(roles);
        }

        return xcUserExt;

    }

    public XcUser getUser (String userId) {
        return xcUserRepository.findById(userId).orElse(null);
    }

    /**
     * 根据用户姓名获取信息
     * @author: olw
     * @Date: 2020/10/18 17:25
     * @param username
     * @returns: com.xuecheng.framework.domain.ucenter.XcUser
    */
    public XcUser findXcUserByUsername (String username) {
       return xcUserRepository.findXcUserByUsername(username);
    }

    /**
     * 根据用户id获取用户组织
     * @author: olw
     * @Date: 2020/10/18 17:34
     * @param userId
     * @returns: com.xuecheng.framework.domain.ucenter.XcCompanyUser
    */
    public XcCompanyUser findByUserId (String userId) {
        return xcCompanyUserRepository.findByUserId(userId);
    }

    /**
     * 获取用户列表
     * @author: olw
     * @Date: 2020/10/29 16:41
     * @param page
     * @param size
     * @param userListRequest
     * @returns: com.xuecheng.framework.model.response.QueryResponseResult
    */
    public QueryResponseResult findUserList (int page, int size, UserListRequest userListRequest) {

        if (userListRequest == null) {
            userListRequest = new UserListRequest();
        }

        page = page <=0 ? 1:page;
        size = size <=0 ? 20:size;
        // 设置分页参数
        PageHelper.startPage(page, size);
        Page<UserInfo> userListPage = xcUserMapper.findUserListPage(userListRequest);
        // 总记录条数
        long total = userListPage.getTotal();
        QueryResult<UserInfo> queryResult = new QueryResult<>();
        queryResult.setTotal(total);
        queryResult.setList(userListPage.getResult());
        return new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 添加用户
     * @author: olw
     * @Date: 2020/11/24 17:35
     * @param userInfo
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult add (UserInfo userInfo) {
        // 校验用户名称是否已存在
        XcUser xcUserByUsername = this.findXcUserByUsername(userInfo.getUsername());
        if (xcUserByUsername != null) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_EXIST);
        }
        //保存用户信息
        XcUser xcUser = new XcUser();
        BeanUtils.copyProperties(userInfo, xcUser);
        xcUser.setPassword(BCryptUtil.encode(userInfo.getPassword()));
        xcUser.setCreateTime(new Date());
        xcUser.setUpdateTime(new Date());
        XcUser user = xcUserRepository.save(xcUser);
        //保存用户角色信息
        String[] split = userInfo.getRoleIds().split(",");
        List<XcUserRole> userRoles = Arrays.stream(split).map(s -> {
            XcUserRole xcUserRole = new XcUserRole();
            xcUserRole.setUserId(user.getId());
            xcUserRole.setCreateTime(new Date());
            xcUserRole.setRoleId(s);
            return xcUserRole;
        }).collect(Collectors.toList());

        xcUserRoleRepository.saveAll(userRoles);
        //根据用户的类型存储用户扩展信息
        String companyId = userInfo.getCompanyId();
        if (StringUtils.isNotEmpty(companyId)) {
            // 保存老师机构信息
            XcCompanyUser xcCompanyUser = new XcCompanyUser();
            xcCompanyUser.setCompanyId(companyId);
            xcCompanyUser.setUserId(user.getId());
            xcCompanyUserRepository.save(xcCompanyUser);
            // 添加老师简介信息
            XcTeacher xcTeacher = new XcTeacher();
            // 默认为用户名
            xcTeacher.setName(user.getName());
            xcTeacher.setPic(user.getUserpic());
            xcTeacher.setUserId(user.getId());
            xcTeacherRepository.save(xcTeacher);

        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public ResponseResult updateStatus (XcUser user) {
        XcUser one = this.findXcUserByUsername(user.getUsername());
        if (one == null) {
            ExceptionCast.cast(AuthCode.AUTH_USER_NOEXIST);
        }
        one.setStatus(user.getStatus());
        one.setUpdateTime(new Date());
        xcUserRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据用户id获取用户信息
     * @author: olw
     * @Date: 2020/10/29 16:08
     * @param userId
     * @returns: com.xuecheng.framework.domain.ucenter.ext.UserInfo
    */
    public UserInfo getUserInfo (String userId) {
        UserInfo userInfo = new UserInfo();
        // 获取用户信息
        Optional<XcUser> optional = xcUserRepository.findById(userId);
        if (optional.isPresent()) {
            BeanUtils.copyProperties(optional.get(), userInfo);
        }
        // 获取用户角色信息
        List<XcUserRole> xcUserRoleList= xcUserRoleRepository.findXcUserRoleByUserId(userId);
        if (xcUserRoleList != null && xcUserRoleList.size() > 0) {

            String roles = xcUserRoleList.stream().map(xcUserRole -> String.valueOf(xcUserRole.getRoleId())).collect(Collectors.joining(","));
            userInfo.setRoleIds(roles);
        }
        // 获取用户机构信息
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
        if (xcCompanyUser != null) {
            userInfo.setCompanyId(xcCompanyUser.getCompanyId());
        }
        return userInfo;
    }

    /**
     * 修改用户信息
     * @author: olw
     * @Date: 2020/11/24 17:52
     * @param userInfo
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult editInfo (UserInfo userInfo) {
        String userId = userInfo.getId();
        // 获取用户信息
        XcUser xcUserByUsername = this.findXcUserByUsername(userInfo.getUsername());
        if (xcUserByUsername == null) {
            ExceptionCast.cast(AuthCode.AUTH_USER_NOEXIST);
        }
        // 更新用户信息
        XcUser xcUser = new XcUser();
        BeanUtils.copyProperties(userInfo, xcUser);
        xcUser.setId(xcUserByUsername.getId());
        xcUser.setUsername(xcUserByUsername.getUsername());
        xcUser.setPassword(xcUserByUsername.getPassword());
        xcUser.setUpdateTime(new Date());
        xcUser.setCreateTime(xcUserByUsername.getCreateTime());

        xcUserRepository.save(xcUser);
        // 更新用户角色信息
        // 考虑用户角色关联的数据不多 一个用户也就一两个角色 所有采用直接删除全部在更新
        // 获取已存在的角色
        List<XcUserRole> xcUserRoleByUserId = xcUserRoleRepository.findXcUserRoleByUserId(userId);
        xcUserRoleRepository.deleteAll(xcUserRoleByUserId);
        // 添加
        String[] split = userInfo.getRoleIds().split(",");
        List<XcUserRole> xcUserRoles = Arrays.stream(split).map(s -> {
            XcUserRole xcUserRole = new XcUserRole();
            xcUserRole.setCreateTime(new Date());
            xcUserRole.setUserId(userInfo.getId());
            xcUserRole.setRoleId(s);
            return xcUserRole;
        }).collect(Collectors.toList());
        xcUserRoleRepository.saveAll(xcUserRoles);
        // 获取机构信息   存在就更新 一个人只属于一个机构
        XcCompanyUser companyUser = xcCompanyUserRepository.findByUserId(userId);
        String companyId = userInfo.getCompanyId();
        // 是否存在机构
        if (companyUser == null && StringUtils.isNotEmpty(companyId)){
            // 添加机构
            XcCompanyUser xcCompanyUser = new XcCompanyUser();
            xcCompanyUser.setCompanyId(companyId);
            xcCompanyUser.setUserId(userId);
            xcCompanyUserRepository.save(xcCompanyUser);
        }else if (companyUser != null) {
            //更新机构
            if(StringUtils.isEmpty(companyId)){
                // 删除绑定的机构
                xcCompanyUserRepository.delete(companyUser);
            }else if (!companyUser.getCompanyId().equals(companyId)) {
                 // 判定是否一致
                 companyUser.setCompanyId(companyId);
                 xcCompanyUserRepository.save(companyUser);
             }
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 修改用户信息(个人中心修改信息)
     * @author: olw
     * @Date: 2020/11/24 19:33
     * @param xcUser
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult edit (XcUser xcUser) {
        XcUser one = this.findXcUserByUsername(xcUser.getUsername());
        if (one == null) {
            ExceptionCast.cast(AuthCode.AUTH_USER_NOEXIST);
        }
        one.setEmail(xcUser.getEmail());
        one.setPhone(xcUser.getPhone());
        one.setSex(xcUser.getSex());
        one.setUserpic(xcUser.getUserpic());
        one.setName(xcUser.getName());
        one.setBirthday(xcUser.getBirthday());
        one.setUpdateTime(new Date());
        xcUserRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 重置用户密码
     * @author: olw
     * @Date: 2020/11/25 17:47
     * @param xcUser
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult resetPassword (XcUser xcUser) {
        Optional<XcUser> optional = xcUserRepository.findById(xcUser.getId());
        if (!optional.isPresent()) {
            ExceptionCast.cast(AuthCode.AUTH_USER_NOEXIST);
        }
        if (StringUtils.isEmpty(xcUser.getPassword())){
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        XcUser user = optional.get();
        user.setPassword(BCryptUtil.encode(xcUser.getPassword()));
        user.setUpdateTime(new Date());
        xcUserRepository.save(user);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据id
     * @author: olw
     * @Date: 2020/12/9 17:26
     * @param id
     * @returns: com.xuecheng.framework.domain.ucenter.XcTeacher
    */
    public XcTeacher getTeacherInfo (String id) {
        XcTeacher xcTeacher = new XcTeacher();
        Optional<XcTeacher> optional = xcTeacherRepository.findById(id);
        xcTeacher = optional.orElseGet(() -> xcTeacherRepository.findByUserId(id));
        return xcTeacher;
    }

    /**
     * 更新老师信息
     * @author: olw
     * @Date: 2020/12/9 17:33
     * @param xcTeacher
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult updateTeacherInfo (XcTeacher xcTeacher) {
        String userId = xcTeacher.getUserId();
        if (StringUtils.isEmpty(userId)) {
            return new ResponseResult(CommonCode.FAIL);
        }
        XcTeacher one = xcTeacherRepository.findByUserId(userId);
        if (one != null) {
            xcTeacher.setId(one.getId());
        }
        xcTeacherRepository.save(xcTeacher);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 绑定手机
     * @author: olw
     * @Date: 2021/3/11 15:55
     * @param phone
     * @param code
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult bindPhone (String phone, String code, String username) {
        boolean flag = checkCode(phone, code);
        if (!flag) {
            return new ResponseResult(UcenterCode.UCENTER_VERIFYCODE_ERROR);
        }
        XcUser user = findXcUserByUsername(username);
        user.setUpdateTime(new Date());
        user.setPhone(phone);
        xcUserRepository.save(user);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 绑定邮箱
     * @author: olw
     * @Date: 2021/3/11 15:55
     * @param email
     * @param code
     * @returns: com.xuecheng.framework.model.response.ResponseResult
     */
    public ResponseResult bindEmail (String email, String code, String username) {
        boolean flag = checkCode(email, code);
        if (!flag) {
            return new ResponseResult(UcenterCode.UCENTER_VERIFYCODE_ERROR);
        }
        XcUser user = findXcUserByUsername(username);
        user.setUpdateTime(new Date());
        user.setEmail(email);
        xcUserRepository.save(user);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public ResponseResult editPass (String newPass, String oldPass, String username) {
        XcUser user = findXcUserByUsername(username);
        String password = user.getPassword();
        boolean matches = BCryptUtil.matches(oldPass, password);
        if (!matches) {
            ExceptionCast.cast(UcenterCode.UCENTER_PASSWORD_ERROR);
        }
        user.setPassword(BCryptUtil.encode(newPass));
        xcUserRepository.save(user);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 校验验证码
    public boolean checkCode (String account, String code) {
        String key = "code_" + account;
        String s = stringRedisTemplate.opsForValue().get(key);
        return code.equals(s);
    }

    public ResponseResult getCode (String account, String username) {
        return sendFeignClient.getCode(account, username);
    }
}
