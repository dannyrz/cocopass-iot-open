package com.LuYuanDong.Open;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.cocopass.helper.CProperties;
import com.rabbitmq.client.Channel;

public class Global extends HttpServlet {
    
    public static Channel RabbitMQChannel;
    public static CProperties iProperties=new CProperties(); 
    public static com.cocopass.helper.CMQ.CMessageQueue CMQ=null;
    
	public void Init() throws ServletException {
            //��Ҫ������д������
    	//Config.test="jashdlajl1897983WRN";
         }
}