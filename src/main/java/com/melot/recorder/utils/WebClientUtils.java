/**
 * This document and its contents are protected by copyright 2012 and owned by
 * Melot Inc.
 * The copying and reproduction of this document and/or its content (whether
 * wholly or partly) or any
 * incorporation of the same into any other material in any media or format of
 * any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) Melot Inc. 2015
 *//*

package com.melot.recorder.utils;


import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import com.melot.recorder.conf.SystemConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

*/
/**
 * Title: WebClient
 * <p>
 * Description: 网络调用工具类,与recorder-server交互
 * </p>
 * 
 * @author 姚国平<a href="mailto:guoping.yao@melot.cn">
 * @version V1.0
 * @since 2015-1-6 下午2:23:45
 *//*

public abstract class WebClientUtils {

    private static Logger logger = Logger.getLogger(WebClientUtils.class);

    
    */
/**
     *  调用录制服务端,保存录制信息 
     *  必传项:"uuid", "roomId", "fileName", "filePath","location", "startTime", "createRole"
     *//*

    public static boolean saveRecordInfo(String uuid,String roomId,String fileName,String filePath,Long startTime, String createRole,String title ){
        final String url = SystemConfig.getRemoteServerAddress() + "/services/videoRecord/save";
        boolean result = false;
        try {
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            JSONObject request = new JSONObject();
            request.put("uuid", uuid);
            request.put("roomId", roomId);
            request.put("fileName", fileName);
            request.put("filePath", filePath);
            request.put("location", SystemConfig.getNetWorkIP());
            request.put("startTime", startTime);
            request.put("createRole", createRole);
            request.put("title", title);
            JSONObject json = webResource.type(MediaType.APPLICATION_JSON).post(JSONObject.class, request);
            if (!json.isNull("resultCode") && "00000000".equals(json.getString("resultCode"))) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("getActorList Error:", e);
        }
        return result;
    }
    
    */
/**
     *  更新录制记录的结束时间,时长等信息 
     *  server端捕获size=0,判定录制的视频有问题
     *  screenCount若小于0;则不作为必传项
     *  原因在于动态截图需要耗时异步处理,视频录制结束,本接口需要调用两次,分别记录视频相关信息和截图张数
     *//*

    public static boolean updateRecordInfo(String uuid,Long endTime, Long duration, Long size, int screenCount, String isAutoPublish){
        final String url = SystemConfig.getRemoteServerAddress() + "/services/videoRecord/finish";
        boolean result = false;
        try {
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            JSONObject request = new JSONObject();
            request.put("uuid", uuid);
            request.put("duration", duration);
            request.put("endTime", endTime);
            request.put("size", size);
            request.put("isAuto", isAutoPublish);
            logger.info("updateRecordInfo params:" + request.toString());
            if(screenCount >= 0 ){
                request.put("screenCount", screenCount);
            }
            JSONObject json = webResource.type(MediaType.APPLICATION_JSON).post(JSONObject.class, request);
            logger.info("updateRecordInfo, message from server is "+json);
            if (!json.isNull("resultCode") && "00000000".equals(json.getString("resultCode"))) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("getActorList Error:", e);
        }
        return result;
    }
    
    public static boolean updateScreenShotInfo(String uuid,int screenCount,String isAutoPublish){
        final String url = SystemConfig.getRemoteServerAddress() + "/services/videoRecord/finish";
        boolean result = false;
        try {
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            JSONObject request = new JSONObject();
            request.put("uuid", uuid);
            request.put("screenCount", screenCount);
            request.put("isAuto", isAutoPublish);
            logger.info("updateScreenShotInfo params:" + request.toString());
            JSONObject json = webResource.type(MediaType.APPLICATION_JSON).post(JSONObject.class, request);
            logger.info("updateScreenShotInfo, message from server is "+json);
            if (!json.isNull("resultCode") && "00000000".equals(json.getString("resultCode"))) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("getActorList Error:", e);
        }
        return result;
        
    }
    
    

}
*/
