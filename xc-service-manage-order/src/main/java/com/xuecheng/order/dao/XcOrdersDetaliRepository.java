package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.order.XcOrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcOrdersDetaliRepository extends JpaRepository<XcOrdersDetail, String> {
}
