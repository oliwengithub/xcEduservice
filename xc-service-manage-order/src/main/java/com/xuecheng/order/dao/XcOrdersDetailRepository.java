package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.order.XcOrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XcOrdersDetailRepository extends JpaRepository<XcOrdersDetail, String> {

    public XcOrdersDetail findXcOrdersDetailByOrderNumber (String orderNumber);

    List<XcOrdersDetail> findXcOrdersDetailByCourseIdAndUserId (String courseId, String userId);
}
