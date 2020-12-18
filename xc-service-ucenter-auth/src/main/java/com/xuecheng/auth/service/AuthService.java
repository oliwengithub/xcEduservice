package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.auth.client.UserClient;
import com.xuecheng.auth.dao.XcUserRepository;
import com.xuecheng.auth.dao.XcUserRoleRepository;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.Constants;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.XcUserRole;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.UcenterCode;
import com.xuecheng.framework.domain.ucenter.response.UserBase;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.BCryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class AuthService {


    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;
    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    XcUserRepository xcUserRepository;

    @Autowired
    XcUserRoleRepository xcUserRoleRepository;

    @Autowired
    UserClient userClient;

    /**
     * 用户认证申请令牌，将令牌存储到redis
     * @author: olw
     * @Date: 2020/10/18 14:49
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @returns: com.xuecheng.framework.domain.ucenter.ext.AuthToken
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {

        if (StringUtils.isEmpty(username)){
            ExceptionCast.cast(UcenterCode.UCENTER_USERNAME_NONE);
        }
        if (StringUtils.isEmpty(password)){
            ExceptionCast.cast(UcenterCode.UCENTER_PASSWORD_NONE);
        }

        // 请求spring security申请令牌
        AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
        if(authToken == null){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        // 用户身份令牌
        String access_token = authToken.getAccess_token();
        // 存储到redis中的内容
        String jsonString = JSON.toJSONString(authToken);
        // 将令牌存储到redis
        boolean result = this.saveToken(access_token, jsonString, tokenValiditySeconds);
        if (!result) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;

    }

    /**
     * 存储到令牌到redis
     * @param access_token 用户身份令牌
     * @param content  内容就是AuthToken对象的内容
     * @param ttl 过期时间
     * @return
     */
    private boolean saveToken(String access_token,String content,long ttl){
        String key = "user_token:" + access_token;
        stringRedisTemplate.boundValueOps(key).set(content,ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire>0;
    }

    // 申请令牌
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret){
        // 从eureka中获取认证服务的地址（因为spring security在认证服务中）
        // 从eureka中获取认证服务的一个实例的地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        // 此地址就是http://ip:port
        URI uri = serviceInstance.getUri();
        // 令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri+ "/auth/oauth/token";
        // 定义header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic(clientId, clientSecret);
        header.add("Authorization",httpBasic);

        // 定义body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
        // String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables

        // 设置restTemplate远程调用时候，对400和401不让报错，正确返回数据
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode()!= HttpStatus.BAD_REQUEST.value() && response.getRawStatusCode()!=HttpStatus.UNAUTHORIZED.value()){
                    super.handleError(response);
                }
            }
        });

        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

        //申请令牌信息
        Map bodyMap = exchange.getBody();
        if(bodyMap == null ||
                bodyMap.get("access_token") == null ||
                bodyMap.get("refresh_token") == null ||
                bodyMap.get("jti") == null){
            String error_description = (String) bodyMap.get("error_description");
            if (error_description.indexOf("UserDetailsService returned null")>=0) {
                ExceptionCast.cast(UcenterCode.UCENTER_ACCOUNT_NOTEXISTS);
            }else if (error_description.indexOf("坏的凭证")>=0) {
                ExceptionCast.cast(UcenterCode.UCENTER_CREDENTIAL_ERROR);
            }
            return null;
        }
        AuthToken authToken = new AuthToken();
        // 用户身份令牌
        authToken.setAccess_token((String) bodyMap.get("jti"));
        // 刷新令牌
        authToken.setRefresh_token((String) bodyMap.get("refresh_token"));
        // jwt令牌
        authToken.setJwt_token((String) bodyMap.get("access_token"));
        return authToken;
    }



    /**
     * 获取http Basic的串
     * @author: olw
     * @Date: 2020/10/18 14:47
     * @param clientId
     * @param clientSecret
     * @returns: java.lang.String
     */
    private String getHttpBasic(String clientId,String clientSecret){
        String string = clientId+":"+clientSecret;
        // 将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }


    /**
     * 根据授权cookie获取用户的token信息
     * @author: olw
     * @Date: 2020/10/19 17:31
     * @param access_token
     * @returns: com.xuecheng.framework.domain.ucenter.ext.AuthToken
     */
    public AuthToken getUserToken (String access_token) {

        String tokenKey = "user_token:" +  access_token;
        // 获取用户token
        String userToken = stringRedisTemplate.opsForValue().get(tokenKey);
        if (userToken != null) {
            try {
                return JSON.parseObject(userToken, AuthToken.class);
            }catch (Exception e){
                LOGGER.error("getUserToken from redis and execute JSON.parseObject error {}",e.getMessage());
                return null;
            }
        }

        return null;
    }

    /**
     * 清除redis中的token
     * @author: olw
     * @Date: 2020/10/19 20:33
     * @param access_token
     * @returns: boolean
     */
    public boolean delToken (String access_token) {
        String name = "user_token:" + access_token;
        stringRedisTemplate.delete(name);
        return true;
    }


    /**
     * 用户注册
     * @author: olw
     * @Date: 2020/10/28 17:38
     * @param xcUser
     * @returns: com.xuecheng.framework.model.response.ResponseResult
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult register (XcUser xcUser) {
        // 根据用户名查询用户是否已经存在
        ResponseResult responseResult = this.checkUsername(xcUser.getUsername());
        if (!responseResult.isSuccess()) {
            return responseResult;
        }

        XcUser user = new XcUser();
        //添加用户
        BeanUtils.copyProperties(xcUser, user);
        user.setCreateTime(new Date());
        user.setPassword(BCryptUtil.encode(xcUser.getPassword()));
        user.setUpdateTime(new Date());
        // 101001学生 101002老师
        // 103001正常 103002 停用 103003 注销
        user.setStatus("103001");
        XcUser save = xcUserRepository.save(user);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setCreateTime(new Date());
        xcUserRole.setUserId(user.getId());
        // 角色类型
        if (save.getUtype().equals("101001")){
            xcUserRole.setCreator("学生");
            xcUserRole.setRoleId(Constants.SYSTEM_ROLE_STUDENT);
        }else if(save.getUtype().equals("101002")) {
            xcUserRole.setCreator("老师");
            xcUserRole.setRoleId(Constants.SYSTEM_ROLE_TEACHER);
        }
        xcUserRoleRepository.save(xcUserRole);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 检验用户名是否存在
     * @author: olw
     * @Date: 2020/10/28 18:02
     * @param username
     * @returns: com.xuecheng.framework.model.response.ResponseResult
     */
    public ResponseResult checkUsername (String username) {
        XcUser user = xcUserRepository.findXcUserByUsername(username);
        if (user != null){
            return new ResponseResult(AuthCode.AUTH_USERNAME_EXIST);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取用户基本信息
     * @author: olw
     * @Date: 2020/12/13 20:24
     * @param userIds
     * @returns: com.xuecheng.framework.domain.ucenter.response.UserBase
    */
    public  Map<String, UserBase> getUserBase (String[] userIds) {
        List<String> list = Arrays.asList(userIds);
        Map<String, UserBase> userBaseMap = new HashMap<>(list.size());
        list.forEach(e->{
            XcUser user = userClient.getUser(e);
            UserBase userBase = new UserBase();
            BeanUtils.copyProperties(user, userBase);

            userBaseMap.put(e, userBase);
        });
        return userBaseMap;
    }
}
