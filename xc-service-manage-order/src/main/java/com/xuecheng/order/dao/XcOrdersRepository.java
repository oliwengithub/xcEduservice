package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.order.XcOrders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcOrdersRepository extends JpaRepository<XcOrders, String> {


    public Page<XcOrders> findAllByUserId (Pageable pageable, String userId);

    public XcOrders findXcOrdersByUserIdAndOrderNumber (String userId, String orderNumber);


}
