package com.hc9.common.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;

import com.swetake.util.Qrcode;

/**
 * 二维码生成工具
 * @author frank
 *
 */
public class QRcodeUtil {
	
	public static BufferedImage createImage(String param,int width, int height) throws UnsupportedEncodingException{
		Qrcode qrcode = new Qrcode();
		qrcode.setQrcodeErrorCorrect('M');//纠错级别,“M”是第二级
		qrcode.setQrcodeEncodeMode('B');//编码方式
		qrcode.setQrcodeVersion(7);//二维码的版本号
		byte[] bytes = param.getBytes("UTF-8");
		BufferedImage bi = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setBackground(Color.WHITE); // 背景颜色
		g.clearRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		if (bytes.length > 0 && bytes.length < width) {
			boolean[][] b = qrcode.calQrcode(bytes);
			for (int i = 0; i < b.length; i++) {
				for (int j = 0; j < b.length; j++) {
					if (b[j][i]) {
						g.fillRect(j * 3 + 2, i * 3 + 2, 3, 3);
					}
				}

			}
		}
		g.dispose();
		return bi;
		
	}
}
