package com.xuecheng.filesystem.controller;

import com.aliyuncs.exceptions.ClientException;
import com.xuecheng.api.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.filesystem.service.OssService;
import com.xuecheng.filesystem.vo.OssToken;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {

    @Autowired
    FileSystemService fileSystemService;
    @Autowired
    OssService ossService;


    @Override
    @PostMapping("/upload")
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata, String userId) {

        return fileSystemService.upload(multipartFile, filetag, businesskey, metadata);
    }

    @GetMapping ("/getToken")
    public OssToken getToken() throws ClientException {
        return ossService.getToken();
    }



}
