package com.LuYuanDong.Open.DAL;
import java.sql.*;

import org.apache.log4j.Logger;
import org.dave.common.database.access.DataAccess;
 

import com.LuYuanDong.Open.Service;
import com.LuYuanDong.Open.DAL.MySql;
//import com.cocopass.helper.MySql;
public class App   extends DataAccess {
	private static final Logger LOG = Logger.getLogger(Service.class.getName());
	public App(Connection conn) {
		super(conn);
	}
   
	
	public com.LuYuanDong.Open.Model.App GetModel(String key) {
		return super.queryForObject("SELECT * FROM App WHERE `Key`=?", 
				new com.LuYuanDong.Open.DBService.AppConvert(), key);
	}
	
	
 
	@Deprecated
public static com.LuYuanDong.Open.Model.App GetModelByCondition(String condition)
  {
	  com.LuYuanDong.Open.Model.App app=new com.LuYuanDong.Open.Model.App();
	  
	  ResultSet rs = null;  
	 
	        try {  
	             String sql = "{call proce_QueryApp(?)}";  
	             String[] in = { condition };  
	             // Integer[] out ={Types.INTEGER};  
	            CallableStatement cs = (CallableStatement) MySql.callProcInput(  sql, in);  
	            rs = cs.executeQuery();  
	            while (rs.next()) {  
	            	app.SetDescription(rs.getString("Description"));
	            	app.SetHost(rs.getString("host")); 
	            	app.SetID(rs.getInt("ID"));
	            	app.SetIP(rs.getString("IP"));
	            	app.SetKey(rs.getString("Key"));
	            	app.SetName(rs.getString("Name"));
	            	app.SetNoticeURL(rs.getString("noticeURL"));
	            	app.SetPort(rs.getInt("Port"));
	            	app.SetProcessname(rs.getString("Processname"));
	            	app.SetSecret((rs.getString("Secert"))); //！！！！此处数据表需要修改
	            	app.SetState(rs.getInt("State"));
	            	app.SetType(rs.getInt("Type"));
	            	app.SetAuthMode(rs.getInt("AuthMode"));
	            }  
	         } catch (Exception e) {  
	        	 LOG.error(e.getMessage());
	             throw new RuntimeException(e.getMessage());  
	       } finally {  
	    	   MySql.close(rs, MySql.getCs(), MySql.getConn());  
	         }  

	  return app;
	  
	  
	  //协议定义自动锁车延长时间
  }
  
  
  
}
