/**
 * This document and its contents are protected by copyright 2012 and owned by Melot Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) Melot Inc. 2015
 */

package com.melot.recorder.webservice.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import com.melot.recorder.utils.GlobleApplicationContext;
import com.melot.recorder.utils.StringUtils;
import com.melot.recorder.worker.RecorderWorker;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;



/**
 * Title: RecorderServlet
 * <p>
 * Description: 录制客户端程序,用于接受录制请求
 * </p>
 * @author  姚国平<a href="mailto:guoping.yao@melot.cn">
 * @version V1.0
 * @since 2015-1-9 下午3:54:35 
 */

public class RecorderServlet extends HttpServlet{

    private static final long serialVersionUID = 6597635930191034965L;
    private static Logger logger = Logger.getLogger(RecorderServlet.class);
    private static RecorderWorker recorderWorker = 
            (RecorderWorker)GlobleApplicationContext.getSpringContext().getBean("recorderWorker");

    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        doPost(req, resp);
        
    }

    @Override

/***
     *  处理recorderServer开始录制, 结束录制, 发布上传文件等操作
     *  开始录制:?MsgTag=1&channel=886&uid=123&
     *  结束录制:?MsgTag=2&uuid=b28e2d90-e8be-11e4-bb45-a79338d3d87d
     *  发布上传:?parameter={"videoPath":"/video/2017-4-2/xxx.mp4","imagePath":"/video/2017-4-2/d.jpg","unToken":"asdadsasda","key":"key12121"}
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String result = "-1";

        PrintWriter p = null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        logger.debug(parameterMap);
        if(parameterMap ==null || parameterMap.isEmpty()) {
            logger.error("请求参数错误,必传项缺失");
        }else{
            String msgTag = request.getParameter("msgTag");
            if(StringUtils.isEmpty(msgTag)){
                //上传视频
                JSONObject json = new JSONObject();
                try {
                    json.accumulate("result",false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result =json.toString() ;
            }else{
                result = recorderWorker.handler(parameterMap);
            }
        }
        try {
            response.setContentType("text/html;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            p = response.getWriter();
            p.write(result);
            p.flush();
        } catch (IOException e) {
            logger.error("create response error: " + e);
        }finally{
            if(p != null ){
                p.close();
            }
        } 
    }
    
}

