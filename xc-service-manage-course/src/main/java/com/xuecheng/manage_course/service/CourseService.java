package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    TeachMapper teachMapper;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMarketRepository courseMarketRepository;

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

    //课程列表分页查询
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest
            courseListRequest) {
        if(courseListRequest == null){
            courseListRequest = new CourseListRequest();
        }
        if(page<=0){
            page = 0;
        }
        if(size<=0){
            size = 20;
        }
        //设置分页参数
        PageHelper.startPage(page, size);
        //分页查询
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        //查询列表
        List<CourseInfo> list = courseListPage.getResult();
        //总记录数
        long total = courseListPage.getTotal();
        //查询结果集
        QueryResult<CourseInfo> courseIncfoQueryResult = new QueryResult<CourseInfo>();
        courseIncfoQueryResult.setList(list);
        courseIncfoQueryResult.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS, courseIncfoQueryResult);
    }

    //添加课程提交
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
    //课程状态默认为未发布
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS,courseBase.getId());
    }

    //根据课程id获取基本信息
    public CourseBase getCoursebaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    //更新课程信息
    @Transactional
    public ResponseResult updateCoursebase(String id, CourseBase courseBase) {
        CourseBase one = this.getCoursebaseById(id);
        if(one == null){
        ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        //修改课程信息
        one.setName(courseBase.getName());
        one.setMt(courseBase.getMt());
        one.setSt(courseBase.getSt());
        one.setGrade(courseBase.getGrade());
        one.setStudymodel(courseBase.getStudymodel());
        one.setUsers(courseBase.getUsers());
        one.setDescription(courseBase.getDescription());
        CourseBase save = courseBaseRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取课程营销信息
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }


    /**
     * 更新课程营销信息（存在即更新，不存在插入）
     * @author: olw
     * @Date: 2019/9/30 23:14
     * @param id
     * @param courseMarket
     * @returns: com.xuecheng.framework.domain.course.CourseMarket
    */
    @Transactional
    public CourseMarket updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket one = this.getCourseMarketById(id);
        if(one != null){
            one.setCharge(courseMarket.getCharge());
            one.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
            one.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
            one.setPrice(courseMarket.getPrice());
            one.setQq(courseMarket.getQq());
            one.setValid(courseMarket.getValid());
            courseMarketRepository.save(one);
        }else{
            //添加课程营销信息
            one = new CourseMarket();
            BeanUtils.copyProperties(courseMarket, one);
            //设置课程id
            one.setId(id);
            courseMarketRepository.save(one);
        }
        return one;
    }
}
