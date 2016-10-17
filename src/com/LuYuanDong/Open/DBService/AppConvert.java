package com.LuYuanDong.Open.DBService;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.dave.common.database.convert.ResultConverter;


public class AppConvert implements ResultConverter<com.LuYuanDong.Open.Model.App>{
	@Override
	public com.LuYuanDong.Open.Model.App convert(ResultSet rs) throws SQLException {
		com.LuYuanDong.Open.Model.App app = new com.LuYuanDong.Open.Model.App();
		app.SetDescription(rs.getString("Description"));
    	app.SetHost(rs.getString("host")); 
    	app.SetID(rs.getInt("ID"));
    	app.SetIP(rs.getString("IP"));
    	app.SetKey(rs.getString("Key"));
    	app.SetName(rs.getString("Name"));
    	app.SetNoticeURL(rs.getString("noticeURL"));
    	app.SetPort(rs.getInt("Port"));
    	app.SetProcessname(rs.getString("Processname"));
    	app.SetSecret((rs.getString("Secret"))); 
    	app.SetState(rs.getInt("State"));
    	app.SetType(rs.getInt("Type"));
    	app.SetAuthMode(rs.getInt("AuthMode"));
		return app;
	}
}
