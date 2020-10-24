package com.xuecheng.ucenter;

import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMapper {

    @Resource
    XcMenuMapper xcMenuMapper;

    @Test
    public void testMenuMapper () {
        List<XcMenu> menuByUserId = xcMenuMapper.findMenuByUserId("49");
        System.out.println(menuByUserId);
    }
}
