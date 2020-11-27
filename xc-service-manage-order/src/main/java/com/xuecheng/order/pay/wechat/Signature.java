package com.xuecheng.order.pay.wechat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.xuecheng.framework.utils.MD5Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


/**
 *
 * @author: olw
 * @date: 2020/11/5 20:36
 * @description:  签名
 */
public class Signature {

	/**
     * 签名算法
     * @param o 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
    public static String getSign(Object o) throws IllegalAccessException {
        ArrayList<String> list = new ArrayList<String>();
        Class cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.get(o) != null && f.get(o) != "") {
            	String name = f.getName();
            	XStreamAlias anno = f.getAnnotation(XStreamAlias.class);
            	if(anno != null){
                    name = anno.value();
                }

                list.add(name + "=" + f.get(o) + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + Config.KEY;

        result = MD5Util.MD5Encode(result).toUpperCase();
        return result;
    }

    /**
     * 签名算法 与上面的一样
     * @author: olw
     * @Date: 2020/11/5 20:44
     * @param map
     * @returns: java.lang.String
    */
    public static String getSignByMap(Map<String,Object> map){
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            if(entry.getValue()!=""){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result = MD5Util.getStringMD5(result).toUpperCase();
        return result;
    }
}
