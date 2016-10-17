package com.LuYuanDong.Open.DAL;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import com.mysql.jdbc.Connection;
import java.sql.*;
import java.util.Properties;  
import com.mysql.jdbc.Driver;  
//http://blog.csdn.net/educast/article/details/42501489
public class MySql {
	// ����Ҫʹ�õı���  
    private static Connection conn = null;  
    private static PreparedStatement ps = null;  
    private static ResultSet rs = null;  
    private static CallableStatement cs = null;  
  
    private static String driver = "com.mysql.jdbc.Driver";  
    private static String url = "jdbc:mysql://192.168.99.154:3306/LuYuanDongOpen_db";  
    private static String userName = "LuYuanDongOpen";  
    private static String password = "LuYuanDongOpen";  
  
    private static Properties pp = null;  
    private static InputStream fis = null;  
  
    public static Connection getConn() {  
        return conn;  
    }  
  
    public static PreparedStatement getPs() {  
        return ps;  
    }  
  
    public static ResultSet getRs() {  
        return rs;  
    }  
     
    public static CallableStatement getCs() {  
        return cs;  
    }  
  
    // ����������ֻ��Ҫһ��  
    static {  
        try {  
            // �������ļ�dbinfo.properties�ж�ȡ������Ϣ  
//             pp = new Properties();  
//             //fis = new FileInputStream("/app.properties"); 
//             fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties");
//             pp.load(fis);  
//        	 driver = pp.getProperty("driver");  
//             url = pp.getProperty("url");  
//             userName = pp.getProperty("userName");  
//             password = pp.getProperty("password");  
//            
////             CProperties cp=new CProperties();
////            CProperties.SetProperties();
////            driver = CProperties.GetValue("driver");//pp.getProperty("driver");  
////            url = CProperties.GetValue("url");//pp.getProperty("url");  
////            userName = CProperties.GetValue("userName");//pp.getProperty("userName");  
////            password = CProperties.GetValue("password");//pp.getProperty("password");  
            Class.forName(driver);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (fis != null)  
                try {  
                    fis.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            fis = null;  
  
        }  
    }  
  
    // �õ�����  
    public static Connection getConnection() {  
        try {  
        	// System.out.println("config:"+url+userName+ password);
            conn = DriverManager.getConnection(url, userName, password);  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        return conn;  
    }  
  
    // �������update/delete/insert  
    public static void executeUpdateMultiParams(String[] sql,  
            String[][] parameters) {  
        try {  
            // �������  
            conn = getConnection();  
            // ���ܴ�����sql���  
            conn.setAutoCommit(false);  
            for (int i = 0; i < sql.length; i++) {  
                if (parameters[i] != null) {  
                    ps = conn.prepareStatement(sql[i]);  
                    for (int j = 0; j < parameters[i].length; j++)  
                        ps.setString(j + 1, parameters[i][j]);  
                }  
                ps.executeUpdate();  
            }  
            conn.commit();  
        } catch (Exception e) {  
            e.printStackTrace();  
            try {  
                conn.rollback();  
            } catch (SQLException e1) {  
                e1.printStackTrace();  
            }  
            throw new RuntimeException(e.getMessage());  
        } finally {  
            // �ر���Դ  
            close(rs, ps, conn);  
        }  
    }  
  
    // update/delete/insert  
    // sql��ʽ:UPDATE tablename SET columnn = ? WHERE column = ?  
    public static void executeUpdate(String sql, String[] parameters) {  
        try {  
            // 1.����һ��ps  
            conn = getConnection();  
            ps = conn.prepareStatement(sql);  
            // ������ֵ  
            if (parameters != null)  
                for (int i = 0; i < parameters.length; i++) {  
                    ps.setString(i + 1, parameters[i]);  
                }  
            // ִ��  
            ps.executeUpdate();  
        } catch (SQLException e) {  
            e.printStackTrace();// �����׶�  
            throw new RuntimeException(e.getMessage());  
        } finally {  
            // �ر���Դ  
            close(rs, ps, conn);  
        }  
    }  
  
    // select  
    public static ResultSet executeQuery(String sql, String[] parameters) {  
        ResultSet rs = null;  
        try {  
            conn = getConnection();  
            ps = conn.prepareStatement(sql);  
            if (parameters != null) {  
                for (int i = 0; i < parameters.length; i++) {  
                    ps.setString(i + 1, parameters[i]);  
                }  
            }  
            rs = ps.executeQuery();  
        } catch (SQLException e) {  
            e.printStackTrace();  
            throw new RuntimeException(e.getMessage());  
        } finally {  
  
        }  
        return rs;  
    }  
  
    // �����޷���ֵ�洢����  
    // ��ʽ�� call procedureName(parameters list)  
    public static void callProc(String sql, String[] parameters) {  
        try {  
            conn = getConnection();  
            cs = conn.prepareCall(sql);  
            // ������ֵ  
            if (parameters != null) {  
                for (int i = 0; i < parameters.length; i++)  
                    cs.setObject(i + 1, parameters[i]);  
            }  
            cs.execute();  
        } catch (Exception e) {  
            e.printStackTrace();  
            throw new RuntimeException(e.getMessage());  
        } finally {  
            // �ر���Դ  
            close(rs, cs, conn);  
        }  
    }  
  
    // ���ô�������������з���ֵ�Ĵ洢����  
    public static CallableStatement callProcInput(String sql, String[] inparameters) {  
        try {  
            conn = getConnection();  
            cs = conn.prepareCall(sql);  
            if(inparameters!=null)  
                for(int i=0;i<inparameters.length;i++)  
                    cs.setObject(i+1, inparameters[i]);                 
            cs.execute();  
        }  
        catch (Exception e) {  
            e.printStackTrace();  
            throw new RuntimeException(e.getMessage());  
        }finally{  
             
        }  
        return cs;  
    }  
     
    // �����з���ֵ�Ĵ洢����  
    public static CallableStatement callProcOutput(String sql,Integer[] outparameters) {  
        try {  
            conn = getConnection();  
            cs = conn.prepareCall(sql);                     
            //��out������ֵ  
            if(outparameters!=null)  
                for(int i=0;i<outparameters.length;i++)  
                    cs.registerOutParameter(i+1, outparameters[i]);  
            cs.execute();  
        }  
        catch (Exception e) {  
            e.printStackTrace();  
            throw new RuntimeException(e.getMessage());  
        }finally{  
             
        }  
        return cs;  
    }  
  
    public static void close(ResultSet rs, Statement ps, Connection conn) {  
        if (rs != null)  
            try {  
                rs.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        rs = null;  
        if (ps != null)  
            try {  
                ps.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        ps = null;  
        if (conn != null)  
            try {  
                conn.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        conn = null;  
    }  
}