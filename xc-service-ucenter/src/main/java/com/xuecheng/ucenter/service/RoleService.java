package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcRole;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.model.response.QueryResult;

/**
 *
 * @author: olw
 * @date: 2020/10/21 18:49
 * @description:  角色业务层
 */
public class RoleService {


    public QueryResult<XcRole> findAll () {

        return new QueryResult<>();
    }

    public boolean add (XcRole role) {
        return true;
    }


    public boolean del (String roleId) {
        return true;
    }

    public boolean update (XcRole xcRole) {
        return true;
    }
}
