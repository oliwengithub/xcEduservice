package com.xuecheng.order.controller;

import com.xuecheng.api.order.OrderPayControllerApi;
import com.xuecheng.framework.domain.order.XcOrders;
import com.xuecheng.framework.domain.order.response.CreateOrderResult;
import com.xuecheng.framework.domain.order.response.OrderCode;
import com.xuecheng.framework.domain.order.response.PayOrderResult;
import com.xuecheng.framework.domain.order.response.PayQrcodeResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.utils.IpUtil;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.order.service.OrderService;
import com.xuecheng.order.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderPayController extends BaseController implements OrderPayControllerApi {

    @Autowired
    PayService payService;

    @Override
    @PostMapping("/pay/createWeixinQrcode")
    public PayQrcodeResult createWeixinQrcode (String orderNumber) {
        XcOauth2Util oauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt jwt = oauth2Util.getUserJwtFromHeader(request);
        String userId = "";
        if (jwt != null) {
            userId = jwt.getId();
        }
        String ip = IpUtil.getIp(request);

        return payService.unifiedPayment(orderNumber, userId, ip);
    }

    @Override
    @GetMapping("/pay/queryWeixinPayStatus")
    public PayOrderResult queryWeixinPayStatus (String orderNumber)  {
        XcOauth2Util oauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt jwt = oauth2Util.getUserJwtFromHeader(request);
        String userId = "";
        if (jwt != null) {
            userId = jwt.getId();
        }
        String ip = IpUtil.getIp(request);
        return payService.queryOrderStats(orderNumber, ip, userId);
    }

}
