package com.LuYuanDong.Open;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.LuYuanDong.Open.Model.*;

public class Verification {
	private static final Logger LOG = Logger.getLogger(Verification.class.getName());
	
    public static boolean IsLegalRequest(Request request)
    {
    	boolean result=true;
    	if(request==null
    			||request.GetAction().indexOf(".")<1
    			||request.GetIP().equals("")
    			||request.GetKey().equals("")
    			||request.GetParameters()==null
    			||request.GetSerialNumber().equals("")
    			||request.GetSign().equals("")
                ||request.GetVersion()<1
    			)
    		result=false;
    	return result;
    }
    
    public static boolean  IsEqualLocalNet(String clientIP) throws UnknownHostException, SocketException
    {
    	boolean result=false;
    	List<com.LuYuanDong.Open.Model.NetWorkCard> list =  GetNetInfo();
        for (com.LuYuanDong.Open.Model.NetWorkCard card : list)
         {

             boolean equal = IsEqualLocalNet( card.GetIP4(), clientIP, card.GetSubnetMask4());
             if (equal)
             {
                 result = true;
                 break;
             }
                 
         }
         return result;
    }
    
    public static boolean IsEqualLocalNet(String ip1, String ip2, String subNetMask)
    {
        boolean result=false;
        int[] ip1Array = TtanseIPTextToIntArray(ip1);
        int[] ip2Array = TtanseIPTextToIntArray(ip2);
        int[] subnetMaskArray = TtanseIPTextToIntArray(subNetMask);
        int value1=0,value2=0;
   
            for (int i = 0; i < 4; i++)
            {
                value1+=ip1Array[i]&subnetMaskArray[i];
                value2+=ip2Array[i]&subnetMaskArray[i];
            }

            if (value1 == value2) { result = true; }
            return result;
    }
    
