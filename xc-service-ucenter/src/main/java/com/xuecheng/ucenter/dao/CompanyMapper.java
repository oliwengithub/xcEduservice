package com.xuecheng.ucenter.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.ucenter.XcCompany;
import com.xuecheng.framework.domain.ucenter.request.RequestCompanyList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper {

    public Page<XcCompany> findListPage(RequestCompanyList requestCompanyList);
}
