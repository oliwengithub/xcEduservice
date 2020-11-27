package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.filter.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AUTH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author: olw
 * @date: 2020/10/20 16:24
 * @description:  zuul服务网关
 */
@Component
public class LoginFilter extends ZuulFilter {

    private static  final Logger LOGGER = LoggerFactory.getLogger(LoginFilter.class);

    @Autowired
    AuthService authService;

    @Override
    public String filterType () {
        // 指定过过滤器的类型
        /**
         * pre: 执行 routing：在路由请求时调用
         * post：在routing和errror过滤器之后调用
         * error：处理请求时发生错误调用
        */
        return "pre";
    }

    @Override
    public int filterOrder () {
        // 此方法返回整型数值，通过此数值来定义过滤器的执行顺序，数字越小优先级越高
        return 0;
    }

    @Override
    public boolean shouldFilter () {
        return true;
    }

    @Override
    public Object run () throws ZuulException {

        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        // 取出头部信息
        String header = authService.getJwtFromHeader(request);
        if (header == null) {
            // 拒绝访问
            access_denied();
            return null;
        }
        // 获取cookie
        String token = authService.getTokenFromCookie(request);
        if (token == null) {
            // 拒绝访问
            access_denied();
            return null;
        }
        // 获取key
        //String key = "user_token:" + token;
        // 校验redis中的jwtToken是否过期
        long expire = authService.getExpire(token);
        if (expire < 0) {
            // 拒绝访问
            access_denied();
            return null;
        }
        if (expire <= 600) {
            // token还有将近10分钟过期是更新token过期时间
            authService.setExpire(token);
        }
        return null;
    }

    /**
     * 拒绝访问
     * @author: olw
     * @Date: 2020/10/20 18:09
     * @param
     * @returns: void
    */
    private void access_denied() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        // 拒绝访问
        requestContext.setSendZuulResponse(false);
        // 设置响应状态码
        requestContext.setResponseStatusCode(200);
        ResponseResult unauthenticated = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String jsonString = JSON.toJSONString(unauthenticated);
        requestContext.setResponseBody(jsonString);
        requestContext.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
    }
}
