package com.xuecheng.learning;


import com.xuecheng.framework.domain.learning.ext.CourseQuestionGroupNode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.learning.service.CourseQuestionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestService {
    @Autowired
    CourseQuestionService courseQuestionService;

    @Test
    public void testService (){
        QueryResponseResult<CourseQuestionGroupNode> courseQuestionList = courseQuestionService.getCourseQuestionList(1, 10);
        List<CourseQuestionGroupNode> list = courseQuestionList.getQueryResult().getList();
        System.out.println(list);
    }

}
