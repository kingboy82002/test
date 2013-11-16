package com.addrlist.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

public class UpdateManager{
	private static final String TAG = "UpdateManager";
	private Context ctx;
	private static String PATH_APP_DOWNLOAD ;//   "/mnt/innerDisk/";//从服务器上下载apk存放文件夹
	private static String PATH_SDCARD_DOWNLOAD = "/sdcard/download/";//从服务器上下载apk存放文件夹
	private String curVersion;
	private String newVersion;
	private int curVersionCode;
	private int newVersionCode;
	private String updateInfo;
	private boolean hasNewVersion;
	private Boolean canceled;	
	private UpdateCallback callback;
	private int progress;
	public String UPDATE_DOWNURL = "";	
	
    public static final String UPDATE_SERVER = "http://192.168.1.143:808/jxapcd/";
    public static final String UPDATE_APKNAME = "addrlist.apk";
    public static final String UPDATE_VERJSON = "ver.json";
    public static final String UPDATE_SAVENAME = "addrlist.apk";
    
	private static final int UPDATE_CHECKFAILED = 0;
	private static final int UPDATE_CHECKCOMPLETED = 1;
	private static final int UPDATE_DOWNLOADING = 2; 
	private static final int UPDATE_DOWNLOAD_ERROR = 3; 
	private static final int UPDATE_DOWNLOAD_COMPLETED = 4; 
	private static final int UPDATE_DOWNLOAD_CANCELED = 5;    
	
	private Dialog mDialog;

    
	public UpdateManager(Context context, UpdateCallback updateCallback) {
        this.ctx = context;
        callback = updateCallback;
        canceled = false;
        PATH_APP_DOWNLOAD = ctx.getDir("download", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE).getAbsolutePath();
        getCurVersion();
    }
	
    public String getNewVersionName()
    {
        return newVersion;
    }
    
    public String getUpdateInfo()
    {
        return updateInfo;
    }	
	
