package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcPermission;
import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcPermissionRepository extends JpaRepository<XcPermission, String> {
}
