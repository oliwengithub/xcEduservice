package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class CourseView implements Serializable {

    private CourseBase courseBase; //基础信息
    private CoursePic coursePic; //课程图片
    private CourseMarket courseMarket; //营销信息
    private TeachplanNode teachplanNode; //教学计划
}
