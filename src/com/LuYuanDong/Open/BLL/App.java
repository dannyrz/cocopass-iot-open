package com.LuYuanDong.Open.BLL;

import org.dave.common.database.DatabaseTransaction;

public class App {
//	public static com.LuYuanDong.Open.Model.App GetNormalModel(String key,String ip)
//	{
//		  String condition="`Key`='"+key+"' AND state=1 AND IP like '%"+ip+"%'";
//		  //System.out.print(condition);
//		  return com.LuYuanDong.Open.DAL.App.GetModel(condition);
//	}
	
	public static com.LuYuanDong.Open.Model.App GetModel(String key)
	{
		DatabaseTransaction trans = new DatabaseTransaction(false,"connectionString2");
		com.LuYuanDong.Open.DBService.App app=new com.LuYuanDong.Open.DBService.App(trans);
		com.LuYuanDong.Open.Model.App model=app.GetModel(key);
    	trans.close();
		return model;
	}
}
