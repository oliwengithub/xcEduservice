package com.xuecheng.framework.domain.order.request;

import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class OrderRequestList extends RequestData {

    String orderNumber;

    String courseId;

    String userId;

    String status;

    String startTime;

    String endTime;



}
