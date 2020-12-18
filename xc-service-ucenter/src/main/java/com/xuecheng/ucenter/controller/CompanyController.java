package com.xuecheng.ucenter.controller;

import com.xuecheng.api.ucenter.CompanyControllerApi;
import com.xuecheng.framework.domain.ucenter.XcCompany;
import com.xuecheng.framework.domain.ucenter.request.RequestCompanyList;
import com.xuecheng.framework.domain.ucenter.response.CompanyResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.ucenter.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ucenter/company")
public class CompanyController implements CompanyControllerApi {

    @Autowired
    CompanyService companyService;

    @PreAuthorize("hasAuthority('xc_sysmanager_company')")
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findAllCompany (@PathVariable("page") int page, @PathVariable("size") int size, RequestCompanyList requestCompanyList) {
        return companyService.findListPage(page, size, requestCompanyList);
    }

    @PreAuthorize("hasAuthority('xc_sysmanager_company_add')")
    @Override
    @PostMapping("/add")
    public ResponseResult add (@RequestBody XcCompany xcCompany) {
        return companyService.add(xcCompany);
    }

    @Override
    @GetMapping("/get/{companyId}")
    public CompanyResult getCompanyInfo (@PathVariable("companyId") String companyId) {
        return companyService.findById(companyId);
    }

    @PreAuthorize("hasAuthority('xc_sysmanager_company_edit')")
    @Override
    @PutMapping("/edit")
    public ResponseResult edit (@RequestBody XcCompany xcCompany) {
        return companyService.update(xcCompany);
    }

    @PreAuthorize("hasAuthority('xc_sysmanager_company')")
    @Override
    @GetMapping("/getall")
    public List<XcCompany> findAll () {
        return companyService.findAll();
    }
}
