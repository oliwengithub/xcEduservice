package com.xuecheng.framework.domain.learning;


import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author: olw
 * @date: 2020/12/13 14:31
 * @description: 课程评问答
 */
@Data
@ToString
@Entity
@Table(name="xc_learning_course_question")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class XcLearningCourseQuestion implements Serializable {
    private static final long serialVersionUID = -3377928059526328863L;
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    @Column(name = "parent_id")
    private String parentId;
    @Column(name = "course_id")
    private String courseId;
    @Column(name = "teachplan_id")
    private String teachplanId;
    @Column(name = "user_id")
    private String userId;
    private String content;
    @Column(name = "is_reply")
    private String isReply;
    @Column(name="reply_count")
    private Integer replyCount;
    @Column(name="create_time")
    private Date createTime;

}
