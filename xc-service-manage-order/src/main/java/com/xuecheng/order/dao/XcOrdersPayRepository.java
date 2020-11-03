package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.order.XcOrdersPay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcOrdersPayRepository extends JpaRepository<XcOrdersPay, String> {
}
