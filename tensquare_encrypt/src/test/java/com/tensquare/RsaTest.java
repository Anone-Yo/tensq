package com.tensquare;

import com.tensquare.encrypt.EncryptApplication;
import com.tensquare.encrypt.rsa.RSA;
import com.tensquare.encrypt.rsa.RsaKeys;
import com.tensquare.encrypt.service.RsaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EncryptApplication.class)
public class RsaTest {

    @Autowired
    private RsaService rsaService;

    @Test
    public void testEncrypt() throws Exception {
        String text = "{\n" +
                "\t\"title\": \"对穿2肠\",\n" +
                "\t\"content\": \"十口心思，思国思君思社稷\"\n" +
                "}";
        String text2 = rsaService.rsaEncryptDataPEM(text, RsaKeys.getServerPubKey());
        System.out.println(text2);
    }

    @Test
    public void testDecrypt() throws Exception {
        String text = "R1zfV3NscoWEltZbXWUOIzbEnLVRdf2XZphTwSJakwUoNVqPPFrN1ChedG5hqm9MquAgLLaHkobYiJKu2oi5d551S37fABEA/pm1b21yaItN8GNlvV1cdMogpzGuIv7YLLsw3xKsgvYcjmOWt5iLUMhcbYV8WBOeFLkv2mirSt4HMy/xIaM1zvnEPDAoV/8i4iFTEU0/kXFSNhvP5nKEDp3ZzTjZPpZs+PRBsTMIR6coMPwvXhnbkD12AV7g1ZIiHlmYRLXeF4Wf4dQXUrzZarHetV+aIWEilzaDuRBJsjrtiNRxuKo29zZkvV0s5OzRb1UPQ3vfMt2qaWB3FU4r0w==";
        String s = rsaService.rsaDecryptDataPEM(text, RsaKeys.getServerPrvKeyPkcs8());
        System.out.println(s);
    }
}
