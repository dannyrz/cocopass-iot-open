<%@ page import="java.io.*,java.util.*,com.cocopass.helper.*,com.LuYuanDong.Open.*"%>
<%@ page language="java"   contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
response.setContentType("text/plain; charset=utf-8"); 
//String path=application.getRealPath(request.getRequestURI());  
//String dir=new File(path).getParent(); 
//String test=request.getServletContext().
Service service=new Service();
String myResponse=service.GetResponseText(request);
service=null;
out.clear(); 

//byte[] a=new byte[]{0,0,0,0,127,127,127,0};
//long b=CByte.bytesToLong(a);
//String c=String.valueOf(b);
%><%=myResponse%>