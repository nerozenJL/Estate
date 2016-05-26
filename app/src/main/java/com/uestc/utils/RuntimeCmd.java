package com.uestc.utils;

import android.util.Log;

import java.io.InputStream;

/**
 * Created by hekesong on 2015/11/14.
 * 这是adb，检测wifi是否连接成功的方法
 */
public class RuntimeCmd {
    private static final long MAXTIME=2000;

    public static int isWifiRight(){
        long start=System.currentTimeMillis();
        int i =0;
        while (true){
            if (System.currentTimeMillis()-start>=MAXTIME)
                break;
            try {
                boolean isRight=false;
                Process p=Runtime.getRuntime().exec("netcfg");
                StringBuilder stringBuilder=new StringBuilder();
                InputStream inputStream=p.getInputStream();
                int ch=0;
                while ((ch=inputStream.read())!=-1){
                    stringBuilder.append((char)ch);
                }
                String lines[]=stringBuilder.toString().split("\n");
                for (String line:lines){
                    if (line.contains("wlan0")&&line.contains("UP")){
                        if (line.contains("0.0.0.0/0")){
                            isRight=false;
                            break;
                        }else {
                            isRight=true;
                            break;
                        }
                    }
                }
                if (!isRight){
                    Thread.sleep(200);
                    continue;
                }else{
                    i = 1;
                    break;
                }

            }catch (Exception e){
                Log.e("RuntimeCmd",e.toString());
            }
        }
        return i;
    }
}
