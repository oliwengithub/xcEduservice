package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsSiteResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:32
 **/
@Service
public class SiteService {

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    /**
     * 查询站点列表
     *
     * @return
     */
    public QueryResponseResult findList (int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        //自定义条件查询
        //定义条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("siteName", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值对象
        CmsSite cmsSite = new CmsSite();
        //设置站点作为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteName())) {
            cmsSite.setSiteName(queryPageRequest.getSiteName());
        }
        /*//设置站点访问路径为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteWebPath())){
            cmsSite.setSiteWebPath(queryPageRequest.getSiteWebPath());
        }*/
        //定义条件对象Example
        Example<CmsSite> example = Example.of(cmsSite, exampleMatcher);
        //分页参数
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsSite> all = cmsSiteRepository.findAll(example, pageable);//实现自定义条件查询并且分页查询
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;

    }

    /**
     *  添加页面站点
     * @author: olw
     * @Date: 2019/7/28 10:27
     * @param cmsSite
     * @returns: com.xuecheng.framework.domain.cms.response.CmsSiteResult
    */
    public CmsSiteResult add (CmsSite cmsSite) {
        if (cmsSite == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CmsSite cmsSite1 = cmsSiteRepository.findCmsSiteBySiteNameAndSiteWebPath(cmsSite.getSiteName(),cmsSite.getSiteWebPath());
        if (cmsSite1 != null) {
            ExceptionCast.cast(CmsCode.CMS_SITE_TEXISTS);
        }
        cmsSite.setSiteId(null);
        cmsSiteRepository.save(cmsSite);
        return new CmsSiteResult(CommonCode.SUCCESS, cmsSite);
    }

    public CmsSiteResult update (String id, CmsSite cmsSite) {
        CmsSite one = getById(id);
        if (one != null){
            //设置更新字段
            one.setSiteName(cmsSite.getSiteName());
            one.setSiteDomain(cmsSite.getSiteDomain());
            one.setSiteCreateTime(cmsSite.getSiteCreateTime());
            one.setSiteWebPath(cmsSite.getSiteWebPath());
            one.setSitePort(cmsSite.getSitePort());
            cmsSiteRepository.save(one);
            return new CmsSiteResult(CommonCode.SUCCESS,one);
        }
        return new CmsSiteResult(CommonCode.FAIL,null);
    }

    public ResponseResult delete (String id) {
        CmsSite one = getById(id);
        if (one != null){
            cmsSiteRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 根据id查询站点
     * @author: olw
     * @Date: 2019/7/28 10:28
     * @param id
     * @returns: com.xuecheng.framework.domain.cms.CmsSite
     */
    public CmsSite getById (String id) {
        CmsSite cmsSite = new CmsSite();
        Optional<CmsSite> optional = cmsSiteRepository.findById(id);
        if (optional.isPresent()) {
            cmsSite = optional.get();
            return cmsSite;
        }
        return cmsSite;
    }

    public List<CmsSite> findAll () {
        return cmsSiteRepository.findAll();
    }
}
