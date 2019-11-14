package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "文件上传管理",description = "文件上传管理",tags = {"文件上传管理"})
public interface FileSystemControllerApi {

    /**
     * 上传文件
     * @param multipartFile 文件
     * @param filetag 文件标签
     * @param businesskey 业务key
     * @param metadata 元信息,json格式
     * @return
     */
    @ApiOperation("文件上传接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="multipartFile", value = "上传的文件", required = true, paramType = "path",dataType = "File"),
            @ApiImplicitParam(name="filetag", value = "业务标签", required = false, paramType = "path"),
            @ApiImplicitParam(name="businesskey", value = "业务key", required = false, paramType = "path"),
            @ApiImplicitParam(name="metadata", value = "文件元信息(JSON格式)", required = false, paramType = "path"),
            @ApiImplicitParam(name="userId", value = "用户id，用于授权", required = false, paramType = "path"),
    })
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata, String userId);

    }
