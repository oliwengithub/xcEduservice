package com.xuecheng.manage_media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

@SpringBootTest()
@RunWith(SpringRunner.class)
public class DigestUtilsTest {

    @Test
    public void Md5Hex () throws IOException {
        String path = "F:\\study\\xcEdu\\xcEduUI\\video\\3\\0\\30061ad747496e88c757515e9d0bf73d\\30061ad747496e88c757515e9d0bf73d.wmv";
        File file = new File(path);
        InputStream inputStream = new FileInputStream(file);
        String md5Hex = DigestUtils.md5Hex(inputStream);
        System.out.println(md5Hex+"-----");
    }

    @Test
    public void test() throws IOException {
        Md5Hex();
        Md5Hex();
        Md5Hex();
    }
}
