package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {

    //创建jwt令牌
    @Test
    public void testCreateJwt(){
        //密钥库文件
        String keystore = "xc.keystore";
        //密钥库的密码
        String keystore_password = "xuechengkeystore";

        //密钥库文件路径
        ClassPathResource classPathResource = new ClassPathResource(keystore);
        //密钥别名
        String alias  = "xckey";
        //密钥的访问密码
        String key_password = "xuecheng";
        //密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource,keystore_password.toCharArray());
        //密钥对（公钥和私钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, key_password.toCharArray());
        //获取私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //jwt令牌的内容
        Map<String,String> body = new HashMap<>();
        body.put("name","itcast");
        String bodyString = JSON.toJSONString(body);
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(bodyString, new RsaSigner(aPrivate));
        //生成jwt令牌编码
        String encoded = jwt.getEncoded();
        System.out.println(encoded);

    }

    //校验jwt令牌
    @Test
    public void testVerify(){
        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
        //jwt令牌
        String jwtString = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE2MDM0MDExMDgsImF1dGhvcml0aWVzIjpbIlhjTWVudShpZD05MDM0NTkzNzg2NTUzOTU4NDIiLCJYY01lbnUoaWQ9OTAzNDU5Mzc4NjU1Mzk1ODQxIiwiY3JlYXRlVGltZT1udWxsIiwiWGNNZW51KGlkPTkwMzQ1OTM3ODY1NTM5NTg0MyIsIlhjTWVudShpZD05MDM0NTkzNzg2NTUzOTU4NDYiLCJjb2RlPXhjX3N5c21hbmFnZXJfdXNlciIsIlhjTWVudShpZD05MDM0NTkzNzg2NTUzOTU4NDUiLCJYY01lbnUoaWQ9OTAzNDU5Mzc4NjU1Mzk1ODQ4IiwicElkPTkwMzQ1OTM3ODY1NTM5NTg0MSIsIlhjTWVudShpZD05MDM0NTkzNzg2NTUzOTU4NDciLCJjb2RlPXhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfcHVibGlzaCIsIm1lbnVOYW1lPee8lui-keivvueoi-iQpemUgOS_oeaBryIsImNyZWF0ZVRpbWU9TW9uIEF1ZyAwNyAxNjoyMTo1MiBDU1QgMjAxNyIsImlzTWVudT0wIiwiaXNNZW51PTEiLCJzb3J0PW51bGwiLCJjb2RlPXhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYWRkIiwiaWNvbj0iLCJtZW51TmFtZT3miJHnmoTor77nqIsiLCJjcmVhdGVUaW1lPU1vbiBBdWcgMDcgMTY6MjE6MTIgQ1NUIDIwMTciLCJtZW51TmFtZT3mlofmoaPmn6Xor6IiLCJjcmVhdGVUaW1lPU1vbiBBdWcgMDcgMTY6Mzk6MDcgQ1NUIDIwMTciLCJtZW51TmFtZT3nlKjmiLfkv67mlLkiLCJtZW51TmFtZT3nlKjmiLfnrqHnkIYiLCJYY01lbnUoaWQ9OTAzNDU5Mzc4NjU1Mzk1ODQ5IiwidXBkYXRlVGltZT1Nb24gQXVnIDA3IDE4OjE4OjM5IENTVCAyMDE3KSIsIlhjTWVudShpZD04OTQ0Nzc5OTU5MDM4MTE1ODQiLCJ1cGRhdGVUaW1lPVR1ZSBBdWcgMDggMTE6MDI6NTUgQ1NUIDIwMTcpIiwiY29kZT14Y19zeXNtYW5hZ2VyIiwibWVudU5hbWU96K--56iL566h55CGIiwic3RhdHVzPTEiLCJYY01lbnUoaWQ9ODk0NDc3ODUxMDgyODgzMDcyIiwiWGNNZW51KGlkPTIyMjIyMjIyMjIyMjIyMjIyMiIsImNvZGU9eGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9tYXJrZXQiLCJjb2RlPWNvdXJzZV9maW5kX2xpc3QiLCJjcmVhdGVUaW1lPUZyaSBBdWcgMDQgMDk6NDc6MDYgQ1NUIDIwMTciLCJjb2RlPXhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfbGlzdCIsIlhjTWVudShpZD04OTQzOTY1MjM1MzI1MTczNzYiLCJtZW51TmFtZT3mt7vliqDnlKjmiLciLCJtZW51TmFtZT3nlKjmiLfliJfooagiLCJ1cGRhdGVUaW1lPU1vbiBBdWcgMDcgMTY6NTc6NTIgQ1NUIDIwMTcpIiwiWGNNZW51KGlkPTkwMzQ1OTM3ODY1NTM5NTg1MCIsInVwZGF0ZVRpbWU9VHVlIEF1ZyAwOCAwOTo1NjoyOSBDU1QgMjAxNykiLCJsZXZlbD0xIiwibGV2ZWw9MiIsImxldmVsPTMiLCJzb3J0PTkiLCJjb2RlPXhjX3RlYWNobWFuYWdlcl9jb3Vyc2UiLCJsZXZlbD1udWxsIiwiY29kZT14Y190ZWFjaG1hbmFnZXJfY291cnNlX2RlbCIsInNvcnQ9NCIsIm1lbnVOYW1lPeWPkeW4g-ivvueoiyIsIlhjTWVudShpZD04OTQ0NzM0ODY3MTI0Mzg3ODQiLCJjcmVhdGVUaW1lPU1vbiBBdWcgMDcgMTE6MTU6MjMgQ1NUIDIwMTciLCJtZW51TmFtZT3liKDpmaTor77nqIsiLCJzb3J0PTIiLCJzb3J0PTEiLCJ1cGRhdGVUaW1lPVdlZCBTZXAgMTMgMTE6MjA6MjYgQ1NUIDIwMTcpIiwicElkPTIyMjIyMjIyMjIyMjIyMjIyMiIsInBJZD04OTMyODg3MTU4ODE4MDc4NzIiLCJwSWQ9MTExMTExMTExMTExMTExMTExIiwiY29kZT14Y19zeXNtYW5hZ2VyX2xvZyIsImNvZGU9eGNfdGVhY2htYW5hZ2VyIiwibWVudU5hbWU957yW6L6R6K--56iL5Z-656GA5L-h5oGvIiwiWGNNZW51KGlkPTExMTExMTExMTExMTExMTExMSIsImNvZGU9eGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9wbGFuIiwibWVudU5hbWU957yW6L6R6K--56iL6K6h5YiSIiwiY29kZT14Y19zeXNtYW5hZ2VyX3VzZXJfZGVsZXRlIiwiWGNNZW51KGlkPTg5MzI4ODcxNTg4MTgwNzg3MiIsImljb249bnVsbCIsIm1lbnVOYW1lPeafpeivouivvueoi-WIl-ihqCIsImNyZWF0ZVRpbWU9TW9uIEF1ZyAwNyAxNjozODozMyBDU1QgMjAxNyIsImNvZGU9eGNfc3lzbWFuYWdlcl91c2VyX2FkZCIsIm1lbnVOYW1lPeeUqOaIt-WIoOmZpCIsImlzTWVudT1udWxsIiwicENvZGU9bnVsbCIsIm1lbnVOYW1lPeezu-e7n-euoeeQhiIsIm1lbnVOYW1lPea3u-WKoOivvueoiyIsImNvZGU9eGNfc3lzbWFuYWdlcl91c2VyX3ZpZXciLCJjb2RlPXhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYmFzZSIsImNyZWF0ZVRpbWU9RnJpIEF1ZyAwNCAxMDo1Nzo1NCBDU1QgMjAxNyIsInBJZD0wMDAwMDAwMDAwMDAwMDAwMDAiLCJzb3J0PTEwIiwic3RhdHVzPW51bGwiLCJYY01lbnUoaWQ9ODkzMzA0OTYwMjgyNzg3ODQwIiwiWGNNZW51KGlkPTg5NDQ3MzY1MTgzNzk5Mjk2MCIsImNvZGU9eGNfc3lzbWFuYWdlcl91c2VyX2VkaXQiLCJtZW51TmFtZT3mlZnlrabnrqHnkIYiLCJjb2RlPXhjX3N5c21hbmFnZXJfZG9jIiwibWVudU5hbWU9YWRkIiwiY3JlYXRlVGltZT1GcmkgQXVnIDA0IDA5OjUzOjIxIENTVCAyMDE3IiwidXBkYXRlVGltZT1udWxsKSIsInVybD1udWxsIl0sImp0aSI6IjVlODAxOTczLWJkOWMtNGIzOC04YTEwLTBkMzVmODcyNDAxMiIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.jm3LbkO38LE40Y_SPmyuIcnYO07HgK3mG-uevV08cvvIgnc0G0FPaJGmqFPITBEHBPaUI2BeXxex3WBPKS-EM7T4_We7jFQa-pdCY8YLsd9vcTRoUHnUnQSPStAOG1Lhfwi_2OgCxX8_ZFFz8qN8zkFMs9p8KcynPkwUccJSf2LacE2t2YMIi4Osw1URSA3g1YBpbCBqK9Ln8mNDYZcUNeJypa4sYeX2kCBOj8BV-66L8vFb_XQO35mUbsnFHsMELi-r8HUVEAJzsa33s_qQeBd3RbOlxmVg8zsEpz9ZVZYo6Cu5Yx7OPBI6NsdU_v4Jllm-coqMaEL6Mcxwrjq_tA";
        //校验jwt令牌
        Jwt jwt = JwtHelper.decodeAndVerify(jwtString, new RsaVerifier(publickey));
        //拿到jwt令牌中自定义的内容
        String claims = jwt.getClaims();
        System.out.println(claims);
    }
}
