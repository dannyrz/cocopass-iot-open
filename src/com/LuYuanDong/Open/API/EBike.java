package com.LuYuanDong.Open.API;

import com.LuYuanDong.Open.Config;
import com.LuYuanDong.Open.Model.Request;
import com.google.gson.JsonObject;

public class EBike extends Terminal{

	long eBikeTerminalID=0;
	long stationTerminalID=0; 
	int pilePort=0;
	
	public EBike(Request request) throws Exception {
		super(request);
		if(jParameters.has("EBikeTerminalID"))
		   eBikeTerminalID=jParameters.get("EBikeTerminalID").getAsLong();
		if(jParameters.has("StationTerminalID"))
			stationTerminalID=jParameters.get("StationTerminalID").getAsLong();
		if(jParameters.has("PilePort"))
		   pilePort=jParameters.get("PilePort").getAsInt();
		
		if(eBikeTerminalID>0) //只要是2.0的车 配的肯定是2.0的桩
		{
		   version= com.ludong.iot.PacketBase.GetVersionByTerminalID(eBikeTerminalID);
		   packet=com.ludong.iot.PacketFactory.GetPacketInstance(version, null);
		   url=GetTranseURL(eBikeTerminalID);
		}
		
		SendToQueen(eBikeTerminalID);
	}

	//预约
	public com.LuYuanDong.Open.Model.Response Subscribe()
	{
		return CofirmOrCancelSubscribe((byte) 1);
	}
	//取消预约
	public com.LuYuanDong.Open.Model.Response CancelSubscribe()
	{
    	return CofirmOrCancelSubscribe((byte) 0);
	}
	
	com.LuYuanDong.Open.Model.Response CofirmOrCancelSubscribe(byte CofirmOrCancel)
	{
		byte tailLightPowerOn=0;
		if(CofirmOrCancel==1)
		   tailLightPowerOn=1;
		return SwitchEBikeTailLightPower(tailLightPowerOn);
	}
	
	
	com.LuYuanDong.Open.Model.Response SwitchEBikeTailLightPower(byte onORoff)
	{
		com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
		 
//		long eBikeTerminalID=jo.get("EBikeTerminalID").getAsLong();
//		long pileTerminalID=jo.get("PileTerminalID").getAsLong(); 
//		int pilePort=jo.get("PilePort").getAsInt();
		com.cocopass.iot.model.DownMessageResult result=packet.SendSwitchEBikeTailLightPowerPacket(request.GetTimeStamp(),eBikeTerminalID, onORoff, url);
		response.SetResult(0);
    	JsonObject jbody=new JsonObject();
    	jbody.add("Parameters", jParameters); 
    	jbody.addProperty("Status", result.GetStatus()); 
    	response.SetBody(jbody);
    	return response;
	}
	
	
	//寻车
	public com.LuYuanDong.Open.Model.Response Find()
	{
		com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
		byte on=1;
//		long eBikeTerminalID=jo.get("EBikeTerminalID").getAsLong();
		com.cocopass.iot.model.DownMessageResult result=packet.SendSwitchEBikeTurnLightFlashPacket(request.GetTimeStamp(),eBikeTerminalID, on, url);
		response.SetResult(0);
    	JsonObject jbody=new JsonObject();
    	jbody.add("Parameters", jParameters); 
    	jbody.addProperty("Status", result.GetStatus()); 
    	response.SetBody(jbody);
    	return response;
	}
	
	//取车
		public com.LuYuanDong.Open.Model.Response Get() throws Exception
		{
//			com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
//			byte ebikePowerOn=1;
//			long eBikeTerminalID=jo.get("EBikeTerminalID").getAsLong();
//			long pileTerminalID=jo.get("PileTerminalID").getAsLong(); 
//			int pilePort=jo.get("PilePort").getAsInt();
//			com.cocopass.iot.model.DownMessageResult result=packet.SendSwitchEBikePowerPacket(eBikeTerminalID, ebikePowerOn, url);
//			response.SetResult(0);
//	    	JsonObject jbody=new JsonObject();
//	    	jbody.add("Parameters", jo); 
//	    	jbody.addProperty("Status", result.GetStatus()); 
//	    	response.SetBody(jbody);
//	    	return response;
			OnPower();
			Station ep=new Station(request);
	    	return ep.OpenPile();
		}
		
		//开启电源
		public com.LuYuanDong.Open.Model.Response OnPower()
		{
			return Power((byte)1);
		}
		
		//关闭电源
		public com.LuYuanDong.Open.Model.Response OffPower()
		{
			return Power((byte)0);
		}
	  
		//开关电源
		com.LuYuanDong.Open.Model.Response Power(byte onORoff)
		{
			com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
//					long eBikeTerminalID=jo.get("EBikeTerminalID").getAsLong();
			com.cocopass.iot.model.DownMessageResult result=packet.SendSwitchEBikePowerPacket(request.GetTimeStamp(),eBikeTerminalID, onORoff, url);
			response.SetResult(0);
			JsonObject jbody=new JsonObject();
			jbody.add("Parameters", jParameters); 
			jbody.addProperty("Status", result.GetStatus()); 
			response.SetBody(jbody);
			return response;
		}
			 
		public com.LuYuanDong.Open.Model.Response Return() throws Exception
		{
			LimitSpeed();
			Station ep=new Station(request);
			return ep.LockPile();
		}
			 
		//限制速度
		com.LuYuanDong.Open.Model.Response LimitSpeed()
		{
			int speed=3;
			return SetMaxSpeed(speed);
		} 
		
		//取消限速
		com.LuYuanDong.Open.Model.Response CancelLimitSpeed()
		{
			int speed=100;
			return SetMaxSpeed(speed);
		} 
			  
			 
		com.LuYuanDong.Open.Model.Response SetMaxSpeed(int speed)
		{
			com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
//					long eBikeTerminalID=jo.get("EBikeTerminalID").getAsLong();
			com.cocopass.iot.model.DownMessageResult result=packet.SendSetEBikeMaxSpeedPacket(request.GetTimeStamp(),eBikeTerminalID, speed, url);
			response.SetResult(0);
			JsonObject jbody=new JsonObject();
			jbody.add("Parameters", jParameters); 
			jbody.addProperty("Status", result.GetStatus()); 
			response.SetBody(jbody);
			return response;
				} 
	
            //锁车成功后复位	
			 public com.LuYuanDong.Open.Model.Response Reset()
			 {
				 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
		    	 com.cocopass.iot.model.DownMessageResult result=packet.SendResetEBikePacket(request.GetTimeStamp(),eBikeTerminalID, url);
		    	
		    	 //GetRealResult(result);
		    	 
		    	 response.SetResult(0);
		    	 JsonObject jbody=new JsonObject();
		    	 jbody.add("Parameters", jParameters); 
		    	 jbody.addProperty("Status", result.GetStatus()); 
		    	 response.SetBody(jbody);
		    	 return response;
			 }
}
