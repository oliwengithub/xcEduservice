package com.xuecheng.manage_media.controller;

import com.netflix.discovery.converters.Auto;
import com.xuecheng.api.media.ManageMediaControllerApi;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import com.xuecheng.manage_media.service.ManageMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author: olw
 * @date: 2020/9/27 20:10
 * @description:  媒资管理模块
 */

@RestController
@RequestMapping("/media/upload")
public class ManageMediaController implements ManageMediaControllerApi {


    @Autowired
    private ManageMediaService manageMediaService;


    @Override
    @PostMapping("/register")
    public ResponseResult register (@RequestParam("fileMd5") String fileMd5, @RequestParam("fileName")String fileName, @RequestParam("fileSize")Long fileSize,
                                    @RequestParam("mimetype")String mimetype, @RequestParam("fileExt")String fileExt) {
        return manageMediaService.register(fileMd5, fileName, fileSize, mimetype, fileExt);
    }

    @Override
    @PostMapping("/checkchunk")
    public CheckChunkResult checkchunk (@RequestParam("fileMd5")String fileMd5, @RequestParam("chunk")Integer chunk, @RequestParam("chunkSize")Integer chunkSize) {
        return manageMediaService.checkunk(fileMd5, chunk, chunkSize);
    }

    @Override
    @PostMapping("/uploadchunk")
    public ResponseResult uploadchunk (@RequestParam("file")MultipartFile file, @RequestParam("chunk")Integer chunk, @RequestParam("fileMd5")String fileMd5) {
        return manageMediaService.uploadchunk(file, chunk, fileMd5);
    }

    @Override
    @PostMapping("/mergechunks")
    public ResponseResult mergechunks (@RequestParam("fileMd5")String fileMd5, @RequestParam("fileName")String fileName, @RequestParam("fileSize")Long fileSize, @RequestParam("mimetype")String mimetype, @RequestParam("fileExt")String fileExt) {
        return manageMediaService.mergechunks(fileMd5, fileName, fileSize, mimetype, fileExt);
    }
}
