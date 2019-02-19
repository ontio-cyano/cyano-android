package com.github.ont.cyanowallet.network.net;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author zhouq
 * @version 1.0
 * @date 2018/8/27
 */
public class HMACSha256 {

    private static final String TAG = "HMACSha256";

    /**
     * sha256_HMAC加密
     *
     * @param message 消息
     * @param secret  秘钥
     * @return 加密后字符串
     */
    public static byte[] sha256_HMAC(String message, String secret) throws Exception {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            return bytes;
        } catch (Exception e) {
            throw e;
        }
    }

}
