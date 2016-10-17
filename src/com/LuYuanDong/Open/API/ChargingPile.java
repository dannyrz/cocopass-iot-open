package com.LuYuanDong.Open.API;

import com.LuYuanDong.Open.Config;
import com.LuYuanDong.Open.Model.Request;

public class ChargingPile  extends Terminal{
	long stationTerminalID=0; 
	int pilePort=0;
	public ChargingPile(Request request) throws Exception {
		super(request);
		if(jParameters.has("StationTerminalID"))
			stationTerminalID=jParameters.get("StationTerminalID").getAsLong();
		if(jParameters.has("PilePort"))
		   pilePort=jParameters.get("PilePort").getAsInt();
		
		if(stationTerminalID>0) //ֻҪ��2.0�ĳ� ��Ŀ϶���2.0��׮
		{
		   version= com.ludong.iot.PacketBase.GetVersionByTerminalID(stationTerminalID);
		   packet=com.ludong.iot.PacketFactory.GetPacketInstance(version, null);
		   url=GetTranseURL(stationTerminalID);
		}
		
		SendToQueen(stationTerminalID);
	}
	
	

}
