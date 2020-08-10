package org.dsher.loris.utils;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLUtils {
	
	private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[] { 
			new X509TrustManager() {     
				public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
					return new X509Certificate[0];
				} 
				public void checkClientTrusted( 
						java.security.cert.X509Certificate[] certs, String authType) {
				} 
				public void checkServerTrusted( 
						java.security.cert.X509Certificate[] certs, String authType) {
				}
			} 
	}; 
	
	public static void trustAllCertificiates() throws GeneralSecurityException {
		SSLContext sc = SSLContext.getInstance("SSL"); 
		sc.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom()); 
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}
	
	public static void revertToDefaultCertificateTrust() throws GeneralSecurityException {
		SSLContext scDef;
		scDef = SSLContext.getInstance("SSL");
		scDef.init(null, null, new java.security.SecureRandom()); 
		HttpsURLConnection.setDefaultSSLSocketFactory(scDef.getSocketFactory());
	}
	
	
	
}
