package com.LuYuanDong.Open.Model;

public   class App
{
    public App()
    { }
 
    private int ID=0;
    private int Type=0;
    private String Name="";
    private String IP="";
    private String Key="";
    private String Secret="";
    private String Host="";
    private int Port=0;
    private String Processname="";
    private String Description="";
    private int State=1;
    private int AuthMode=1;
    private String NoticeURL="";
    
    public void SetAuthMode(int authMode)
    {
        this.AuthMode= authMode;
    }
    public int GetAuthMode()
    {
        return this.AuthMode;
    }
    public void SetState(int state)
    {
        this.State= state;
    }
    public int GetState()
    {
        return this.State;
    }
    public void SetNoticeURL(String noticeURL)
    {
        this.NoticeURL= noticeURL;
    }
    public String GetNoticeURL()
    {
        return this.NoticeURL;
    } 
    public void SetProcessname(String processname)
    {
        this.Processname= processname;
    }
    public String GetProcessname()
    {
        return this.Processname;
    } 
    
    public void SetDescription(String description)
    {
        this.Description= description;
    }
    public String GetDescription()
    {
        return this.Description;
    } 
    
    public void SetIP(String ip)
    {
        this.IP= ip;
    }
    public String GetIP()
    {
        return this.IP;
    } 
    public void SetKey(String key)
    {
        this.Key= key;
    }
    public String GetKey()
    {
        return this.Key;
    }
    
    public void SetPort(int port)
    {
        this.Port= port;
    }
    public int GetPort()
    {
        return this.Port;
    } 
    
    public void SetHost(String host)
    {
        this.Host= host;
    }
    public String GetHost()
    {
        return this.Host;
    } 
    
    public void SetSecret(String secret)
    {
        this.Secret= secret;
    }
    public String GetSecret()
    {
        return this.Secret;
    }
    
    public void SetID(int id)
    {
        this.ID= id;
    }
    
    public int GetID()
    {
        return this.ID;
    }
    
    public void SetType(int type)
    {
        this.Type= type;
    }
    public int GetType()
    {
        return this.Type;
    } 
    
    public void SetName(String name)
    {
        this.Name= name;
    }
    public String GetName()
    {
        return this.Name;
    } 
}
