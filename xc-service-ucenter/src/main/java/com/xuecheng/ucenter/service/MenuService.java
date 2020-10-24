package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.model.response.QueryResult;

/**
 *
 * @author: olw
 * @date: 2020/10/21 18:49
 * @description:  角色业务层
 */
public class MenuService {


    public QueryResult<XcMenu> findAll () {

        return new QueryResult<>();
    }

    public boolean add (XcMenu xcMenu) {
        return true;
    }


    public boolean del (String menuId) {
        return true;
    }

    public boolean update (XcMenu xcMenu) {
        return true;
    }
}
