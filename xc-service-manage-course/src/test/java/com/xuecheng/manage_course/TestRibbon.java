package com.xuecheng.manage_course;


import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRibbon {
    @Autowired
    RestTemplate restTemplate;


    @Test
    public void testRibbon(){
       String serviceId = "XC-SERVICE-MANAGE-CMS";
       for (int i = 0; i < 10; i++) {
           ResponseEntity<CmsPage> forEntity = restTemplate.getForEntity("http://"+serviceId+"/cms/page/get/5d57b51f70e79e4a8c6cda2d", CmsPage.class);
           CmsPage body = forEntity.getBody();
           System.out.println(body);
       }


    }

}
