package com.hpe.kit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateSQL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String sql="INSERT INTO `deg_subscribers` (`DEG_MDN`, `DEG_IMSI`, `DEG_MIN`, `DEG_CUSTIDINT`, `DEG_CUSTCATIDINT`, `DEG_CUSTSTATUS`, `DEG_EVENTTIMESTAMPINTEGER`, `DEG_ESN`, `DEG_MEID`, `DEG_IMEI`, `DEG_ICCID`, `DEG_PROVTIME`, `DEG_PROVCHANGETIME`, `DEG_LASTUPDATE`, `DEG_ENTITLEMENT_LIST`, `DEG_COMPANION_DEVICE_COUNT`, `DEG_ROAMING_IMSI`, `TT_AUTOTIMESTAMP`, `DEG_ENCRYPTED_MDN`) VALUES";
		//String sql=sql+""
		int sqlcount=10000;
		long mdn=18980200000L;
		long imsi=460030912100000L;
		long iccid=899660133003257771L;
		
		try{
		 File writeName = new File("C:\\Temp\\1.sql");
		 File variable = new File("C:\\Temp\\2.sql");
		 writeName.createNewFile();
		 variable.createNewFile();
		 FileWriter writer = new FileWriter(writeName);
		 FileWriter writer_var = new FileWriter(variable);
		 BufferedWriter out = new BufferedWriter(writer);
		 BufferedWriter out_var = new BufferedWriter(writer_var);
		 out.write(sql+"\r\n");
		for(int i=0;i<sqlcount;i++){
			String tmp="("+mdn+",'"+imsi+"', 0, '3', 3, 'AE', '', '96662', '64335', '87771', '"+iccid+"', '2019-12-13 16:28:47', '2020-07-08 07:31:58', '2020-07-08 07:31:58', 'VoWiFi=6100,iCloudVoWiFi=6101,tethering=6100,VoLTE=6100', '5', NULL, NULL, ''),";
			
			out_var.write(imsi+","+mdn+"\r\n");
			//sql=sql+tmp;
			mdn++;
			imsi++;
			iccid++;
			System.out.println(tmp);
			out.write(tmp+"\r\n"); // 
		}
		 out.flush();
		out_var.flush();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
