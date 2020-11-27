package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcMenuRepository extends JpaRepository<XcMenu, String> {

    public XcMenu findXcMenuByCode (String code);
}
