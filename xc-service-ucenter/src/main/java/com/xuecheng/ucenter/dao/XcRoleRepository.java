package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcRole;
import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcRoleRepository extends JpaRepository<XcRole, String> {
}
