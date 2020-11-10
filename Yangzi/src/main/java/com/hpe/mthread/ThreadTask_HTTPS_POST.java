package com.hpe.mthread;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

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
public class ThreadTask_HTTPS_POST extends Thread{

	private String ThreadName="";
	private InputRequest request;
	private Variable var=null;
	private static java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
	
	public ThreadTask_HTTPS_POST(String name,InputRequest req){
		super(name);
		this.ThreadName=name;
		this.request=req;
	}
	

	@Override
	public void run() {
		
		List<String> lst_req= request.getJsonReq();
	    Map<String,String> headers = request.getHeaders();
	    
		try {
		TrustManager[] trustAllCerts = new TrustManager[1];
		TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        
		sc.init(null, trustAllCerts, null);

        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
        		sc,
                new String[] { "TLSv1.2" },
                null,
                //new String[] { "TLS_RSA_WITH_AES_256_GCM_SHA384","SSL_RSA_WITH_3DES_EDE_CBC_SHA" },
                //SSLConnectionSocketFactory.getDefaultHostnameVerifier()
                new ThreadPoolMain().new NullHostNameVerifier()
        		);
        
        RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(PropertyUtil.getInstance().getReadTimeout())      // set ReadTimeout
				.setConnectTimeout(PropertyUtil.getInstance().getConnectTimeout())   // set ConnectTimeout
				.setConnectionRequestTimeout(5000)
				.build();
        
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(sslsf)
                .build();
       
        HttpPost httpPost= new HttpPost(request.getUrl());
        if(PropertyUtil.getInstance().getTPS()==-1) {
          System.out.println("URL:"+request.getUrl());
   		  System.out.println("###################### Request Header #########################");
   		}
        // set headers
     	for (Map.Entry<String, String> entry : headers.entrySet()) {
     		httpPost.addHeader(entry.getKey(),entry.getValue());
     		if(PropertyUtil.getInstance().getTPS()==-1) {
     		  System.out.println("   "+entry.getKey()+":"+entry.getValue());
     		}
     	}
     	

     	// one by one send the request
     	for(String req:lst_req){
     		
			//replace environment variable
			if(req.contains("$(IMSI)")||req.contains("$(UNIQUEID)")||req.contains("$(TOKEN)")){
					req=this.replaceVariable(req);
			}
			
			if(PropertyUtil.getInstance().getTPS()==-1) {
				System.out.println("####################### Request  body  #######################");
				JSONArray jsonAry = (JSONArray) JSONValue.parseWithException(req);				
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String jsonString = gson.toJson(jsonAry);
		        System.out.println(jsonString);
				
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    GZIPOutputStream gzipos = new GZIPOutputStream(baos);
		    gzipos.write(req.getBytes());
		    gzipos.flush();
		    gzipos.close();
		    ByteArrayEntity byteEntity = new ByteArrayEntity(baos.toByteArray());
		    
		    httpPost.setEntity(byteEntity);
		    
		    // start send request:
		    long starttime=System.currentTimeMillis();
		    
		    CloseableHttpResponse response = httpclient.execute(httpPost);
		      
		    HttpEntity responseEntity = response.getEntity();
		    
		    int  statuscode = response.getStatusLine().getStatusCode();
		    String strResponseBode="";
		    if(statuscode == 200) {
		    	strResponseBode=EntityUtils.toString(responseEntity);
		    	if(strResponseBode.length()>10) {
		    		//System.out.println("Response:"+strResponseBode);
		    	}else {
		    		String strTimestamp=format2.format(new Date());
		    		System.err.println(strTimestamp+" Thread_"+this.ThreadName+ ":"+ statuscode+ " Response:"+ strResponseBode );
		    		
		    		ThreadPoolMain.synchronizedFailure();
		    	}
		    }else {
		    	String strTimestamp=format2.format(new Date());
		    	System.err.println(strTimestamp+" Thread_"+this.ThreadName+ ":"+ statuscode );
		    	
		    	ThreadPoolMain.synchronizedFailure();
		    }
		    
		    long endtime=System.currentTimeMillis();
			// calculate the duration between request and response
	        ThreadPoolMain.synchronizedMethod1(endtime - starttime);
		    
		    if(PropertyUtil.getInstance().getTPS()==-1) {
		    	System.out.println( "###################### Response Header #########################");
		    	 Header[] hs = response.getAllHeaders();
				 for (Header h : hs)
				 {
				    System.out.println("   "+h.getName() + ":" + h.getValue());
				  }
				 System.out.println("   StatusCode:"+statuscode);
				 System.out.println("   ProtocolVersion:"+response.getStatusLine().getProtocolVersion());
				 
				 
				 System.out.println("####################### Response  body #########################");
				 //JSONObject jsonObj = (JSONObject)(new JSONParser().parse(strResponseBode));
				 //System.out.println(jsonObj.toJSONString()); 
				 
				 JSONArray jsonAry = (JSONArray) JSONValue.parseWithException(strResponseBode);				
				 Gson gson = new GsonBuilder().setPrettyPrinting().create();
				 String jsonString = gson.toJson(jsonAry);
			     System.out.println(jsonString);
			        
		    }
			 
	     	}
     	
	}catch(Exception ex) {
		ex.printStackTrace();
		ThreadPoolMain.synchronizedFailure();
	}
		
	}
	
