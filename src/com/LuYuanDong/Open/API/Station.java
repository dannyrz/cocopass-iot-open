package com.LuYuanDong.Open.API;

import java.util.Date;

import org.apache.log4j.Logger;

import com.LuYuanDong.Open.Config;
import com.LuYuanDong.Open.Global;
import com.LuYuanDong.Open.Service;
import com.LuYuanDong.Open.Model.Request;
import com.cocopass.helper.CDate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Station  extends Terminal{
	long stationTerminalID=0; 
	int pilePort=0;
	Gson json=new Gson();
	
	private static final Logger LOG = Logger.getLogger(Station.class.getName());
	
	public Station(Request request) throws Exception {
		super(request);
		
			if(jParameters.has("StationTerminalID"))
				stationTerminalID=jParameters.get("StationTerminalID").getAsLong();
			if(jParameters.has("PilePort"))
			   pilePort=jParameters.get("PilePort").getAsInt();
			if(stationTerminalID>0) //只要是2.0的车 配的肯定是2.0的桩
			{
			   version= com.ludong.iot.PacketBase.GetVersionByTerminalID(stationTerminalID);
			   packet=com.ludong.iot.PacketFactory.GetPacketInstance(version, null);
			   url=GetTranseURL(stationTerminalID);
			}
			
			SendToQueen(stationTerminalID);	
	}
	
	public void SendActionSuccessToQueen(long terminalID,int status) {

		try {

			serialNumber = request.GetSerialNumber();

			actionLog = new com.ludong.model.ActionLog();
			actionLog.SetActionCode(request.GetActionCode());
			// actionLog.SetAppKey(request.GetKey());
			actionLog.SetParameters(request.GetParameters().toString());
			actionLog.SetSamplingTime(new Date().getTime());
			actionLog.SetSerialNumber(serialNumber);
			actionLog.SetStatus(status);
			actionLog.SetTerminalID(terminalID);
			actionLog.SetTimestamp(request.GetTimeStamp());

			byte[] bytes = gson.toJson(actionLog).getBytes();

			// if(!Global.RabbitMQChannel.isOpen())
			// {
			// Global.RabbitMQChannel=Global.RabbitMQChannel.getConnection().createChannel();
			// }

			Global.CMQ.PublishMessage(Config.ECActionLog, "", bytes);

			// Global.RabbitMQChannel.basicPublish(Config.ECActionLog,
			// Config.InstructionRequestRouteKey,
			// MessageProperties.PERSISTENT_TEXT_PLAIN,bytes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
		}

	}
	
	
	public com.LuYuanDong.Open.Model.Response GetInfo(){
		com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
		com.ludong.model.Station stationInfo=new com.ludong.model.Station();
		int portNum=Integer.parseInt(com.cocopass.helper.CRedis.getMapValue("Station:"+stationTerminalID, "PortNum"));
		stationInfo.setPortNum(portNum);
		stationInfo.setTerminalID(stationTerminalID);
		response.SetResult(0);
      	JsonObject jbody=new JsonObject();
      	//jbody.add("Parameters", jParameters); 
      	jbody.add("Station", gson.toJsonTree(stationInfo));
      	response.SetBody(jbody);
		return response;
	}
	
	public com.LuYuanDong.Open.Model.Response OpenPile(){
    	//int port= jo.get("Port").getAsInt();
    	return SwitchPileLock((byte)0);
    }

    public com.LuYuanDong.Open.Model.Response LockPile(){
    	//int port= jo.get("Port").getAsInt();
    	return SwitchPileLock((byte)1);
    }
    
    /**
     * 控制电动车电源开关，绿源芯或者GPS MOS管
     * @param JSON       参数JSON
     * @param TerminalID    被操作的车辆GPSID
     * @param OnOrOff  动作    on/off   1/0 用数字可忽略大小写
     * */
    com.LuYuanDong.Open.Model.Response SwitchPileLock(byte openORlock){
      	 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
      	 com.cocopass.iot.model.DownMessageResult result=packet.SendSwitchEBikePileLockPacket(request.GetTimeStamp(),stationTerminalID, pilePort, openORlock,url);
      	 
      	 actionLog.SetSamplingTime(new Date().getTime());
      	 int status=0;
      	 if(result!=null)
      		 status=result.GetStatus();
      	 else
      		 status=4;
      	 actionLog.SetStatus(status);
   		 //SendToQueen();
      	
      	 //GetRealResult(result);
      	 
      	 response.SetResult(0);
      	 JsonObject jbody=new JsonObject();
      	 jbody.add("Parameters", jParameters); 
      	 jbody.addProperty("Status", status); 
      	 response.SetBody(jbody);
      	 return response;
       }
     
     public com.LuYuanDong.Open.Model.Response OnChargingPower(){
    	 return SwitchChargingPower((byte)1);
     }
     
     public  com.LuYuanDong.Open.Model.Response OffChargingPower(){
    	 return SwitchChargingPower((byte)0);
     }
     /**
      * 充电桩充电开关
      * @param OnOrOff
      * @return
      */
     com.LuYuanDong.Open.Model.Response SwitchChargingPower(byte onOrOff){
    	 int batteryCapacity=0;
    	 int batteryVoltage=0;
    	 long sessionID=request.GetTimeStamp();
    	 if(jParameters.has("SessionID"))
    		 sessionID=jParameters.get("SessionID").getAsLong();
    	 if(jParameters.has("BatteryCapacity"))
    		 batteryCapacity=jParameters.get("BatteryCapacity").getAsInt();
    	 if(jParameters.has("BatteryVoltage"))
    		 batteryVoltage=jParameters.get("BatteryVoltage").getAsInt();
    	 
    	 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
      	 com.cocopass.iot.model.DownMessageResult result=packet.SendSwitchPileChargerPacket(request.GetTimeStamp(),stationTerminalID,sessionID,batteryVoltage,batteryCapacity,pilePort,onOrOff,url);
      	 
      	 actionLog.SetSamplingTime(new Date().getTime());
      	 int status=0;
      	 if(result!=null)
      		 status=result.GetStatus();
      	 else
      		 status=4;
      	 actionLog.SetStatus(status);
   		 //SendToQueen();
      	 if(version<2f){
      		 SendActionSuccessToQueen(stationTerminalID,3);
      	 }
      	 //GetRealResult(result);
      	 
      	 response.SetResult(0);
      	 JsonObject jbody=new JsonObject();
      	 jbody.add("Parameters", jParameters); 
      	 jbody.addProperty("Status", status); 
      	 response.SetBody(jbody);
      	 return response;
     }
   
     public com.LuYuanDong.Open.Model.Response OnPileLightPower(){
    	 return SwitchPileLight((byte)1);
     }
     
     public com.LuYuanDong.Open.Model.Response OffPileLightPower(){
    	 return SwitchPileLight((byte)0);
     }
     
     /**
      * 控制桩体灯光开关
      * @param OnOrOff
      * @return
      */
     com.LuYuanDong.Open.Model.Response SwitchPileLight(byte onOrOff)
     {
    	 int lightNum=1;
    	 if(jParameters.has("LightNum"))
    		 lightNum=jParameters.get("LightNum").getAsInt();
    	 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
      	 com.cocopass.iot.model.DownMessageResult result=packet.SendSwitchPileLightPacket(request.GetTimeStamp(),stationTerminalID, pilePort, lightNum, onOrOff, url);
      	 
      	 actionLog.SetSamplingTime(new Date().getTime());
      	 int status=0;
      	 if(result!=null)
      		 status=result.GetStatus();
      	 else
      		 status=4;
      	 actionLog.SetStatus(status);
   		 //SendToQueen();
      	 
      	 //GetRealResult(result);
      	 
      	 response.SetResult(0);
      	 JsonObject jbody=new JsonObject();
      	 jbody.add("Parameters", jParameters); 
      	 jbody.addProperty("Status", status); 
      	 response.SetBody(jbody);
      	 return response;
     }
     
     public com.LuYuanDong.Open.Model.Response SetStationPileNum(){
    	 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response();
    	 
    	 int pileNum=jParameters.get("PileNum").getAsInt();
    	 
      	 com.cocopass.iot.model.DownMessageResult result=packet.SendSetStationPileNumPacket( request.GetTimeStamp(),stationTerminalID, pileNum,  url);
      	 
      	 actionLog.SetSamplingTime(new Date().getTime());
      	 int status=0;
      	 if(result!=null)
      		 status=result.GetStatus();
      	 else
      		 status=4;
      	 actionLog.SetStatus(status);
   		 //SendToQueen();
      	 
      	 //GetRealResult(result);
      	 
      	 response.SetResult(0);
      	 JsonObject jbody=new JsonObject();
      	 jbody.add("Parameters", jParameters); 
      	 jbody.addProperty("Status", status); 
      	 response.SetBody(jbody);
    	 return response;
     }
     
     public com.LuYuanDong.Open.Model.Response ReSetPilePort(){
    	 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response(); 	 
    	 
      	 com.cocopass.iot.model.DownMessageResult result=packet.SendReSetPilePortPacket( request.GetTimeStamp(),stationTerminalID, pilePort,  url);
      	 
      	 actionLog.SetSamplingTime(new Date().getTime());
      	 int status=0;
      	 if(result!=null)
      		 status=result.GetStatus();
      	 else
      		 status=4;
      	 actionLog.SetStatus(status);
   		 //SendToQueen();
      	 
      	 //GetRealResult(result);
      	 
      	 response.SetResult(0);
      	 JsonObject jbody=new JsonObject();
      	 jbody.add("Parameters", jParameters); 
      	 jbody.addProperty("Status", status); 
      	 response.SetBody(jbody);
    	 return response;
     }
     
     
     public com.LuYuanDong.Open.Model.Response GetLatestChargingInfo(){
    	 com.LuYuanDong.Open.Model.Response response =new com.LuYuanDong.Open.Model.Response(); 	 
    	 String chargingSessionID=jParameters.get("ChargingSessionID").getAsString();
      	 String jsonBatteryInfo=com.cocopass.helper.CRedis.get(chargingSessionID);
      	 com.ludong.model.BatteryInfo binfo=gson.fromJson(jsonBatteryInfo, com.ludong.model.BatteryInfo.class);
    	 if(binfo!=null)
    	 {
    		 response.SetResult(0);
    		 response.SetBody(binfo);
    	 }
      	 return response;
     }

}
