package com.addrlist.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.addrlist.adapter.MyListViewAdapter;
import com.addrlist.util.DialogHelper;
import com.addrlist.util.UpdateManager;

public class ManageActivity extends AbActivity {
	private MyListViewAdapter myListViewAdapter = null;
	private List<Map<String, Object>> list = null;
	private ListView mListView = null;
	private UpdateManager updateMan;
    String version = null;
    private ProgressDialog updateProgressDialog;
    
	// 自动更新回调函数
    UpdateManager.UpdateCallback appUpdateCb = new UpdateManager.UpdateCallback() 
    {

        public void downloadProgressChanged(int progress) {
            if (updateProgressDialog != null && updateProgressDialog.isShowing()) {
                updateProgressDialog.setProgress(progress);
            }
        }

        //下载失败
        public void downloadCompleted(Boolean sucess, CharSequence errorMsg) {
			if (updateProgressDialog != null && updateProgressDialog.isShowing()) {
				updateProgressDialog.dismiss();
			}
			if (sucess) {
				updateMan.update();
			} else {
				DialogHelper.Confirm(ManageActivity.this,
						getText(R.string.dialog_error_title),
						getText(R.string.dialog_downfailed_msg).toString() + errorMsg.toString(),
						getText(R.string.dialog_downfailed_btndown),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								updateMan.downloadPackage();
							}
						}, getText(R.string.dialog_downfailed_btnnext), null);
			}
        }

        public void downloadCanceled(){

        }

        //检查是否有版本更新
        public void checkUpdateCompleted(Boolean sucess,Boolean hasUpdate,CharSequence updateInfo,CharSequence errorMsg) {
        	if(sucess){
				if (hasUpdate) {
					DialogHelper.Confirm(ManageActivity.this,
							getText(R.string.dialog_update_title),
							getText(R.string.dialog_update_msg).toString() +updateInfo+ getText(R.string.dialog_update_msg2).toString(), 
							getText(R.string.dialog_update_btnupdate),
							new DialogInterface.OnClickListener() {
	
								public void onClick(DialogInterface dialog, int which) {
									updateProgressDialog = new ProgressDialog( ManageActivity.this);
									updateProgressDialog.setMessage(getText(R.string.dialog_downloading_msg));
									updateProgressDialog.setIndeterminate(false);
									updateProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
									updateProgressDialog.setMax(100);
									updateProgressDialog.setProgress(0);
									updateProgressDialog.show();
	
									updateMan.downloadPackage();
								}
							},getText( R.string.dialog_update_btnnext), null);
				}else{
					Toast.makeText(ManageActivity.this, getText(R.string.dialog_update_msg3).toString(), Toast.LENGTH_LONG).show(); 
				}
	        }else{
	        	Toast.makeText(ManageActivity.this, getText( R.string.dialog_updatefailed_msg).toString()+errorMsg.toString(), Toast.LENGTH_LONG).show(); 
	        }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.manage);
        AbTitleBar mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(R.string.about);
        mAbTitleBar.setLogo(R.drawable.button_selector_back);
        mAbTitleBar.setTitleLayoutBackground(R.drawable.top_bg);
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoLine(R.drawable.line);
        //mAbTitleBar.setVisibility(View.GONE);
	    
        mAbTitleBar.getLogoView().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        //////////////////////////////////
		//获取ListView对象
        mListView = (ListView)this.findViewById(R.id.mListView);
        
        //ListView数据
    	list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map = new HashMap<String, Object>();
    	
    	map = new HashMap<String, Object>();
    	map.put("itemsIcon",R.drawable.image_bg);
    	map.put("itemsTitle", "检查更新");
    	map.put("itemsText", "");
    	list.add(map);
    	
    	
    	//使用自定义的Adapter
    	myListViewAdapter = new MyListViewAdapter(this, list,R.layout.list_items,
				new String[] { "itemsIcon", "itemsTitle","itemsText" }, new int[] { R.id.itemsIcon,
						R.id.itemsTitle,R.id.itemsText });
    	mListView.setAdapter(myListViewAdapter);
    	//item被点击事件
    	mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
				showToastInThread("点击："+position);
				updateMan = new UpdateManager(ManageActivity.this,appUpdateCb);
				updateMan.checkUpdate();
			}
		});    	
    }
    
}


