package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsTemplateControllerApi;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsSiteResult;
import com.xuecheng.framework.domain.cms.response.CmsTemplateResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cms/template")
public class CmsTemplateController implements CmsTemplateControllerApi {
    @Autowired
    TemplateService templateService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList (@PathVariable("page") int page, @PathVariable("size")int size, QueryPageRequest queryPageRequest) {
        return templateService.findList(page,size,queryPageRequest);
    }

    @Override
    @PostMapping("/add")
    public CmsTemplateResult add(@RequestBody CmsTemplate cmsTemplate) {
        return templateService.add(cmsTemplate);
    }

    @Override
    @GetMapping("/get/{id}")
    public CmsTemplate findById(@PathVariable("id") String id) {
        return templateService.getById(id);
    }

    @Override
    @PutMapping("/edit/{id}")//这里使用put方法，http 方法中put表示更新
    public CmsTemplateResult edit(@PathVariable("id")String id, @RequestBody CmsTemplate cmsTemplate) {
        return templateService.update(id,cmsTemplate);
    }

    @Override
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return templateService.delete(id);
    }

    @Override
    @GetMapping("/get/all")
    public List<CmsTemplate> findAll () {
        return templateService.findAll();
    }
}
