package com.xuecheng.learning;

import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.utils.MD5Util;
import com.xuecheng.learning.client.CourseSearchClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {


    @Autowired
    CourseSearchClient courseSearchClient;

    @Test
    public void testClient () {
        String teachplanIds = "402885816347f814016348d7153c0002,4028e58161bd3b380161bd40cf340009,";
        TeachplanMediaPub media = courseSearchClient.getMedia(teachplanIds);
        System.out.println(media);
    }
}
