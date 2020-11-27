package com.xuecheng.order.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.order.XcOrders;
import com.xuecheng.framework.domain.order.XcOrdersDetail;
import com.xuecheng.framework.domain.order.XcOrdersPay;
import com.xuecheng.framework.domain.order.response.OrderCode;
import com.xuecheng.framework.domain.order.response.PayOrderResult;
import com.xuecheng.framework.domain.order.response.PayQrcodeResult;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.utils.HttpClient;
import com.xuecheng.framework.utils.XMLUtil;
import com.xuecheng.order.client.SearchClient;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.dao.*;
import com.xuecheng.order.pay.wechat.Config;
import com.xuecheng.order.pay.wechat.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 * @author: olw
 * @date: 2020/11/5 18:53
 * @description:  支付业务层
 */
@Service
public class PayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayService.class);

    @Autowired
    XcOrdersRepository xcOrdersRepository;

    @Resource
    XcOrdersPayRepository xcOrdersPayRepository;

    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    XcTaskRepository xcTaskRepository;

    @Autowired
    XcOrdersDetailRepository xcOrdersDetailRepository;

    @Value("${server.isDebug}")
    private boolean isDebug;

    @Autowired
    SearchClient searchClient;

    /**
     * 统一下单接口
     * @author: olw
     * @Date: 2020/11/5 19:12
     * @param orderNumber
     * @param userId
     * @returns: com.xuecheng.framework.domain.order.response.PayQrcodeResult
    */
    public PayQrcodeResult unifiedPayment (String orderNumber, String userId , String ip){
        Optional<XcOrders> ordersOptional = xcOrdersRepository.findById(orderNumber);
        if (!ordersOptional.isPresent()) {
            ExceptionCast.cast(OrderCode.Pay_NOTFOUNDORDER);
        }
        XcOrders order = xcOrdersRepository.findXcOrdersByUserIdAndOrderNumber(userId, orderNumber);
        if (order == null) {
            ExceptionCast.cast(OrderCode.Pay_USERERROR);
        }
        String details = order.getDetails();
        Map map = new HashMap(1);
        List<Map> mapList = JSONArray.parseArray(details, Map.class);
        if (mapList != null && mapList.size() > 0){
            map = mapList.get(0);
        }
        String courseId = (String) map.get("courseId");
        // 获取课程信息
        CoursePub coursePub = this.searchCoursePub(courseId);
       try {
           float orderPrice = order.getPrice();
           int price = getPrice(orderPrice);
           // 获取微信支付二维码
           Map<String, String> resultMap = this.createPayQrcode(coursePub, price, orderNumber, ip);

           if ((Config.PAY_RETURN_CODE_SUCCESS.equals(resultMap.get(Config.REQUEST_STATUS_KEY))
                   && Config.PAY_RESULT_SUCCESS.equals(resultMap.get(Config.PAY_STATUS_KEY)))){
               PayQrcodeResult payQrcodeResult = new PayQrcodeResult(CommonCode.SUCCESS);
               payQrcodeResult.setCodeUrl(resultMap.get("code_url"));
               payQrcodeResult.setOrderNumber(orderNumber);
               payQrcodeResult.setMoney(orderPrice);
               return payQrcodeResult;
           }else if ((Config.PAY_RETURN_CODE_SUCCESS.equals(resultMap.get(Config.REQUEST_STATUS_KEY))) && Config.PAY_RESULT_FAIL.equals(resultMap.get(Config.PAY_STATUS_KEY))
                   && (Config.PAY_RESULT_ERR_CODE_ORDERPAID.equals(resultMap.get("err_code")))) {
               // 统一下单失败 订单在微信已经支付，但订单状态没有更新
               finishOrderPay(orderNumber, userId);
               return new PayQrcodeResult(OrderCode.Pay_ORDERPAID);
           }

       }catch (Exception e) {
           LOGGER.error("统一下单支付失败", e);
           ExceptionCast.cast(OrderCode.Pay_ERROR);
       }
        return new PayQrcodeResult(OrderCode.Pay_ERROR);
    }

    /**
     * 查新订单支付转态
     * @author: olw
     * @Date: 2020/11/10 21:25
     * @param orderNumber
     * @returns: com.xuecheng.framework.domain.order.response.PayOrderResult
    */
    public PayOrderResult queryOrderStats (String orderNumber, String ip, String userId)  {
        // 查询订单支付状态
        XcOrdersPay ordersPay = xcOrdersPayRepository.findXcOrdersPayByOrderNumber(orderNumber);

        // 402001未支付 402002支付成功 402003已关闭 402004支付失败
        if (ordersPay != null && "402001".equals(ordersPay.getStatus())) {
            // 订单未支付不一定未支付
            // 查询订单微信支付状态
            Map<String, String> stringMap = this.checkWeiXinPayStatus(orderNumber);
            String result_code = stringMap.get("result_code");
            String return_code = stringMap.get("return_code");
            String trade_state = stringMap.get("trade_state");
            int total_fee = Integer.parseInt(stringMap.get("total_fee"));
            float money = (float) (total_fee*0.01);
            if ((Config.PAY_RETURN_CODE_SUCCESS.equals(return_code)) && (Config.PAY_RESULT_SUCCESS.equals(result_code))
                    &&  (Config.PAY_RESULT_NOPAY.equals(trade_state))) {
                // 未支付 返回支付二维码
                PayQrcodeResult payQrcodeResult = this.unifiedPayment(orderNumber, userId, ip);
                if (payQrcodeResult.isSuccess()) {
                    String codeUrl = payQrcodeResult.getCodeUrl();
                    PayOrderResult payOrderResult = new PayOrderResult(CommonCode.SUCCESS);
                    payOrderResult.setCodeUrl(codeUrl);
                    payOrderResult.setOrderNumber(orderNumber);
                    payOrderResult.setMoney(money);
                    return payOrderResult;
                }

            }else if ((Config.PAY_RETURN_CODE_SUCCESS.equals(return_code)) && (Config.PAY_RESULT_SUCCESS.equals(result_code))
                    &&  (Config.PAY_RESULT_SUCCESS.equals(trade_state))) {
                // 订单在微信已经支付，但订单状态没有更新
                // 更新订单信息
                // 1.订单表 2.订单支付表
                XcOrdersPay xcOrdersPay = finishOrderPay(orderNumber, userId);
                return new PayOrderResult(CommonCode.SUCCESS, xcOrdersPay);
            }
        }else if (ordersPay != null && "402002".equals(ordersPay.getStatus())) {
            // 订单支付成功 直接返回数据
            return new PayOrderResult(CommonCode.SUCCESS, ordersPay);
        }
        return new PayOrderResult(CommonCode.FAIL);
    }


    /**
     *
     * @author: 李海涛
     * @Date: 2019/7/29 11:11 AM
     * @param price  支付金额
     * @returns: int 换算成单位为分的数字
     */
    private int getPrice(double price){
        if(isDebug){
            return 1;
        }
        return (int)(price * 100);
    }

    /**
     * 根据课程id到搜索服务获取课程信息
     * @author: olw
     * @Date: 2020/11/11 16:23
     * @param courseId
     * @returns: com.xuecheng.framework.domain.course.CoursePub
    */
    public CoursePub searchCoursePub (String courseId) {
        CoursePub coursePub = new CoursePub();
        Map<String, CoursePub> all = searchClient.getAll(courseId);
        if ( all != null) {
            coursePub = all.get(courseId);
        }
        return coursePub;
    }

    /**
     * 统一下单请求微信支付生成支付二维码
     * @author: olw
     * @Date: 2020/11/11 17:56
     * @param coursePub
     * @param price
     * @returns: java.util.Map<java.lang.String,java.lang.String>
    */
    private Map<String, String> createPayQrcode(CoursePub coursePub, int price, String orderNumber, String ip) {
        Map<String, String> map = new HashMap<>();

        try {
            String nonceStr = XMLUtil.createNonceStr(32);
            SortedMap<Object, Object> orderInfo = new TreeMap() {{
                put("appid",Config.APP_ID);
                put("body",coursePub.getName());
                put("mch_id",Config.MCH_ID);
                put("nonce_str",nonceStr);
                put("notify_url",Config.NOTIFY_URL_SHOPPING);
                put("out_trade_no",orderNumber);
                put("spbill_create_ip",ip);
                put("total_fee",price);
                put("trade_type",Config.TRADE_TYPE);
            }};
            String wxPaySign = XMLUtil.createWxPaySign(Config.KEY, orderInfo);
            orderInfo.put("sign", wxPaySign);
            String xml = XMLUtil.map2XML(orderInfo);
            HttpClient httpClient = new HttpClient(Config.URL);
            httpClient.setXmlParam(xml);
            httpClient.post();
            // 发起微信支付 获取支付二维码
            String content = httpClient.getContent();
            map = VerifyUtil.doXMLParse(content);
        }catch (Exception e) {
            LOGGER.error("发起微信支付失败", e);
        }
        return map;
    }

    /**
     * 查询订单微信支付状态
     * @author: olw
     * @Date: 2020/11/11 18:57
     * @param orderNumber
     * @returns: java.util.Map<java.lang.String,java.lang.String>
    */
    private Map<String, String> checkWeiXinPayStatus (String orderNumber) {
        // nonce_str out_trade_no appid mch_id
        // 构建签名参数
        String nonceStr = XMLUtil.createNonceStr(32);
        SortedMap<Object, Object> map = new TreeMap() {{
            put("appid",Config.APP_ID);
            put("mch_id",Config.MCH_ID);
            put("nonce_str",nonceStr);
            put("out_trade_no",orderNumber);
        }};
        // 生成签名
        String wxPaySign = XMLUtil.createWxPaySign(Config.KEY, map);
        Map<String, String> stringMap = new HashMap();
        try {
            map.put("sign", wxPaySign);
            String xml = XMLUtil.map2XML(map);
            HttpClient httpClient = new HttpClient(Config.CHECK_PAY_URL);
            httpClient.setXmlParam(xml);
            httpClient.post();
            // 查询支付状态
            String content = httpClient.getContent();
            stringMap = VerifyUtil.doXMLParse(content);
        }catch (Exception e) {
            LOGGER.error("调用微信支付接口异常", e);
        }
        return stringMap;
    }

    /**
     * 添加选课任务请求内容 （课程相关信息）
     * @author: olw
     * @Date: 2020/11/11 20:25
     * @param orderNumber
     * @returns: java.lang.String
    */
    private String requestBody (String orderNumber, String userId) {
        // 获取购买的课程id
        XcOrdersDetail ordersDetail = xcOrdersDetailRepository.findXcOrdersDetailByOrderNumber(orderNumber);
        String courseId = ordersDetail.getCourseId();
        CoursePub coursePub = this.searchCoursePub(courseId);
        Map<String, Object> map = new HashMap(){{
           put("courseId", courseId);
           put("courseName", coursePub.getName());
           put("userId", userId);
           put("valid", coursePub.getValid());
           put("startTime", coursePub.getStartTime());
           put("endTime", coursePub.getEndTime());
        }};
        // 获取课程总小结数
        String teachplan = coursePub.getTeachplan();
        TeachplanNode teachplanNode = JSON.parseObject(teachplan, TeachplanNode.class);
        List<TeachplanNode> children = teachplanNode.getChildren();
        int count = 0;
        for (TeachplanNode child : children) {
            int size = child.getChildren().size();
            count = count + size;
        }
        map.put("teachpalnNum", count);

        String jsonString = JSON.toJSONString(map);
        return jsonString;
    }

    /**
     * 完成订单支付
     * @author: olw
     * @Date: 2020/11/14 15:19
     * @param orderNumber
     * @param userId
     * @returns: boolean
    */
    @Transactional(rollbackFor = Exception.class)
    public XcOrdersPay finishOrderPay (String orderNumber, String userId) {
        // 查询订单支付状态
        XcOrdersPay ordersPay = xcOrdersPayRepository.findXcOrdersPayByOrderNumber(orderNumber);
        if(ordersPay == null) {
            ordersPay = new XcOrdersPay();
            ordersPay.setOrderNumber(orderNumber);
        }

        Optional<XcOrders> optional = xcOrdersRepository.findById(orderNumber);
        if (optional.isPresent()) {
            XcOrders orders = optional.get();
            orders.setStatus("401002");
            xcOrdersRepository.save(orders);
        }
        ordersPay.setStatus("402002");
        xcOrdersPayRepository.save(ordersPay);

        // 创建选课任务
        XcTask xcTask = new XcTask();
        xcTask.setMqExchange(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE);
        xcTask.setMqRoutingkey(RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE_KEY);
        // 105001未完成 105002 完成 105003 错误
        xcTask.setStatus("105001");
        // 106001 添加选课
        xcTask.setTaskType("106001");
        // 设置默认版本号
        xcTask.setVersion(0);
        Date date = new Date();
        xcTask.setCreateTime(date);
        xcTask.setUpdateTime(date);
        xcTask.setCreateTime(date);
        // 构建任务请求内容
        String requestBody = this.requestBody(orderNumber, userId);
        xcTask.setRequestBody(requestBody);
        xcTaskRepository.save(xcTask);

        return ordersPay;
    }


}
