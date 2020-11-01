package com.hpe.mthread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hpe.kit.C3P0Util;
import com.hpe.kit.PropertyUtil;
import com.hpe.model.InputRequest;
import com.hpe.model.Variable;
import com.hpe.performance.ThreadPoolMain;

/**
 * 
 * @author wanxingc
 *
 */
public class ThreadTask_APP extends Thread {
	private String Method = "POST";
	private String ThreadName="";
	private Map<String,String> headers = new HashMap();
	private InputRequest req;
	
	public ThreadTask_APP(String name,InputRequest req){
		super(name);

		this.ThreadName=name;
		this.headers=req.getHeaders();
		this.req=req;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//System.out.println("Thread:"+this.getName().toString() + " success");
			try {
				
				URL url = new URL(req.getUrl());
				List<String> lst_req= req.getJsonReq();
				// send all requests 
				for(String reqStr:lst_req){
					
					//System.out.println(req);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					
					conn.setRequestMethod(Method);       // POST GET PUT DELETE	
					
					// it is for header setting
					java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMdd'T'hhmmssZ");
					String strAuthenticationTime=format2.format(new Date());
					String strAuthentication="";
					String strAppName="";
					String strGlobalID=strAuthenticationTime;
					String strKey="";
					
					for (Map.Entry<String, String> entry : headers.entrySet()) {
					    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
						if(entry.getKey().equals("X-Header-Authorization")) {
							strAuthentication="NULL";
							continue;
						}else if(entry.getKey().equals("X-AuthorizationTime")) {
							conn.setRequestProperty("X-AuthorizationTime",strAuthenticationTime);
							
						}else if(entry.getKey().equals("X-Global-Transaction-ID")) {
							conn.setRequestProperty("X-Global-Transaction-ID",strGlobalID);
							
						}else if(entry.getKey().equals("X-APP-NAME")){
							strAppName=entry.getValue().trim();
							conn.setRequestProperty("X-APP-NAME",entry.getValue());
						}else if(entry.getKey().equals("key")) {
							strKey=entry.getValue().trim();       // get key for sign authentication
						}else{
							conn.setRequestProperty(entry.getKey(),entry.getValue());
						}
					}
					
					//set header authorization
					if(strAuthentication.equals("NULL")) {
						String strConn= strAuthenticationTime+"@"+strAppName+"@"+strGlobalID;
						strAuthentication=Signature(strConn,strKey);
						conn.setRequestProperty("X-Header-Authorization",strAuthentication);
					}
					
					conn.setConnectTimeout(PropertyUtil.getInstance().getConnectTimeout());
					conn.setReadTimeout(PropertyUtil.getInstance().getReadTimeout());// 
					
					conn.setDoOutput(true);
					
					// replace variable environment
					reqStr=replaceVariable(reqStr);
					
					
					 if(PropertyUtil.getInstance().getTPS()==-1) {
					   System.out.println("URL:"+req.getUrl());
				   	   System.out.println("###################### Request Header #########################");
				   	   Iterator<?> it = conn.getRequestProperties().keySet().iterator();
				   			  
				   	  
					   while (it.hasNext()) {
					   	    String name = (String) it.next();
					   	    String value=conn.getRequestProperty(name);
					   	    System.out.println("  "+name+":"+value);
					   }
					   System.out.println("####################### Request   body  #######################");
					   JSONObject jsonObj = (JSONObject)(new JSONParser().parse(reqStr));
					   Gson gson = new GsonBuilder().setPrettyPrinting().create();
					   String jsonString = gson.toJson(jsonObj);
					   System.out.println(jsonString);
					 }
				
					//start send request:
					long starttime=System.currentTimeMillis();
					
					OutputStream os = conn.getOutputStream();
					os.write(reqStr.getBytes());
					os.flush();
					os.close();
					
					
					
					// waiting for the response
					int responseCode = conn.getResponseCode();
					StringBuffer response=new StringBuffer();
					
					if (responseCode == HttpURLConnection.HTTP_OK) {    // success
						String encoding = conn.getContentEncoding();
//						if(encoding==null){
//							System.err.println(this.ThreadName+":"+ responseCode +"    "+ conn.getResponseMessage() +" respnse bode is null");
//							//ThreadPoolMain.log.append("Thread_"+this.ThreadName+ ":"+ responseCode+ "Response:"+ strResponseBode +"\n");
//							ThreadPoolMain.synchronizedFailure();
//						
//					    }else{
					    	// read response content
							BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
							String inputLine;
							response = new StringBuffer();
							while ((inputLine = in.readLine()) != null) {
								response.append(inputLine);
							}
							in.close();
							
							// print result
							//System.out.println(this.ThreadName+":"+ responseCode +" "+ response.toString());
					   // }
						
					} else {
						System.err.println(this.ThreadName+":"+ responseCode +"    "+ conn.getResponseMessage());
						//calcure the failure requests;
						ThreadPoolMain.synchronizedFailure();
					}
					
					long endtime=System.currentTimeMillis();
					// calculate the duration between request and response
					ThreadPoolMain.synchronizedMethod1(endtime - starttime);
					
					if(PropertyUtil.getInstance().getTPS()==-1) {
				    	System.out.println("###################### Response Header #########################");
				    	Iterator<?> it = conn.getHeaderFields().keySet().iterator();
					   	  
						   while (it.hasNext()) {
						   	    String name = (String) it.next();
						   	    String value=conn.getHeaderField(name);
						   	    System.out.println("  "+name+":"+value);
						   }
						 System.out.println("  StatusCode:"+responseCode);
						 System.out.println("  ProtocolVersion:"+conn.getResponseMessage());
						 
						 
						 System.out.println("####################### Response  body #######################");
						 JSONObject jsonObj = (JSONObject)(new JSONParser().parse(response.toString()));
						 Gson gson = new GsonBuilder().setPrettyPrinting().create();
						 String jsonString = gson.toJson(jsonObj);
						 System.out.println(jsonString); 
				    }
					
					//long expired=conn.getExpiration();
					//System.out.println("expired:"+expired);		
					conn.disconnect();	
				}
				//Thread.sleep(1000);
			} catch (Exception ex) {
				ThreadPoolMain.synchronizedFailure();
				ex.printStackTrace();
			}
	}
	
