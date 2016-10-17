package com.LuYuanDong.Open.DBService;

import org.dave.common.BaseService;
import org.dave.common.database.DatabaseTransaction;
import org.dave.common.database.access.DataAccess;
 

public class App extends BaseService{
	
	com.LuYuanDong.Open.DAL.App dal=null;
	public App(DatabaseTransaction trans) {
		super(trans);
		dal=new com.LuYuanDong.Open.DAL.App(super.getConnection());
	}

	public App() {
		super();
	}
	
	
	
	
	/**
	 * Í¨¹ýid²éÑ¯
	 * @param id
	 * @return
	 */
	public com.LuYuanDong.Open.Model.App GetModel(String key) {
		return dal.GetModel(key);
	}
}
