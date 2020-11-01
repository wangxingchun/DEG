package com.hpe.mthread;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class miTM implements TrustManager,X509TrustManager{

	
	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// TODO Auto-generated method stub
		//System.out.println("###miTM->checkClientTrusted");
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// TODO Auto-generated method stub
		//System.out.println("###miTM->checkServerTrusted");
	}
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		//System.out.println("###miTM->getAcceptedIssuers");
		return null;
	}

	public boolean isServerTrusted(X509Certificate[] certs) {
		//System.out.println("###miTM->isServerTrusted");
		return true;
	}
	public boolean isClientTrusted(X509Certificate[] certs) {
		//System.out.println("###miTM->isClientTrusted");
		return true;
	}


}
