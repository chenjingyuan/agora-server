package com.melot.recorder.utils;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;



/**
 *  网络工具类 
 * 
 */
public abstract class HttpUtils {

	private static Logger logger = Logger.getLogger(HttpUtils.class);
	private static final String UNKNOWN = "unknown";
	
	private static final String[] HTTP_HEADER = new String[]{"X-Forwarded-For","Proxy-Client-IP","WL-Proxy-Client-IP","HTTP_CLIENT_IP","HTTP_X_FORWARDED_FOR"};
	
	
	/**
	 * 从request请求解析出body
	 * 
	 */
	public static String getHttpRequestBody(HttpServletRequest request) {

		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			request.setCharacterEncoding("utf-8");
			reader = request.getReader();
			String input = null;
			while ((input = reader.readLine()) != null) {
				sb.append(input);
			}
		} catch (IOException e) {
			logger.error("Parse error happened, IOException is " + e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("Can't close the IO, IOException is " + e.getMessage());
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 从request获取ip地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = null;
		for (String header : HTTP_HEADER) {
			ip = request.getHeader(header);
			if(ip != null && ip.length() > 0 || !UNKNOWN.equalsIgnoreCase(ip)){
				return ip;
			}
		}
		return request.getRemoteAddr();
	}


}
