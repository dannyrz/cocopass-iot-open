package com.LuYuanDong.Open;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.LuYuanDong.Open.Model.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class Service {
	private static final Logger LOG = Logger.getLogger(Service.class.getName());
	Gson gson = new Gson();
	javax.servlet.http.HttpServletRequest request;
	Response response;
	Request myRequest;
	com.LuYuanDong.Open.Model.App app;

	public String GetResponseText(javax.servlet.http.HttpServletRequest request)
			throws UnknownHostException, SocketException {
		this.request = request;
		response = new Response();
		// {"Key":"","TimeStamp":0,"Version":0.0,"DataTypeID":0,"Body":[{"ID":0,"Type":0,"Name":"","IP":"","Key":"","Secert":"","Host":"","Port":0,"Processname":"","Description":"","State":1,"NoticeURL":""}],"Sign":""}

		// String
		// strMobileNO=com.cocopass.helper.CString.FormatFixLong(13539838277L,
		// 12);
		// System.out.println(strMobileNO);
		// byte[]
		// arrayMobileNO=com.cocopass.helper.CByte.hexStringToBytes(strMobileNO);
		//
		// System.out.println(com.cocopass.helper.CByte.BytesToHexString(arrayMobileNO));

		InputStream is;
		String content = "";
		try {
			is = request.getInputStream();
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = is.read(b)) != -1) {
				content = new String(b, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
		}

		//com.LuYuanDong.Open.BLL.App.GetModel("wedfwef34645w4cbnm");
		
		/**
		 * 临时测试 // if(!content.equals("")) // { ////
		 * com.LuYuanDong.Open.Model.PushData pd=new
		 * com.LuYuanDong.Open.Model.PushData(); ////
		 * com.LuYuanDong.Open.Model.App apps=new
		 * com.LuYuanDong.Open.Model.App(); //// List
		 * <com.LuYuanDong.Open.Model.App> list= new ArrayList(); ////
		 * list.add(apps); //// pd.SetBody(list); //
		 * com.cocopass.iot.model.PushObject pd=gson.fromJson(content,
		 * com.cocopass.iot.model.PushObject.class); // String a =
		 * pd.GetBody().toString(); // response.SetBody(pd); // return
		 * ReturnResponseText(); // }
		 */

		LOG.info("receive txt:" + content);
		//System.out.println("receive txt:" + content +"/r/n");

		/*
		 * 判断请求数据是否符合规范,包括判断action是否有点 (.)
		 */
		myRequest = GetRequest(content);

		if (!Verification.IsLegalRequest(myRequest)) {
			response.SetResult(1);
			return ReturnResponseText();
			// response.SetBody(myRequest.GetIP());//myRequest.GetParameters().get("GPSID")
		}

		if (Config.RunMode == 0) {
			
			String key="APP:"+myRequest.GetKey();
			String secret=com.cocopass.helper.CRedis.getMapValue(key, "Secret");
			String authMode=com.cocopass.helper.CRedis.getMapValue(key, "AuthMode");
			String state=com.cocopass.helper.CRedis.getMapValue(key, "State");
			
			if(!com.cocopass.helper.CString.IsNullOrEmpty(secret)
					&&!com.cocopass.helper.CString.IsNullOrEmpty(authMode)
					&&!com.cocopass.helper.CString.IsNullOrEmpty(state))
			{
				app=new com.LuYuanDong.Open.Model.App();
				app.SetAuthMode(Integer.parseInt(authMode));
				app.SetKey(myRequest.GetKey());
				app.SetSecret(secret);
				app.SetState(Integer.parseInt(state));
			}
			else
			{
				app = com.LuYuanDong.Open.BLL.App.GetModel(myRequest.GetKey());
			}
			
		} else if (Config.RunMode == 1) {
			String jsonApp = Global.iProperties.GetValue("app");
			app = gson.fromJson(jsonApp, com.LuYuanDong.Open.Model.App.class);
		}
		
		if (app == null) {
			response.SetResult(2);
			return ReturnResponseText();
		}

		// LOG.info("app json:" + gson.toJson(app));
		/*
		 * 判断来源方是否已注册及正常运行
		 */
		if (!Verification.IsNormalApp(app)) {
			response.SetResult(3);
			return ReturnResponseText();
		}

		/*
		 * 验证服务器端校验码与客户端提交是否相同 如果在内网，是否需要开放无需认证校验码？如果没有校验码，相当于很弱的IP信任安全机制
		 */
		if (!Verification.IsEqualSignValue(app.GetSecret(), myRequest)) {
			response.SetResult(4);
			return ReturnResponseText();
		}

		int authMode = app.GetAuthMode(); // 获取认证方式
		HttpSession session = request.getSession();// 获取Session

		/*
		 * 认证方式1:内网+已登记内网IP,无需Session 认证方式2:外网+已登记外网IP,需要Session
		 * 认证方式3:不管内网外网,无需登记IP,无需登陆Session,但时间戳一次有效
		 */
		if (authMode == 1 || authMode == 2) {
			if (app.GetIP().indexOf(myRequest.GetIP()) < 0) {
				response.SetResult(5);
				return ReturnResponseText();
			}

			boolean isInOneNet = Verification.IsEqualLocalNet(myRequest.GetIP());

			/*
			 * 如果不在内网，并且不是登陆请求的Action就要判断Session
			 */
			if (!isInOneNet && !myRequest.GetAction().equals("App.Login")) {
				// 并且不是登陆请求就要判断Session
				if ((session == null || session.getAttribute("App") == null)) {
					response.SetResult(6);
					return ReturnResponseText();
				}
			}

		} else if (authMode == 3) {
//			String key = myRequest.GetSerialNumber();
//			// 不存在
//
//			if(session.getAttribute(key)==null){
//			//if (com.cocopass.helper.CRedis.getMapValue(key, "RequestTimeStamp")== null) {
//				// LocalTime time = LocalTime.now(ZoneId.of("Etc/UTC"));
//				// long systemTimeStamp=time.now().
//				// System.out.println("system timestamp:" + systemTimeStamp);
//				long snap = Math.abs(myRequest.GetTimeStamp() * 1000 - (new Date().getTime()));// -Config.TimeZone*3600*1000
//				if (snap > (5 * 60 * 1000)) {
//					response.SetResult(7);
//					return ReturnResponseText();
//				} else {
//					session.setAttribute(key, myRequest.GetTimeStamp());
//					//com.cocopass.helper.CRedis.SetMapValue(key, "RequestTimeStamp", String.valueOf(myRequest.GetTimeStamp()));
//				}
//			} else // 存在
//			{
//				response.SetResult(8);
//				return ReturnResponseText();
//			}
		}

		if(myRequest.getActionType()==1){
			String[] arrayTerminalID= {myRequest.getTerminalID()};
			if(myRequest.getTerminalID().indexOf(",")>0)
				arrayTerminalID=myRequest.getTerminalID().split(",");
			for(String tid : arrayTerminalID){
				String key="Terminal:"+tid;
				String latestInstructTime=com.cocopass.helper.CRedis.getMapValue(key, "LatestInstructTime");
				if(!com.cocopass.helper.CString.IsNullOrEmpty(latestInstructTime)){
					long snap = Math.abs(Long.parseLong(latestInstructTime) - (new Date().getTime()));
					if(snap<10000)
					{
						response.SetResult(12);
						return ReturnResponseText();
					}
					else{
						com.cocopass.helper.CRedis.SetMapValue(key, "LatestInstructTime",String.valueOf(new Date().getTime()));
					}
				}else{
					com.cocopass.helper.CRedis.SetMapValue(key, "LatestInstructTime",String.valueOf(new Date().getTime()));
				}
			}
		}
		// 判断是否为登陆请求，如果不是登陆请求并且 Session["App"]为空 并且不在同一子网，则为非法

		Class<?> iClass = null;
		try {

			// 调用Person类中的sayChina方法
			String[] action = myRequest.GetAction().split("\\.");
			String typeName = "com.LuYuanDong.Open.API." + action[0];
			String methodName = action[1];
			iClass = Class.forName(typeName); // Object instance=
			Constructor<?> cons[] = iClass.getConstructors();
			Object instance = cons[0].newInstance(myRequest);
			Method method = iClass.getMethod(methodName);
			// System.out.println(method);
			response = (com.LuYuanDong.Open.Model.Response) method.invoke(instance);

		} catch (ClassNotFoundException e) {
			response.SetResult(9);
			LOG.error(e.getMessage());

		} catch (NoSuchMethodException e) {
			response.SetResult(10);
			LOG.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.SetResult(11);
			LOG.error(e.getMessage());
		}

		return ReturnResponseText();
	}

	String ReturnResponseText() {
		String secret = "";
		if (app != null) {
			secret = app.GetSecret();
		}
		String sign = Verification.CreatResponseSign(secret, this.response);
		this.response.SetSign(sign);
		String responseTxt = gson.toJson(this.response);
		return responseTxt;
	}

	Request GetRequest(String txt) {
		Request request = null;
		try {
			request = gson.fromJson(txt, Request.class);// new
														// TypeToken<Request>(){}.getType()
		} catch (Exception e) {
			com.cocopass.helper.CLoger.Error(e.getMessage());
		}
		if (request != null) {
			request.SetIP(this.request.getRemoteAddr());
		}
		return request;
	}
}
