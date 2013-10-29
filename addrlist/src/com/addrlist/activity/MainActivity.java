package com.addrlist.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.addrlist.adapter.MyListViewAdapter;
import com.addrlist.db.dao.UserInfoDao;
import com.addrlist.global.MyApplication;
import com.addrlist.model.UserInfo;


public class MainActivity extends AbActivity {

	private MyApplication application = null;
	private EditText search_et = null;
	private AbTitleBar mAbTitleBar = null;
	private MyListViewAdapter myListViewAdapter = null;
	private List<Map<String, Object>> list = null;
	private ListView mListView = null;
	private ArrayList<UserInfo> aList = new ArrayList<UserInfo>();
	private ArrayList<UserInfo> newList = new ArrayList<UserInfo>();
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.main);
        application = (MyApplication)abApplication;
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(R.string.app_name);
        mAbTitleBar.setTitleLayoutBackground(R.drawable.top_bg);
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setTitleLayoutGravity(Gravity.CENTER, Gravity.CENTER);
		initTitleRightLayout();
		
		//获取ListView对象
        mListView = (ListView)this.findViewById(R.id.mListView);
        
        //ListView数据
    	list = new ArrayList<Map<String, Object>>();
//    	Map<String, Object> map = new HashMap<String, Object>();
//    	
//    	map = new HashMap<String, Object>();
//    	map.put("itemsIcon",R.drawable.image_bg);
//    	map.put("itemsTitle", "张三");
//    	map.put("itemsText", "13575490835");
//    	list.add(map);
//    	
//    	map = new HashMap<String, Object>();
//    	map.put("itemsIcon",R.drawable.image_bg);
//    	map.put("itemsTitle", "李四");
//    	map.put("itemsText", "18658867571");
//    	list.add(map);
    	
    	
    	//使用自定义的Adapter
    	myListViewAdapter = new MyListViewAdapter(this, list,R.layout.list_items,
				new String[] { "itemsIcon", "itemsTitle","itemsText" }, new int[] { R.id.itemsIcon,
						R.id.itemsTitle,R.id.itemsText });
    	mListView.setAdapter(myListViewAdapter);
    	
    	
    	showProgressDialog("正在加载数据...");
    	new Thread(new Runnable() {
			@Override
			public void run() {
				//getUserInfoList();
				Message message = new Message(); 
	            message.what = 1;
				mhandler.sendMessage(message);
			}
		}).start();
    	

	}
	
	Handler mhandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				showToast("执行成功");
		    	UserInfo bean1 = new UserInfo();
		    	bean1.setUsername("张三");
		    	bean1.setMobile("13575490835");
		    	aList.add(bean1);
		    	
		    	UserInfo bean2 = new UserInfo();
		    	bean2.setUsername("李四");
		    	bean2.setMobile("18658867571");
		    	aList.add(bean2);
				removeProgressDialog();
				break;
			}
		}
		
	};

    private void initTitleRightLayout(){
    	mAbTitleBar.clearRightView();
    	View rightViewApp = mInflater.inflate(R.layout.app_btn, null);
    	mAbTitleBar.addRightView(rightViewApp);
    	Button appBtn = (Button)rightViewApp.findViewById(R.id.appBtn);
    	
    	appBtn.setOnClickListener(new View.OnClickListener(){

 			@Override
 			public void onClick(View v) {
 				//Intent intent = new Intent(MainActivity.this,DankeActivity.class); 
 				//startActivity(intent);
 			}
         });
    	/**
    	 * 搜索框
    	 */
    	search_et = (EditText)findViewById(R.id.search_et);
    	search_et.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(search_et.getText().toString().trim().equals(getApplication().getString(R.string.search_tip))){
					search_et.setText("");
					search_et.setTextColor(Color.BLACK);
				}else{
					
				}
			}
    	});
    	search_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				newList.clear();  
	            if (search_et.getText() != null && search_et.getText().toString().length()>2) {  
	                String input_info = search_et.getText().toString();  
	                newList = getNewData(input_info);  
	                
	            	Map<String, Object> map = null;
	            	list.clear();
	                for(int i=0;i<newList.size();i++){
	                	UserInfo bean = newList.get(i);
		                map = new HashMap<String, Object>();
		            	map.put("itemsIcon",R.drawable.image_bg);
		            	map.put("itemsTitle", bean.getUsername());
		            	map.put("itemsText", bean.getMobile());
		            	list.add(map);
	                }
	            	myListViewAdapter = new MyListViewAdapter(MainActivity.this, list,R.layout.list_items,
	        				new String[] { "itemsIcon", "itemsTitle","itemsText" }, new int[] { R.id.itemsIcon,
	        						R.id.itemsTitle,R.id.itemsText });
	            	mListView.setAdapter(myListViewAdapter);
	                
	                 
	            }
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
    }
    
    
    //当editetext变化时调用的方法，来判断所输入是否包含在所属数据中  
    private ArrayList<UserInfo> getNewData(String input_info) {  
        //遍历list  
        for (int i = 0; i < aList.size(); i++) {  
            UserInfo domain = aList.get(i);  
            //如果遍历到的名字包含所输入字符串  
            if (domain.getMobile().contains(input_info)) {
            	//赋值为另一个变量,可以方便修改里面的内容
            	UserInfo ui = new UserInfo();
            	ui.setUsername(domain.getUsername());
            	ui.setMobile(domain.getMobile().replaceAll(input_info, "<font color='red'>"+input_info+"</font>"));
                newList.add(ui);  
            }  
        }  
        return newList;  
    }

    public void getUserInfoList(){
		//初始化数据库操作实现类
    	UserInfoDao userDao = new UserInfoDao(MainActivity.this);
    	//(1)获取数据库
    	userDao.startReadableDatabase(false);
    	//(2)执行查询
    	List<UserInfo> userList = userDao.queryList(null, null, null, null, null, null, null);
    	//int totalCount = userDao.queryCount(null, null);
    	int totalCount = userList.size();
    	showToastInThread("记录数："+totalCount);
    	//(3)关闭数据库
    	userDao.closeDatabase(false);	
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		initTitleRightLayout();
	}
	
	public void onPause() {
		super.onPause();
	}
	
	private static Boolean isExit = false;  
    private static Boolean hasTask = false;  
    Timer tExit = new Timer();  
    TimerTask task = new TimerTask() {  
        @Override 

        public void run() {  
            isExit = true;  
            hasTask = true;  
        }  
    };  

	
	@Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            if(isExit == false ) {  
                isExit = true;  
                showToast("再按一次退出程序");  
                if(!hasTask) {  
                    tExit.schedule(task, 2000);  
                }  
            } else {  
                finish();  
                System.exit(0);  
            }  
        }  
        return false;  

    }
}
