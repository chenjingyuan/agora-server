/**
 * Copyright 2012-2014 the original author or authors.
 */
package com.melot.recorder.conf;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.melot.recorder.utils.CollectionUtils;
import com.melot.recorder.utils.RandomUtils;
import sun.dc.pr.PRError;

/**
 * @author guoping.yao@melot.com
 */
public class SystemConfig {

    private static final Logger logger = Logger.getLogger(SystemConfig.class);
    private static final String UNDERLINE = "_";
    private static Properties pp;
    /*录制命令路径*/
    private static String recorderCommond;
    /*声网Agora appId*/
    private static String appId;
    /**
     * 设置录制文件存放的根目录
     **/
    private static String recordFileRootDir;
    /**
     * Linux SDK 内置的 applite 程序 video_recorder 存放的目录
     **/
    private static String appliteDir;
    /**
     * 是否只录视频 default 0(只录视频）
     **/
    private static int isAudioOnly;
    /**
     * 设置空闲频道超时退出时间。如果频道内无用户超过设定的时间，录制程序自动退出。默认为 300 秒
     **/
    private static int idle;

    private static String recorderCmd = "%s --appId %s --uid 0 --channel %s --appliteDir %s --isAudioOnly %d --idle %d --recordFileRootDir %s";
    private static String covertCommand = "python %s %s ";
    private static String searchComand = "python %s %s%s/ %s";
    private static String detailComand = "python %s %s";
    private static String stopCommand ="kill -2 %s";
    private static String convertShell;
    private static String searchShell;
    private static String detailShell;


    private static String netWorkIP = null;
    private static int jettyServerPort;
    private static String remoteAdress;


    private static boolean isDebug = false;


    private SystemConfig() {
    }

    static {
        reload();
    }

    public static void reload() {
        pp = new Properties();
        InputStream ins = null;
        try {
            ins = SystemConfig.class.getClassLoader().getResourceAsStream("system.properties");
            logger.info("system config file load begin");
            pp.load(ins);

            isDebug = getBooleanProperty("recorder.exe.logging.debug", false);
            jettyServerPort = getIntProperty("jetty.server.port", 6666);
            //recorder
            recorderCommond = getStringProperty("recorder.command.path", "");
            appId = getStringProperty("recorder.appId", "f555ffac5de640d7af5db2f0c7eb2aea");
            appliteDir = getStringProperty("recorder.appliteDir.path", "");
            recordFileRootDir = getStringProperty("recorder.config.recordFileRootDir", "");
            idle = getIntProperty("recorder.config.idle", 300);
            isAudioOnly = getIntProperty("recorder.config.isAudioOnly", 1);

            //covert
            convertShell = getStringProperty("covert.shell", "");
            searchShell = getStringProperty("search.shell", "");
            detailShell = getStringProperty("detail.shell", "");

            remoteAdress = getStringProperty("resource.remoteAdress", "");
        } catch (Exception e) {
            logger.error("SystemConfig.Load.Error", e);
        }
        logger.info("config init successfully...");
    }

    public static String getNetWorkIP() {
        return netWorkIP;
    }

    public static int getJettyServerPort() {
        return jettyServerPort;
    }

    /**
     * 获取本机网卡外网地址
     */
    private static String setNetWorkIP() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) {// only ipv4
                        if (!address.isLoopbackAddress() && !address.isSiteLocalAddress()) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("get server address error.", e);
        }
        return null;
    }

    private static boolean getBooleanProperty(String key, boolean defaultValue) {
        if (pp == null || pp.getProperty(key) == null) {
            return defaultValue;
        }
        if ("true".equals(pp.getProperty(key).trim())) {
            return true;
        }
        if ("false".equals(pp.getProperty(key).trim())) {
            return false;
        }
        return defaultValue;
    }


    private static String getStringProperty(String key, String defaultValue) {
        if (pp == null || pp.getProperty(key) == null) {
            return defaultValue;
        }
        return pp.getProperty(key).trim();
    }

    private static int getIntProperty(String key, int defaultValue) {
        if (pp == null || pp.getProperty(key) == null) {
            return defaultValue;
        }
        int value = defaultValue;
        try {
            value = Integer.parseInt(pp.getProperty(key).trim());
        } catch (NumberFormatException e) {
            value = defaultValue;
        }
        return value;
    }


    /**
     * 判断目录文件夹是否存在,不存在则创建文件夹;
     */
    private static void createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists() && !folder.isDirectory()) {
            if (folder.mkdirs()) {
                logger.debug("create folder success");
            } else {
                return;
            }
        }
    }

    /**
     * 获取cmd 命令行
     *
     * @param channel
     * @return
     */
    public static String getRecorderCommand(String channel) {

        return String.format(recorderCmd, recorderCommond, appId,channel, appliteDir, isAudioOnly, idle, recordFileRootDir);
    }

    /**
     * 查询视频文件存储路径命令
     **/
    public static String getSearchComand(String channel, String date) {

        return String.format(searchComand, searchShell, recordFileRootDir, date, channel);
    }

    /**
     * 视频转换命令
     **/
    public static String getConvertCommand(String filePath) {

        return String.format(covertCommand, convertShell, filePath);
    }
    /**
     * 录制结束命令
     **/
    public static String getConvertCommand(Integer pid) {

        return String.format(stopCommand,pid);
    }




    public static String getUrl(String filePath) {

        return remoteAdress+filePath.replace(recordFileRootDir,"");
    }

    /**
     * 系统日志级别
     */
    public static boolean isDebug() {
        return isDebug;
    }


    public static void main(String[] args) {

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"cmd"});
            InputStream in = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDetailCommand(String filepath) {
        return String.format(detailComand,detailShell,filepath);
    }
}
