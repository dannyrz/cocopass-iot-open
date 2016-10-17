package com.LuYuanDong.Open.API;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.LuYuanDong.Open.Model.Request;
import com.cocopass.iot.model.CPageRecord;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class GPS extends Terminal{

	public GPS(Request request) throws Exception {
		super(request);
		// TODO Auto-generated constructor stub
	}
	
	public <T> com.LuYuanDong.Open.Model.Response GetLogFromDataBase() throws ParseException
	{
		 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
		 long startTime=jParameters.get("StartTime").getAsLong();
		 long endTime=jParameters.get("EndTime").getAsLong();
		 boolean isOnlySuccessPosition=jParameters.get("IsOnlySuccessPosition").getAsBoolean();
		 int page=jParameters.get("Page").getAsInt();
		 int pageNum=jParameters.get("PageNum").getAsInt();

         com.ludong.bll.GPSInfo bll=new com.ludong.bll.GPSInfo();
         //List<com.ludong.model.GPSInfo> list=gps.GetList(terminalID, startTime, endTime, isOnlySuccessPosition,page,pageNum);
         CPageRecord<T> records=bll.GetPageRecord(terminalID, startTime, endTime, isOnlySuccessPosition, page, pageNum, true);
         response.SetResult(0);
		 response.SetBody(records);
         return response;
	}
	
	public  com.LuYuanDong.Open.Model.Response GetLatestLocationFromCache() throws ParseException{
		 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
		
		//if()
		com.ludong.model.GPSInfo gpsInfo=new com.ludong.model.GPSInfo();	
		float latitude=Float.parseFloat(com.cocopass.helper.CRedis.getMapValue("Terminal:"+terminalID, "Latitude"));
		float longitude=Float.parseFloat(com.cocopass.helper.CRedis.getMapValue("Terminal:"+terminalID, "Longitude"));
		String bDLocation=com.cocopass.helper.CRedis.getMapValue("Terminal:"+terminalID, "BDLocation");
		String gDLocation=com.cocopass.helper.CRedis.getMapValue("Terminal:"+terminalID, "GDLocation");
		long samplingTime=Long.parseLong(com.cocopass.helper.CRedis.getMapValue("Terminal:"+terminalID, "SamplingTime"));
		gpsInfo.SetTerminalID(terminalID);	
		gpsInfo.SetLatitude(latitude);
		gpsInfo.SetLongitude(longitude);
		gpsInfo.setBDLocation(bDLocation);
		gpsInfo.setGDLocation(gDLocation);
		gpsInfo.SetSamplingTime(samplingTime);
		response.SetResult(0);
		response.SetBody(gpsInfo);
        return response;
	}
	/**
	 * 修改停车间隔时间
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的车辆GPSID
	 * @param HeartPonginterval
	 */
	public com.LuYuanDong.Open.Model.Response SetStopedInterval() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		int stopedInterval = jParameters.get("StopedInterval").getAsInt();
		com.cocopass.iot.model.DownMessageResult result = packet.SendSetStopedIntervalPacket(request.GetTimeStamp(),
				terminalID, stopedInterval, url);

		actionLog.SetSamplingTime(new Date().getTime());
		actionLog.SetStatus(result.GetStatus());

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;

	}
	
	/**
	 * 修改震动灵敏度
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的车辆GPSID
	 * @param HeartPonginterval
	 */
	public com.LuYuanDong.Open.Model.Response SetTotalMileage() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		float totalMileage = jParameters.get("TotalMileage").getAsFloat();

		com.cocopass.iot.model.DownMessageResult result = packet.SendSetTotalMileagePacket(request.GetTimeStamp(),
				terminalID, totalMileage, url);

		actionLog.SetSamplingTime(new Date().getTime());
		actionLog.SetStatus(result.GetStatus());

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;

	}
	
	/**
	 * 修改震动灵敏度
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的车辆GPSID
	 * @param HeartPonginterval
	 */
	public com.LuYuanDong.Open.Model.Response SetShakeSensitivity() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		int level = jParameters.get("Level").getAsInt();
		int rate = jParameters.get("Rate").getAsInt();
		com.cocopass.iot.model.DownMessageResult result = packet.SendSetShakeSensitivityPacket(request.GetTimeStamp(),
				terminalID, level, rate, url);

		actionLog.SetSamplingTime(new Date().getTime());
		actionLog.SetStatus(result.GetStatus());

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;

	}
	
	/**
	 * 修改震动灵敏度
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的车辆GPSID
	 * @param HeartPonginterval
	 */
	public com.LuYuanDong.Open.Model.Response SetAlarmMobileNO() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		long mobileNO = jParameters.get("MobileNO").getAsLong();
		com.cocopass.iot.model.DownMessageResult result = packet.SendSetAlarmMobileNOPacket(request.GetTimeStamp(),
				terminalID, mobileNO, url);

		actionLog.SetSamplingTime(new Date().getTime());
		actionLog.SetStatus(result.GetStatus());

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;

	}
	
	/**
	 * 批量增加GPS位置数据到数据库
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param List<GPSInfo>
	 */
	public com.LuYuanDong.Open.Model.Response AddGPSInfoToDatabase() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		JsonArray list = jParameters.getAsJsonArray();
		List<com.ludong.model.GPSInfo> listGPSInfo = gson.fromJson(list,
				new TypeToken<List<com.ludong.model.GPSInfo>>() {
				}.getType());
		int result = com.ludong.bll.GPSInfo.AddListToDB(listGPSInfo);
		if (result > 0)
			response.SetResult(0);
		return response;
	}



}
