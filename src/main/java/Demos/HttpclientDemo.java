package Demos;

import java.util.*;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpclientDemo {

	private static RequestConfig requestConfig = RequestConfig.custom()
			.setProxy(new HttpHost("127.0.0.1",8888))
			.setConnectTimeout(10000)
			.setConnectionRequestTimeout(15000).build();
	
	private static CloseableHttpClient httpClient=HttpClients.createDefault();//HTTP整体框架,会初始化,这里是静态的是同一个静态对象会话
	
	public static String sendHttpGet(String url) throws Exception {
		httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);//GET数据集合对象
		httpGet.setConfig(requestConfig);
		//------------构建一个请求框架-------------
		httpGet.addHeader("User-Agent", "Mozilla");//设置各种头信息
		httpGet.addHeader("cookie","aaa=111;bbb=222");
		//------------构建头信息-------------------
		CloseableHttpResponse response = httpClient.execute(httpGet);
		System.out.println(response.getStatusLine().getStatusCode() + "\n");
		//------------发送请求-------------------
		HttpEntity entity = response.getEntity();
		String responseContent = EntityUtils.toString(entity, "UTF-8"); // GBK
		System.out.println(responseContent);
		//------------获取返回内容-------------------
		Header header=response.getHeaders("Set-Cookie")[0];//从header获取cookie
		header.getValue();//获取cookie的值
		
		response.close();
		httpClient.close();
		return responseContent;
	}

	public static String sendHttpPost(String url, String body) throws Exception {
		httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);
		
		httpPost.addHeader("User-Agent", "Posttest");
		httpPost.addHeader("cookie","aaa=111;bbb=222");
		
		httpPost.setEntity(new StringEntity(body));
		
		CloseableHttpResponse response = httpClient.execute(httpPost);//执行
		
		System.out.println(response.getStatusLine().getStatusCode() + "\n");

		HttpEntity entity = response.getEntity();
		String responseContent = EntityUtils.toString(entity, "UTF-8"); // GBK, 这里是将http请求的数据
		System.out.println(responseContent);

		response.close();
		httpClient.close();
		return responseContent;
	}

	public static String sendHttpPost(String url, Map<String, String> params) throws Exception {
		httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);
		httpPost.addHeader("User-Agent", "Posttest");

		List<NameValuePair> plist = new ArrayList<NameValuePair>();
		for (String pKey : params.keySet()) {
			plist.add(new BasicNameValuePair(pKey, params.get(pKey)));
        }

		httpPost.setEntity(new UrlEncodedFormEntity(plist));
		
		CloseableHttpResponse response = httpClient.execute(httpPost);
		System.out.println(response.getStatusLine().getStatusCode() + "\n");

		HttpEntity entity = response.getEntity();
		String responseContent = EntityUtils.toString(entity, "UTF-8"); // GBK
		System.out.println(responseContent);

		response.close();
		httpClient.close();
		return responseContent;
	}
}