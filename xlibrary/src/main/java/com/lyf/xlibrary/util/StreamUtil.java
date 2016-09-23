package com.lyf.xlibrary.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 此类描述的是：输入流转字节流
 * 作者：肖雷
 * 时间：2016/5/11 16:00
 * 公司：上海家乐宝真好电子商务有限公司
 */
public class StreamUtil {
    /**
     * 得到图片字节流 数组大小
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        return outStream.toByteArray();
    }
}
