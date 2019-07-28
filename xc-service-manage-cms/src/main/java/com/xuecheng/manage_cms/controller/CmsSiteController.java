package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsSiteControllerApi;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsSiteResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cms/site")
public class CmsSiteController implements CmsSiteControllerApi {

    @Autowired
    SiteService siteService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList (@PathVariable("page") int page, @PathVariable("size")int size, QueryPageRequest queryPageRequest) {
        return siteService.findList(page,size,queryPageRequest);
    }

    @Override
    @PostMapping("/add")
    public CmsSiteResult add(@RequestBody CmsSite cmsSite) {
        return siteService.add(cmsSite);
    }

    @Override
    @GetMapping("/get/{id}")
    public CmsSite findById(@PathVariable("id") String id) {
        return siteService.getById(id);
    }


    @Override
    @PutMapping("/edit/{id}")//这里使用put方法，http 方法中put表示更新
    public CmsSiteResult edit(@PathVariable("id")String id, @RequestBody CmsSite cmsSite) {
        return siteService.update(id,cmsSite);
    }

    @Override
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return siteService.delete(id);
    }

    @Override
    @GetMapping("/get/all")
    public List<CmsSite> findAll () {
        return siteService.findAll();
    }

}
