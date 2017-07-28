/**
 * This document and its contents are protected by copyright 2012 and owned by Melot Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * <p>
 * Copyright (c) Melot Inc. 2015
 */

package com.melot.recorder.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.melot.recorder.utils.ProcessUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.melot.recorder.conf.SystemConfig;
import com.melot.recorder.utils.RandomUtils;


/**
 * Title: RecorderWorker
 * <p>
 * Description: 执行命令,进行视频录制, 完成录制需要执行截图操作
 * </p>
 *
 * @author 姚国平<a href="mailto:guoping.yao@melot.cn">
 * @version V1.0
 * @since 2015-4-22 下午5:07:27
 */

public class RecorderWorker {

    private static Logger logger = Logger.getLogger(RecorderWorker.class);

    private static Object initLock = new Object();

    /**
     * 执行任务线程池
     */

    private ExecutorService executorPool = null;

    private Semaphore semaphore = null;


    /**
     * 保存当前执行的任务线程对象pid,用于destroy
     */

    private Map<String, String> workingProcess = new HashMap<String, String>();


    /**
     * constructor
     */

    public RecorderWorker(int poolSize) {
        synchronized (initLock) {
            if (semaphore == null) {
                semaphore = new Semaphore(poolSize);
            }
            if (executorPool == null) {
                executorPool = Executors.newFixedThreadPool(poolSize, Executors.defaultThreadFactory());
            }
        }
    }


    /**
     * 处理入口:分发执行视频录制相关任务
     */

    public String handler(Map<String, String[]> parameterMap) {
        JSONObject result = new JSONObject();
        try {
            String msgTag = parameterMap.get("msgTag")[0];
            //start
            if ("1".equals(msgTag)) { //执行视频开播任务
                final String channel = parameterMap.get("channel")[0];
                String cmd = SystemConfig.getRecorderCommand(channel);
                startRecord(cmd, channel);
                result.accumulate("success", true);
                result.accumulate("date", getGMTString());
            }
            //stop ,covert
            else if ("2".equals(msgTag)) {
                final String date = parameterMap.get("date")[0];
                final String channel = parameterMap.get("channel")[0];
                final String cmd = SystemConfig.getSearchComand(channel, date);
                Process process = Runtime.getRuntime().exec(cmd);
                finishRecord(channel);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String filepath;
                while ((filepath = bufferedReader.readLine()) != null) {
                    System.out.println(filepath);
                    bufferedReader.close();
                    convertVideo(filepath);
                    break;
                }
                JSONArray array = getRecordDetail(filepath);
                result.accumulate("success", true);
                result.accumulate("storeUrl", SystemConfig.getUrl(filepath));
                result.accumulate("recordInfo", array);
            }


        } catch (Exception e) {
            logger.error("execute recorder handler error:\n request is" + parameterMap, e);
        }
        return result.toString();

    }

