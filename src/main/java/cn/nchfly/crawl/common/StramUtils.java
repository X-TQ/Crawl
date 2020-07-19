package cn.nchfly.crawl.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @Description:
 * @Author: xtq
 * @Date: 2020/7/18 14:44
 */
public class StramUtils {
    public static String StreamToString(InputStream in){
        //[1]在读取过程中将读取的内容读取到缓存中，然后一次性转换成字符串返回
        //ByteArrayOutputStream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //[2]创建字节数组
        byte[] bytes = new byte[1024];
        //[3]记录读取内容的临时变量
        int temp = -1;
        try {
            //[4]读流的操作，读到没有为止，循环
            while ((temp = in.read(bytes)) != -1) {
                bos.write(bytes,0,temp);
            }
            //[5]将读取的数据转换成字符串，返回出去
            return bos.toString();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                //[6]关闭流和缓存
                bos.close();
                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
