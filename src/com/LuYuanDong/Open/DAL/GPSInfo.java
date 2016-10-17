package com.LuYuanDong.Open.DAL;

import java.sql.Connection;

import org.dave.common.database.access.DataAccess;
import org.dave.common.database.convert.LongConverter;

public class GPSInfo  extends DataAccess{
	
	protected GPSInfo(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 增加一行GPS数据
	 * @param 所有字段参数
	 * @return
	 */
 
	public  boolean Add(String TableName,com.ludong.model.GPSInfo gpsInfo)
	{
		long TerminalID=gpsInfo.GetTerminalID();
		float Longitude=gpsInfo.GetLongitude();
		float Latitude=gpsInfo.GetLatitude();
		float Speed=gpsInfo.GetSpeed();
		int Direction=gpsInfo.GetDirection();
		boolean PositionState=gpsInfo.GetPositionState();
		int  AntennaState=gpsInfo.GetAntennaState();
		int PowerState=gpsInfo.GetPowerState();
		float TotalMileage=gpsInfo.GetTotalMileage();
		long SamplingTime=gpsInfo.GetSamplingTime();
		long WrittenIntoSystemTime=gpsInfo.GetWrittenIntoSystemTime();
		String GSMStationID=gpsInfo.GetGSMStationID();
		String GSMStationAreaID=gpsInfo.GetGSMStationAreaID();
		int GSMSignalValue=gpsInfo.GetGSMSignalValue();
		int GPSSignalValue=gpsInfo.GetGPSSignalValue();
		float BuiltInBatteryVoltageQuantity=gpsInfo.GetBuiltInBatteryVoltageQuantity();
		float ExternalVoltage=gpsInfo.GetExternalVoltage();
		boolean IsDefence=gpsInfo.GetIsDefence();
		boolean IsMosOpen=gpsInfo.GetIsMosOpen();
		float Version=gpsInfo.GetVersion();
		boolean IsRealTime=gpsInfo.GetIsRealTime();
		int AlarmNO=gpsInfo.GetAlarmNO();
		int AlarmParam=gpsInfo.GetAlarmParam();
		String GDLocation=gpsInfo.getGDLocation();
		String BDLocation=gpsInfo.getBDLocation();
		String Address=gpsInfo.getAddress();
		boolean result=false;
		String sql="INSERT INTO "+TableName+"(TerminalID,Longitude,Latitude,Speed,Direction,PositionState,"
				+ "AntennaState,PowerState,TotalMileage,"
				+ "SamplingTime,WrittenIntoSystemTime,GSMStationID,GSMStationAreaID,GSMSignalValue,"
				+ "GPSSignalValue,BuiltInBatteryVoltageQuantity,"
				+ "ExternalVoltage,IsDefence,IsMosOpen,Version,IsRealTime,AlarmNO,AlarmParam,"
				+ "GDLocation,BDLocation,Address) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		long rows= super.insert(sql, new LongConverter(), TerminalID,Longitude,Latitude,Speed,Direction,PositionState,AntennaState,
				PowerState,TotalMileage,SamplingTime,WrittenIntoSystemTime,GSMStationID,GSMStationAreaID,GSMSignalValue,
				GPSSignalValue,BuiltInBatteryVoltageQuantity,
						 ExternalVoltage,IsDefence,IsMosOpen,Version,IsRealTime,AlarmNO,AlarmParam,GDLocation,BDLocation,Address);
		if(rows==1)
			result=true;
		return result;
			
	}

}
