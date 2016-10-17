package com.LuYuanDong.Open.API;

import java.text.ParseException;
import java.util.List;

import com.cocopass.iot.model.CPageRecord;
import com.google.gson.JsonObject;

public class Push {
	JsonObject jParameters = null;
	com.LuYuanDong.Open.Model.Request request;
	public Push(com.LuYuanDong.Open.Model.Request request) throws Exception {
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
         com.cocopass.bll.PushData bll=new com.cocopass.bll.PushData();
         //List<com.cocopass.iot.model.PushData> list=bll.GetList( startTime, endTime,page,pageNum);
         CPageRecord<T> records=bll.GetPageRecord(startTime, endTime, page, pageNum, true);
		 response.SetResult(0);
		 response.SetBody(records);
         return response;
	}
}
