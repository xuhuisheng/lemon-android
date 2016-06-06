package com.mossle.android;

public class AppConstants {
    public static final String BASE_URL = "http://www.mossle.com";
	// public static final String BASE_URL = "http://10.235.121.13:8080/mossle-app-lemon";
	// public static final String BASE_URL = "http://192.168.31.207:8080/mossle-app-lemon";
	// public static final String BASE_URL = "http://10.235.128.246/mossle-app-mossle";

	private static String baseUrl = BASE_URL;

	public static String getBaseUrl() {
		return baseUrl;
	}
	public static void setBaseUrl(String baseUrl) {
		AppConstants.baseUrl = baseUrl;
	}
}