    /**
     * 获取网卡信息
     * @return
     * @throws UnknownHostException
     * @throws SocketException
     */
    //@Deprecated
    public static List<com.LuYuanDong.Open.Model.NetWorkCard> GetNetInfo() throws UnknownHostException, SocketException  
    {
    	List<com.LuYuanDong.Open.Model.NetWorkCard> list=new ArrayList<com.LuYuanDong.Open.Model.NetWorkCard>();
//       Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();  
//         while (interfaces.hasMoreElements()) {  
//           NetworkInterface ni = interfaces.nextElement();  
//           com.LuYuanDong.Open.Model.NetWorkCard model=new  com.LuYuanDong.Open.Model.NetWorkCard();
//           model.SetName(ni.getDisplayName());
//           model.SetDefaultGateway(ni.);
//         }  
    	//InetAddress localHost = Inet4Address.getLocalHost();
    	Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();   
    	while (networkInterfaces.hasMoreElements()) {   
            NetworkInterface networkInterface = networkInterfaces.nextElement();   

    	for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
    		com.LuYuanDong.Open.Model.NetWorkCard model=new  com.LuYuanDong.Open.Model.NetWorkCard(); 
    		
    		String ip= address.getAddress().getHostAddress();
    		if(ip.indexOf(".")>0){
    		model.SetIP4(ip);
    		String subnetMask="255.255.255.255";
    		short sSubnetMask=address.getNetworkPrefixLength();
    		if(sSubnetMask==8)
    			subnetMask="255.0.0.0";
    		else if(sSubnetMask==16)
    			subnetMask="255.255.0.0";
    		else if(sSubnetMask==24)
    			subnetMask="255.255.255.0";
    	   model.SetSubnetMask4(subnetMask);
    	   list.add(model);
    		}
    	}
    	}
         return list;
    }
    
    /**
     * 获取网卡IP信息，非127.0.0.1
     * @return
     * @throws UnknownHostException
     * @throws SocketException
     */
    public static   List<com.LuYuanDong.Open.Model.NetWorkCard> GetLocalNetInfo() {   
        try {   
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();   
            //Collection<InetAddress> addresses = new ArrayList<InetAddress>();   
            List<com.LuYuanDong.Open.Model.NetWorkCard> list=new ArrayList<com.LuYuanDong.Open.Model.NetWorkCard>();    
            while (networkInterfaces.hasMoreElements()) {   
                NetworkInterface networkInterface = networkInterfaces.nextElement();   
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();   
                while (inetAddresses.hasMoreElements()) {   
                	com.LuYuanDong.Open.Model.NetWorkCard model=new  com.LuYuanDong.Open.Model.NetWorkCard(); 
                    InetAddress inetAddress = inetAddresses.nextElement();   
                    if (!inetAddress.isLoopbackAddress()) {
                    String ip=inetAddress.getHostAddress();

                    if(ip.indexOf(".")>0){
                    	model.SetIP4(ip);
                    	String subnetMask="255.255.255.255";
//                		short sSubnetMask=address.getNetworkPrefixLength();
//                		if(sSubnetMask==8)
//                			subnetMask="255.0.0.0";
//                		else if(sSubnetMask==16)
//                			subnetMask="255.255.0.0";
//                		else if(sSubnetMask==24)
//                			subnetMask="255.255.255.0";
//                	   model.SetSubnetMask4(subnetMask);
                	   list.add(model);
                    	}
                    }
                }   
            }   
               
            return list;   
        } catch (SocketException e) {   
            throw new RuntimeException(e.getMessage(), e);   
        }   
    }   
    
    /**
     * 获取本机所有IP集合，非127.0.0.1
     * @return
     */
    public static Collection<InetAddress> getAllHostAddress() {   
        try {   
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();   
            Collection<InetAddress> addresses = new ArrayList<InetAddress>();   
               
            while (networkInterfaces.hasMoreElements()) {   
                NetworkInterface networkInterface = networkInterfaces.nextElement();   
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();   
                while (inetAddresses.hasMoreElements()) {   
                    InetAddress inetAddress = inetAddresses.nextElement();   
                    addresses.add(inetAddress);   
                    if (!inetAddress.isLoopbackAddress()) {
                    	com.cocopass.helper.CLoger.Debug("本机IP:"+inetAddress.getHostAddress());     
                           }   
                }   
            }   
               
            return addresses;   
        } catch (SocketException e) {   
            throw new RuntimeException(e.getMessage(), e);   
        }   
    }   
    
    public static int[] TtanseIPTextToIntArray(String ip)
    {
        String[] ipArray = ip.split("\\.");
        int[] result=new int[4];
        for (int i = 0; i < 4; i++)
        {
            result[i] = Integer.parseInt(ipArray[i]) ;
        }
        return result;
    }
    
    
    public static boolean IsNormalApp(com.LuYuanDong.Open.Model.App app)
    {
    	boolean result=true;
    	 
    	if(app==null||app.GetState()==0)
    		result=false;
    	return result;
    }
    
    public static String CreatRequestSign(String secret,com.LuYuanDong.Open.Model.Request request)
    {
        String result = "";

        if (secret!=null && !secret.equals(""))
        {
        	String prepareText = secret + 
        			request.GetAction() + 
        			request.getActionType()+
        			request.GetKey()  + 
        			request.GetParameters().toString() +
        			request.GetSerialNumber()+  //如果注销，有些请求不存在设备ID的情况
        			request.getTerminalID()+
        			request.GetTimeStamp() + 
        			request.GetVersion() + 
        			secret;
        	
            result = DigestUtils.md5Hex(prepareText);
            
            //LOG.info("待签名字符串："+prepareText);
            //LOG.info("签名字符串MD5值:"+result);
        }
        return result.toUpperCase();
    }
    
    public static String CreatResponseSign(String secret,com.LuYuanDong.Open.Model.Response response)
    {
        String result = "";

        if (secret!=null && !secret.equals(""))
        {
        	String prepareText = secret + 
        			response.GetBody() + 
        			response.GetResult()+ 
        			secret;
            result = DigestUtils.md5Hex(prepareText);
        }

        return result.toUpperCase();
    }
    
 
    public static boolean IsEqualSignValue(String secret,com.LuYuanDong.Open.Model.Request request)
    {
    	boolean result = true;
        String sign2 = CreatRequestSign(secret,request);
        if (!request.GetSign().toUpperCase().equals(sign2))
        {
            result = false;
        }
        return result;
    }
	 
}
