package com.xuecheng.auth.controller;


import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.api.auth.UserRegisterControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.domain.ucenter.response.UserBase;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi, UserRegisterControllerApi {

    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;

    @Autowired
    AuthService authService;

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        if(loginRequest == null || StringUtils.isEmpty(loginRequest.getUsername())){
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        if(loginRequest == null || StringUtils.isEmpty(loginRequest.getPassword())){
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        // 账号
        String username = loginRequest.getUsername();
        // 密码
        String password = loginRequest.getPassword();

        // 申请令牌
        AuthToken authToken =  authService.login(username,password,clientId,clientSecret);

        // 用户身份令牌
        String access_token = authToken.getAccess_token();
        // 将令牌存储到cookie
        this.saveCookie(access_token);

        return new LoginResult(CommonCode.SUCCESS,access_token);
    }

    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        String access_token = getCookie();
        // 清除用户token
        authService.delToken(access_token);
        // 清除cookie
        clearCookie(access_token);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    @GetMapping("/userjwt")
    public JwtResult userJwt () {
        String access_token = getCookie();
        AuthToken authToken = authService.getUserToken(access_token);

        // 未获取到值直接返回null 有前端处理
        if (authToken == null) {
            new JwtResult(CommonCode.FAIL, null);
        }
        return new JwtResult(CommonCode.SUCCESS, authToken.getJwt_token());
    }

    @Override
    @GetMapping("/getuserbase/{userIds}")
    public Map<String, UserBase> getUserBase (@PathVariable("userIds") String userIds) {
        // 方便接口扩展 将参数修改成id数组
        if (StringUtils.isEmpty(userIds)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String[] userIdsString = userIds.split(",");
        return authService.getUserBase(userIdsString);
    }

    @Override
    @PostMapping("/register")
    public ResponseResult register (XcUser xcUser) {
        return authService.register(xcUser);
    }

    @Override
    @PostMapping("/checkusername")
    public ResponseResult checkUsername (String username) {
        return authService.checkUsername(username);
    }

    /**
     * 获取cookie
     * @author: olw
     * @Date: 2020/10/19 17:20
     * @returns: java.lang.String
    */
    private String getCookie () {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> uid = CookieUtil.readCookie(request, "uid");
        return uid.get("uid");
    }

    /**
     * 将令牌存储到cookie
     * @author: olw
     * @Date: 2020/10/19 20:28
     * @param token
     * @returns: void
    */
    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response,cookieDomain,"/","uid",token,cookieMaxAge,false);

    }

    /**
     * 清除登录cookie
     * @author: olw
     * @Date: 2020/10/19 20:31
     * @param token
     * @returns: void
    */
    private void clearCookie (String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, 0, false);
    }
}
