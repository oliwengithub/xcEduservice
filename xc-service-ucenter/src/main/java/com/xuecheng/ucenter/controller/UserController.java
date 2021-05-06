package com.xuecheng.ucenter.controller;

import com.xuecheng.api.ucenter.UcenterControllerApi;
import com.xuecheng.api.ucenter.UserAccountApi;
import com.xuecheng.framework.domain.Constants;
import com.xuecheng.framework.domain.ucenter.XcTeacher;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.UserInfo;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.domain.ucenter.request.UserListRequest;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.Oauth2Util;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.ucenter.service.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 *
 * @author: olw
 * @date: 2020/10/18 17:08
 * @description:  用户信息接口
 */
@RestController
@RequestMapping("/ucenter")
public class UserController  extends BaseController implements UcenterControllerApi, UserAccountApi {

    @Autowired
    private UserService userService;

    @Override
    @GetMapping("/getuserext")
    public XcUserExt getUserExt (@RequestParam("username") String username) {
        return userService.getUserExt(username);
    }

    @Override
    @GetMapping("/getuserinfo/{userId}")
    public UserInfo getUserInfo (@PathVariable("userId") String userId) {
        // TODO: 2020/12/6 userId参数可以不用传递 直接通过token获取
        return userService.getUserInfo(userId);
    }

    /**
     * 获取单独的用户对象信息
     * @author: olw
     * @Date: 2020/12/13 17:09
     * @param userId
     * @returns: com.xuecheng.framework.domain.ucenter.XcUser
    */
    @Override
    @GetMapping("/getuser/{userId}")
    public XcUser getUser (@PathVariable("userId") String userId) {
        return userService.getUser(userId);
    }


    @Override
    @PostMapping("/edituserinfo")
    public ResponseResult edit (@RequestBody XcUser xcUser) {
        // 提供给普通用户的信息修改
        return userService.edit(xcUser);
    }

    @PreAuthorize("hasAuthority('xc_sysmanager_user_view')")
    @Override
    @GetMapping("/finduserList/{page}/{size}")
    public QueryResponseResult findUserList (@PathVariable("page") int page, @PathVariable("size") int size, UserListRequest userListRequest) {
        Map<String, String> fromHeader = Oauth2Util.getJwtClaimsFromHeader(request);
        String roles = fromHeader.get("roles");
        if (roles!=null && StringUtils.isNotEmpty(roles)) {
            String[] split = roles.split(",");
            String sysRoleId = ArrayUtils.contains(split, Constants.SYSTEM_ROLE_SUPER) ? Constants.SYSTEM_ROLE_SUPER : (
                    ArrayUtils.contains(split, Constants.SYSTEM_ROLE_ADMIN) ? Constants.SYSTEM_ROLE_ADMIN : (
                            ArrayUtils.contains(split, Constants.SYSTEM_ROLE_TEACHER) ? Constants.SYSTEM_ROLE_TEACHER : ""
                    )
            );
            userListRequest.setSysRoleId(sysRoleId);
        }

        return userService.findUserList(page, size, userListRequest);
    }

    @GetMapping("/findteacherlist/{page}/{size}")
    @Override
    public QueryResponseResult findTeacherList (@PathVariable("page") int page, @PathVariable("size") int size, UserListRequest userListRequest) {
        XcOauth2Util.UserJwt userJwtFromHeader = XcOauth2Util.getUserJwtFromHeader(request);
        String companyId = userJwtFromHeader.getCompanyId();
        if (StringUtils.isEmpty(companyId)) {
            ExceptionCast.cast(CommonCode.UNAUTHORISE);
        }
        userListRequest.setCompanyId(companyId);
        return userService.findTeacherList(page, size, userListRequest);
    }

    @PreAuthorize("hasAuthority('xc_sysmanager_user_edit')")
    @Override
    @PutMapping("/edituser")
    public ResponseResult editInfo (@RequestBody UserInfo userInfo) {
        return userService.editInfo(userInfo);
    }

    @PreAuthorize("hasAuthority('xc_sysmanager_user_add')")
    @Override
    @PostMapping("/adduser")
    public ResponseResult add (@RequestBody UserInfo userInfo) {
        return userService.add(userInfo);
    }


    @PreAuthorize("hasAuthority('xc_sysmanager_user_update')")
    @Override
    @PutMapping("/updateuser")
    public ResponseResult updateStatus (@RequestBody XcUser xcUser) {
        return userService.updateStatus(xcUser);

    }

    @PreAuthorize("hasAuthority('xc_sysmanager_user_resetpwd')")
    @Override
    @PutMapping("/resetuser")
    public  ResponseResult resetPassword (@RequestBody XcUser xcUser) {
        return userService.resetPassword(xcUser);
    }

    @Override
    @GetMapping("/getteacher/{id}")
    public XcTeacher getTeacherInfo (@PathVariable("id") String id) {
        return userService.getTeacherInfo(id);
    }

    @Override
    @PostMapping("/editteacher")
    public ResponseResult editTeacherInfo (@RequestBody XcTeacher xcTeacher) {
        return userService.updateTeacherInfo(xcTeacher);
    }


    @PostMapping("/bindphone")
    @Override
    public ResponseResult bindPhone (String phone, String code) {
        Map<String, String> fromHeader = Oauth2Util.getJwtClaimsFromHeader(request);
        String username = fromHeader.get("user_name");
        if (StringUtils.isEmpty(username)) {
            return new ResponseResult(CommonCode.UNAUTHENTICATED);
        }
        return userService.bindPhone(phone,code,username);
    }

    @Override
    public ResponseResult removeBind (String account) {
        return null;
    }

    @PostMapping("/bindemail")
    @Override
    public ResponseResult bindEmail (String email, String code) {
        Map<String, String> fromHeader = Oauth2Util.getJwtClaimsFromHeader(request);
        String username = fromHeader.get("user_name");
        if (StringUtils.isEmpty(username)) {
            return new ResponseResult(CommonCode.UNAUTHENTICATED);
        }
        return userService.bindEmail(email,code,username);
    }

    @PostMapping("/editpass")
    @Override
    public ResponseResult editPass (String newPass, String oldPass) {
        Map<String, String> fromHeader = Oauth2Util.getJwtClaimsFromHeader(request);
        String username = fromHeader.get("user_name");
        if (StringUtils.isEmpty(username)) {
            return new ResponseResult(CommonCode.UNAUTHENTICATED);
        }
        return userService.editPass(newPass, oldPass, username);
    }

    @PostMapping("/getcode")
    @Override
    public ResponseResult getCode (@RequestParam("account") String account) {
        Map<String, String> fromHeader = Oauth2Util.getJwtClaimsFromHeader(request);
        String username = fromHeader.get("user_name");
        if (StringUtils.isEmpty(username)) {
            return new ResponseResult(CommonCode.UNAUTHENTICATED);
        }
        return userService.getCode(account, username);
    }
}
