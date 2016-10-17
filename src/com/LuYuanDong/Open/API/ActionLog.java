package com.LuYuanDong.Open.API;

import java.text.ParseException;
import java.util.List;

import com.cocopass.iot.model.CPageRecord;
import com.google.gson.JsonObject;

public class ActionLog {
	JsonObject jParameters = null;
	com.LuYuanDong.Open.Model.Request request;
	public ActionLog(com.LuYuanDong.Open.Model.Request request) throws Exception {
		this.request = request;
		this.jParameters = request.GetParameters();

	}
	public <T> com.LuYuanDong.Open.Model.Response GetLogFromDataBase() throws ParseException
	{
		 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
		 long startTime=jParameters.get("StartTime").getAsLong();
		 long endTime=jParameters.get("EndTime").getAsLong();
		 int page=jParameters.get("Page").getAsInt();
		 int pageNum=jParameters.get("PageNum").getAsInt();
		 com.ludong.bll.ActionLog bll = new com.ludong.bll.ActionLog();
		 String condition=" SamplingTime Between "+startTime+" AND "+endTime; 
         //List<com.ludong.model.ActionLog> list=bll.GetList(condition,page,pageNum);
		 CPageRecord<T> records=bll.GetPageRecord(condition, page, pageNum, true);
		 response.SetResult(0);
		 response.SetBody(records);
         return response;
	}
}
