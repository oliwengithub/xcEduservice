package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author: olw
 * @date: 2020/10/20 16:24
 * @description:  zuul服务网关测试
 */
//@Component
public class LoginFilterTest extends ZuulFilter {

    private static  final Logger LOGGER = LoggerFactory.getLogger(LoginFilterTest.class);


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
        // 过滤器的业务逻辑

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        HttpServletRequest request = requestContext.getRequest();
//取出头部信息Authorization
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            requestContext.setSendZuulResponse(false);// 拒绝访问
            requestContext.setResponseStatusCode(200);// 设置响应状态码
            ResponseResult unauthenticated = new ResponseResult(CommonCode.UNAUTHENTICATED);
            String jsonString = JSON.toJSONString(unauthenticated);
            requestContext.setResponseBody(jsonString);
//            requestContext.getResponse().setContentType("application/json;charset=UTF‐8");
            requestContext.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
            return null;
        }
        return null;
    }
}
