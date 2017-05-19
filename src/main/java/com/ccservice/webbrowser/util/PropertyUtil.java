package com.ccservice.webbrowser.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/** 
 * PropertyUtil属性文件读写工具类 
 */  
public class PropertyUtil {
	/**
	 * 配置文件
	 */
	public static String PROPERTY_FILE="application.properties";
    /** 
     * 根据Key 读取Value 
     *  
     * @param key 
     * @return 
     */  
    public static String readData(String key) {  
        Properties props = new Properties();  
        try { 
        	
            InputStream in = PropertyUtil.class.getClassLoader().getResourceAsStream(PROPERTY_FILE);
            props.load(in);
            in.close();  
            String value = props.getProperty(key);  
            return value;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
      
    /** 
     * 修改或添加键值对 如果key存在，修改 反之，添加。 
     *  
     * @param key 
     * @param value 
     */  
    public static void writeData(String key, String value) {  
        Properties prop = new Properties();  
        try {  
            File file = new File(PROPERTY_FILE);  
            if (!file.exists())  
                file.createNewFile();  
            InputStream fis = new FileInputStream(file);  
            prop.load(fis);  
            fis.close();//一定要在修改值之前关闭fis  
            OutputStream fos = new FileOutputStream(PROPERTY_FILE);  
            prop.setProperty(key, value);  
            prop.store(fos, "Update '" + key + "' value");  
            IOUtils.closeQuietly(fos);  
        } catch (IOException e) {  
            System.err.println("Visit " + PROPERTY_FILE + " for updating "  
                    + value + " value error");  
        }  
    }  
    public static void main(String[] args) {
    	String value=readData("druid.username");
    	System.out.println(value);
    }  
}  
