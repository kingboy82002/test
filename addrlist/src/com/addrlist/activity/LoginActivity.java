package com.addrlist.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.addrlist.global.Constant;
import com.addrlist.global.MyApplication;
import com.addrlist.model.User;

public class LoginActivity extends AbActivity {
	
	private EditText userName = null;
	private EditText userPwd = null;
	private User mUser = null;
	private MyApplication application;
	private AbTitleBar mAbTitleBar = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.login);
        application = (MyApplication)abApplication;
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(R.string.login);
        //mAbTitleBar.setLogo(R.drawable.button_selector_back);//返回按钮
        mAbTitleBar.setTitleLayoutBackground(R.drawable.top_bg);
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoLine(R.drawable.line);
        mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.CENTER);
        
        userName = (EditText)this.findViewById(R.id.userName);
		userPwd = (EditText)this.findViewById(R.id.userPwd);
		CheckBox checkBox = (CheckBox) findViewById(R.id.login_check);
		userName.setText("测试");
		userPwd.setText("123456");
		   
	    Button login = (Button)this.findViewById(R.id.loginBtn);
	    Button register = (Button)this.findViewById(R.id.registerBtn);
		login.setOnClickListener(new LoginOnClickListener());
				
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				showDialog("提示","请进入网站注册",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
			}
		});
        
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = abSharedPreferences.edit();
				editor.putBoolean(Constant.USERPASSWORDREMEMBERCOOKIE, isChecked);
				editor.commit();
				application.userPasswordRemember = true;
			}
		});
        String name = abSharedPreferences.getString(Constant.USERNAMECOOKIE, "");
        String password = abSharedPreferences.getString(Constant.USERPASSWORDCOOKIE, "");
		boolean userPwdRemember = abSharedPreferences.getBoolean(Constant.USERPASSWORDREMEMBERCOOKIE, false);
		if(userPwdRemember){
			userName.setText(name);
			userPwd.setText(password);
			checkBox.setChecked(true);
		}else{
			userName.setText("");
			userPwd.setText("");
			checkBox.setChecked(false);
		}
    }
    
   public class  LoginOnClickListener implements View.OnClickListener{
		
		@Override
		public void onClick(View v) {
			final String mStr_name = userName.getText().toString();
			final String mStr_pwd = userPwd.getText().toString();
			if (TextUtils.isEmpty(mStr_name)) {
				userName.setError(getResources().getText(R.string.error_name));
				userName.setFocusable(true);
				userName.requestFocus();
				return;
			}
			if (TextUtils.isEmpty(mStr_pwd)) {
				userPwd.setError(getResources().getText(R.string.error_pwd));
				userPwd.setFocusable(true);
				userPwd.requestFocus();
				return;
			}
			showDialog(0);
			
			application.mUser = new User();
			if(application.mUser !=null){
				if(application.userPasswordRemember){
					Editor editor = abSharedPreferences.edit();
					editor.putString(Constant.USERNAMECOOKIE, mStr_name);
					editor.putString(Constant.USERPASSWORDCOOKIE,mStr_pwd);
					application.firstStart = false;
					editor.putBoolean(Constant.FIRSTSTART, false);
					editor.commit();
				}else{
					Editor editor = abSharedPreferences.edit();
					application.firstStart = false;
					editor.putBoolean(Constant.FIRSTSTART, false);
					editor.commit();
				}
			}
			
			showToast("正在登陆");
			Intent intent = new Intent(LoginActivity.this,MainActivity.class); 
			startActivity(intent);
			finish();
		}
	}
    
}


