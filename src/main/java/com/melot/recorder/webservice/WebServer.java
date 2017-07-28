/**
 * This document and its contents are protected by copyright 2012 and owned by Melot Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) Melot Inc. 2015
 */

package com.melot.recorder.webservice;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import com.melot.recorder.webservice.servlet.RecorderServlet;




/**
 * Title: WebServer
 * <p>
 * Description: WebServer Container
 * </p>
 * @author  姚国平<a href="mailto:guoping.yao@melot.cn">
 * @version V1.0
 * @since 2015-1-9 下午1:15:14 
 */
public class WebServer {

    private static Logger logger = Logger.getLogger(WebServer.class);
    
    private Server server;
    private static final WebAppContext application = new WebAppContext();
    
    public WebServer(int port, String contextPath){
        if(server == null){
            Server server = new Server(port);
            application.setResourceBase("jetty");
            application.addServlet(new ServletHolder(new RecorderServlet()), "/services/room/recorder");
            application.setContextPath(contextPath);
            server.setHandler(application);
            XmlConfiguration configuration;
            
            try {
                configuration = new XmlConfiguration(ClassLoader.getSystemResource("jetty/jetty.xml"));
                configuration.configure(server);
                server.start();
                logger.info("server start successfully....");
            } catch (Exception e) {
                logger.error("server start error,\n",e);
            }
        }
    } 
    
}

