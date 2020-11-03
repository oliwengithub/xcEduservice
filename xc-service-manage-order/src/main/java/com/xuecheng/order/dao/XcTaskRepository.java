package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface XcTaskRepository extends JpaRepository<XcTask, String> {

    /**
     * 取出指定时间之前的记录
     * @author: olw
     * @Date: 2020/10/24 15:12
     * @param pageable
     * @param updateTime
     * @returns: org.springframework.data.domain.Page<com.xuecheng.framework.domain.task.XcTask>
    */
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);

    /**
     * 每一次操作都更新版本号
     * @author: olw
     * @Date: 2020/10/25 10:48
     * @param id
     * @param version
     * @returns: int
    */
    @Modifying
    @Query("update XcTask set version = :version + 1 where id = :id and version = :version")
    int updateVersion (@Param("id") String id, @Param("version") int version);
}
