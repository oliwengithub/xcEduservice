package com.xuecheng.manage_media.mongodb;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.manage_media.ManageMediaApplication;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SpringBootTest()
@RunWith(SpringRunner.class)
public class MongodbTest {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Test
    public void testInsert () {
        MediaFileProcess_m3u8 mediaFileProcessM3u8 = new MediaFileProcess_m3u8();
        List<String> m3u8s = new ArrayList(){{
            add("1");
            add("2");
            add("3");
            add("4");
        }};
        MediaFile mediaFile = new MediaFile("2222", "1", "1", "1", "1", "1", "1", 1L, "1", new Date(), "1", mediaFileProcessM3u8, "1");
        mediaFileProcessM3u8.setTslist(m3u8s);

        MediaFile insert = mediaFileRepository.insert(mediaFile);
        System.out.println(insert);
    }
}
