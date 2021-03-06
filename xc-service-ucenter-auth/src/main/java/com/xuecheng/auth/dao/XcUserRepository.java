package com.xuecheng.auth.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcUserRepository extends JpaRepository<XcUser, String> {

    public XcUser findXcUserByUsername (String username);
}
