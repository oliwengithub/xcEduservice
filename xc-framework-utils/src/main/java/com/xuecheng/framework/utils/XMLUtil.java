package com.xuecheng.framework.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import java.security.MessageDigest;
import java.util.Set;
import java.util.SortedMap;

/**
 *
 * @author: olw
 * @date: 2020/11/9 19:57
 * @description:  微信支付xml统一工具类
 */
public class XMLUtil {

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 微信支付签名算法sign
     *
     * @param parameters 参数集合
     * @return 返回md5签名
     */
    @SuppressWarnings("unchecked")
    public static String createWxPaySign (String signKey, SortedMap<Object, Object> parameters) {
        // 多线程访问的情况下需要用StringBuffer
        StringBuilder sb = new StringBuilder();
        // 所有参与传参的key按照accsii排序（升序）
        Set es = parameters.keySet();
        for (Object set : es) {
            String k = set.toString();
            Object v = parameters.get(k);
            sb.append(k)
                    .append("=")
                    .append(v.toString())
                    .append("&");
        }
        sb.append("key=")
                .append(signKey);
        return str2MD5(sb.toString(), "utf-8").toUpperCase();
    }

    /**
     * MD5加密
     *
     * @param data   要加密的数据
     * @param encode 加密的编码
     * @return md5字符串
     */
    public static String str2MD5 (String data, String encode) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (encode == null || "".equals(encode)) {
                resultString = byteArrayToHexString(md.digest(data.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(data
                        .getBytes(encode)));
            }
        } catch (Exception exception) {
        }
        return resultString;
    }

    /**
     * byte数组转换16进制字符串
     *
     * @param b 要转换的byte数组
     * @return 16进制字符串
     */
    private static String byteArrayToHexString (byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }


    /** byte转换成16进制字符串
     * @param b 要转换的byte
     * @return byte对应的16进制字符串
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0){
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /** map集合参数转换为xml格式
     * @param dataMap 要转换的map对象
     * @return XML格式的字符串
     */
    public static String map2XML(SortedMap<Object, Object> dataMap)
    {
       /* synchronized (XX.class)
        // 不是多线程访问可以不用加锁
        {*/
            StringBuilder strBuilder = new StringBuilder();
            Set<Object> objSet = dataMap.keySet();
            strBuilder.append("<xml>");
            for (Object key : objSet)
            {
                if (key == null)
                {
                    continue;
                }
                Object value = dataMap.get(key);
                strBuilder.append("<")
                        .append(key.toString())
                        .append(">")
                        .append(value)
                        .append("</")
                        .append(key.toString())
                        .append(">\n");
            }
            strBuilder.append("</xml>");
            return strBuilder.toString();
        //}
    }

    /** 生成随机数
     * @param count 要生成的随机数位数
     * @return 随机数字符串
     */
    public static String createNonceStr(int count){
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

}
