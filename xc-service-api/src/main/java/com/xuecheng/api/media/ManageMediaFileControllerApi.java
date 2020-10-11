package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 *
 * @author: olw
 * @date: 2020/9/28 19:17
 * @description:  媒资文件管理
 */
@Api(value = "媒资文件管理",description = "媒资文件管理",tags = {"媒资文件管理"})
public interface ManageMediaFileControllerApi {

    @ApiOperation(value = "查询所有文件列表")
    public QueryResponseResult<MediaFile> findAll (Integer page, Integer size, QueryMediaFileRequest queryMediaFileRequest);

    @ApiOperation(value = "手动触发视频处理")
    public ResponseResult process(String id);
}
