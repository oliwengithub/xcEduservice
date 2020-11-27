package com.xuecheng.api.order;

import com.xuecheng.framework.domain.order.XcOrders;
import com.xuecheng.framework.domain.order.request.OrderRequestList;
import com.xuecheng.framework.domain.order.response.PayOrderResult;
import com.xuecheng.framework.domain.order.response.PayQrcodeResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("订单支付接口")
public interface OrderPayControllerApi {

    @ApiOperation("创建微信支付二维吗")
    public PayQrcodeResult createWeixinQrcode (String orderNumber);

    @ApiOperation("查询微信支付结果")
    public PayOrderResult queryWeixinPayStatus (String orderNumber);


}
