package com.hpe.mthread;

import java.util.Date;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;


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
public class ThreadTask_HTTPS_GET extends Thread{
	
	private String ThreadName="";
	private InputRequest request;
    private Variable var=null;
    private static java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
    
	public ThreadTask_HTTPS_GET(String name,InputRequest req){
		super(name);
		this.ThreadName=name;
		this.request=req;
	}


    
	@Override
	public void run() {
		List<String> lst_req = request.getGetReq();
		String DEGCID="NULL";
		try {
				TrustManager[] trustAllCerts = new TrustManager[1];
				TrustManager tm = new miTM();
				trustAllCerts[0] = tm;
				SSLContext sc = SSLContext.getInstance("SSL");
				CookieStore cookieStore = new BasicCookieStore();

				sc.init(null, trustAllCerts, null);

				// Allow TLSv1 protocol only
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sc, new String[] { "TLSv1.2" }, null,
						// new String[] {
						// "TLS_RSA_WITH_AES_256_GCM_SHA384","SSL_RSA_WITH_3DES_EDE_CBC_SHA" },
						// SSLConnectionSocketFactory.getDefaultHostnameVerifier()
						new ThreadPoolMain().new NullHostNameVerifier());

				RequestConfig requestConfig = RequestConfig.custom()
						.setCookieSpec(CookieSpecs.STANDARD)
						.setSocketTimeout(PropertyUtil.getInstance().getReadTimeout()) // set ReadTimeout
						.setConnectTimeout(PropertyUtil.getInstance().getConnectTimeout()) // set ConnectTimeout
						.setConnectionRequestTimeout(5000).build();

				CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
						.setDefaultRequestConfig(requestConfig).setSSLSocketFactory(sslsf).build();

				
			for (String req : lst_req) {
				if(DEGCID!="NULL") {
					BasicClientCookie cookie = new BasicClientCookie("DEGCID",DEGCID);
					cookie.setVersion(0);
					cookie.setDomain("/DEGDROIDInterface/");
					cookie.setPath("/");
					cookieStore.addCookie(cookie);
				}
				
				// replace environment variable
				if (req.contains("$(IMSI)") || req.contains("$(IMSI_EAP)") || req.contains("$(TOKEN)")) {
					req = this.replaceVariable(req);
				}
				// System.out.println("Request:"+req);
				
				HttpGet httpGet = new HttpGet(req);
				
			    if(PropertyUtil.getInstance().getTPS()==-1) {
			    	  System.out.println("URL:"+request.getUrl());
			   		  System.out.println(  "###################### Request Header #########################");
			   		  List<Cookie> lst=cookieStore.getCookies();
						 for (Cookie h : lst)
						 {
						    System.out.println("   "+h.getName() + ":" + h.getValue());
						  }
						 
						 System.out.println("###################### Request body  ##########################");
						 System.out.println(req);
			    }
		
				
				
				// start http request:
				long starttime = System.currentTimeMillis();
				
				CloseableHttpResponse response = httpclient.execute(httpGet);

				HttpEntity responseEntity = response.getEntity();

				int statuscode = response.getStatusLine().getStatusCode();
				
				String strResponseBode="";

				if (statuscode == 200) {
					strResponseBode = EntityUtils.toString(responseEntity);
					if (strResponseBode.length() > 10) {
						//System.out.println("Response:" + strResponseBode);

					} else {
						String strTimestamp=format2.format(new Date());
						System.err.println(strTimestamp+" Thread_"+this.ThreadName+ ":"+ statuscode+ " Response:"+
						 strResponseBode );
						
						ThreadPoolMain.synchronizedFailure();
					}
				} else {
					    String strTimestamp=format2.format(new Date());
					    System.err.println(strTimestamp+" Thread_"+this.ThreadName+ ":"+ statuscode );
					
					ThreadPoolMain.synchronizedFailure();
				}
                
				Header[] hs = response.getAllHeaders();
				DEGCID="NULL";               //reset DEGCID
				for (Header h : hs) {
					String strValue=h.getValue();
					if(strValue.startsWith("DEGCID")) {
						int index=strValue.indexOf("=");
						DEGCID=strValue.substring(index+1);
					}
				}

				long endtime = System.currentTimeMillis();
				// calculate the duration between request and response
				ThreadPoolMain.synchronizedMethod1(endtime - starttime);
				
				
			    if(PropertyUtil.getInstance().getTPS()==-1) {
			    	System.out.println("##################### Response Header #########################");
					 for (Header h : hs)
					 {
					    System.out.println("   "+h.getName() + ":" + h.getValue());
					  }
					 System.out.println("   StatusCode:"+statuscode);
					 System.out.println("   ProtocolVersion:"+response.getStatusLine().getProtocolVersion());
					 
					 
					 System.out.println("####################### Response  body #######################");
					 System.out.println(strResponseBode); 
			    }
			}
			} catch (Exception ex) {
				ex.printStackTrace();
				ThreadPoolMain.synchronizedFailure();
			}
		}

	// Replace json request variable to real value
	private String replaceVariable(String req)  {
		String strRequest = req;
		
//		long var_length=(long)PropertyUtil.getInstance().getVariable().size();
//		Random ran1 = new Random();
//		int ran=(int) ran1.nextInt((int)var_length);
//		Variable var=(Variable)PropertyUtil.getInstance().getVariable().get(ran);
//						
		//System.out.println("IMSI:"+var.getIMSI());
		
		
		if(var==null) {
			var=ThreadPoolMain.getIMSI();
		}
	    
		if(strRequest.contains("$(IMSI_EAP)")){    //NAI
			String IMSI=getIMSI(var.getIMSI());  
			strRequest=strRequest.replaceAll("\\$\\(IMSI_EAP\\)", IMSI);   
		}
		
		if(strRequest.contains("$(IMSI)")) { // base64
			String IMSI=getIMSIBase64(var.getIMSI()); 
			strRequest=strRequest.replaceAll("\\$\\(IMSI\\)", IMSI);  
		}
		
		if(strRequest.contains("$(TOKEN)")){
			long s=System.currentTimeMillis();
			String token=C3P0Util.getInstance().getAndroidToken(var.getIMSI());
			
			if(token.contains("MyToken")) {
				String strTimestamp=format2.format(new Date());
				System.err.println(strTimestamp+" No Found Token in DEGDROID_TOKENS  from IMSI:"+var.getIMSI());
			}
			//System.out.println("Query Token="+token);
			long e=System.currentTimeMillis();
			if((e-s)>500){
				String strTimestamp=format2.format(new Date());
				System.err.println(strTimestamp+" Query Android TOKEN over IMSI spend time:"+(e-s)+ " millisecond");
				//ThreadPoolMain.log.append("Query Android TOKEN over IMSI spend time:"+(e-s)+ " millisecond"+" \n");
			}
			
			try {
				//token=URLEncoder.encode(token,"UTF-8"); 
				token=token.replace("/", "%2F").replace("+", "%2B");
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			strRequest=strRequest.replaceAll("\\$\\(TOKEN\\)", token);       //replace TOKEN
		}
		return strRequest;
	}
	
	//get base64 IMSI
	private String getIMSIBase64(String imsi) {
		String mcc = imsi.substring(0, 3);
		String mnc = imsi.substring(3, 6);
		String sub = "0" + imsi + "@nai.epc.mnc" + mnc + ".mcc" + mcc + ".3gppnetwork.org";
		return (Base64.encodeBase64String(sub.getBytes()));
		
	}

	// get NAI Format IMSI
	private String getIMSI(String imsi) {
		// Base64 base64Codec = new Base64();
		byte[] payload = new byte[5];
		payload[0] = 2;
		payload[1] = 0;
		payload[2] = 0;
		payload[3] = 59;
		payload[4] = 1;

		String mcc = imsi.substring(0, 3);
		String mnc = imsi.substring(3, 6);

		String sub = "0" + imsi + "@nai.epc.mnc" + mnc + ".mcc" + mcc + ".3gppnetwork.org";

		byte[] newSubidBin = new byte[sub.length() + 5];
		// payload
		for (int i = 0; i < 5; i++) {
			newSubidBin[i] = payload[i];
		}

		for (int i = 5; i < newSubidBin.length; i++) {
			newSubidBin[i] = sub.getBytes()[i - 5];
		}
		return (Base64.encodeBase64String(newSubidBin));
	}
}
