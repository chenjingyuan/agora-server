/**
 * Copyright 2012-2014 the original author or authors.
 */
package com.melot.recorder.utils;

import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;


/**
 * Title: RestClient
 * <p>
 * Description: jersey 请求客户端
 * </p>
 * @author  姚国平<a href="mailto:guoping.yao@melot.cn">
 * @version V1.0
 * @since 2015-1-5 下午7:10:55 
 */
public class RestClient {

	private static final Logger logger = Logger.getLogger(RestClient.class);
	private static String DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON;
	private static final String GET = "GET";
	private static final String POST = "POST";

	public static Client getClient() {
		return Client.create();
	}

	/**
	 * create a webResource by url
	 * 
	 * @param url
	 * @return
	 */
	public static WebResource getWebResource(String url) {
		return getClient().resource(url);
	}

	public static JSONObject send(WebResource wr, String mediaType,
			JSONObject msg, String method) {
		JSONObject json = null;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("RestClient " + method + " " + wr.getURI().toString() + ": ======================>\n" + msg);
			}
			if ("GET".equals(method)) {
				json = wr.type(mediaType).get(JSONObject.class);
			} else if ("POST".equals(method)) {
				if (msg == null) {
					json = wr.type(mediaType).post(JSONObject.class);
				} else {
					json = wr.type(mediaType).post(JSONObject.class, msg);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("RestClient receive :<=====================\n" + json);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	public static JSONObject send(String url, String mediaType, JSONObject msg,
			String method) {
		WebResource wr = getWebResource(url);
		return send(wr, mediaType, msg, method);
	}

	public static JSONObject send(String url, String mediaType, String msg,
			String method) throws JSONException {
		return send(url, mediaType, new JSONObject(msg), method);
	}

	public static JSONObject post(String url, String mediaType, JSONObject msg) {
		return send(url, mediaType, msg, POST);
	}

	public static JSONObject post(String url, JSONObject msg) {
		return post(url, DEFAULT_MEDIA_TYPE, msg);
	}

	public static JSONObject post(String url, String mediaType, String msg) throws JSONException {
		return send(url, mediaType, msg, POST);
	}

	public static JSONObject post(String url, String msg) throws JSONException {
		return post(url, DEFAULT_MEDIA_TYPE, msg);
	}

	public static JSONObject get(String url) {
		return get(url, DEFAULT_MEDIA_TYPE);
	}

	public static JSONObject get(String url, String mediaType) {
		return send(url, mediaType, (JSONObject) null, GET);
	}

	public static JSONObject get(WebResource wr, String mediaType, JSONObject msg) {
		return send(wr, mediaType, msg, GET);
	}
}
