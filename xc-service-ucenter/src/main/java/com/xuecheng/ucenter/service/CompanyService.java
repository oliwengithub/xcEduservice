package com.xuecheng.ucenter.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.Constants;
import com.xuecheng.framework.domain.ucenter.XcCompany;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.UserInfo;
import com.xuecheng.framework.domain.ucenter.request.RequestCompanyList;
import com.xuecheng.framework.domain.ucenter.response.CompanyCode;
import com.xuecheng.framework.domain.ucenter.response.CompanyResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.ucenter.dao.CompanyMapper;
import com.xuecheng.ucenter.dao.XcCompanyRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author: olw
 * @date: 2020/10/21 18:49
 * @description:  机构业务层
 */
@Service
public class CompanyService {

    @Resource
    CompanyMapper companyMapper;

    @Autowired
    XcCompanyRepository xcCompanyRepository;

    @Autowired
    UserService userService;

    /**
     * 查询机构列表
     * @author: olw
     * @Date: 2020/11/22 17:47
     * @param requestCompanyList
     * @returns: com.xuecheng.framework.model.response.QueryResponseResult
    */
    public QueryResponseResult findListPage (int page, int size, RequestCompanyList requestCompanyList) {

        if (requestCompanyList == null) {
            requestCompanyList = new RequestCompanyList();
        }
        page = page <=0 ? 1:page;
        size = size <=0 ? 20:size;
        PageHelper.startPage(page, size);
        Page<XcCompany> listPage = companyMapper.findListPage(requestCompanyList);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(listPage.getResult());
        queryResult.setTotal(listPage.getTotal());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseResult add (XcCompany xcCompany) {
        XcCompany one = xcCompanyRepository.findXcCompaniesByName(xcCompany.getName());
        if (one != null) {
            ExceptionCast.cast(CompanyCode.COMPANY_NAME_EXIST);
        }
        xcCompanyRepository.save(xcCompany);
        // 添加联系人账号
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(xcCompany.getEmail());
        userInfo.setPhone(xcCompany.getMobile());
        userInfo.setEmail(xcCompany.getEmail());
        userInfo.setName(xcCompany.getLinkname());
        userInfo.setPassword(RandomStringUtils.randomAlphanumeric(6));
        userInfo.setRoleIds(Constants.SYSTEM_ROLE_COMPANY);
        userInfo.setStatus(Constants.USER_STATUS_NORMAL);
        userInfo.setUtype(Constants.USER_TYPE_TEACHER);
        // TODO: 2021/4/18 添加机构法人未测试
        userInfo.setCompanyId(xcCompany.getId());
        userService.add(userInfo);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CompanyResult findById (String companyId) {
        Optional<XcCompany> optional = xcCompanyRepository.findById(companyId);
        if (optional.isPresent()) {
            return new CompanyResult(CommonCode.SUCCESS, optional.get());
        }
        return new CompanyResult(CompanyCode.COMPANY_NOT_EXIST, null);
    }


    public ResponseResult update (XcCompany xcCompany) {
        xcCompanyRepository.save(xcCompany);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public List<XcCompany> findAll () {
        return xcCompanyRepository.findAll();
    }
}
