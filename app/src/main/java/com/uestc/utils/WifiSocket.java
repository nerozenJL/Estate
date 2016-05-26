package com.uestc.utils;

import android.os.Vibrator;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.uestc.service.WifiUnlocker;

/**
 * Created by hekesong on 2015/11/11.
 * 这是低版本wifi开门，向硬件发送socket连接
 */
public class WifiSocket {
    public static int port = 4800;
    protected InputStream socketReader;
    protected PrintWriter socketWriter;
    private String phone = "";
    private String password = "";
    private String secret = "";
    private static final long MAXTIME=8000;
    private Socket client = null;
    public static String TAG;
    public WifiSocket(String phone, String password, String secret){
        this.password =password;
        this.phone = phone;
        this.secret = secret;
    }
    public static final int SOCKET_CONNECT_SUCCESS= WifiUnlocker.SOCKET_COMMUNATE_SUCCESS;
    public static final int SOCKET_CONNECT_FAILED=WifiUnlocker.SOCKET_COMMUNATE_FAILED;
    //public static final int SOCKET_CONNECT_OVERTIME=WifiUnlocker.;

        public int connect() {

//            new socketClientAsyncTask().execute();
            int result=0;   //1表示成功，2表示失败
            int connecttime=0;
               try {
                   long start= System.currentTimeMillis();
                   //Thread.sleep(4000);
//                   while (true) {
//
//                       try {
//                           client = new Socket("192.168.1.8", port);
//                           if (client!=null)
//                               break;
//                       } catch (Exception e) {
//                           if (System.currentTimeMillis()-start>=MAXTIME)
//                               break;
//                           Thread.sleep(500);
//
//                       }
//                   }
                   while (true) {

                       try {
                           client = new Socket("192.168.1.8", port);
                           if (client!=null)
                               break;
                       } catch (Exception e) {
                           if (System.currentTimeMillis()-start>=MAXTIME)
                               break;
                           Log.d(TAG, "connect: socket connect failed detected!connecttime= " + connecttime);
                           Thread.sleep(500);
                           //connecttime++;
                       }
                   }
//                   if (connecttime>3)
//                       return SOCKET_CONNECT_FAILED;
//                   while(!client.isConnected()) {
//                       Thread.sleep(100);
//                       client = new Socket("192.168.1.8", port);
//                   }
                   socketReader = client.getInputStream();
                   socketWriter = new PrintWriter((new OutputStreamWriter(client.getOutputStream())));
                   Thread.sleep(10);
                   // 锟斤拷锟斤拷锟角凤拷锟斤拷20锟街斤拷 16锟斤拷锟狡电话锟斤拷锟斤拷
                   char[] a = new char[20];
                   a[0] = 0x00;
                   a[1] = 0x0B;
                   Log.e("phone", phone);
                   for (int i = 0; i < phone.length(); i++) {
                       String string = phone.substring(i, i + 1);
                       int m = Integer.parseInt(string);
                       switch (m) {
                           case 0:
                               a[i + 2] = '0';
                               break;
                           case 1:
                               a[i + 2] = '1';
                               break;
                           case 2:
                               a[i + 2] = '2';
                               break;
                           case 3:
                               a[i + 2] = '3';
                               break;
                           case 4:
                               a[i + 2] = '4';
                               break;
                           case 5:
                               a[i + 2] = '5';
                               break;
                           case 6:
                               a[i + 2] = '6';
                               break;
                           case 7:
                               a[i + 2] = '7';
                               break;
                           case 8:
                               a[i + 2] = '8';
                               break;
                           case 9:
                               a[i + 2] = '9';
                               break;
                           default:
                               break;
                       }

                   }

                   socketWriter.print(a);
                   socketWriter.flush();
                   Thread.sleep(1);

                   // 锟斤拷锟斤拷锟角凤拷锟斤拷20锟街斤拷 16锟斤拷锟斤拷时锟斤拷
                   char[] b = new char[20];
                   b[0] = 0x01;
                   b[1] = 0x0E;
                   SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                   Date curDate = new Date(System.currentTimeMillis());// 锟斤拷取锟斤拷前时锟斤拷
                   String time = formatter.format(curDate);
                   for (int j = 0; j < time.length(); j++) {
                       String string = time.substring(j, j + 1);
                       int m = Integer.parseInt(string);
                       switch (m) {
                           case 0:
                               b[j + 2] = '0';
                               break;
                           case 1:
                               b[j + 2] = '1';
                               break;
                           case 2:
                               b[j + 2] = '2';
                               break;

                           case 3:
                               b[j + 2] = '3';
                               break;
                           case 4:
                               b[j + 2] = '4';
                               break;
                           case 5:
                               b[j + 2] = '5';
                               break;
                           case 6:
                               b[j + 2] = '6';
                               break;
                           case 7:
                               b[j + 2] = '7';
                               break;
                           case 8:
                               b[j + 2] = '8';
                               break;
                           case 9:
                               b[j + 2] = '9';
                               break;
                           default:
                               break;
                       }
                   }
                   socketWriter.print(b);
                   socketWriter.flush();
                   Thread.sleep(1);

                   char[] c = new char[20];
                   c[0] = 0x02;
                   c[1] = 0x28;

                   Log.e("password origin--->", password);

                   for (int i = 0; i < secret.length(); i++) {
                       String string = secret.substring(i, i + 1);
                       int m = Integer.parseInt(string);
                       switch (m) {
                           case 0:
                               c[i + 2] = '0';
                               break;
                           case 1:
                               c[i + 2] = '1';
                               break;
                           case 2:
                               c[i + 2] = '2';
                               break;

                           case 3:
                               c[i + 2] = '3';
                               break;
                           case 4:
                               c[i + 2] = '4';
                               break;
                           case 5:
                               c[i + 2] = '5';
                               break;
                           case 6:
                               c[i + 2] = '6';
                               break;
                           case 7:
                               c[i + 2] = '7';
                               break;
                           case 8:
                               c[i + 2] = '8';
                               break;
                           case 9:
                               c[i + 2] = '9';
                               break;
                           default:
                               break;
                       }
                   }

                   socketWriter.print(c);
                   socketWriter.flush();
                   Thread.sleep(1);
                   Log.e("hahahahahaha", "111");

                   byte[] ch = new byte[20];

                   socketReader.read(ch);

                   byte m = (byte) 0xFF;

                   // String temp;
                   // temp = Integer.toHexString(ch[2]&0xFF);

                   if (ch[2] == m) {
                       result = SOCKET_CONNECT_SUCCESS;
                   } else {
                       result = SOCKET_CONNECT_FAILED;
                   }
                   System.out.println("sucesss!!!");
                   socketReader.close();
                   socketWriter.close();
                   client.close();


               }catch (Exception e) {
                   Log.e("Exception", "Exception");
                   e.printStackTrace();
               }
                return result;
        }

}
