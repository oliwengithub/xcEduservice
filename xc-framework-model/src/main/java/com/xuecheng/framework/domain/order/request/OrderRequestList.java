package com.xuecheng.framework.domain.order.request;

import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRequestList extends RequestData {

    String courseId;

    String userId;

}
