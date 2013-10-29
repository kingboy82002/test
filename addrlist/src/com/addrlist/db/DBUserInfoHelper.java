package com.addrlist.db;

import android.content.Context;

import com.ab.db.orm.AbDBHelper;
import com.addrlist.model.UserInfo;

public class DBUserInfoHelper extends AbDBHelper {

	private static final String DBNAME = "addrlist.db";
    
	private static final int DBVERSION = 6;

	private static final Class<?>[] clazz = { UserInfo.class};

	public DBUserInfoHelper(Context context) {
		super(context, DBNAME, null, DBVERSION, clazz);
	}

}
