package com.uestc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by Nerozen on 2016/3/23.
 */
public class ConfigurationFilesAdapter {
    //private String filePath;
    private Properties fileAsPropertyObject;

    public ConfigurationFilesAdapter(){
        try{
            //FileOutputStream fileStream = new FileOutputStream(filePath);
            fileAsPropertyObject = new Properties();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean SetOrUpdateProperty(String filePath, String configKey, String configValue){
        boolean updateResult = false;
        FileOutputStream fileStream = null;
        try{
            File file = new File(filePath);
            fileStream = new FileOutputStream(file, true);
            if(!file.exists()){
                updateResult = false;
            }
            else{
                InputStream is = new FileInputStream(file);
                this.fileAsPropertyObject.load(is);
                is.close();
                OutputStream os = new FileOutputStream(filePath);
                this.fileAsPropertyObject.setProperty(configKey, configValue);
                this.fileAsPropertyObject.store(os, null);
                os.close();
                updateResult = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return updateResult;
    }

    public String ReadPropertyByFileNameAndKey() {
        return null;
    }

    public boolean ReadPropertyByConfigKey(String configKey){
        return true;
    }


    /*退出成功，写入自动登录配置文件使下次自动登录无效化 2016-3-23 */
    /*try{
        FileOutputStream fileStream = new FileOutputStream("/data/data/com.znt.estate/autologinconfig.properties", false);
        Properties autoLoginProperties = new Properties();

        File autoLoginFile=new File("/data/data/com.znt.estate/autologinconfig.properties");
        if(!autoLoginFile.exists()){
            autoLoginFile.createNewFile();
        }
        autoLoginProperties.setProperty("isAutoLoginExpected", "false");
        autoLoginProperties.store(fileStream, null);

    }catch (Exception e){
        e.printStackTrace();
    }*/
}
