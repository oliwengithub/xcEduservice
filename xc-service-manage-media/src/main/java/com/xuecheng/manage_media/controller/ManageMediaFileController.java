package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.ManageMediaFileControllerApi;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.ManageMediaService;
import com.xuecheng.manage_media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author: olw
 * @date: 2020/9/28 19:26
 * @description:  媒资文件管理
 */
@RestController
@RequestMapping("/media/file")
public class ManageMediaFileController implements ManageMediaFileControllerApi {

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private ManageMediaService manageMediaService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult<MediaFile> findAll (@PathVariable("page") Integer page, @PathVariable("size") Integer size, QueryMediaFileRequest queryMediaFileRequest) {
        return mediaFileService.findAll(page, size, queryMediaFileRequest);
    }

    @Override
    @PostMapping("/process/{id}")
    public ResponseResult process (@PathVariable("id") String id) {
        return manageMediaService.sendProcessVideoMsg(id);
    }
}
