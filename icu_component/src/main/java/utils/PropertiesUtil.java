package utils;

import config.PropertiesConfig;
import start.TimerStart;

import java.io.*;
import java.util.Properties;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/14
 * PropertiesUtil
 */
public class PropertiesUtil{
    private String properiesName;
    public PropertiesUtil(String properiesName){
        this.properiesName = properiesName;
    }

    public String getPropery(String key){
        Properties prop = new Properties();
        InputStream is = null;
        try{
            is = TimerStart.class.getClassLoader().getResourceAsStream(this.properiesName);
            prop.load(is);
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                is.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return prop.getProperty(key);
    }

    public static void main(String[] args){
        PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfig.icuProperties);
        String demo = propertiesUtil.getPropery("timer.hashed.wheel.producer.task");
        System.out.println(demo);
    }
}
