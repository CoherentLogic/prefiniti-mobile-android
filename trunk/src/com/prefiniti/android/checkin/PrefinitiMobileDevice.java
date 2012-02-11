package com.prefiniti.android.checkin;

import android.location.Location;
import android.widget.TextView;
import android.view.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class PrefinitiMobileDevice {

	private String deviceCode;
	private String serverURI;

	public PrefinitiMobileDevice(String code, String uri) {
		this.deviceCode = code;
		this.serverURI = uri;		
	}

	public void SetLocation(Location location, String comment) throws Exception {
		BufferedReader in = null;
		String url = this.serverURI + "OpenHorizon/Objects/MobileDevice/UpdateLocation.cfm";
		url += "?DeviceCode=" + this.deviceCode;
		url += "&Latitude=" + URLEncoder.encode(Double.toString(location.getLatitude()), "utf-8");
		url += "&Longitude=" + URLEncoder.encode(Double.toString(location.getLongitude()), "utf-8");
		url += "&Elevation=" + URLEncoder.encode(Double.toString(location.getAltitude()), "utf-8");
		url += "&Accuracy=" + URLEncoder.encode(Float.toString(location.getAccuracy()), "utf-8");
		url += "&Provider=" + URLEncoder.encode(location.getProvider(), "utf-8");
		url += "&Speed=" + URLEncoder.encode(Float.toString(location.getSpeed()), "utf-8");
		url += "&Bearing=" + URLEncoder.encode(Float.toString(location.getBearing()), "utf-8");
		url += "&Comment=" + URLEncoder.encode(comment);
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader
			(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
