package com.fcm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.TopicManagementResponse;

/**
 * Google_FireBase tools
 * 
 * @author wanxingc
 *
 */
public class FireBaseUtil {

	// Multiple Map
	private static Map<String, FirebaseApp> firebaseAppMap = new ConcurrentHashMap<>();
	// AndroidConfig.Builder object
	private static com.google.firebase.messaging.AndroidConfig.Builder androidConfigBuilder = AndroidConfig.builder();
	// AndroidNotification.Builder object
	private static AndroidNotification.Builder androidNotifiBuilder = AndroidNotification.builder();

	/**
	 * 判断SDK是否初始化
	 * 
	 * @param appName
	 * @return
	 */
	public static boolean isInit(String appName) {
		return firebaseAppMap.get(appName) != null;
	}

	/**
	 * @param jsonPath JSON路径
	 * @param dataUrl  firebase数据库
	 * @param appName  APP名字
	 * @throws IOException
	 */
	public static void initSDK(String jsonPath, String dataUrl, String appName) throws IOException {
		FileInputStream serviceAccount = new FileInputStream(jsonPath);
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl(dataUrl).build();
		// 初始化firebaseApp
		FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
		// 存放
		firebaseAppMap.put(appName, firebaseApp);
	}

	/**
	 * 单设备推送
	 * 
	 * @param appName 应用的名字
	 * @param token   注册token
	 * @param title   推送题目
	 * @param bady    推送内容
	 * @return
	 * @throws IOException
	 * @throws FirebaseMessagingException
	 */
	public static void pushSingle(String appName, String token, String title, String body)
			throws IOException, FirebaseMessagingException {
		// instance
		FirebaseApp firebaseApp = firebaseAppMap.get(appName);
		// instance is null
		if (firebaseApp == null) {
			return;
		}
		// generate message context
		Message message = Message.builder().setNotification(new Notification(title, body)).setToken(token).build();
		// send response :messageID
		String response = FirebaseMessaging.getInstance(firebaseApp).send(message);
		System.out.println("单个设备推送成功 : " + response);
	}

	/**
	 * 给设备订阅主题
	 * 
	 * @param appName 应用的名字
	 * @param Tokens  设备的token,最大1000个
	 * @param topic   要添加的主题
	 * @return
	 * @throws FirebaseMessagingException
	 * @throws IOException
	 */
	public static void registrationTopic(String appName, List<String> tokens, String topic)
			throws FirebaseMessagingException, IOException {
		// 获取实例
		FirebaseApp firebaseApp = firebaseAppMap.get(appName);
		// 实例不存在的情况
		if (firebaseApp == null) {
			return;
		}
		// 订阅，返回主题管理结果对象。
		TopicManagementResponse response = FirebaseMessaging.getInstance(firebaseApp).subscribeToTopic(tokens, topic);
		System.out.println("添加设备主题，成功：" + response.getSuccessCount() + ",失败：" + response.getFailureCount());
	}

	/**
	 * 取消设备的订阅主题
	 * 
	 * @param appName 应用的名字
	 * @param tokens  设备的token,最大1000个
	 * @param topic   取消的主题
	 * @return
	 * @throws FirebaseMessagingException
	 * @throws IOException
	 */
	public static void cancelTopic(String appName, List<String> tokens, String topic)
			throws FirebaseMessagingException, IOException {
		// 获取实例
		FirebaseApp firebaseApp = firebaseAppMap.get(appName);
		// 实例不存在的情况
		if (firebaseApp == null) {
			return;
		}
		// 取消订阅，返回主题管理结果对象。
		TopicManagementResponse response = FirebaseMessaging.getInstance(firebaseApp).unsubscribeFromTopic(tokens,
				topic);
		System.out.println("取消设备主题，成功：" + response.getSuccessCount() + ",失败：" + response.getFailureCount());
	}

	/**
	 * 按主题推送
	 * 
	 * @param appName 应用的名字
	 * @param topic   主题的名字
	 * @param title   消息题目
	 * @param body    消息体
	 * @return
	 * @throws FirebaseMessagingException
	 * @throws IOException
	 */
	public static void sendTopicMes(String appName, String topic, String title, String body)
			throws FirebaseMessagingException, IOException {
		// 获取实例
		FirebaseApp firebaseApp = firebaseAppMap.get(appName);
		// 实例不存在的情况
		if (firebaseApp == null) {
			return;
		}
		// 构建消息
		Message message = Message.builder().setNotification(new Notification(title, body)).setTopic(topic).build();
		// 发送后，返回messageID
		String response = FirebaseMessaging.getInstance(firebaseApp).send(message);
		System.out.println("主题推送成功: " + response);
	}

	/**
	 * 单条Android设备推送消息(和pushSingle方法几乎没有区别)
	 * 
	 * @param appName 应用的名字
	 * @param token   注册token
	 * @param title   推送题目
	 * @param bady    推送内容
	 * @throws FirebaseMessagingException
	 */
	public static void pushSingleToAndroid(String appName, String token, String title, String body)
			throws FirebaseMessagingException {
		// 获取实例
		FirebaseApp firebaseApp = firebaseAppMap.get(appName);
		// 实例为空的情况
		if (firebaseApp == null) {
			return;
		}
		androidConfigBuilder.setRestrictedPackageName("io.telecomm.telecomm");
		androidNotifiBuilder.setColor("#55BEB7");// 设置消息通知颜色
		androidNotifiBuilder.setIcon("https://www.shiku.co/images/favicon.png");// 设置消息图标
		androidNotifiBuilder.setTitle(title);// 设置消息标题
		androidNotifiBuilder.setBody(body); // 设置消息内容
		AndroidNotification androidNotification = androidNotifiBuilder.build();
		androidConfigBuilder.setNotification(androidNotification);
		AndroidConfig androidConfig = androidConfigBuilder.build();
		// 构建消息
		Message message = Message.builder().setToken(token).setAndroidConfig(androidConfig).build();
		// 发送后，返回messageID
		String response = FirebaseMessaging.getInstance(firebaseApp).send(message);
		System.out.println("单个安卓设备推送成功 : " + response);
	}

	/**
	 * Android按主题推送(和sendTopicMes方法几乎没有区别)
	 * 
	 * @param appName 应用的名字
	 * @param topic   主题的名字
	 * @param title   消息题目
	 * @param body    消息体
	 * @return
	 * @throws FirebaseMessagingException
	 * @throws IOException
	 */
	public static void sendTopicMesToAndroid(String appName, String topic, String title, String body)
			throws FirebaseMessagingException, IOException {
		// Generate instance
		FirebaseApp firebaseApp = firebaseAppMap.get(appName);
		// instance is null
		if (firebaseApp == null) {
			return;
		}
		androidNotifiBuilder.setColor("#55BEB7");// 设置消息通知颜色
		androidNotifiBuilder.setIcon("https://www.shiku.co/images/favicon.png");// 设置消息图标
		androidNotifiBuilder.setTitle(title);// 设置消息标题
		androidNotifiBuilder.setBody(body);// 设置消息内容
		AndroidNotification androidNotification = androidNotifiBuilder.build();
		androidConfigBuilder.setNotification(androidNotification);
		AndroidConfig androidConfig = androidConfigBuilder.build();
		// Generate message
		Message message = Message.builder().setTopic(topic).setAndroidConfig(androidConfig).build();
		String response = FirebaseMessaging.getInstance(firebaseApp).send(message);
		System.out.println("android Push Success: " + response);
	}
}