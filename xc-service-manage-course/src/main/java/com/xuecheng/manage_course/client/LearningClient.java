package com.xuecheng.manage_course.client;

import com.xuecheng.framework.client.XcServiceList;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(XcServiceList.XC_SERVICE_LEARNING)
/**
 *
 * @author: olw
 * @date: 2020/12/16 17:50
 * @description:  学习中心服务客服端接口
 */
public interface LearningClient {


}
