package com.LuYuanDong.Open.Model;

public class PushData {
   String Key="";
   Long TimeStamp=0l;
   float Version=0f;
   int DataTypeID=0;
   Object Body=null;
   String Sign="";
   
   public void SetDataTypeID(int dataTypeID)
   {
       this.DataTypeID= dataTypeID;
   }
   public int GetDataTypeID()
   {
       return this.DataTypeID;
   }
   
   
   public void SetBody(Object body)
   {
       this.Body= body;
   }
   public Object GetBody()
   {
       return this.Body;
   }
   public void SetTimeStamp(long timeStamp)
   {
       this.TimeStamp= timeStamp;
   }
   public long GetTimeStamp()
   {
       return this.TimeStamp;
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
}
