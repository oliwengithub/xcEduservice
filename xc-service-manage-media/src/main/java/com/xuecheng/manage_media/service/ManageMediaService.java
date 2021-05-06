package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ser.std.FileSerializer;
import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.internal.RealNumbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 *
 * @author: olw
 * @date: 2020/9/27 20:13
 * @description:  媒资管理业务
 */
@Service
public class ManageMediaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageMediaService.class);


    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 视频处理路由
     */
    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    public  String routingkey_media_video;

    /**
     * 上传文件根目录
    */
    @Value("${xc-service-manage-media.upload-location}")
    String uploadPath;


    /**
     * 文件上传注册
     * @author: olw
     * @Date: 2020/9/27 20:23
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult register (String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {

        //检查文件是否上传
        //1、得到文件的路径
        String filePath = getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        //2、查询数据库文件是否存在
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        // 文件存在直接返回
        if(file.exists() && optional.isPresent()){
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        boolean fileFold = createFileFold(fileMd5);
        if(!fileFold){
        // 上传文件目录创建失败
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_CREATEFOLDER_FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 检查块文件
     * @author: olw
     * @Date: 2020/9/27 20:32
     * @param fileMd5
     * @param chunk
     * @param chunkSize
     * @returns: com.xuecheng.framework.domain.media.response.CheckChunkResult
    */
    public CheckChunkResult checkunk (String fileMd5, Integer chunk, Integer chunkSize) {
        // 得到块文件所在路径
        String chunkfileFolderPath = getChunkFileFolderPath(fileMd5);
        // 块文件的文件名称以1,2,3..序号命名，没有扩展名
        File chunkFile = new File(chunkfileFolderPath+chunk);
        if(chunkFile.exists()){
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK,true);
        }else{
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK,false);
        }
    }

    /**
     * 执行上传
     * @author: olw
     * @Date: 2020/9/27 20:40
     * @param file 上传目录
     * @param chunk
     * @param fileMd5
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult uploadchunk (MultipartFile file, Integer chunk, String fileMd5) {
        // 上传内容为空
        if(file == null){
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_ISNULL);
        }
        // 创建块文件目录
        boolean fileFold = createChunkFileFolder(fileMd5);
        // 块文件
        File chunkfile = new File(getChunkFileFolderPath(fileMd5) + chunk);
        // 上传的块文件
        InputStream inputStream= null;
        FileOutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(chunkfile);
            IOUtils.copy(inputStream,outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("upload chunk file fail:{}",e.getMessage());
            ExceptionCast.cast(MediaCode.CHUNK_FILE_UPLOAD_FAIL);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 合并块文件
     * @author: olw
     * @Date: 2020/9/27 20:53
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @returns: com.xuecheng.framework.model.response.ResponseResult
     */
    public ResponseResult mergechunks (String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt, String tag) {
        // 获取块文件的路径
        String chunkfileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkfileFolder = new File(chunkfileFolderPath);
        if(!chunkfileFolder.exists()){
            chunkfileFolder.mkdirs();
        }
        // 合并文件路径
        File mergeFile = new File(getFilePath(fileMd5,fileExt));
        // 创建合并文件
        // 合并文件存在先删除再创建
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        boolean newFile = false;
        try {
            newFile = mergeFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("mergechunks..create mergeFile fail:{}",e.getMessage());
        }
        if(!newFile){
            ExceptionCast.cast(MediaCode.MERGE_FILE_CREATEFAIL);
        }
        // 获取块文件，此列表是已经排好序的列表
        List<File> chunkFiles = getChunkFiles(chunkfileFolder);
        // 合并文件
        mergeFile = mergeFile(mergeFile, chunkFiles);
        if(mergeFile == null){
            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        // 校验文件
        boolean checkResult = this.checkFileMd5(mergeFile, fileMd5);
        if(!checkResult){
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        // 将文件信息保存到数据库
        MediaFile mediaFile = new MediaFile(fileMd5, fileMd5+"."+fileExt, fileName, getFileFolderRelativePath(fileMd5, fileExt), getFileFolderPath(fileMd5), fileExt, mimetype, fileSize, "301002", new Date(), null, null, tag);
        MediaFile save = mediaFileRepository.insert(mediaFile);

        // 发送消息到mq 进行视频处理
        sendProcessVideoMsg(fileMd5);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 向MQ发送视频处理消息
     * @author: olw
     * @Date: 2020/9/29 17:13
     * @param mediaId
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult sendProcessVideoMsg(String mediaId){
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if(!optional.isPresent()){
            return new ResponseResult(CommonCode.FAIL);
        }
        // 发送视频处理消息
        Map<String,String> msgMap = new HashMap<>();
        msgMap.put("mediaId",mediaId);
        // 发送的消息
        String msg = JSON.toJSONString(msgMap);
        try {
            this.rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingkey_media_video,
                    msg);
            LOGGER.info("send media process task msg:{}",msg);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.info("send media process task error,msg is:{},error:{}",msg,e.getMessage());
            return new ResponseResult(CommonCode.FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     * @param fileMd5 文件md5值
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    private String getFilePath(String fileMd5,String fileExt){
        String filePath = uploadPath+fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) +
                "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
        return filePath;
    }

    /**
     * 得到文件目录相对路径，路径中去掉根目录
     * @author: olw
     * @Date: 2020/9/27 20:27
     * @param fileMd5
     * @param fileExt
     * @returns: java.lang.String
    */
    private String getFileFolderRelativePath(String fileMd5,String fileExt){
        String filePath = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        return filePath;
    }

    /**
     * 得到文件所在目录
     * @author: olw
     * @Date: 2020/9/27 20:27
     * @param fileMd5
     * @returns: java.lang.String
    */
    private String getFileFolderPath(String fileMd5){
        String fileFolderPath = uploadPath+ fileMd5.substring(0, 1) + "/" + fileMd5.substring(1,
                2) + "/" + fileMd5 + "/" ;
        return fileFolderPath;
    }

    /**
     * 创建文件目录
     * @author: olw
     * @Date: 2020/9/27 20:26
     * @param fileMd5
     * @returns: boolean
    */
    private boolean createFileFold(String fileMd5){
        // 创建上传文件目录
        String fileFolderPath = getFileFolderPath(fileMd5);
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()) {
            // 创建文件夹
            boolean mkdirs = fileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }

    /**
     * 得到块文件所在目录
     * @author: olw
     * @Date: 2020/9/27 20:32
     * @param fileMd5
     * @returns: java.lang.String
    */
    private String getChunkFileFolderPath(String fileMd5){
        String fileChunkFolderPath = getFileFolderPath(fileMd5) +"/" + "chunks" + "/";
        return fileChunkFolderPath;
    }


    /**
     * 创建块文件目录
     * @author: olw
     * @Date: 2020/9/27 20:48
     * @param fileMd5
     * @returns: boolean
    */
    private boolean createChunkFileFolder(String fileMd5){
        // 创建上传文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        if (!chunkFileFolder.exists()) {
            // 创建文件夹
            boolean mkdirs = chunkFileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }

    /**
     * 获取块文件，以文件名称做为排序条件
     * @author: olw
     * @Date: 2020/9/27 21:09
     * @param chunkfileFolder
     * @returns: java.util.List<java.io.File>
    */
    private List<File> getChunkFiles (File chunkfileFolder) {

        File[] files = chunkfileFolder.listFiles();
        List<File> files1 = Arrays.asList(files);
        files1.sort((o1, o2) -> {
            if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                return 1;
            }else {
                return -1;
            }
        });

        return files1;
    }

    /**
     * 合并文件
     * @author: olw
     * @Date: 2020/9/27 21:11
     * @param mergeFile
     * @param chunkFiles
     * @returns: java.io.File
    */
    private File mergeFile (File mergeFile, List<File> chunkFiles) {

        try {
            // 创建写文件对象
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile,"rw");
            // 遍历分块文件开始合并
            // 读取文件缓冲区
            byte[] b = new byte[1024];
            for(File chunkFile:chunkFiles){
                RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"r");
                int len = -1;
                // 读取分块文件
                while((len = raf_read.read(b)) != -1){
                    // 向合并文件中写数据
                    raf_write.write(b,0,len);
                }
                raf_read.close();
            }
            raf_write.close();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("merge file error:{}",e.getMessage());
            return null;
        }
        return mergeFile;
    }


    /**
     * 校验文件的md5值
     * @author: olw
     * @Date: 2020/9/27 21:16
     * @param mergeFile
     * @param md5
     * @returns: boolean
    */
    private boolean checkFileMd5(File mergeFile,String md5){
        if(mergeFile == null || StringUtils.isEmpty(md5)){
            return false;
        }
        // 进行md5校验
        FileInputStream mergeFileIsstream = null;
        try {
            mergeFileIsstream = new FileInputStream(mergeFile);
            // 得到文件的md5
            // 进行多次加密 有一次成功就结束
            int i = 0;
            while (i < 3) {
                String mergeFileMd5 = DigestUtils.md5Hex(mergeFileIsstream);
                // 比较md5
                if(md5.equalsIgnoreCase(mergeFileMd5)){
                    return true;
                }
                i = i+1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("checkFileMd5 error,file is:{},md5 is: {}",mergeFile.getAbsoluteFile(),md5);
        }finally{
            try {
                mergeFileIsstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
