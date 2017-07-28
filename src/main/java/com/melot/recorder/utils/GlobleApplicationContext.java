/**
 * Copyright 2012-2014 the original author or authors.
 */
package com.melot.recorder.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author guoping.yao@melot.com
 *
 */
public class GlobleApplicationContext implements ApplicationContextAware {

	
	private static ApplicationContext springContext;


	public static ApplicationContext getSpringContext() {
		return springContext;
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		setSpringContext(applicationContext);
		
	}


	private static void setSpringContext(ApplicationContext springContext) {
		GlobleApplicationContext.springContext = springContext;
	}
	
	

}
