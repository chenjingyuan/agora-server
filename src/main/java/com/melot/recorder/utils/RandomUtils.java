/**
 * Copyright 2012-2014 the original author or authors.
 */
package com.melot.recorder.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @author guoping.yao@melot.com
 *
 */
public abstract class RandomUtils {

	private static Random random = new Random(System.currentTimeMillis());
	private RandomUtils(){
		
	}
	/**
	 *	@return 32-bits uuid 
	 */
	public static String randomUUID(){
		return UUID.randomUUID().toString().replace("-","");
	}


	
	/**
	 *	@param min lower bound of the return
	 * 	@param max upper bound of the return
	 * 	@return return the random value greater than min  
	 *  min < return < max
	 *  bug: probability of max-1 is double than others
	 *  
	 *  
	 *  
	 */
	public static int getRandomValue(int min, int max){
	    int value = 0;
	    do{
	        value =  random.nextInt(max) % (max - min ) + min + 1;
	    }while(value == max);
		
	    return value;
	} 
	
	
}
