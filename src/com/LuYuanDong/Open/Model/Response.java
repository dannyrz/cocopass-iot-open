package com.LuYuanDong.Open.Model;

public class Response {
	 int Result=0;
     String Message="";
     private Object Body=null;
     private String Sign = ""; //�жϷ��������Ƿ��޸�
     
   //�ֱ�ʵ��set��get����
     public void SetResult(int result)
     {
         this.Result= result;
     }
     
     public int GetResult()
     {
         return this.Result;
     }
     
     public void SetMessage(String message)
     {
         this.Message= message;
     }
     
     public String GetMessage()
     {
         return this.Message;
     }
     
     public void SetBody(Object body)
     {
         this.Body= body;
     }
     
     public Object GetBody()
     {
         return this.Body;
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
