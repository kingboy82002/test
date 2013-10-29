package com.addrlist.db.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.addrlist.db.DBUserInfoHelper;
import com.addrlist.model.UserInfo;

public class UserInfoDao extends AbDBDaoImpl<UserInfo> {

	public UserInfoDao(Context context) {
		super(new DBUserInfoHelper(context),UserInfo.class);
	}

}
