package com.xuecheng.framework.domain.learning.requset;

import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author: olw
 * @date: 2020/10/30 21:14
 * @description:  查询用户选课列表参数
 */
@Data
@ToString
public class ChooseCourseResultList extends RequestData {

    /**用户id*/
    private String userId;
}
