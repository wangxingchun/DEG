package com.hpe;

import java.util.LinkedList;
import java.util.List;

import com.fcm.FireBaseUtil;

/**
FCM
* @author wanxingc
* @version 
*/
public class SendFCM {
	private static final String String = null;
	//token
	public static String token = "e1v12xfHs3g:APA91bFExfQRg1h4AZc3GqLHxW4ohe3D7Ca0xvGMJkSbY3qe9yXf4Xe10c1O4fHJ2IkbsNF6Z0lr97EV7G1ybP4GLDY5nGVa1ufLOtMAKhcXBfua1bGvubf5whjTLuFj6BQdflFz2n2w";
	//app name
	public static String appName = "myAppName";
	//topic
	public static String topic = "China";
	//message title
	public static String title = "tip";
	//message content
	public static String body = "are you ok?";

	//test
	
	public static void main(String args [] ) throws Exception {
		//add token
		List<String> tokens = new LinkedList();
		tokens.add(token);
		//proxy port
		System.setProperty("proxyHost", "localhost");
		System.setProperty("proxyPort", "1080");
		//FirebaseApp没有初始化
		if(!FireBaseUtil.isInit(appName)) {
			String jsonPath = "path/to/serviceAccountKey.json" ;
			String dataUrl = "https://telecomm-773e6e.firebaseio.com/";
			//FirebaseApp
			FireBaseUtil.initSDK(jsonPath, dataUrl, appName);
		}
		FireBaseUtil.pushSingle(appName, token, title, body); 
		FireBaseUtil.registrationTopic(appName, tokens, topic);  
		FireBaseUtil.sendTopicMes(appName, topic, title, body);  
		FireBaseUtil.cancelTopic(appName, tokens, topic);  
		FireBaseUtil.pushSingleToAndroid(appName, token, title, body);
		FireBaseUtil.registrationTopic(appName, tokens, topic); 
		FireBaseUtil.sendTopicMesToAndroid(appName, topic, title, body);
	} 	
}