    private JSONArray getRecordDetail(String filepath) {
        JSONArray jsonArray=new JSONArray();
        final String detailCmd = SystemConfig.getDetailCommand(filepath);
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(detailCmd);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject jsonObject=new JSONObject();
                System.out.println(line);
                String[] params=line.split(",");
                jsonObject.accumulate("userId",params[0]);
                jsonObject.accumulate("start_time",params[1]);
                jsonObject.accumulate("end_time",params[2]);
                jsonObject.accumulate("start_date",params[3]);
                 jsonArray.put(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }


    public void convertVideo(final String filepath) {
        executorPool.execute(new Runnable() {
            public void run() {
                String cmd = SystemConfig.getConvertCommand(filepath);
                try {
                    Thread.sleep(1000);
                    Process proc = Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    public static String getGMTString() {
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
        s.setTimeZone(TimeZone.getTimeZone("GMT"));
        return s.format(new Date());
    }


    public static void main(String[] args) {
        System.out.println(getGMTString());
    }

    /**
     * 启动线程池处理录制逻辑
     */

    private void startRecord(final String cmd, final String channel) {
        executorPool.execute(new Runnable() {
            BufferedReader buffer = null;

            public void run() {
                logger.info("cmd---->> " + cmd);
                try {
                    Process process = Runtime.getRuntime().exec(cmd);
                    long pid = ProcessUtils.getProcessPid(process);
                    System.out.println("pid---->>" + pid);
                    if (pid != 0L) {
                        workingProcess.put(channel, String.valueOf(pid));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /***
     *  结束视频录制
     *
     */

    private JSONObject finishRecord(String channel) {
        JSONObject result = new JSONObject();
        logger.info("finish recorder------->>> uuid=" + channel);
        String pid = workingProcess.get(channel);
        Process proc = null;
        if (pid != null) {
            try {
                String cmd = "kill -2 " + pid;
                logger.info("finsishRecord command:" + cmd);
                proc = Runtime.getRuntime().exec(cmd);
                proc.waitFor();
                result.put("result", "success");
                return result;
            } catch (Exception e) {
                logger.error("finishRecord error:\n", e);
            } finally {
                if (proc != null) {
                    proc.destroy();
                }
                workingProcess.remove(channel);
            }
        }

        return result;
    }


/**
 *  检测当前uuid是否是正在录制状态, 用于数据库垃圾数据清理
 *
 *//*

    private JSONObject checkRecordState(String uuid){
        JSONObject result = new JSONObject();
        try{
            result.put("result", "success");
            if(workingProcess.containsKey(uuid)){
                result.put("isRecording", "1");
            }else{
                result.put("isRecording", "0");
            }
        }catch (JSONException e) {
            logger.error("startRecord error:\n", e);
        }
        return result;
    }
    
    
    */
/***
 *  执行截图任务, 从视频中获取图片作为封面
 * cmd: /path/record -i /video/data/2015_4_15/3/7503303_1_1429086593889.mp4  -r 0.01 -vframes 10 -f image2 -s 640*360 -q:v  5  -y  /path/KKID/kkid_taskid_YYMMDD_HHMMSS_%3d.jpg
 *//*

    public static int obtainCover(String absoluteFilePath){
        logger.info("begin screen shot");
        long begin = System.currentTimeMillis();
        String cmd = SystemConfig.getScreenShotCommand(absoluteFilePath);//获取截图命令
        Process proc = null;
        try{
            proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
        }catch (Exception e) {
            logger.error("screenShot  error:\n cmd:", e);
        }
        logger.info("finish screen shot,total cost:"+(System.currentTimeMillis() - begin));
        return SystemConfig.getScreenShotCount();
    }
    
    
    */
/**
 *  通过线程处理掉输出缓冲区内容
 *//*

    static class BufferReaderThread extends Thread{
        private Process proc;
        private String procId;
        public BufferReaderThread(Process proc,String procId){
            this.proc = proc;
            this.procId = procId;
            
        }
        @Override
        public void run() {
            BufferedReader buffer= null;
            try {
                buffer= new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                String error = null;  
                while ((error = buffer.readLine()) != null) {
                    if(SystemConfig.isDebug()){
                        logger.error("ProcId["+procId+"]ffmpeg error logger:"+error);
                    }
                }
            } catch (IOException e) {
                logger.error("LogThread error:\n", e);
            }finally{
                if(buffer!=null){
                    try {
                        buffer.close();
                    } catch (IOException e) {
                        logger.error("LogThread error:\n", e);
                    }
                }
                
            }
        }
    }
    
    
    */
/**
 *  从视频内截取封面图片,同时更新数据库
 *//*

    static class ScreenShotThread extends Thread{
        private String absoluteFilePath;
        private String uuid;
        private String isAuto;
        public ScreenShotThread(String absoluteFilePath, String uuid,String isAuto) {
            super();
            this.absoluteFilePath = absoluteFilePath;
            this.uuid = uuid;
            this.isAuto = isAuto;
        }
        @Override
        public void run() {
            try {
                int screenCount = obtainCover(absoluteFilePath);
                WebClientUtils.updateScreenShotInfo(uuid,screenCount,isAuto);
            } catch (Exception e) {
                logger.error("screenShot error:\n", e);
            }
        }
    }
    */


}