	private void getCurVersion() {
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo( ctx.getPackageName(), 0);
            curVersion = pInfo.versionName;
            curVersionCode = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
            curVersion = "1.0";
            curVersionCode = 1;
        }
    }	
	
    public void checkUpdate() {        
        hasNewVersion = false;
        //showRoundProcessDialog(ctx, R.layout.loading_process_dialog_anim);
        new Thread(){

            @Override
            public void run() {
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>getServerVerCode() ");
                try {
                	String verjson = "";
//                	String retmsg = callws("{\"transid\":\"0000\"}");
//                	String retmsg = "{\"ALInfoError\":{\"Sucess\":\"1\"},\"updateverson\":[{\"verCode\":2,\"verName\":\"1.3\",\"updateInfo\":\"更新\",\"downurl\":\"http://192.168.1.143:808/mapp/bjgis.apk\"}]}";
                	String retmsg = HttpUtils.sendGet(UPDATE_SERVER+UPDATE_VERJSON, "utf-8");
                	
	 				JSONObject para = new JSONObject(retmsg);
	 				if(para.getJSONObject("ALInfoError").getString("Sucess").equals("1")){
	 					//服务端接收成功
	 					verjson = para.getString("updateverson");
	 					
	 					//String verjson = NetHelper.httpStringGet(UPDATE_CHECKURL);
	                    Log.i(TAG, verjson);
	                    JSONArray array = new JSONArray(verjson);

	                    if (array.length() > 0) {
	                        JSONObject obj = array.getJSONObject(0);
	                        newVersionCode = Integer.parseInt(obj.getString("verCode"));
	                        newVersion = obj.getString("verName");
	                        updateInfo = obj.getString("updateInfo");
	                        UPDATE_DOWNURL = UPDATE_SERVER+UPDATE_APKNAME;
	                        Log.i(TAG, "newVersionCode" + newVersionCode);
	                        Log.i(TAG, "newVersion" + newVersion);
	                        Log.i(TAG, "UPDATE_DOWNURL:" + UPDATE_DOWNURL);
	                        if (newVersionCode > curVersionCode) {
	                            hasNewVersion = true;
	                        }
	                    }
	                    
	                    updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);//发送版本更新检查结束信息
	 				}else{
	 					Log.e(TAG, para.getJSONObject("ALInfoError").getString("Description"));
	 					updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_CHECKFAILED,para.getJSONObject("ALInfoError").getString("Description")));
	 				}
                } catch (Exception e) {
                	updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_CHECKFAILED,e.getMessage()));
                }
            };
        }.start();

    }
    
    public void showRoundProcessDialog(Context mContext, int layout){
        OnKeyListener keyListener = new OnKeyListener()
        {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_SEARCH)
                {
                    return true;
                }
                return false;
            }
        };

        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.setOnKeyListener(keyListener);
        mDialog.show();
        // 注意此处要放在show之后 否则会报异常
        mDialog.setContentView(layout);
    }
    
    public void update() {
    	try{
	    	String path="";
	    	if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
	        	path = PATH_SDCARD_DOWNLOAD; 
	        }else{
	        	path = PATH_APP_DOWNLOAD;
	        }
	    	
	    	
	    	File apkfile = new File(path,UPDATE_SAVENAME);  
	        if (!apkfile.exists()) {
	         	return;
	        }
	         
	        //设置文件权限属性
	        Log.i(TAG,apkfile.getAbsolutePath());
	        String[] command = {"chmod", "777", apkfile.getAbsolutePath()}; 
	        ProcessBuilder builder = new ProcessBuilder(command); 
	        try { 
	             builder.start(); 
	        } catch (IOException e) { 
	             // TODO Auto-generated catch block 
	             e.printStackTrace(); 
	        } 
	         
	        //启动apk安装
	        Intent intent = new Intent(Intent.ACTION_VIEW);      
	        intent.setDataAndType( Uri.fromFile(new File(path, UPDATE_SAVENAME)), "application/vnd.android.package-archive");
	        ctx.startActivity(intent);
    	}catch(Exception ex){
    		Log.e(TAG, ex.getMessage());
    	}
    }
    
    public void downloadPackage() {
    	new Thread() {
    		@Override  
            public void run() {  
                try {
                	String path = "";
                    URL url = new URL(UPDATE_DOWNURL);  
                  
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                    conn.connect();  
                    int length = conn.getContentLength();  
                    InputStream is = conn.getInputStream();  
                                        
                    
                    //判断是否存在sd卡
                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                    	path = PATH_SDCARD_DOWNLOAD; 
                    }else{
                    	path = PATH_APP_DOWNLOAD;
                    }
                    
                    File dirfile = new File(path);
                    if(!dirfile.exists()){
                		dirfile.mkdir();
                	}
                    
                    File ApkFile = new File(path,UPDATE_SAVENAME);
                    
                    if(ApkFile.exists()){
                        ApkFile.delete();
                    }                        
                    
                    FileOutputStream fos = new FileOutputStream(ApkFile);  
                     
                    int count = 0;  
                    byte buf[] = new byte[512];  
                      
                    do{                              
                        int numread = is.read(buf);  
                        count += numread;  
                        progress =(int)(((float)count / length) * 100);  
                       
                        updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING)); 
                        if(numread <= 0){
                            updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
                            break;  
                        }  
                        fos.write(buf,0,numread);  
                    }while(!canceled);  
                    if(canceled)
                    {
                        updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
                    }
                    fos.close();  
                    is.close();  
                } catch (MalformedURLException e) {  
                    e.printStackTrace();                     
                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                } catch(IOException e){  
                    e.printStackTrace();                      
                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                }                    
            } 
        }.start();
    }

    public void cancelDownload()
    {
        canceled = true;
    }
    
    Handler updateHandler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
            case UPDATE_CHECKFAILED:
            	callback.checkUpdateCompleted(false,false,"",msg.obj.toString());
            	break;
            case UPDATE_CHECKCOMPLETED:
                callback.checkUpdateCompleted(true,hasNewVersion, newVersion,"");
                break;
            case UPDATE_DOWNLOADING:                
                callback.downloadProgressChanged(progress);
                break;
            case UPDATE_DOWNLOAD_ERROR:                
                callback.downloadCompleted(false, msg.obj.toString());
                break;
            case UPDATE_DOWNLOAD_COMPLETED:                
                callback.downloadCompleted(true, "");
                break;
            case UPDATE_DOWNLOAD_CANCELED:                
                callback.downloadCanceled();
            default:
                break;
            }
        }
    };

    public interface UpdateCallback {
        public void checkUpdateCompleted(Boolean sucess,Boolean hasUpdate,CharSequence updateInfo,CharSequence errorMsg);
        public void downloadProgressChanged(int progress);
        public void downloadCanceled();
        public void downloadCompleted(Boolean sucess, CharSequence errorMsg);
    }    
}
