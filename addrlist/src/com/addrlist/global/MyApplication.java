package com.addrlist.global;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.addrlist.model.User;


public class MyApplication extends Application {

	//登录用户
    public User mUser = null;
    public String cityid = Constant.DEFAULTCITYID;
    public String cityName = Constant.DEFAULTCITYNAME;
    public boolean userPasswordRemember = false;
    public boolean ad = false;
    public boolean firstStart = true;
    
	@Override
	public void onCreate() {
		super.onCreate();
		initLoginParams();
	}
	
	/**
	 * 上次登录参数
	 * @throws 
	 * @date：2012-7-17 下午06:07:29
	 * @version v1.0
	 */
	private void initLoginParams() {
		SharedPreferences  sp = getSharedPreferences(Constant.sharePath, Context.MODE_PRIVATE);
		String userName = sp.getString(Constant.USERNAMECOOKIE, null);
		String userPwd = sp.getString(Constant.USERPASSWORDCOOKIE, null);
		Boolean userPwdRemember = sp.getBoolean(Constant.USERPASSWORDREMEMBERCOOKIE, false);
		if(userName!=null){
			mUser = new User();
			mUser.name = userName;
			mUser.password = userPwd;
			userPasswordRemember = userPwdRemember;
		}
	}

}
