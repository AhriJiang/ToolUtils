package Demos;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Assert;


public class HttpFluent {
	
	public static Logger logger = Logger.getLogger(HttpFluent.class);
	
	/**
	 * Get请求
	 * @param url 地址
	 * @param flag 代理调试开关  false : 不使用代理   true : 使用代理
	 * @param ProxyPort 代理端口号
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws Exception
	 */
	public String GetByProxyOrNot(boolean flag,String ProxyPort,String url) {
		try {
			if (flag==false) {
				return Request.Get(url)
						.execute()
						.returnContent()
						.asString();
			} else {
				return Request.Get(url)
						.viaProxy("127.0.0.1:"+ ProxyPort)
						.execute()
						.returnContent()
						.asString();
			}
		} catch (Exception e) {
			return "Something error occured!";
		}
		
	}
	/**
	 * 
	 * Post请求
	 * @param url 地址
	 * @param flag 代理调试开关 false : 不使用代理   true : 使用代理
	 * @param ProxyPort 代理端口号
	 * @return
	 * @throws Exception
	 */
	public String PostByProxyOrNot(boolean flag,String ProxyPort,String url) throws Exception {
		
		if (flag==false) {
			return Request.Post(url)
					.execute()
					.returnContent()
					.asString();
		} else {
			return Request.Post(url)
					.viaProxy("127.0.0.1:"+ProxyPort)
					.execute()
					.returnContent()
					.asString();
		}

	}
	/**
	 * 
	 * @param flag 代理调试开关
	 * @param ProxyPort 代理端口
	 * @param url 地址栏
	 * @param params map参数
	 * @return
	 * @throws Exception
	 */
	public String PostByMaps(boolean flag, String ProxyPort,String url, Map<String, String> params) throws Exception {
		Form formParams=Form.form(); 
		//Form.form().add("username",  "vip").add("password",  "secret")
		for (String pKey : params.keySet()) {
			formParams.add(pKey, params.get(pKey));
        }
		if (flag==false) {
			return Request.Post(url)
					.bodyForm(formParams.build())
					.execute()
					.returnContent()
					.asString();
		} else {
			return Request.Post(url)
					.viaProxy("127.0.0.1:"+ProxyPort)
					.bodyForm(formParams.build())
					.execute()
					.returnContent()
					.asString();
		}
		
	}
	
	/**
	 * 获取响应返回的状态码
	 * @param response 一个返回请求对象
	 * @return
	 * @throws IOException
	 */
	public int getStatusCode (Response response) throws IOException{
		return response
			.returnResponse()
			.getStatusLine()
			.getStatusCode();
	}
	
	/**
	 * 
	 * @param url 发送地址url
	 * @param ProxyPort 端口号
	 * @param boolean false : 不使用代理   true : 使用代理
	 * @param body 发送请求数据body
	 * @param type ContenType类型 如: ContentType.parse("application/x-www-form-urlencoded")
	 * @return
	 * @throws Exception
	 */
	public String PostStringBody(boolean flag ,String ProxyPort, String url, String body,ContentType type) throws Exception {
		
		if (flag==false) {
			return Request.Post(url)
					.bodyString(body, type)
//					.addHeader("test", "header")
//					.addHeader("test2", "header2")
					.execute()
					.returnContent()
					.asString();
		} else {
			return Request.Post(url)
					.viaProxy("127.0.0.1:"+ProxyPort)
					.bodyString(body, type)
//					.addHeader("test", "header")
//					.addHeader("test2", "header2")
					.execute()
					.returnContent()
					.asString();
		}

	}
	
	/**
	 * 
	 * @param url 送达地址
	 * @param fileNameWithPath 文件路径
	 * @param contentType ContenType类型 如: ContentType.parse("application/x-www-form-urlencoded")
	 * @return
	 */
	public String Postfile(String url, String fileNameWithPath, ContentType contentType) {
		 
	    String fileName = fileNameWithPath;
	    File uploadFile =new File(fileNameWithPath);
	    String exception = "上传文件时,出了一个小意外";
	    
	    try {
			return Request.Post(url)
//					.viaProxy("127.0.0.1:8888")
			        .connectTimeout(20000)
			        .socketTimeout(20000)
			        .bodyFile(uploadFile, contentType)
			        .execute()
					.returnContent()
					.asString();
		} catch (ClientProtocolException e) {
			Assert.fail(exception);
			return exception;
		} catch (IOException e) {
			Assert.fail(exception);
			return exception;
		}
		
	}
	
