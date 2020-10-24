package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompany;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.model.response.QueryResult;

/**
 *
 * @author: olw
 * @date: 2020/10/21 18:49
 * @description:  角色业务层
 */
public class CompanyService {


    public QueryResult<XcCompany> findAll () {

        return new QueryResult<>();
    }

    public boolean add (XcCompany xcCompany) {
        return true;
    }


    public boolean del (String companyId) {
        return true;
    }

    public boolean update (XcCompany xcCompany) {
        return true;
    }
}
