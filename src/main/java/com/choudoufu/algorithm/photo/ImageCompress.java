package com.choudoufu.algorithm.photo;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import com.choudoufu.algorithm.BaseClient;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 图片压缩
 * @author pengyh
 *
 */
public class ImageCompress extends BaseClient{

    private final static String sourceImg = baseDataPath+"photo/input.jpg";
    private final static String destImg = baseDataPath+"photo/out.jpg";

    private Image img;
    private int width;
    private int height;

    /**
     * 构造函数。
     * @param fileName
     * @throws IOException
     */
    public ImageCompress(String fileName) throws IOException {
        File file = new File(fileName);
        img = ImageIO.read(file); // 构造Image对象
        width = img.getWidth(null);
        height = img.getHeight(null);
    }

    /**
     * 按照宽度还是高度进行压缩
     * @param w 指定压缩宽度
     * @param h 指定压缩高度
     * @throws ImageFormatException
     * @throws IOException
     */
    public void compressFix(int w, int h) throws ImageFormatException, IOException{
        if(width / height > w / h){
            compressImg(w, (int)(height * w / width));
        } else {
            compressImg((int)(width * h / height), h);
        }
    }

    public void compressImg(int w, int h) throws ImageFormatException, IOException{
        /**
         * Image.SCALE_SMOOTH 的缩略算法
         * 生成缩略图片的平滑度的
         * 优先级比速度高
         * 生成的图片质量比较好
         * 但速度慢
         *
         */
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        //绘制新图时，使用Image.SCALE_SMOOTH算法，压缩后的图片质量相对比较光滑，没有明显的锯齿形
//		image.getGraphics().drawImage(img, 0, 0, w, h, null);           <span style="color:#ff6666;">//---------压缩图片如图1</span>
        image.getGraphics().drawImage(img.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null);
        //<span style="color:#ff6666;">//-------压缩后图片如图2</span>

        File destFile = new File(destImg);
        FileOutputStream out = new FileOutputStream(destFile); // 输出到文件流
        // 可以正常实现bmp、png、gif转jpg
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        encoder.encode(image); // JPEG编码
        out.close();
    }


    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws IOException {
        long begin = System.currentTimeMillis();
        System.out.println("图片压缩开始");
        ImageCompress compress = new ImageCompress(sourceImg);
//		compress.compressImg(150, 150);  不根据宽度或高度等比例压缩
        compress.compressFix(1920, 1080);//等比例以宽度或高度为基准进行压缩
        long end = System.currentTimeMillis();
        System.out.println("图片压缩结束：" + (end-begin));
    }


}