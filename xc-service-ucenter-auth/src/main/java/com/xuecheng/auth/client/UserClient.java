package com.xuecheng.auth.client;


import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author: olw
 * @date: 2020/10/18 17:57
 * @description:  用户中心服务接口
 */
@FeignClient(XcServiceList.XC_SERVICE_UCENTER)
public interface UserClient {

    @GetMapping("/ucenter/getuserext")
    public XcUserExt getUserExt (@RequestParam("username") String username);

    @GetMapping("/ucenter/getuser/{userId}")
    public XcUser getUser (@PathVariable("userId") String userId);
}
