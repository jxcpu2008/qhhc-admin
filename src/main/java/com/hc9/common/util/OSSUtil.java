package com.hc9.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;

public class OSSUtil {
	private static final String ACCESS_ID = "ZTDSrbC6D4YqMdyp";
	private static final String ACCESS_KEY = "pwXHm6zXtwD0sLbQ32mWJMe0yAGroP";
	private static final String OSS_ENDPOINT = "http://oss-cn-shenzhen.aliyuncs.com/";
	private static final String bucketName = "hcfile";
	private static final String URL="http://hcfile.hc9.com/";
	private static OSSClient client = null;

	private static void initialize() {
		if (client == null) {
			ClientConfiguration config = new ClientConfiguration();

			client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, config);
		}
	}

	/**
	 * 上传文件到oss服务器
	 * 
	 * @throws IOException
	 */
	public static Map<String,String> uploadToOss(HttpServletRequest request,String folder) throws IOException {
		initialize();
		ensureBucket(client, bucketName);

		MultipartFile file = null;
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		String fileName;
		Iterator<String> fielnames = multipartRequest.getFileNames();

		List<String> fn = new ArrayList<String>();
		while (fielnames.hasNext()) {
			fn.add(fielnames.next());
		}
		Map<String,String> results=new HashMap<>();
		for (String item : fn) {
			file = multipartRequest.getFile(item);
			ObjectMetadata objectMeta = new ObjectMetadata();
//			Date expire = new Date(new Date().getTime() + 3600 * 1000);
//			objectMeta.setExpirationTime(expire);
			objectMeta.setContentLength(file.getSize());
			fileName = file.getOriginalFilename();
			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			fileName = System.currentTimeMillis() + "." + suffix;
			objectMeta.setContentType("image/" + suffix);
			InputStream is = file.getInputStream();
			String filedir = "upload/"+folder+"/" + fileName;
			PutObjectRequest objectRequest = new PutObjectRequest(bucketName, filedir, is, objectMeta);
			PutObjectResult pr=client.putObject(objectRequest);
			results.put("eTag", pr.getETag());
			results.put("fileDir", URL+filedir);
			results.put("fileName", fileName);
		}
		return results;
	}

	/**
	 * 创建Bucket
	 * 
	 * @param client
	 * @param bucketName
	 * @throws OSSException
	 * @throws ClientException
	 */
	private static void ensureBucket(OSSClient client, String bucketName) throws OSSException, ClientException {

		try {
			// 创建bucket
			client.createBucket(bucketName);
		} catch (ServiceException e) {
			if (!OSSErrorCode.BUCKET_ALREADY_EXISTS.equals(e.getErrorCode())) {
				// 如果Bucket已经存在，则忽略
				throw e;
			}
		}
	}
	

	public static void deleteFileFromOSS(String file){
		initialize();
		ensureBucket(client, bucketName);
		client.deleteObject(bucketName,file);
	}
}
