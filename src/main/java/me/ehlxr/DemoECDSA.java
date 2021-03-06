package me.ehlxr;

import org.apache.commons.codec.binary.Base64;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by lixiangrong on 2017/8/1.
 */
public class DemoECDSA {

    //摘要
    private static final String strMsg = "hold on";

    public static void main(String[] args) throws Exception {
        jdkECDSA();
    }

    /**
     * ECDSA 微软的椭圆曲线算法 jdk1.7以后引入的算法
     *
     * @throws Exception
     */
    public static void jdkECDSA() throws Exception {
        //1.初始化密钥
        KeyPair keyPair = initKey();

        //2.执行签名（用私钥签名）
        ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
        byte[] sign = privateKeySign(strMsg, ecPrivateKey);
        String signStr = Base64.encodeBase64String(sign);
        System.out.println("sign String :" + signStr);//数字签名格式转换，以便报文传输用

        ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64String(ecPublicKey.getEncoded());
        System.out.println("publicKeyStr String :" + publicKeyStr);//提供给对端，以便于对端使用公钥验证签名


        //3.验证签名（公钥验证签名）
        boolean result = publicKeyVerify(Base64.decodeBase64(signStr), Base64.decodeBase64(publicKeyStr));
        System.out.println("JDK DSA verify:" + result);
    }

    /**
     * 1.初始化密钥，采用ECDSA
     *
     * @return
     * @throws Exception
     */
    public static KeyPair initKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(256); //key长度设置
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        return keyPair;
    }


    /**
     * 2.执行签名（用私钥签名）
     *
     * @return
     * @throws Exception
     */
    public static byte[] privateKeySign(String data, ECPrivateKey ecPrivateKey) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(ecPrivateKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Signature signature = Signature.getInstance("SHA1withECDSA");
        signature.initSign(privateKey);
        signature.update(strMsg.getBytes());
        byte[] sign = signature.sign();

        return sign;
    }

    /**
     * 3.公钥验证签名（摘要+签名串+公钥）
     *
     * @throws Exception
     */
    public static boolean publicKeyVerify(byte[] sign, byte[] dsaPublicKey) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(dsaPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Signature signature = Signature.getInstance("SHA1withECDSA");
        signature.initVerify(publicKey);
        signature.update(strMsg.getBytes());
        boolean result = signature.verify(sign);

        return result;
    }

}