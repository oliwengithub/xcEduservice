package com.xuecheng.manage_course;


import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFeign {
    @Autowired
    CmsPageClient cmsPageClient;

    @Test
    public void testFeign(){
        CmsPage page = cmsPageClient.findById("5a96114fb00ffc4b44f63e06");
        System.out.println(page);

    }

}
