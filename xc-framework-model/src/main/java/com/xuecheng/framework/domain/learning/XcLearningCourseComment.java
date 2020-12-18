package com.xuecheng.framework.domain.learning;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author: olw
 * @date: 2020/12/13 14:31
 * @description: 课程评论表
 */
@Data
@ToString
@Entity
@Table(name="xc_learning_course_comment")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class XcLearningCourseComment implements Serializable {
    private static final long serialVersionUID = 1127719133426190459L;
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    @Column(name = "course_id")
    private String courseId;
    @Column(name = "user_id")
    private String userId;
    private BigDecimal score;
    private String comment;
    private String status;
    @Column(name="create_time")
    private Date createTime;

}
