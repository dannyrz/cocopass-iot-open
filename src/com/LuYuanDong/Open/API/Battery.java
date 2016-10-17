package com.LuYuanDong.Open.API;

import java.text.ParseException;
import java.util.List;

import com.LuYuanDong.Open.Model.Request;
import com.cocopass.iot.model.CPageRecord;

public class Battery extends Terminal{

	public Battery(Request request) throws Exception {
		super(request);
		// TODO Auto-generated constructor stub
	}

	
	public <T> com.LuYuanDong.Open.Model.Response GetLogFromDataBase() throws ParseException
	{
		 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
		 long startTime=jParameters.get("StartTime").getAsLong();
		 long endTime=jParameters.get("EndTime").getAsLong();
		 int page=jParameters.get("Page").getAsInt();
		 int pageNum=jParameters.get("PageNum").getAsInt();

         com.ludong.bll.BatteryInfo bll=new com.ludong.bll.BatteryInfo();
         CPageRecord<T> records=bll.GetPageRecord(terminalID, startTime, endTime,page,pageNum,true);
		 response.SetResult(0);
		 response.SetBody(records);
         return response;
	}
}
