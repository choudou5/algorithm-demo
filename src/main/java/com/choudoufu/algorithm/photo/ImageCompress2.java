package com.choudoufu.algorithm.photo;

import com.choudoufu.algorithm.BaseClient;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xuhaowende on 2017/10/15.
 */
public class ImageCompress2 extends BaseClient{

    private final static String sourceImg = baseDataPath+"photo/input.jpg";
    private final static String destImg = baseDataPath+"photo/out2.jpg";

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws IOException {
        long begin = System.currentTimeMillis();
        System.out.println("图片压缩开始");
        resizeImg(sourceImg, destImg, 1920, 1080);
        long end = System.currentTimeMillis();
        System.out.println("图片压缩结束：" + (end-begin));
    }

    /**
     * 强制压缩/放大图片到固定的大小
     * @param w int 新宽度
     * @param h int 新高度
     */
    public static void resizeImg(String srcFile, String descFile, int w, int h) throws IOException {
        // SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
        BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB );
        File file = new File(srcFile);// 读入文件
        Image img = ImageIO.read(file);      // 构造Image对象
        image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图
        File destFile = new File(descFile);
        FileOutputStream out = new FileOutputStream(destFile); // 输出到文件流
        // 可以正常实现bmp、png、gif转jpg
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        encoder.encode(image); // JPEG编码
        out.close();
    }
}
