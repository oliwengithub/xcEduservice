package com.xuecheng.framework.domain.learning;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author: olw
 * @date: 2020/12/18 13:28
 * @description:  课程统计
 */
@Data
@ToString
@Entity
@Table(name="xc_learning_course_state")
@GenericGenerator(name = "jpa-assigned", strategy = "assigned")
public class XcLearningCourseState implements Serializable {

    private static final long serialVersionUID = 6280031061312473264L;
    @Id
    @GeneratedValue(generator = "jpa-assigned")
    @Column(name = "course_id", length = 32)
    private String courseId;
    private BigDecimal score;
    @Column(name = "favorite_num")
    private Integer favoriteNum;
    @Column(name = "choose_num")
    private Integer chooseNum;
    @Column(name = "course_duration")
    private Double courseDuration;

}
