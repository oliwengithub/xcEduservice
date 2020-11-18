package com.xuecheng.framework.domain.order.ext;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author: olw
 * @date: 2020/11/5 17:48
 * @description:  微信支付统一下单实体
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {
    /**公众账号ID*/
    private String appid;
    /**商户号*/
    private String mch_id;
    /**随机字符串*/
    private String nonce_str;
    /**签名类型*/
    private String sign_type;
    /**签名*/
    private String sign;
    /**商品描述*/
    private String body;
    /**附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用*/
    private String attach;
    /**商户订单号*/
    private String out_trade_no;
    /**标价金额 ,单位为分*/
    private int total_fee;
    /**终端IP*/
    private String spbill_create_ip;
    /**通知地址*/
    private String notify_url;
    /**交易类型*/
    private String trade_type;
}
