package com.LuYuanDong.Open.Model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Request {
	 private int AppID = 0;
	 private String Key = "";
     private float Version=1.0f;
     private String Sign = "";
     private String Action = "";
     private String ActionCode = "";
     private JsonObject  Parameters;
     private String IP = "";
     private String SerialNumber="";
     private long TimeStamp=0;
     private int ActionType=1;
     private String TerminalID;
     
     
     public int getActionType() {
		return ActionType;
	}
	public void setActionType(int actionType) {
		ActionType = actionType;
	}
	public String getTerminalID() {
		return TerminalID;
	}
	public void setTerminalID(String terminalID) {
		TerminalID = terminalID;
	}
	public void SetActionCode(String actionCode)
     {
         this.ActionCode= actionCode;
     }
     public String GetActionCode()
     {
         return this.ActionCode;
     } 
     
     public void SetTimeStamp(long timeStamp)
     {
         this.TimeStamp= timeStamp;
     }
     public long GetTimeStamp()
     {
         return this.TimeStamp;
     } 
     public void SetIP(String ip)
     {
         this.IP= ip;
     }
     public String GetIP()
     {
         return this.IP;
     } 
     
     public void SetSerialNumber(String serialNumber)
     {
         this.SerialNumber= serialNumber;
     }
     public String GetSerialNumber()
     {
         return this.SerialNumber;
     } 
     
     public void SetParameters(JsonObject parameters)
     {
         this.Parameters= parameters;
     }
     public JsonObject GetParameters()
     {
         return this.Parameters;
     } 
     
     public void SetAppID(int appID)
     {
         this.AppID= appID;
     }
     
     public int GetAppID()
     {
         return this.AppID;
     }
     
     public void SetKey(String key)
     {
         this.Key= key;
     }
     
     public String GetKey()
     {
         return this.Key;
     }
     
     public void SetVersion(float version)
     {
         this.Version= version;
     }
     public float GetVersion()
     {
         return this.Version;
     }
     
     public void SetSign(String sign)
     {
         this.Sign= sign;
     }
     public String GetSign()
     {
         return this.Sign;
     } 
     
     public void SetAction(String action)
     {
         this.Action= action;
     }
     public String GetAction()
     {
         return this.Action;
     } 
     
}
