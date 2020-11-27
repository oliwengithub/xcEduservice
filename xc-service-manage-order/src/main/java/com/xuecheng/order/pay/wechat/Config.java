package com.xuecheng.order.pay.wechat;

public class Config {
    /**小程序唯一标识   (在微信小程序管理后台获取)*/
    public static final String APP_ID = "wx8397f8696b538317";
    /**小程序的 app secret (在微信小程序管理后台获取)*/
    public static final String SECRET = "634a74db15ead6fddd2c7bbe9a15b1f3";

    //todo 修改此处配置
    /**商户号*/
    public static final String MCH_ID = "1473426802";
    /**API密钥*/
    public static final String KEY = "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb";
    /**交易类型*/
    public static final String TRADE_TYPE = "NATIVE";
    /**统一下单API接口链接*/
    public static final String URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    /**异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。*/
//    public static final String NOTIFY_URL = "https://88988c78.ngrok.io/api/pay/notify/shopping";
    /**todo 修改成正式地址*/
    public static final String NOTIFY_URL_SHOPPING = "http://wxapi.gz.itcast.cn/WeChatPay/WeChatPayNotify";
    /** 客户端轮训地址查询支付状态地址*/
    public static final String CHECK_PAY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    /**签名类型，默认为MD5，支持HMAC-SHA256和MD5。*/
    public static final String SIGN_TYPE = "MD5";


    /**返回状态码(此字段是接口通信情况标识，非交易成功与否的标识): SUCCESS/FAIL*/
    public static final String REQUEST_STATUS_KEY = "return_code";
    /**业务结果：SUCCESS/FAIL*/
    public static final String PAY_STATUS_KEY = "result_code";
    public static final String PAY_RETURN_CODE_SUCCESS = "SUCCESS";
    // public static final String PAY_RESULT_SUCCESS = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    // public static final String PAY_RESULT_FAIL = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[报文为空]]></return_msg></xml>";
    /**业务结果： 判断交易是否成功 NOPAY未支付 ORDERPAID订单已支付（重复调用统一下单接口）*/
    public static final String PAY_RESULT_NOPAY = "NOPAY";
    public static final String PAY_RESULT_SUCCESS = "SUCCESS";
    public static final String PAY_RESULT_FAIL = "FAIL";
    public static final String PAY_RESULT_ERR_CODE_ORDERPAID = "ORDERPAID";


}