	/**
	 * 将一个返回响应转换成一个String对象
	 * @param response 一个返回请求对象
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String TransResponse2String(Response response) throws ClientProtocolException, IOException{
		return response
			.returnContent()
			.asString();
	}
	
	/**
	 * 
	 * @param text 正则匹配的文本
	 * @param LBorder 左边界文本
	 * @param RBorder 右边界文本
	 * @param index 匹配第几个结果,第一个是0
	 * @return
	 */
	public String LRString(String text,String LBorder,String RBorder,int index){
		
		int LBorderLength=LBorder.length();
		int RBorderLength=RBorder.length();
		int count=0;
		
		String RegRule=LBorder+".*?"+RBorder;
		System.out.println("匹配的规则:"+RegRule);		
		Pattern pattern =Pattern.compile(RegRule);
		Matcher RegResult=pattern.matcher(text);
		
		while (RegResult.find()) {
			
			if (!(count<index)) {
				String FindResult=RegResult.group(0);
				System.out.println(FindResult);
				return FindResult;
			}
			count++;
		}
		
		return "没有匹配到预期结果文本:"+text;
		
	}
	
	/**
	 * 将String转换为Jsoup的document对象,之后可像操作HTML的dom一样调用
	 * @param body Response数据
	 * @return
	 */
	
	public Document String2JsoupDocument(String body){
		
		Document document= Jsoup.parse(body);
		return document;
		
	}
	
	/**
	 * String转换为XML对象,根据name返回子节点属性文本
	 * @param body XML文本
	 * @param name 子节点名称
	 * @return
	 */
	public String GetXmlDocumentText(String body,String name) {
		org.dom4j.Document document = null;
		String text=null;
		try {
			document = DocumentHelper.parseText(body);
			Element root = document.getRootElement();
			text = root.element(name).getText();
		} catch (DocumentException e) {
			System.out.println("XML对象转换失败");
		}
		return text;
	}
	
	
	/**
	 * 将String数据转换为JsonArray数组
	 * @param body Response数据
	 * @return
	 */
	public JSONArray String2JSONArray(String body) {

		JSONArray JsonArray = new JSONArray(body);
		return JsonArray;

	}
	
	/**
	 * 将String数据转换为JsonObject
	 * @param body Response数据
	 * @return
	 */
	public JSONObject String2JSONObject(String body) {

		JSONObject JsonObject = new JSONObject(body);
		return JsonObject;

	}
	
	/**
	 * 从JsonArray数组中根据index获取特定下标的JsonObject
	 * @param JsonArray 转换后的JsonArray对象
	 * @param index JsonArray的下标, 从0开始
	 * @return
	 */
	
	public JSONObject GetJsonObjectByIndex(JSONArray JsonArray, int index) {

		JSONObject myjObject = JsonArray.getJSONObject(index);
		return myjObject;
	}
	

	public String Request(String RequestMethod, String URL, ContentType ContentType, String Body) {
		String Result=null;
		if (RequestMethod.equals("GET")) {
			try {
				Result=Request.Get(URL)
						.bodyString(Body, ContentType)
						.execute()
						.returnContent()
						.asString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (RequestMethod.equals("POST")) {
			try {
				Result=Request.Post(URL)
						.bodyString(Body, ContentType)
						.execute()
						.returnContent()
						.asString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (RequestMethod.equals("PUT")) {
			try {
				Result=Request.Put(URL)
						.bodyString(Body, ContentType)
						.execute()
						.returnContent()
						.asString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (RequestMethod.equals("DELETE")) {
			try {
				Result=Request.Delete(URL)
						.bodyString(Body, ContentType)
						.execute()
						.returnContent()
						.asString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