	// Replace json request variable to real value
	private String replaceVariable(String req) {
		String strRequest = req;
		Variable var=ThreadPoolMain.getIMSI();
	
		if(strRequest.contains("$(TIMESTAME)")){    //NAI
			java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
			String strAuthenticationTime=format2.format(new Date());
			
			int a= new java.util.Random().nextInt(9000)+1000; //get 3 digital number
			strAuthenticationTime=strAuthenticationTime+""+String.valueOf(a);
			strRequest=strRequest.replaceAll("\\$\\(TIMESTAME\\)", strAuthenticationTime);   
		}
		
		if(strRequest.contains("$(TOKEN)")){
			long s=System.currentTimeMillis();
			String token=C3P0Util.getInstance().getAppToken(var.getIMSI());
			
			long e=System.currentTimeMillis();
			if((e-s)>500){
				System.err.println("Query APP TOKEN over IMSI spend time:"+(e-s)+ " millisecond");
						
			}
			strRequest=strRequest.replaceAll("\\$\\(TOKEN\\)", token);       //replace TOKEN
		}
		return strRequest;
	}
	
private  String Signature(String strSource,String strkey){
	
	try {
		byte[] hmacData = null;

	    SecretKeySpec secretKey = new SecretKeySpec(strkey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(secretKey);
        hmacData = mac.doFinal(strSource.getBytes("UTF-8"));
        
        return Base64.encodeBase64String(hmacData);
	}catch(Exception ex) {
		ex.printStackTrace();
	}
	return null;
	}

}