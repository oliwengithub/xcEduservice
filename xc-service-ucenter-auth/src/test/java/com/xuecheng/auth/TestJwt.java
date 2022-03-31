package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.Constants;
import org.apache.commons.lang3.ArrayUtils;
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

import javax.xml.bind.SchemaOutputResolver;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

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
        String jwtString = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiI0MDI4ODI4Mjc1ZjQyYjRkMDE3NWY0YzY2Y2VhMDAwOSIsInVzZXJwaWMiOiJncm91cDEvTTAwLzAwLzAwL3dLZ0NnbC05NW55QVhsM1pBQUIxQmlFNmlyMDY1OS5qcGciLCJ1c2VyX25hbWUiOiJ0YW5nZXJpbmUiLCJyb2xlcyI6IjQsMywyMCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjoi5aSn5qmYIiwidXR5cGUiOiIxMDEwMDIiLCJpZCI6IjhhODM4NGIyNzVmYTVkZmYwMTc1ZmE2MjYzODMwMDAyIiwiZXhwIjoxNjE2NDM2MzkxLCJhdXRob3JpdGllcyI6WyJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX3RlYWNocGxhbl9saXN0IiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9tZWRpYSIsInhjX3N5c21hbmFnZXJfY29tcGFueV9lZGl0IiwieGNfc3lzbWFuYWdlcl9jb21wYW55X2FkZCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfcGxhbiIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2UiLCJ4Y190ZWFjaG1hbmFnZXIiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX3RlYWNocGxhbl9hZGQiLCJ4Y19zeXNtYW5hZ2VyX2NvbXBhbnkiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX3ByZXZpZXciLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX2Jhc2UiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX2RlbCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfbGlzdCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfcGljIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9tYXJrZXQiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX3B1Ymxpc2giLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX3RlYWNocGxhbl9kZWwiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX2FkZCJdLCJqdGkiOiI3ZDIxZjIzMC02ZTYwLTQxNGMtYTNiYS01YzgyNmU4MzFlZjciLCJjbGllbnRfaWQiOiJYY1dlYkFwcCJ9.CbrAD6AKKcUb-BO_gBPilqPgg6UWh18v2iydmOm7CcEIKkUxhzdzAyeyfq3lB7D5ck8SZCob4ThZPQ026Nn72vFytohO74MmWMRtw1-FspTEwroJhxrRrtpepEhB1PJp7oTyJeOzF7A7uVN2yEkAO3V0kO4uSssjzoEQd6DEJW4qDJ2FFR8oXfWZZF096bUMOqPzmFAgylf2KJ943KJxZZaK8QYzVHIfeVBGj8bHopDfTj2CDLdvCa9rpxLEkce3oXnI4NH4I643bgPVmdFWFGQWVd2H-tb-GZkcFq3gKvSzHCwIF0i5gzHwte5lygAG3lGnYJot_xjDqWpREpltiQ";
        //校验jwt令牌
        Jwt jwt = JwtHelper.decodeAndVerify(jwtString, new RsaVerifier(publickey));
        //Jwt jwt = JwtHelper.decode(jwtString);

        //拿到jwt令牌中自定义的内容
        String claims = jwt.getClaims();
        System.out.println(claims);
    }

    @Test
    public void testCheckString () {
        String roles = "20,3,5";
        String[] split = roles.split(",");
        String sysRoleId = ArrayUtils.contains(split, Constants.SYSTEM_ROLE_SUPER) ? Constants.SYSTEM_ROLE_SUPER : (
                ArrayUtils.contains(split, Constants.SYSTEM_ROLE_ADMIN) ? Constants.SYSTEM_ROLE_ADMIN : (
                        ArrayUtils.contains(split, Constants.SYSTEM_ROLE_TEACHER) ? Constants.SYSTEM_ROLE_TEACHER : ""
                )
        );
        System.out.println(sysRoleId);
    }
}
