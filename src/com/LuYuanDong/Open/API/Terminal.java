package com.LuYuanDong.Open.API;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.LuYuanDong.Open.Config;
import com.LuYuanDong.Open.Global;
import com.LuYuanDong.Open.Service;
import com.cocopass.helper.CDate;
import com.cocopass.helper.CRedis;
import com.cocopass.iot.model.CPageRecord;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.MessageProperties;

public class Terminal {
	private static final Logger LOG = Logger.getLogger(Service.class.getName());
	JsonObject jParameters = null;
	float version = 2.1f;
	long terminalID = 0;

	// String terminalID="";
	String url = "";
	com.ludong.iot.IPacket packet;
	com.LuYuanDong.Open.Model.Request request;
	String serialNumber = "";
	Gson gson = new Gson();
	com.ludong.model.ActionLog actionLog;

	public Terminal(com.LuYuanDong.Open.Model.Request request) throws Exception {
		this.request = request;
		this.jParameters = request.GetParameters();
		this.terminalID = jParameters.has("TerminalID") ? jParameters.get("TerminalID").getAsLong() : 0;

		if (terminalID > 0) { // 只要是2.0的车 配的肯定是2.0的桩
			version = com.ludong.iot.PacketBase.GetVersionByTerminalID(terminalID);
			packet = com.ludong.iot.PacketFactory.GetPacketInstance(version, null);
			url = GetTranseURL(terminalID);
			SendToQueen(this.terminalID);
		}

	}

	String GetTranseURL(long terminalID) {
		String iurl = null;
		if (Config.RunMode == 1) {
			if (version > 1.1f) {
				iurl = Config.TCPTranseURL;
			} else {
				iurl = Config.UDPTranseURL;
			}
		} else {
			String transeURL = com.cocopass.helper.CRedis.getMapValue("Terminal:" + terminalID, "TranseURL");
			if (transeURL != null && !transeURL.equals("")) {
				iurl = transeURL;
			} else {
				if (version > 1.1f) {
					iurl = Config.TCPTranseURL;
				} else {
					iurl = Config.UDPTranseURL;
				}
			}
		}
		return iurl;
	}

	public void SendToQueen(long terminalID) {

		try {

			serialNumber = request.GetSerialNumber();

			actionLog = new com.ludong.model.ActionLog();
			actionLog.SetActionCode(request.GetActionCode());
			actionLog.SetAppKey(request.GetKey());
			actionLog.SetParameters(request.GetParameters().toString());
			actionLog.SetSamplingTime(new Date().getTime());
			actionLog.SetSerialNumber(serialNumber);
			actionLog.SetStatus(1);
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
	
	void GetRealResult(com.cocopass.iot.model.DownMessageResult result) {
		String success = "";
		try {
			for (int i = 0; i < 25; i++) {
				Thread.sleep(i * 10);
				success = CRedis.get(serialNumber);
				if (success.equals("success")) {
					result.SetStatus(0);
					break;
				}

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
		}

	}

	/**
	 * 修改电动车GPSID
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的车辆GPSID
	 * @param NewTerminalID
	 */
	public com.LuYuanDong.Open.Model.Response SetTerminalID() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		long newTerminalID = jParameters.get("NewTerminalID").getAsLong();

		com.cocopass.iot.model.DownMessageResult result = packet.SendSetTerminalIDPacket(request.GetTimeStamp(),
				terminalID, newTerminalID, url);

		actionLog.SetSamplingTime(new Date().getTime());
		actionLog.SetStatus(result.GetStatus());
		// SendToQueen();
		// 此处加入同步从缓存数据库检查请求应答
		// GetRealResult(result);

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;
	}

	/**
	 * 重启设备
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的车辆GPSID
	 * @param NewTerminalID
	 */
	public com.LuYuanDong.Open.Model.Response ReBoot() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();

		com.cocopass.iot.model.DownMessageResult result = packet.SendReBootTerminalPacket(request.GetTimeStamp(),
				terminalID, url);

		actionLog.SetSamplingTime(new Date().getTime());
		actionLog.SetStatus(result.GetStatus());
		// SendToQueen();
		// 此处加入同步从缓存数据库检查请求应答
		// GetRealResult(result);

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;
	}

	/**
	 * 修改设备鉴权地址端口
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的车辆GPSID
	 * @param NewTerminalID
	 */
	public com.LuYuanDong.Open.Model.Response SetAuthHostAndPort() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		int port = jParameters.get("Port").getAsInt();
		String host = jParameters.get("Host").getAsString();
		// String url="";

		com.cocopass.iot.model.DownMessageResult result = packet.SendSetTerminalAuthHostAndPort(request.GetTimeStamp(),
				terminalID, host, port, url);

		actionLog.SetSamplingTime(new Date().getTime());
		actionLog.SetStatus(result.GetStatus());
		// SendToQueen();
		// 此处加入同步从缓存数据库检查请求应答
		// GetRealResult(result);

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;
	}

