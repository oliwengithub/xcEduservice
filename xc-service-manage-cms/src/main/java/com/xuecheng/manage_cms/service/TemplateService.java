package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsTemplateResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.xml.transform.Templates;
import java.awt.color.CMMException;
import java.awt.color.ICC_ColorSpace;
import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:32
 **/
@Service
public class TemplateService {

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    /**
     * 查询模板列表
     *
     * @return
     */
    public QueryResponseResult findList (int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("templateName", ExampleMatcher.GenericPropertyMatchers.contains());
        CmsTemplate template = new CmsTemplate();

        //设置模板参数
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateName())){
            template.setTemplateName(queryPageRequest.getTemplateName());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            template.setSiteId(queryPageRequest.getSiteId());
        }
        //定义条件对象
        Example<CmsTemplate> example =Example.of(template,exampleMatcher);
        //分页参数
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsTemplate> all = cmsTemplateRepository.findAll(example, pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    public CmsTemplateResult add (CmsTemplate cmsTemplate) {
        if (cmsTemplate == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CmsTemplate cmsTemplate1 = cmsTemplateRepository.findByTemplateNameAndTemplateFileId(cmsTemplate.getTemplateName(),cmsTemplate.getTemplateFileId());
        if (cmsTemplate1 != null){
            ExceptionCast.cast(CmsCode.CMS_TEMPLATE_TEXISTS);
        }
        cmsTemplate.setSiteId(null);
        cmsTemplateRepository.save(cmsTemplate);
        return new CmsTemplateResult(CommonCode.SUCCESS,cmsTemplate);
    }

    public CmsTemplate getById (String id) {
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(id);
        if (optional.isPresent()){
             return optional.get();
        }
        return null;
    }

    public CmsTemplateResult update (String id, CmsTemplate cmsTemplate) {
        CmsTemplate one = getById(id);
        if (one != null){
            //设置更新字段
            one.setTemplateName(cmsTemplate.getTemplateName());
            one.setTemplateParameter(cmsTemplate.getTemplateParameter());
            one.setTemplateFileId(cmsTemplate.getTemplateFileId());
            cmsTemplateRepository.save(one);
            return new CmsTemplateResult(CommonCode.SUCCESS,one);
        }
        return new CmsTemplateResult(CommonCode.FAIL,null);
    }

    public ResponseResult delete (String id) {
        CmsTemplate cmsTemplate = getById(id);
        if (cmsTemplate != null){
            //执行删除操作
            cmsTemplateRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);

    }

    /**
     * 查询所有模板一级导航
     * @author: olw
     * @Date: 2019/7/28 15:48
     * @param
     * @returns: java.util.List<com.xuecheng.framework.domain.cms.CmsTemplate>
    */
    public List<CmsTemplate> findAll () {
        return cmsTemplateRepository.findAll();
    }
}
