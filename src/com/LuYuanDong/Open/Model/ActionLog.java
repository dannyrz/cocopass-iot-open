package com.LuYuanDong.Open.Model;

public class ActionLog {
	String AppKey="";
	String SerialNumber="";
	long TerminalID=0;
	long Timestamp = 0;  //�·�ʱ�����Я�����ն�ָ�� 6�ֽ�
	String ActionCode="";
	String Parameters="";
	int Status=0;
	String SamplingTime=""; //����ʱ�䣬��ʾ�ն�Ӧ����߷�����ʱ��
	String AddTime="";
	
	
	 public void SetAddTime(String addTime)
	    {
	        this.AddTime= addTime;
	    }
	    public String GetAddTime()
	    {
	        return this.AddTime;
	    }
	
	 public void SetSamplingTime(String samplingTime)
	    {
	        this.SamplingTime= samplingTime;
	    }
	    public String GetSamplingTime()
	    {
	        return this.SamplingTime;
	    }
	    
	 public void SetParameters(String parameters)
	    {
	        this.Parameters= parameters;
	    }
	    public String GetParameters()
	    {
	        return this.Parameters;
	    }
	    
	    public void SetActionCode(String actionCode)
	    {
	        this.ActionCode= actionCode;
	    }
	    public String GetActionCode()
	    {
	        return this.ActionCode;
	    }
	
	 public void SetTimestamp(long timestamp)
	    {
	        this.Timestamp= timestamp;
	    }
	    public long GetTimestamp()
	    {
	        return this.Timestamp;
	    }
	    
	    
	
	 public void SetTerminalID(long terminalID)
	    {
	        this.TerminalID= terminalID;
	    }
	    public long GetTerminalID()
	    {
	        return this.TerminalID;
	    }
	
	 public void SetSerialNumber(String serialNumber)
	    {
	        this.SerialNumber= serialNumber;
	    }
	    public String GetSerialNumber()
	    {
	        return this.SerialNumber;
	    }
	
	 public void SetAppKey(String appKey)
	    {
	        this.AppKey= appKey;
	    }
	    public String GetAppKey()
	    {
	        return this.AppKey;
	    }
	
	  public void SetStatus(int status)
	    {
	        this.Status= status;
	    }
	    public int GetStatus()
	    {
	        return this.Status;
	    }
}
