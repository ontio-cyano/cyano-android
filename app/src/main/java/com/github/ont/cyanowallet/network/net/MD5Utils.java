package com.github.ont.cyanowallet.network.net;


import java.security.MessageDigest;

/**
 * @author zhouq
 * @version 1.0
 * @date 2018/8/27
 */
public class MD5Utils {

    private static final String TAG = "MD5Utils";

    private static final String UTF8 = "utf-8";

    /**
     * MD5加密
     *
     * @param origin 字符
     * @return
     */
    public static byte[] MD5Encode(byte[] bytes) throws Exception {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] rs = md.digest(bytes);
            return rs;
        } catch (Exception e) {
            throw e;
        }
    }


}