	/**
	 * 修改设备APN
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            APNName APNUserName APNPassword被操作的车辆GPSID
	 * @param
	 */
	public com.LuYuanDong.Open.Model.Response SetTerminalAPN() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		String apnName = jParameters.get("APNName").getAsString();
		String apnUserName = jParameters.get("APNUserName").getAsString();
		String apnPassword = jParameters.get("APNPassword").getAsString();
		// String url="";

		com.cocopass.iot.model.DownMessageResult result = packet.SendSetTerminalAPNPacket(request.GetTimeStamp(),
				terminalID, apnName, apnUserName, apnPassword, url);

		actionLog.SetSamplingTime(new Date().getTime());
		actionLog.SetStatus(result.GetStatus());

		// 此处加入同步从缓存数据库检查请求应答
		// GetRealResult(result);

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;
	}

	/**
	 * 修改心跳间隔时间
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的车辆GPSID
	 * @param HeartPonginterval
	 */
	public com.LuYuanDong.Open.Model.Response SetHeartPongInterval() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		int heartPonginterval = jParameters.get("HeartPongInterval").getAsInt();
		com.cocopass.iot.model.DownMessageResult result = packet.SendSetHeartPongIntervalPacket(request.GetTimeStamp(),
				terminalID, heartPonginterval, url);

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
	 * 修改骑行间隔时间
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的车辆GPSID
	 * @param HeartPonginterval
	 */
	public com.LuYuanDong.Open.Model.Response SetRunningInterval() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		int runningInterval = jParameters.get("RunningInterval").getAsInt();
		com.cocopass.iot.model.DownMessageResult result = packet.SendSetRunningIntervalPacket(request.GetTimeStamp(),
				terminalID, runningInterval, url);

		actionLog.SetSamplingTime(new Date().getTime());
		actionLog.SetStatus(result.GetStatus());

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;

	}

	

	

	
	

//	/**
//	 * 控制电动车电源开关，绿源芯或者GPS MOS管
//	 * 
//	 * @param JSON
//	 *            参数JSON
//	 * @param TerminalID
//	 *            被操作的车辆GPSID
//	 * @param OnOrOff
//	 *            动作 on/off 1/0 用数字可忽略大小写
//	 */
//	public com.LuYuanDong.Open.Model.Response SwitchEBikePower() {
//		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
//
//		int onORoff = jParameters.get("OnOrOff").getAsInt();
//		com.cocopass.iot.model.DownMessageResult result = packet.SendSwitchEBikePowerPacket(request.GetTimeStamp(),
//				terminalID, (byte) onORoff, url);
//
//		actionLog.SetSamplingTime(new Date().getTime());
//		int status = 0;
//		if (result != null)
//			status = result.GetStatus();
//		else
//			status = 4;
//		actionLog.SetStatus(status);
//
//		// GetRealResult(result);
//
//		response.SetResult(0);
//		JsonObject jbody = new JsonObject();
//		jbody.add("Parameters", jParameters);
//		jbody.addProperty("Status", status);
//		response.SetBody(jbody);
//		return response;
//	}

//	/**
//	 * 控制电动车尾灯开关
//	 * 
//	 * @param jo
//	 *            参数JSON
//	 * @param TerminalID
//	 *            被操作的设备ID
//	 * @param OnOrOff
//	 *            动作 on/off 1/0 用数字可忽略大小写
//	 */
//	public com.LuYuanDong.Open.Model.Response SwitchEBikeTailLightPower() {
//		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
//
//		int onORoff = jParameters.get("OnOrOff").getAsInt();
//		com.cocopass.iot.model.DownMessageResult result = packet
//				.SendSwitchEBikeTailLightPowerPacket(request.GetTimeStamp(), terminalID, (byte) onORoff, url);
//
//		// GetRealResult(result);
//
//		response.SetResult(0);
//		JsonObject jbody = new JsonObject();
//		jbody.add("Parameters", jParameters);
//		jbody.addProperty("Status", result.GetStatus());
//		response.SetBody(jbody);
//		return response;
//	}

//	/**
//	 * 控制电动车尾灯闪烁
//	 * 
//	 * @param JSON
//	 *            参数JSON
//	 * @param TerminalID
//	 *            被操作的网点的通讯设备ID
//	 * @param OnOrOff
//	 *            动作 on/off 1/0 用数字可忽略大小写
//	 */
//	public com.LuYuanDong.Open.Model.Response SwitchEBikeTurnLightFlash() {
//		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
//		int onORoff = jParameters.get("OnOrOff").getAsInt();
//		com.cocopass.iot.model.DownMessageResult result = packet
//				.SendSwitchEBikeTurnLightFlashPacket(request.GetTimeStamp(), terminalID, (byte) onORoff, url);
//
//		response.SetResult(0);
//		JsonObject jbody = new JsonObject();
//		jbody.add("Parameters", jParameters);
//		jbody.addProperty("Status", result.GetStatus());
//		response.SetBody(jbody);
//		return response;
//	}

