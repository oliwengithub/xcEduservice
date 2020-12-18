package com.xuecheng.framework.domain.learning;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 2018/2/10.
 */
@Data
@ToString
@Entity
@Table(name="xc_learning_course_schedule")
@GenericGenerator(name = "jpa-assigned", strategy = "assigned")
public class XcLearningCourseSchedule implements Serializable {
    private static final long serialVersionUID = -916357210051789799L;

    @Id
    @GeneratedValue(generator = "jpa-assigned")
    @Column(name = "teachplan_id")
    private String teachplanId;
    @Column(name = "course_id")
    private String courseId;
    @Column(name = "user_id")
    private String userId;
    @Column(name="updateTime")
    private Date updateTime;

}
