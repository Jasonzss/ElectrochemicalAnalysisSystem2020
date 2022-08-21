package com.bluedot.utils;

import org.apache.commons.fileupload.FileItem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;


/**
 * @Author Jason
 * @CreationDate 2022/08/16 - 21:47
 * @Description ：
 */
public class ImageUtil {
    public static Byte[][] imgToBinary(FileItem userImg) {
        return null;
    }

    public static BufferedImage createAuthImg(String code){
        //在内存中创建一个图片
        BufferedImage image = new BufferedImage(220,40,BufferedImage.TYPE_INT_RGB);

        //得到图片
        //画笔
        Graphics2D g = (Graphics2D) image.getGraphics();
        //设置图片的背景颜色
        g.setColor(Color.WHITE);
        g.fillRect(0,0,220,40);
        //给图片写数据
        g.setColor(Color.GRAY);

        //设置随机字体
        String[] fontName = new String[3];
        fontName[0] = "DialogInput";
        fontName[1] = "DialogInput";
        fontName[2] = "SansSerif";

        for (int i = 0; i < code.length(); i++) {
            //随机数字大小
            int size = new Random().nextInt(10) + 22;

            //挨个画出字符
            g.setFont(new Font(fontName[size%3],Font.BOLD,size));
            g.drawString(String.valueOf(code.charAt(i)),25*i,30);
        }

        return image;
    }
}
