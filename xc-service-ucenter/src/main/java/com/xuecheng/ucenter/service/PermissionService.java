package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcPermission;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.model.response.QueryResult;

/**
 *
 * @author: olw
 * @date: 2020/10/21 18:49
 * @description:  角色业务层
 */
public class PermissionService {


    public QueryResult<XcPermission> findAll () {

        return new QueryResult<>();
    }

    public boolean add (XcPermission xcPermission) {
        return true;
    }


    public boolean del (String permissionId) {
        return true;
    }

    public boolean update (XcPermission xcPermission) {
        return true;
    }
}
