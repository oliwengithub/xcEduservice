package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.order.XcOrders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcOrdersRepository extends JpaRepository<XcOrders, String> {
}
