package com.xuecheng.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Test2Json {


    @Test
    public void test2Json () {
        String detail = "[{\"itemId\":\"4028e58161bd22e60161bd23672a0001\",\"itemNum\":1,\"itemPrice\":0.01,\"orderNumber\":\"299118286820741120\"}]";
        List lists = JSONArray.parseArray(detail, Map.class);
        System.out.println(lists);
    }
}
