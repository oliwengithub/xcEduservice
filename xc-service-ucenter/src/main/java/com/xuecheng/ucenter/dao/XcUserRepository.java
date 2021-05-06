package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;
import sun.awt.EmbeddedFrame;

public interface XcUserRepository extends JpaRepository<XcUser, String> {

    public XcUser findXcUserByUsername(String username);
    public XcUser findXcUserByUsernameOrId(String username, String id);
    public XcUser findXcUserByUsernameOrEmail(String username, String email);
}