	private String getResponseBody(HttpEntity responseEntity) throws UnsupportedOperationException, IOException {
		String JSONResString="";
		InputStream is = responseEntity.getContent();
		GZIPInputStream gzin = new GZIPInputStream(is);
	      
	    Reader r = new InputStreamReader(gzin);
	    BufferedReader br = new BufferedReader(r);
	      
	    String line = br.readLine();
	    if (line != null)
	      {
	        while (line != null)
	        {
	        	JSONResString = JSONResString + line + "\n";
	            System.out.println(line);
	            line = br.readLine();
	        }
	        
	        //JSONObject sobj = new JSONObject();  
            //sobj = JSONObject.fromObject(conResult);
	      }  
		return JSONResString;
	}
	
	
	// Replace json request variable to real value
		private String replaceVariable(String jsonreq){
			String json=jsonreq;
			
			if(var==null) {
				var=ThreadPoolMain.getIMSI();
			}
				
			String IMSI=getIMSI(var.getIMSI());  // base64
			
			
			json=json.replaceAll("\\$\\(IMSI\\)", IMSI);                                //replace IMSI
			json=json.replaceAll("\\$\\(UNIQUEID\\)",(String)var.getUNIQUEID());        //replace UNIQUEID
			
			if(json.contains("$(TOKEN)")){
				long s=System.currentTimeMillis();
				String token=C3P0Util.getInstance().getToken(var.getIMSI());
				//System.out.println("Query Token="+token);
				String strTimestamp=format2.format(new Date());
				if(token.contains("MyToken")) {
					
					System.err.println(strTimestamp+" No Found Token in DEG_TOKENS  from IMSI:"+var.getIMSI());
				}
				
				long e=System.currentTimeMillis();
				if((e-s)>500){
					System.err.println(strTimestamp+" Query TOKEN over IMSI spend time:"+(e-s)+ " millisecond");
					
				}
				json=json.replaceAll("\\$\\(TOKEN\\)", token);       //replace TOKEN
			}
			return json;
		}
		
		// get NAI Format IMSI
		private String getIMSI(String imsi){
			//Base64 base64Codec = new Base64();
			byte[] payload = new byte[5];
			payload[0]=2;
			payload[1]=0;
			payload[2]=0;
			payload[3]=59;
			payload[4]=1;
			
			String mcc=imsi.substring(0, 3);
			String mnc=imsi.substring(3, 6);
			
			String sub ="0"+ imsi+"@nai.epc.mnc"+mnc+".mcc"+mcc+".3gppnetwork.org";
			
			byte[] newSubidBin = new byte[sub.length()+5];
			//payload
			for(int i =0; i<5; i++){
				newSubidBin[i] = payload[i];	
			}
			
			for(int i =5; i<newSubidBin.length; i++){
				newSubidBin[i] = sub.getBytes()[i-5];
			}
			return (Base64.encodeBase64String(newSubidBin));
		}
}
