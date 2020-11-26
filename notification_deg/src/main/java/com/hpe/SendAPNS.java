package com.hpe;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;

import org.apache.commons.codec.binary.Hex;

import com.clevertap.apns.ApnsClient;
import com.clevertap.apns.Notification;
import com.clevertap.apns.NotificationResponse;
import com.clevertap.apns.clients.ApnsClientBuilder;

public class SendAPNS {
	static String apns_token = "uZEKfI+DL9NuUpa/ji11guRN7uw="; // uZEKfI+DL9NuUpa/ji11guRN7uw=
	// static String apns_token="B9910A7C8F832FD36E5296BF8E2D7582E44DEEEC";
	static String apns_topic = "ru.mts.aes.ech";

	public static void main(String[] args) {
		String jdkversion = System.getProperty("java.version");
		System.out.println("JDK Version=" + jdkversion);
		// ALPN.debug = true;
		System.out.println("Send APNS");
		try {
			SendAPNS.Send();
			// String token=SendAPNS.strTo16(apns_token);
			// System.out.println(token);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void Send() throws UnrecoverableKeyException, KeyManagementException, CertificateException,
			NoSuchAlgorithmException, KeyStoreException, IOException {

		try {
			// hexadecimal token
			final Base64.Decoder decoder = Base64.getDecoder();
			byte[] decoded = decoder.decode(apns_token);
			apns_token = Hex.encodeHexString(decoded);

			FileInputStream cert = new FileInputStream(
					"C:\\Project\\HPDEG\\MTS_DEG\\Requirement\\cert\\apns\\ech_prod.p12");
			final ApnsClient client = new ApnsClientBuilder().withDevelopmentGateway().inSynchronousMode()
					.withCertificate(cert).withPassword("Tud9P\\fT;dCqbuq2(5{\\").withDefaultTopic(apns_topic).build();

			Notification n = new Notification.Builder(apns_token).alertBody("Hello").build();

			NotificationResponse result = client.push(n); // Synchronous send message

			System.out.println("Result:" + result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
