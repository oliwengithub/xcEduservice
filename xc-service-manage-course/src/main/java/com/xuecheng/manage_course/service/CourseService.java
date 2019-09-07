package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.TeachMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    TeachMapper teachMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    //查询课程计划
    public TeachplanNode findTeacheplanList (String courseId) {
        return teachMapper.selectList(courseId);
    }

    //添加课程计划
    public ResponseResult addTeachplan (Teachplan teachplan) {
        if (teachplan == null
                || StringUtils.isEmpty(teachplan.getPname())
                || StringUtils.isEmpty(teachplan.getCourseid())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        //取出课程的id
        String courseId = teachplan.getCourseid();

        //取出父节点id
        String parentId = teachplan.getParentid();
        if (StringUtils.isEmpty(parentId)){
            //如果父节点id为空 根据课程id获取根节点id
            parentId = getTeachplanParentId(courseId);
        }
        //取出父节点信息
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(parentId);
        if (!teachplanOptional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Teachplan parentTeach = teachplanOptional.get();

        //设置添加课程计划参数
        teachplan.setParentid(parentId);
        teachplan.setStatus("0");//未发布
        //根据父节点的级别设置当前级别
        String parentGrade = parentTeach.getGrade();
        if (parentGrade.equals("1")){
            teachplan.setGrade("2");
        }else if (parentGrade.equals("2")){
            teachplan.setGrade("3");
        }
        teachplanRepository.save(teachplan);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    private String getTeachplanParentId(String courseId){
        Optional<CourseBase> courseBase = courseBaseRepository.findById(courseId);
        if (!courseBase.isPresent()){
            return null;
        }
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
       if (teachplanList == null || teachplanList.size() == 0){
           //创建一个新的根节点
           //获取课程名称
           String pname = courseBase.get().getName();
           //创建新节点对象
           Teachplan teachplan = new Teachplan();
           teachplan.setPname(pname);
           teachplan.setCourseid(courseId);
           //设置根节点
           teachplan.setGrade("1");
           //设置状态为未发布状态
           teachplan.setStatus("0");
           //设置parentId为根节点
           teachplan.setParentid("0");
           teachplanRepository.save(teachplan);
           return teachplan.getId();
       }
       return teachplanList.get(0).getId();
    }
}
