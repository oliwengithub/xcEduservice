package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author: olw
 * @date: 2020/10/11 17:16
 * @description:
 */
public interface TeachplanMediaPubRepository extends JpaRepository<TeachplanMediaPub, String> {

    /**
     * 根据课程id清除所有媒资关联信息（课程发布后清空然后重新插入再由loastash重新采集）
     * @author: olw
     * @Date: 2020/10/11 17:23
     * @param courseId
     * @returns: long
    */
    public long deleteByCourseId(String courseId);
}
