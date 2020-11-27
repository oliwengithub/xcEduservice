package com.xuecheng.order;

import com.xuecheng.framework.domain.order.ext.OrderInfo;
import com.xuecheng.framework.domain.order.response.PayQrcodeResult;
import com.xuecheng.framework.utils.XMLUtil;
import com.xuecheng.order.pay.wechat.Config;
import com.xuecheng.order.pay.wechat.Signature;
import com.xuecheng.order.pay.wechat.VerifyUtil;
import com.xuecheng.order.service.PayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOrderPay {

    @Autowired
    PayService payService;

    @Test
    public void testPay() {


        String orderNumber = "319867403872112641";
        String ip = "127.0.0.1";
        String userId = "49";

        PayQrcodeResult payQrcodeResult = payService.unifiedPayment(orderNumber, userId, ip);
        System.out.println(payQrcodeResult);
    }

    @Test
    public void testCreateXml () throws Exception {
        /*String orderNumber = "299036931059486720";
        String ip = "127.0.0.1";
        String userId = "49";
        OrderInfo order = new OrderInfo();

        order.setAppid(Config.APP_ID);
        order.setMch_id(Config.MCH_ID);
        order.setNonce_str(createNonceStr(32));
        order.setOut_trade_no(orderNumber);

        String sign = Signature.getSign(order);
        order.setSign(sign);
        String requestXml = XMLUtil.createRequestXml(order);
        System.out.println(requestXml);*/
    }

    private String createNonceStr(int count){
        String[] nums = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        int maxIndex = nums.length - 1;
        int numIndex;
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++){
            numIndex = (int)(Math.random() * maxIndex);
            builder.append(nums[numIndex]);
        }
        return builder.toString();
    }

    @Test
    public void doXMLParse () throws Exception {
        String xml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx8397f8696b538317]]></appid> <mch_id><![CDATA[1473426802]]></mch_id> <nonce_str><![CDATA[l9LtOk97IIOiAGW4]]></nonce_str> <sign><![CDATA[BE1CF21EBFD3692DF74E2157CE723CCD]]></sign> <result_code><![CDATA[SUCCESS]]></result_code> <prepay_id><![CDATA[wx10201542260489f034281664c1e04a0000]]></prepay_id> <trade_type><![CDATA[NATIVE]]></trade_type> <code_url><![CDATA[weixin://wxpay/bizpayurl?pr=Behm7DJ00]]></code_url> </xml>";
//        {nonce_str=l9LtOk97IIOiAGW4, code_url=weixin://wxpay/bizpayurl?pr=Behm7DJ00, appid=wx8397f8696b538317, sign=BE1CF21EBFD3692DF74E2157CE723CCD, trade_type=NATIVE, return_msg=OK, result_code=SUCCESS, mch_id=1473426802, return_code=SUCCESS, prepay_id=wx10201542260489f034281664c1e04a0000}
        Map<String, String> stringStringMap = VerifyUtil.doXMLParse(xml);
        System.out.println(stringStringMap);
    }

    @Test
    public void testCheckPay () throws Exception {

    /*<xml>
        <return_code><![CDATA[SUCCESS]]></return_code>
        <return_msg><![CDATA[OK]]></return_msg>
        <appid><![CDATA[wx8397f8696b538317]]></appid>
        <mch_id><![CDATA[1473426802]]></mch_id>
        <device_info><![CDATA[]]></device_info>
        <nonce_str><![CDATA[qARBBjo3mP5Deq22]]></nonce_str>
        <sign><![CDATA[5B32C7A220795522133EB506C5E405DC]]></sign>
        <result_code><![CDATA[SUCCESS]]></result_code>
        <total_fee>1</total_fee>
        <out_trade_no><![CDATA[319867403872112641]]></out_trade_no>
        <trade_state><![CDATA[NOTPAY]]></trade_state>
        <trade_state_desc><![CDATA[订单未支付]]></trade_state_desc>
    </xml>*/
    /*<xml><return_code><![CDATA[SUCCESS]]></return_code>
        <return_msg><![CDATA[OK]]></return_msg>
        <appid><![CDATA[wx8397f8696b538317]]></appid>
        <mch_id><![CDATA[1473426802]]></mch_id>
        <nonce_str><![CDATA[djMCZR1BxxhNfh3B]]></nonce_str>
        <sign><![CDATA[12296BD5AF3C4FD5BC2F0C296AFAFC76]]></sign>
        <result_code><![CDATA[FAIL]]></result_code>
        <err_code><![CDATA[ORDERPAID]]></err_code>
        <err_code_des><![CDATA[该订单已支付]]></err_code_des>
    </xml>*/

    }
}
