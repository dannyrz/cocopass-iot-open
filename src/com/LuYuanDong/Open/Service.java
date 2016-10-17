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
		 * ��ʱ���� // if(!content.equals("")) // { ////
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
		 * �ж����������Ƿ���Ϲ淶,�����ж�action�Ƿ��е� (.)
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
		 * �ж���Դ���Ƿ���ע�ἰ��������
		 */
		if (!Verification.IsNormalApp(app)) {
			response.SetResult(3);
			return ReturnResponseText();
		}

		/*
		 * ��֤��������У������ͻ����ύ�Ƿ���ͬ ������������Ƿ���Ҫ����������֤У���룿���û��У���룬�൱�ں�����IP���ΰ�ȫ����
		 */
		if (!Verification.IsEqualSignValue(app.GetSecret(), myRequest)) {
			response.SetResult(4);
			return ReturnResponseText();
		}

		int authMode = app.GetAuthMode(); // ��ȡ��֤��ʽ
		HttpSession session = request.getSession();// ��ȡSession

		/*
		 * ��֤��ʽ1:����+�ѵǼ�����IP,����Session ��֤��ʽ2:����+�ѵǼ�����IP,��ҪSession
		 * ��֤��ʽ3:������������,����Ǽ�IP,�����½Session,��ʱ���һ����Ч
		 */
		if (authMode == 1 || authMode == 2) {
			if (app.GetIP().indexOf(myRequest.GetIP()) < 0) {
				response.SetResult(5);
				return ReturnResponseText();
			}

			boolean isInOneNet = Verification.IsEqualLocalNet(myRequest.GetIP());

			/*
			 * ����������������Ҳ��ǵ�½�����Action��Ҫ�ж�Session
			 */
			if (!isInOneNet && !myRequest.GetAction().equals("App.Login")) {
				// ���Ҳ��ǵ�½�����Ҫ�ж�Session
				if ((session == null || session.getAttribute("App") == null)) {
					response.SetResult(6);
					return ReturnResponseText();
				}
			}

		} else if (authMode == 3) {
//			String key = myRequest.GetSerialNumber();
//			// ������
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
//			} else // ����
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
		// �ж��Ƿ�Ϊ��½����������ǵ�½������ Session["App"]Ϊ�� ���Ҳ���ͬһ��������Ϊ�Ƿ�

		Class<?> iClass = null;
		try {

			// ����Person���е�sayChina����
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
