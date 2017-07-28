package com.melot.recorder.conf;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 *	配置文件配置项读取, spring配置文件加载时读取 *.properties 文件 
 * 
 */
public class PropertiesLoader extends PropertyPlaceholderConfigurer {

	 private Map<String, String> propertiesMap;

	    @Override
	    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
	        super.processProperties(beanFactoryToProcess, props);

	        propertiesMap = new ConcurrentHashMap<String, String>();
	        for (Object key : props.keySet()) {
	            String keyStr = key.toString();
	            propertiesMap.put(keyStr, resolvePlaceholder(keyStr, props));
	        }
	    }

	    public Map<String, String> getPropertiesMap() {
	        return propertiesMap;
	    }
	
}
