package com.addrlist.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

public class HttpUtils {
	/**
	 * ???httpGET请�?
	 * @param url ?��?
	 * @param charSet �????
	 * @return
	 */
	public static String sendGet(String url,String charSet){
		if (charSet == null || charSet.length() == 0){
			charSet = "utf-8";
		}
		StringBuffer result = new StringBuffer();
		try {
			URL U = new URL(url);
			URLConnection connection = U.openConnection();
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),charSet));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			in.close();
		} catch (Exception e) {
			throw new RuntimeException("???get请�?IO?��?�?rl:"+url,e);
		}
		return result.toString();
	}
	
	
	public static String sendPost(String url,Map<String,String> param,String charSet){
		StringBuffer result = new StringBuffer();
		try {
			URL httpurl = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
			if(param!=null){
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				PrintWriter out = new PrintWriter(httpConn.getOutputStream());
				int i = 0;
				Set<Map.Entry<String, String>> set = param.entrySet();
				for (Map.Entry<String, String> entry:set){
					out.print(entry.getKey());
					out.print("=");
					out.print(entry.getValue());
					if (i!=set.size()-1){
						out.print("&");
					}
					i++;
				}
				out.flush();
				out.close();
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), charSet));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			in.close();
		} catch (MalformedURLException e) {
			throw new RuntimeException("???post请�??��?,url:"+url,e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("�?????�????charSet:"+charSet,e);
		} catch (IOException e) {
			throw new RuntimeException("???post请�?IO?��?,url:"+url,e);
		}
		return result.toString();
	}
}
