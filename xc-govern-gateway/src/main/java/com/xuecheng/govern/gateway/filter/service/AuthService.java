package com.xuecheng.govern.gateway.filter.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author: olw
 * @date: 2020/10/20 17:59
 * @description:  服务网关校验业务
 */
@Service
public class AuthService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${auth.tokenValiditySeconds}")
    private long tokenValiditySeconds;

    /**
     * 查询身份令牌
     * @author: olw
     * @Date: 2020/10/20 18:03
     * @param request
     * @returns: java.lang.String
    */
    public String getTokenFromCookie(HttpServletRequest request){
        Map<String, String> cookieMap = CookieUtil.readCookie(request, "uid");
        String access_token = cookieMap.get("uid");
        if(StringUtils.isEmpty(access_token)){
            return null;
        }
        return access_token;
    }

    /**
     * 从header中查询jwt令牌
     * @author: olw
     * @Date: 2020/10/20 18:04
     * @param request
     * @returns: java.lang.String
    */
    public String getJwtFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
        // 拒绝访问
            return null;
        }
        if(!authorization.startsWith("Bearer ")){
        // 拒绝访问
            return null;
        }
        return authorization;
    }

    /**
     * 查询令牌的有效期
     * @author: olw
     * @Date: 2020/10/20 18:04
     * @param access_token
     * @returns: long
    */
    public long getExpire(String access_token) {
        //token在redis中的key
        String key = "user_token:"+access_token;
        Long expire = stringRedisTemplate.getExpire(key);
        return expire;
    }

    /**
     * 查询令牌的有效期
     * @author: olw
     * @Date: 2020/10/20 18:04
     * @param access_token
     * @returns: long
     */
    public void setExpire(String access_token) {
        //token在redis中的key
        String key = "user_token:"+access_token;
        stringRedisTemplate.expire(key, tokenValiditySeconds, TimeUnit.SECONDS);
    }
}
