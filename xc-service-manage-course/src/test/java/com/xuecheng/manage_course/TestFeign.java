package com.xuecheng.manage_course;


import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.service.CourseService;
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
    @Autowired
    CourseService courseService;

    @Test
    public void testPublishPageFeign(){
        CmsPostPageResult cmsPostPageResult = courseService.publish_page("4028e581617f945f01617f9dabc40000");
        System.out.println(cmsPostPageResult);

    }

    @Test
    public void testFeign(){
        CmsPage page = cmsPageClient.findById("5f89625c8d58795f488551a3");
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(page);
        System.out.println(cmsPostPageResult);

    }

}
