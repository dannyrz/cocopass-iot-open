package com.LuYuanDong.Open;

public class Config {
   //public static String test="";
   public static String connectionStrings="";
   public static String TCPTranseURL="";
   public static String UDPTranseURL="";
   public static String TranseURL="";
   
 
   public static String CRouteInfo;
   public static int RedisPort;
   public static String RedisHost;
   public static String  RedisPassword;
   public static int RedisPoolMaxActive=500;
   public static int		RedisPoolMaxIdle=5;
   public static long		RedisPoolMaxWaitMillis=100000;
   public static int		RedisPoolTimeOut=10000;
 
   
   public static String QReceivedTerminalBytes;
   public static String RabbitMQHost ;
   public static  String RabbitMQUserName ;
   public static  String RabbitMQPassword ;
   public static  int RabbitMQPort  ;
   
   public static String MQName ;
   public static String MQHost ;
   public static String MQUserName ;
   public static String MQPassword ;
   public static int MQPort  ;
   public static String MQAliasName= "";
   
   public static String ECActionLog="EC-ActionLog";
   public static String InstructionRequestRouteKey="instruction.request.action";
   
   public static int RunMode;
   public static int TimeZone=8;
   

   
}
