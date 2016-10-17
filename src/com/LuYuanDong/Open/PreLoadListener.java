package com.LuYuanDong.Open;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.dave.common.database.DatabaseConnectionPool;

import com.cocopass.helper.CProperties;
import com.cocopass.helper.CRedis;
import com.cocopass.helper.CMQ.MQFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public  class PreLoadListener implements ServletContextListener{
	private static   Logger LOG = Logger.getLogger(PreLoadListener.class.getName());
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
//		Date date=new Date();
//		Properties properties = new Properties();
//		try {
//			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties"));
//			Config.connectionStrings=properties.getProperty("connectionStrings");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//Config.test="contextInitialized start!"+date.toGMTString();
		
   	 try {
         // Load app.properties
Global.iProperties.SetProperties();

Config.TimeZone=Integer.parseInt(Global.iProperties.GetValue("TimeZone"));
Config.RunMode=Integer.parseInt(Global.iProperties.GetValue("RunMode"));
Config.TCPTranseURL=Global.iProperties.GetValue("TCPTranseURL");
Config.UDPTranseURL=Global.iProperties.GetValue("UDPTranseURL");
Config.TranseURL=Global.iProperties.GetValue("TranseURL");




//Config.CRouteInfo=Global.iProperties.GetValue("CRouteInfo");

//if(Config.RunMode==0){
	Config.RedisPort=Integer.parseInt(Global.iProperties.GetValue("RedisPort"));
	Config.RedisHost=Global.iProperties.GetValue("RedisHost");
	Config.RedisPassword=Global.iProperties.GetValue("RedisPassword");
	Config.RedisPoolMaxActive=Integer.parseInt(Global.iProperties.GetValue("RedisPoolMaxActive"));
	Config.RedisPoolMaxIdle=Integer.parseInt(Global.iProperties.GetValue("RedisPoolMaxIdle"));
	Config.RedisPoolMaxWaitMillis= Long.parseLong(Global.iProperties.GetValue("RedisPoolMaxWaitMillis"));
	Config.RedisPoolTimeOut=Integer.parseInt(Global.iProperties.GetValue("RedisPoolTimeOut"));
	CRedis.StartPool( Config.RedisPoolMaxActive, Config.RedisPoolMaxIdle, Config.RedisPoolMaxWaitMillis, Config.RedisHost, Config.RedisPort, Config.RedisPoolTimeOut, Config.RedisPassword);
//}

 


//Config.RabbitMQHost=Global.iProperties.GetValue("RabbitMQHost");
//Config.RabbitMQUserName=Global.iProperties.GetValue("RabbitMQUserName");
//Config.RabbitMQPassword=Global.iProperties.GetValue("RabbitMQPassword");
//Config.RabbitMQPort= Integer.parseInt(Global.iProperties.GetValue("RabbitMQPort"));
	
Config.QReceivedTerminalBytes=Global.iProperties.GetValue("QReceivedTerminalBytes");
Config.ECActionLog=Global.iProperties.GetValue("ECActionLog");
Config.InstructionRequestRouteKey=Global.iProperties.GetValue("InstructionRequestRouteKey");



//  ConnectionFactory factory = new ConnectionFactory();  
//  factory.setHost(Config.RabbitMQHost);  
//  factory.setUsername(Config.RabbitMQUserName);
//  factory.setPassword(Config.RabbitMQPassword);
//  factory.setPort(Config.RabbitMQPort);
//  Connection connection = factory.newConnection();
//  Global.RabbitMQChannel=connection.createChannel();


     Config.MQName	=	Global.iProperties.GetValue("MQName");
	 Config.MQAliasName	=	Global.iProperties.GetValue("MQAliasName");
	 Config.MQHost	=	Global.iProperties.GetValue("MQHost");
	 Config.MQUserName	=	Global.iProperties.GetValue("MQUserName");
	 Config.MQPassword	=	Global.iProperties.GetValue("MQPassword");
	 Config.MQPort	= Integer.parseInt(Global.iProperties.GetValue("MQPort"));
	 
	 Global.CMQ	= MQFactory.createMQ(Config.MQName);
	 Global.CMQ.SetConnection(Config.MQAliasName, Config.MQHost, Config.MQUserName, Config.MQPassword, Config.MQPort);
	 Global.CMQ.SetService();

  
   } catch (Exception e) {
          // TODO Auto-generated catch block
           
        // logger.error(e.getMessage(), e);
         e.printStackTrace();
         LOG.error(e.getMessage());
     }


//SendMessageToTerminal ds1 = new SendMessageToTerminal();
//Thread t1 = new Thread(ds1);
//t1.start(); 

}
		
}

