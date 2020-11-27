package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcPermission;
import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XcPermissionRepository extends JpaRepository<XcPermission, String> {

    public XcPermission findXcPermissionByRoleIdAndMenuId (String roleId, String menuId);

    public List<XcPermission> findXcPermissionByRoleId (String roleId);
}