//	/**
//	 * 控制电动车鞍座锁
//	 * 
//	 * @param JSON
//	 *            参数JSON
//	 * @param TerminalID
//	 *            被操作的网点的通讯设备ID
//	 * @param OpenOrLock
//	 *            动作 open/lock 1/0 用数字可忽略大小写
//	 */
//	public com.LuYuanDong.Open.Model.Response SwitchEBikeSaddleLock() {
//		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
//		String strOpenOrLock = jParameters.get("OpenOrLock").getAsString();
//		int onORoff = Integer.parseInt(strOpenOrLock);
//		com.cocopass.iot.model.DownMessageResult result = packet.SendSwitchEBikeSaddleLockPacket(request.GetTimeStamp(),
//				terminalID, (byte) onORoff, url);
//
//		// GetRealResult(result);
//
//		response.SetResult(0);
//		JsonObject jbody = new JsonObject();
//		jbody.add("Parameters", jParameters);
//		jbody.addProperty("Status", result.GetStatus());
//		response.SetBody(jbody);
//		return response;
//	}

	/**
	 * 电动车状态复位
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 * 
	 */
	/*
	public com.LuYuanDong.Open.Model.Response ResetEBike() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		com.cocopass.iot.model.DownMessageResult result = packet.SendResetEBikePacket(request.GetTimeStamp(),
				terminalID, url);

		// GetRealResult(result);

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;
	}
*/
	/**
	 * 电动车限速
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 * 
	 */
	/*
	public com.LuYuanDong.Open.Model.Response SetEBikeMaxSpeed() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		int speed = jParameters.get("Speed").getAsInt();
		com.cocopass.iot.model.DownMessageResult result = packet.SendSetEBikeMaxSpeedPacket(request.GetTimeStamp(),
				terminalID, speed, url);
		// GetRealResult(result);

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;
	}
*/
	/**
	 * 控制车桩锁车器口开关
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的网点的通讯设备ID
	 * @param Port
	 *            锁车桩口 如 1，2，3，4，5，6，7
	 * @param OpenOrLock
	 *            动作 open/lock 1/0 用数字可忽略大小写
	 */
	/*
	public com.LuYuanDong.Open.Model.Response SwitchEBikePileLock() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		int port = jParameters.get("Port").getAsInt();
		byte openOrLock = jParameters.get("OpenOrLock").getAsByte();
		com.cocopass.iot.model.DownMessageResult result = packet.SendSwitchEBikePileLockPacket(request.GetTimeStamp(),
				terminalID, port, openOrLock, url);

		// GetRealResult(result);

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;
	}
*/
	/**
	 * 控制车桩锁车器口开关
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param TerminalID
	 *            被操作的网点的通讯设备ID
	 * @param Port
	 *            锁车桩口 如 1，2，3，4，5，6，7
	 * @param LightNumber
	 *            灯序列号
	 * @param OnOrOff
	 *            动作 open/lock 1/0 用数字可忽略大小写
	 */
	/*
	public com.LuYuanDong.Open.Model.Response SwitchEBikePileLight() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		int port = jParameters.get("Port").getAsInt();
		int lightNumber = jParameters.get("LightNumber").getAsInt();
		byte onORoff = jParameters.get("OnOrOff").getAsByte();
		com.cocopass.iot.model.DownMessageResult result = packet.SendSwitchPileLightPacket(request.GetTimeStamp(),
				terminalID, port, lightNumber, onORoff, url);

		// GetRealResult(result);

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;
	}
*/

	
	/**
	 * 批量增加控制器数据到数据库
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param List<ControllerInfo>
	 */
	public com.LuYuanDong.Open.Model.Response AddControllerInfoToDatabase() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		return response;
	}

	/**
	 * 批量增加电池数据到数据库
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param List<BatteryInfo>
	 */
	public com.LuYuanDong.Open.Model.Response AddBatteryInfoToDatabase() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		return response;
	}

	/**
	 * 批量增加锁车桩状态数据到数据库
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param List<EBikePileState>
	 */
	public com.LuYuanDong.Open.Model.Response AddEBikePileStateToDatabase() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		return response;
	}

	/**
	 * 批量增加交流充电桩状态数据到数据库
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param List<ACChargePileState>
	 */
	public com.LuYuanDong.Open.Model.Response AddACChargePileStateToDatabase() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		return response;
	}

	/**
	 * 批量增加交流充电桩充电数据到数据库
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param List<ACChargePileInfo>
	 */
	public com.LuYuanDong.Open.Model.Response AddACChargePileInfoToDatabase() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		return response;
	}
	
	
	public com.LuYuanDong.Open.Model.Response SetReceiveEndPoint() {
		int tcpPort = jParameters.get("ReceiveTCPPort").getAsInt();
		int udpPort = jParameters.get("ReceiveUDPPort").getAsInt();
		String host = jParameters.get("ReceiveHost").getAsString();
		String key="Terminal:"+terminalID;
		String transeURL="http://"+host+":9090";
		long r0=com.cocopass.helper.CRedis.SetMapValue(key, "ReceiveHost", host);
		long r1=com.cocopass.helper.CRedis.SetMapValue(key, "ReceiveTCPPort", String.valueOf(tcpPort));
		long r2=com.cocopass.helper.CRedis.SetMapValue(key, "ReceiveUDPPort", String.valueOf(udpPort));
		long r3=com.cocopass.helper.CRedis.SetMapValue(key, "TranseURL", transeURL);
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		response.SetResult(0);
		return response;
	}
	
	public com.LuYuanDong.Open.Model.Response SetDataInterval() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		int heartPonginterval = jParameters.get("Interval").getAsInt();
		com.cocopass.iot.model.DownMessageResult result = packet.SendSetDataIntervalPacket(request.GetTimeStamp(),
				terminalID, heartPonginterval, url);

		actionLog.SetSamplingTime(new Date().getTime());
		actionLog.SetStatus(result.GetStatus());

		response.SetResult(0);
		JsonObject jbody = new JsonObject();
		jbody.add("Parameters", jParameters);
		jbody.addProperty("Status", result.GetStatus());
		response.SetBody(jbody);
		return response;
	}
	
	public com.LuYuanDong.Open.Model.Response SetSecondDeviceFactoryMode() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		com.cocopass.iot.model.DownMessageResult result = packet.SendSetSecondDeviceFactoryModePacket(request.GetTimeStamp(),
				terminalID, url);

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
	 * 获取在线离线统计情况
	 * @param <T>
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param  
	 */	
	public <T> com.LuYuanDong.Open.Model.Response GetOnLineShaft() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		com.cocopass.bll.OnlineShaft bll=new com.cocopass.bll.OnlineShaft();
		long startTime = jParameters.get("StartTime").getAsLong();
		long endTime = jParameters.get("EndTime").getAsLong();
		int page=jParameters.get("Page").getAsInt();
		 int pageNum=jParameters.get("PageNum").getAsInt();
		String condition=" TerminalID="+terminalID+" AND AddTime BETWEEN "+startTime+" AND "+endTime;
		try {
			//List<com.cocopass.iot.model.OnLineShaft> list=bll.GetList(condition,page,pageNum);
			 CPageRecord<T> records=bll.GetPageRecord(condition, page, pageNum, true);
			response.SetResult(0);
			response.SetBody(records);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	
	/**
	 * 获取连接报到服务器情况
	 * @param <T>
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param  
	 */

	public <T> com.LuYuanDong.Open.Model.Response GetLoginInfoList() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		com.cocopass.bll.LoginInfo bll=new com.cocopass.bll.LoginInfo();
		long startTime = jParameters.get("StartTime").getAsLong();
		long endTime = jParameters.get("EndTime").getAsLong();
		int page=jParameters.get("Page").getAsInt();
		 int pageNum=jParameters.get("PageNum").getAsInt();
		String condition=" TerminalID="+terminalID+" AND SamplingTime BETWEEN "+startTime+" AND "+endTime;
		try {
			//List<com.cocopass.iot.model.LoginInfo> list=bll.GetList(condition,page,pageNum);
			 CPageRecord<T> records=bll.GetPageRecord(condition, page, pageNum, true);
			response.SetResult(0);
			response.SetBody(records);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	/**
	 * 获取连接报到服务器情况
	 * @param <T>
	 * 
	 * @param JSON
	 *            参数JSON
	 * @param  
	 */
	
	public <T> com.LuYuanDong.Open.Model.Response GetAuthInfoList() {
		com.LuYuanDong.Open.Model.Response response = new com.LuYuanDong.Open.Model.Response();
		com.cocopass.bll.AuthInfo bll=new com.cocopass.bll.AuthInfo();
		long startTime = jParameters.get("StartTime").getAsLong();
		long endTime = jParameters.get("EndTime").getAsLong();
		int page=jParameters.get("Page").getAsInt();
		 int pageNum=jParameters.get("PageNum").getAsInt();
		String condition=" TerminalID="+terminalID+" AND SamplingTime BETWEEN "+startTime+" AND "+endTime;
		try {
			//List<com.cocopass.iot.model.AuthInfo> list=bll.GetList(condition,page,pageNum);
			 CPageRecord<T> records=bll.GetPageRecord(condition, page, pageNum, true);
			response.SetResult(0);
			response.SetBody(records);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
}
