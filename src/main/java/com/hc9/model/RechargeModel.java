package com.hc9.model;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.springframework.stereotype.Component;

/**
 * Excel导出
 * 
 * @author Mr.Po
 * 
 */
@Component
public class RechargeModel {

	/**
	 * excel导出
	 * 
	 * @param title
	 *            标题
	 * @param column
	 *            列宽度（如果为null，默认高度为15）
	 * @param header
	 *            头部
	 * @param content
	 *            内容
	 * @param response
	 *            响应
	 * @return 是否成功
	 */
	public boolean downloadExcel(String title, Integer[] column, String[] header, List<Map<String, String>> content,
			HttpServletResponse response) {
		try {
			String filename = title + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
			filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
			response.reset();
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/msexcel");// 设置为下载application/x-download
			response.setHeader("Content-Disposition", "inline;filename=\"" + filename + ".xls\"");
			OutputStream os = response.getOutputStream();// 取得输出流
			// 提示下载
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			// 创建excel工作表，指定名字和位置
			WritableSheet sheet = wwb.createSheet(title, 0);

			// 添加标题（行宽）
			for (int i = 0; i < header.length; i++) {
				sheet.addCell(new Label(i, 0, header[i]));
				// 设置excel列宽
				if (column != null) {
					sheet.setColumnView(i, column[i]);
				} else {// 如果没有设置默认为宽度为50
					sheet.setColumnView(i, 15);
				}
			}

			// 添加内容
			for (int i = 0; i < content.size(); i++) {
				for (int j = 0; j < content.get(i).size(); j++) {
					sheet.addCell(new Label(j, i + 1, content.get(i).get(header[j]) + ""));
				}
			}

			// 写入工作表
			wwb.write();
			wwb.close();
			os.close();
		} catch (IOException | WriteException e) {
			// TODO: handle exception
		}
		return true;
	}

	/**
	 * excel导出
	 * 
	 * @param title
	 *            标题
	 * @param column
	 *            列宽度（如果为null，默认高度为15）
	 * @param header
	 *            头部
	 * @param content
	 *            内容
	 * @param response
	 *            响应
	 * @return 是否成功
	 */
	public boolean downloadExcel(String title, Integer[] column, String[] header, List<Map<String, String>> content,
			HttpServletResponse response, HttpServletRequest request) {
		try {
			String filename = title + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

			if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
				filename = URLEncoder.encode(filename, "UTF-8");
			} else {
				filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
			}
			response.reset();
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/msexcel");// 设置为下载application/x-download
			response.setHeader("Content-Disposition", "inline;filename=\"" + filename + ".xls\"");
			OutputStream os = response.getOutputStream();// 取得输出流
			// 提示下载
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			// 创建excel工作表，指定名字和位置
			WritableSheet sheet = wwb.createSheet(title, 0);

			// 添加标题（行宽）
			for (int i = 0; i < header.length; i++) {
				sheet.addCell(new Label(i, 0, header[i]));
				// 设置excel列宽
				if (column != null) {
					sheet.setColumnView(i, column[i]);
				} else {// 如果没有设置默认为宽度为50
					sheet.setColumnView(i, 15);
				}
			}

			// 添加内容
			for (int i = 0; i < content.size(); i++) {
				for (int j = 0; j < content.get(i).size(); j++) {
					sheet.addCell(new Label(j, i + 1, content.get(i).get(header[j]) + ""));
				}
			}

			// 写入工作表
			wwb.write();
			wwb.close();
			os.close();
		} catch (IOException | WriteException e) {
			// TODO: handle exception
		}
		return true;
	}

	public boolean downloadUserVoExcel(String title, Integer[] column, String[] header, List<UserVo> content,
			HttpServletResponse response) {
		try {
			String filename = title + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
			filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
			response.reset();
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/msexcel");// 设置为下载application/x-download
			response.setHeader("Content-Disposition", "inline;filename=\"" + filename + ".xls\"");
			OutputStream os = response.getOutputStream();// 取得输出流
			// 提示下载
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			// 创建excel工作表，指定名字和位置
			//TODO 每页40000条记录
			int limit=40000;
			int totalNum=content.size();
			int totalPages=totalNum/limit;
			for(int currentPage=0;currentPage<=totalPages;currentPage++){
				
				WritableSheet sheet = wwb.createSheet("数据页"+currentPage, currentPage);
				// 添加标题（行宽）
				for (int j = 0; j < header.length; j++) {
					sheet.addCell(new Label(j, 0, header[j]));
					// 设置excel列宽
					if (column != null) {
						sheet.setColumnView(j, column[j]);
					} else {// 如果没有设置默认为宽度为50
						sheet.setColumnView(j, 15);
					}
				}
				for (int k =0; k< limit; k++) {
					if(totalNum<=currentPage*limit+k){
						break;
					}
					sheet.addCell(new Label(0, k + 1, content.get(currentPage*limit+k).getUserName() ));
					sheet.addCell(new Label(1, k + 1, content.get(currentPage*limit+k).getName() ));
					sheet.addCell(new Label(2, k + 1, content.get(currentPage*limit+k).getPhone() ));
					sheet.addCell(new Label(3, k + 1, content.get(currentPage*limit+k).getUserType() ));
					sheet.addCell(new Label(4, k + 1, content.get(currentPage*limit+k).getCreateTime() ));
					sheet.addCell(new Label(5, k + 1, content.get(currentPage*limit+k).getGenUser() ));
					sheet.addCell(new Label(6, k + 1, content.get(currentPage*limit+k).getGenUserDept() ));
					sheet.addCell(new Label(7, k + 1, content.get(currentPage*limit+k).getChannel() ));
				}
			}
			



			// 写入工作表
			wwb.write();
			wwb.close();
			os.close();
		} catch (IOException | WriteException e) {
			e.printStackTrace();
		}
		System.out.println("下载excl");
		return true;
	}
}
