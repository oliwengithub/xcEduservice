package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

/**
 *
 * @author: olw
 * @date: 2020/9/28 19:25
 * @description:  媒资文件管理业务层
 */
@Service
public class MediaFileService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    public QueryResponseResult<MediaFile> findAll (Integer page, Integer size, QueryMediaFileRequest queryMediaFileRequest) {

        if (queryMediaFileRequest == null) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }

        // 构建查询条件
        MediaFile mediaFile = new MediaFile();
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }

        // 构建分页对象
        page = page == 0 ? 1 : page-1;
        size = size == 0 ? 10 : size;
        Pageable pageable = new PageRequest(page, size);

        // 构建查询条件（规则）
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<MediaFile> example = Example.of(mediaFile, matcher);
        // 构造查询对象
        Page<MediaFile> mediaFileRepositoryAll = mediaFileRepository.findAll(example, pageable);

        QueryResult<MediaFile> mediaFileList = new QueryResult<>();
        mediaFileList.setList(mediaFileRepositoryAll.getContent());
        mediaFileList.setTotal(mediaFileRepositoryAll.getTotalElements());

        return new QueryResponseResult(CommonCode.SUCCESS, mediaFileList);
    }
}
