package com.LuYuanDong.Open.Model;

public class NetWorkCard {
	 
        private String Name="";
        private String Mac="";
        private String IP4="";
        private String IP6="";
        private String SubnetMask4;
        private String DefaultGateway4;
        private String SubnetMask6;
        private String DefaultGateway6;
        /// <summary>
        /// 
        /// </summary>
        public void SetName(String name)
        {
            this.Name= name;
        }
        
        public String GetName()
        {
            return this.Name;
        }
        public void SetMac(String mac)
        {
            this.Name= mac;
        }
        
        public String GetMac()
        {
            return this.Mac;
        }
        public void SetIP4(String ip4)
        {
            this.IP4= ip4;
        }
        
        public String GetIP4()
        {
            return this.IP4;
        }
        public void SetIP6(String ip6)
        {
            this.IP6= ip6;
        }
        
        public String GetIP6()
        {
            return this.IP6;
        }
        
        public void SetSubnetMask4(String  subnetMask4)
        {
            this.SubnetMask4= subnetMask4;
        }
        
        public String  GetSubnetMask4()
        {
            return this.SubnetMask4;
        }
        
        public void SetDefaultGateway4(String  defaultgateway4)
        {
            this.DefaultGateway4= defaultgateway4;
        }
        
        public String  GetDefaultGateway4()
        {
            return this.DefaultGateway4;
        }
        
        public void SetSubnetMask6(String  subnetMask6)
        {
            this.SubnetMask4= subnetMask6;
        }
        
        public String  GetSubnetMask6()
        {
            return this.SubnetMask6;
        }
        
        public void SetDefaultGateway6(String  defaultgateway6)
        {
            this.DefaultGateway6= defaultgateway6;
        }
        
        public String  GetDefaultGateway6()
        {
            return this.DefaultGateway6;
        }

    
}
