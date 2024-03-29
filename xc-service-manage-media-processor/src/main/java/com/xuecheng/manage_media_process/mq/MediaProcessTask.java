package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author: olw
 * @date: 2020/9/28 19:57
 * @description:  视频处理监听队列（消费方）
 */
@Component
public class MediaProcessTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaProcessTask.class);
    /**
    * ffmpeg绝对路径
     */
    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;

    /**
     * 上传文件根目录
     */
    @Value("${xc-service-manage-media.video-location}")
    String serverPath;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @RabbitListener(queues = {"${xc-service-manage-media.mq.queue-media-video-processor}"}, containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask (String msg) {
        Map msgMap = JSON.parseObject(msg, Map.class);
        // 解析mq 发送过来的信息
        LOGGER.info("receive media process task msg: {}", msgMap);

        // 媒资文件id
        String fileMd5 = (String) msgMap.get("mediaId");
        // 获取媒资文件信息
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        if(!optional.isPresent()){
            return ;
        }
        MediaFile mediaFile = optional.get();
        // 媒资文件类型
        String fileType = mediaFile.getFileType();
        // 目前只处理ffmpeg支持视频文件
        if(fileType == null || !videoType(fileType)){
            // 处理状态为无需处理
            mediaFile.setProcessStatus("303004");
            mediaFileRepository.save(mediaFile);
            return ;
        }else{
            // 处理状态为未处理
            mediaFile.setProcessStatus("303001");
            mediaFileRepository.save(mediaFile);
        }
        // 进行视频转码 生成mp4
        // 原始上传文件路径
        String video_path = serverPath + mediaFile.getFilePath()+mediaFile.getFileName();
        // 生成mp4的文件名
        String mp4_name = mediaFile.getFileId()+".mp4";
        // 生成文件的服务路径（去除配置的更目录）
        String mp4folder_path = serverPath + mediaFile.getFilePath();
        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_path);
        String result = videoUtil.generateMp4();
        if(!"success".equals(result)){
            // 操作失败写入处理日志

            //处理状态为处理失败
            mediaFile.setProcessStatus("303003");
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return ;
        }

        // 生成m3u8
        //此地址为mp4的地址
        video_path = serverPath + mediaFile.getFilePath()+mp4_name;
        String m3u8_name = mediaFile.getFileId()+".m3u8";
        String m3u8folder_path = serverPath + mediaFile.getFilePath()+"hls/";
        HlsVideoUtil hlsVideoUtil = new
                HlsVideoUtil(ffmpeg_path,video_path,m3u8_name,m3u8folder_path);
        result = hlsVideoUtil.generateM3u8();
        if(result == null || !result.equals("success")){
            // 操作失败写入处理日志
            // 处理状态为处理失败
            mediaFile.setProcessStatus("303003");
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return ;
        }
        // 获取m3u8列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        // 更新处理状态为成功
        mediaFile.setProcessStatus("303002");
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        // m3u8文件url
        mediaFile.setFileUrl(mediaFile.getFilePath()+"hls/"+m3u8_name);
        mediaFileRepository.save(mediaFile);
    }



    /**
     * 校验资源格式是否为视频
     * @author: olw
     * @Date: 2021/5/6 16:22
     * @param prefix
     * @returns: boolean
    */
    private  boolean videoType(String prefix) {
        if("mp4".equalsIgnoreCase(prefix) || "avi".equalsIgnoreCase(prefix) || "MPEG-1".equalsIgnoreCase(prefix)
                || "RM".equalsIgnoreCase(prefix) || "ASF".equalsIgnoreCase(prefix) || "WMV".equalsIgnoreCase(prefix)
                || "qlv".equalsIgnoreCase(prefix) || "MPEG-2".equalsIgnoreCase(prefix) || "MPEG4".equalsIgnoreCase(prefix)
                || "mov".equalsIgnoreCase(prefix) || "3gp".equalsIgnoreCase(prefix)) {
            return true;
        }
        return false;

    }

}
