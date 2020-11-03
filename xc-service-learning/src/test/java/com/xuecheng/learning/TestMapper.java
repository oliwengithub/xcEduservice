package com.xuecheng.learning;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.requset.ChooseCourseResultList;
import com.xuecheng.learning.dao.LearningCourseMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMapper {

    @Resource
    private LearningCourseMapper learningCourseMapper;

    @Test
    public void testChooseCourseMapper () {
        ChooseCourseResultList chooseCourseResultList = new ChooseCourseResultList();
        chooseCourseResultList.setUserId("49");
        PageHelper.offsetPage(1, 20);
        Page<XcLearningCourse> courseListPage = learningCourseMapper.findChooseCourseListPage(chooseCourseResultList);
        List<XcLearningCourse> result = courseListPage.getResult();
        System.out.println(result);
    }
}
