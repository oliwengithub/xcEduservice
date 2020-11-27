package com.xuecheng.order;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.order.client.SearchClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

    @Autowired
    SearchClient searchClient;

    @Test
    public void testSearchClient () {
        String courseId = "4028e58161bd3b380161bd3bcd2f0000";
        Map<String, CoursePub> all = searchClient.getAll(courseId);
        System.out.println(all);
    }

    @Test
    public void testPlan () {
        String courseId = "4028e58161bd3b380161bd3bcd2f0000,4028e58161bcf7f40161bcf8b77c0000";
        Map<String, CoursePub> all = searchClient.getAll(courseId);
        CoursePub coursePub = all.get(courseId);
        String teachplan = coursePub.getTeachplan();
        TeachplanNode teachplanNode = JSON.parseObject(teachplan, TeachplanNode.class);
        List<TeachplanNode> children = teachplanNode.getChildren();
        int count = 0;
        for (TeachplanNode child : children) {
            int size = child.getChildren().size();
            count = count + size;

        }
        System.out.println(count);
    }
}
