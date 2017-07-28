/**
 * This document and its contents are protected by copyright 2012 and owned by Melot Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) Melot Inc. 2015
 */
package com.melot.recorder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.melot.recorder.worker.Executor;


/**
 * Title: Main
 * <p>
 * Description: 项目启动入口
 * </p>
 * @author  姚国平<a href="mailto:guoping.yao@melot.cn">
 * @version V1.0
 * @since 2015-1-5 下午7:10:55 
 */
public class Main {

    public static void main(String[] args) {
        ApplicationContext applicationContext = 
                new ClassPathXmlApplicationContext("spring/recorder.spring.xml");
        
        String[] beanNames =  applicationContext.getBeanNamesForType(Executor.class);
        if(beanNames != null && beanNames.length > 0){
            for(String beanName : beanNames ){
                Executor excutor = (Executor)applicationContext.getBean(beanName);
                excutor.execute();
            }
        }
    }
}
