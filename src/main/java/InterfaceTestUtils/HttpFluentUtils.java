package InterfaceTestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Assert;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
/**
 * @author Lucif
 *
 */
public class HttpFluentUtils {

	public static Logger logger = Logger.getLogger(HttpFluentUtils.class);
	private Properties pro=new Properties();
	Map<String,String> headerMap=new HashMap<>();
	public HttpFluentUtils(Map headerMap){
		this.headerMap=headerMap;
	}
	public HttpFluentUtils(){
	}
	public Properties GetHttpfluentProperties(Properties pro){
		try {
			InputStream in = new FileInputStream("src/main/resources/Httpfluent.properties");
			pro.load(in);
			in.close();
		} catch (Exception e) {
			logger.warn("没有找到Httpfluent.properties文件");
			e.printStackTrace();
		}
		return pro;
	}

	
	
	/**
	 * Delete请求
	 * 
	 * @param url
	 *            地址
	 * @return
	 * @throws Exception
	 */
	public String Delete(String url){

		try {
			return Request
					.Delete(url)
//					.viaProxy("127.0.0.1:8888")
					.execute()
					.returnContent()
					.asString();
		} catch (ClientProtocolException e) {
			logger.warn("删除链接可能有点小问题");
			e.printStackTrace();
		} catch (IOException e) {
			logger.warn("删除链接可能有点小问题");
			e.printStackTrace();
		}
		return "删除链接可能有点小问题";

	}
	
	/**
	 * 下载文件并另存为File
	 * @param URL  下载地址链接URL
	 * @param File 保存路径+文件名 如 C:/abc.txt;
	 * @return
	 */
	public String Download(String URL,File File){
		
		String FileSavePath="";
		
		if (!File.exists()) {
			try {
				File.createNewFile();
			} catch (IOException e) {
				logger.warn("创建下载保存路径失败, 请手动添加路径:"+File.getAbsolutePath());
				e.printStackTrace();
			}
		}
		try {
			FileSavePath=File.getAbsolutePath();
			Request.Get(URL)
			.connectTimeout(20000)
			.socketTimeout(20000)
//		.viaProxy("127.0.0.1:8888")
			.execute().saveContent(File);
			File.delete();
		} catch (ClientProtocolException e) {
			logger.info("文件路径出错, 请校验检查");
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("文件路径出错, 请校验检查");
			e.printStackTrace();
		}

		return FileSavePath;
	}
	
	
	/**
	 * Get请求
	 * 
	 * @param url
	 *            地址
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws Exception
	 */
	public String Get(String url) throws ClientProtocolException, IOException {

		return Request
				.Get(url)
//				.viaProxy("127.0.0.1:8888")
				.execute()
				.returnContent()
				.asString();

	}

	/**
	 * 
	 * Post请求
	 * 
	 * @param url
	 *            地址
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws Exception
	 */

	public String Post(String url) throws ClientProtocolException, IOException{

		return Request
				.Post(url)
//				.viaProxy("127.0.0.1:8888")
				.execute()
				.returnContent()
				.asString();

	}

	/**
	 * 
	 * @param url
	 *            地址栏
	 * @param params
	 *            map参数
	 * @return
	 * @throws Exception
	 */

	public String PostByMaps(String url, Map<String, String> params) throws Exception {
		Form formParams = Form.form();
		// Form.form().add("username", "vip").add("password", "secret")
		for (String pKey : params.keySet()) {
			formParams.add(pKey, params.get(pKey));
		}

		return Request
				.Post(url)
				.bodyForm(formParams.build())
//				.viaProxy("127.0.0.1:8888")
				.execute()
				.returnContent()
				.asString();

	}

	/**
	 * 获取响应返回的状态码
	 * 
	 * @param response
	 *            一个返回请求对象
	 * @return
	 * @throws IOException
	 */
	public int getStatusCode(Response response) throws IOException {

		return response
				.returnResponse()
				.getStatusLine()
				.getStatusCode();

	}

	/**
	 * 
	 * @param url
	 *            发送地址url
	 * @param body
	 *            发送请求数据body
	 * @param type
	 *            ContenType类型 如:
	 *            ContentType.parse("application/x-www-form-urlencoded")
	 * @return
	 * @throws Exception
	 */
	public String PostStringBody(String url, String body, ContentType type) throws Exception {

		return Request
				.Post(url)
				.bodyString(body, type)
				// .addHeader("test", "header")
				// .addHeader("test2", "header2")
				.execute()
				.returnContent()
				.asString();
	}

	/**
	 * 
	 * @param url
	 *            送达地址
	 * @param fileNameWithPath
	 *            文件路径,如"C:/abc.txt"
	 * @param contentType
	 *            ContenType类型 如:
	 *            ContentType.parse("application/x-www-form-urlencoded")
	 * @return
	 */

	public String Postfile(String url, String fileNameWithPath, ContentType contentType) {

		String fileName = fileNameWithPath;
		File uploadFile = new File(fileNameWithPath);
		String exception = "上传文件时,出了一个小意外";

		try {
			return Request
					.Post(url)
					// .viaProxy("127.0.0.1:8888")
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
	 * 
	 * @param response
	 *            一个返回请求对象
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */

	public String TransResponse2String(Response response) throws ClientProtocolException, IOException {
		return response.returnContent().asString();
	}

	/**
	 * 
	 * @param text
	 *            正则匹配的文本
	 * @param LBorder
	 *            左边界文本
	 * @param RBorder
	 *            右边界文本
	 * @param index
	 *            匹配第几个结果,第一个是0
	 * @return
	 */

	public String LRString(String text, String LBorder, String RBorder, int index) {

		int LBorderLength = LBorder.length();
		int RBorderLength = RBorder.length();
		int count = 0;

		String RegRule = LBorder + ".*?" + RBorder;
		logger.info("匹配的规则:" + RegRule);
		Pattern pattern = Pattern.compile(RegRule);
		Matcher RegResult = pattern.matcher(text);

		while (RegResult.find()) {

			if (!(count < index)) {
				String FindResult = RegResult.group(0);
				logger.info("匹配到预期结果文本:" + FindResult);
				return FindResult;
			}
			count++;
		}

		return "没有匹配到预期结果文本:" + text;

	}

	/**
	 * 将String转换为Jsoup的document对象,之后可像操作HTML的dom一样调用
	 * 
	 * @param body
	 *            Response数据
	 * @return
	 */

	public Document String2JsoupDocument(String body) {

		Document document = Jsoup.parse(body);
		return document;

	}

	/**
	 * String转换为XML对象,根据name返回子节点属性文本
	 * 
	 * @param body
	 *            XML文本
	 * @param name
	 *            子节点名称
	 * @return
	 */
	public String GetXmlDocumentText(String body, String name) {
		org.dom4j.Document document = null;
		String text = null;
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
	 * String转换为XML对象,根据name返回子节点属性文本
	 * 
	 * @param body
	 *            XML文本
	 * @param id
	 *            子节点id
	 * @return
	 * @throws DocumentException 
	 */
	public String GetXmlDocumentID(String body, String id) throws DocumentException {
		org.dom4j.Document document = null;
		String text = null;
		document = DocumentHelper.parseText(body);
		Element root = document.getRootElement();
		root=root.elementByID(id);
		
		return root.getText();
	}

	/**
	 * 将String数据转换为JsonArray数组
	 * 
	 * @param body
	 *            Response数据
	 * @return
	 */
	public JSONArray String2JSONArray(String body) {

		JSONArray JsonArray = new JSONArray(body);
		return JsonArray;

	}

	/**
	 * 将String数据转换为JsonObject
	 * 
	 * @param body
	 *            Response数据
	 * @return
	 */
	public static JSONObject String2JSONObject(String body) {

		JSONObject JsonObject = new JSONObject(body);
		return JsonObject;

	}

	/**
	 * 从JsonArray数组中根据index获取特定下标的JsonObject
	 * 
	 * @param JsonArray
	 *            转换后的JsonArray对象
	 * @param index
	 *            JsonArray的下标, 从0开始
	 * @return
	 */

	public JSONObject GetJsonObjectByIndex(JSONArray JsonArray, int index) {

		JSONObject myjObject = JsonArray.getJSONObject(index);
		return myjObject;
	}

	/**
	 * 判断一个String对象是否符合标准的JsonObject格式
	 * 
	 * @param body
	 *            传入一个String数据,判断是否是一个JsonObject
	 * @return 返回判定结果
	 */
	private static boolean AssertJsonObject(String body) {
		try {
			JSONObject JsonObject = new JSONObject(body);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断一个String对象是否符合标准的JsonArray格式
	 * 
	 * @param body
	 *            传入一个String数据,判断是否是一个JsonArray
	 * @return 返回判定结果
	 */
	private static boolean AssertJsonArray(String body) {
		try {
			JSONArray JsonObject = new JSONArray(body);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 根据Excel生成的JsonObject驱动, 发出http(s)请求, 根据notNeededParamTags手动去除某些可能不必要发送的字段
	 * ①GET 标准GET请求, 将JsonObject中所有的键值对拼接到参数中,如果需要限制某些字段,
	 * 需要修改notNeededParamsTags字段 ②POST 根据ContentType
	 * 将Body数据做不同形式的加工,JsonObject类型数据将有大量的数据内容操作,该方法将根据HasJsonArrays判断是否需要拼接JsonArray数组文本
	 * ③UPLOAD 根据ContentType 上传文件类型, 该方法要求JsonObject必须含有FilePath,如C:/ABC.txt
	 * ④DOWNLOAD 根据DownloadFilePath, 下载文件并保存成功后返回路径, 下载完成后删除文件Result.temp
	 * @param jsb
	 *            传入JsonObject格式测试数据,JsonObject必须含有一下几个Key: URL, Method,
	 *            ContentType, TestCaseCore, Expected_Key , Expected_Value
	 * @param CookieCharactor
	 *            区分OMS和SP的万能权限Cookie, 默认值 "Sp", OMS请填写"OMS", 不区分大小写 
	 * @return 请求返回对象的String类型数据
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String Request(JSONObject jsb,HttpResponse Response,String CookieCharactor) throws ClientProtocolException, IOException {
		
		pro=GetHttpfluentProperties(pro);
		Request request;
		Header Cookie;
		String MasterCookieName=pro.getProperty("CookieName");
		String MasterCookieValue;
		if (CookieCharactor.toLowerCase().equals("oms")) {
			MasterCookieValue=pro.getProperty("OMSCookieValue");
		}else{
			MasterCookieValue=pro.getProperty("SpCookieValue");
		}
		
		
		if (jsb.has("URLFOLDER") && jsb.has("Method") && jsb.has("ContentType") && jsb.has("Expected_Key")
				&& jsb.has("Expected_Value") && jsb.has("TestCaseCore")) {
			
			String URL = pro.getProperty("ServerIp")+jsb.getString("URLFOLDER");
			URL=AsureURL(URL);
			String[] notNeededParamTags = new String[] { "URLFOLDER", "Method", "ContentType", "TestCaseCore",
					"Expected_Key", "Expected_Value", "正/反例", "测试字段", "测试目的", "HasJsonArrays" };

			if (jsb.get("Method").equals("GET")) {
				String params;
				params = SetGETRequestParams(jsb, notNeededParamTags);

				logger.info("GET请求参数:" + params);
				logger.info("GET请求测试字段: " + jsb.get("TestCaseCore"));
				logger.info("测试目的:"+ jsb.get("测试目的"));
				logger.info("GET请求:" + URL + params);
				
				request=Request.Get(URL + params)
						.setHeader(MasterCookieName, MasterCookieValue)
						;
//						.connectTimeout(100);
//						.viaProxy("127.0.0.1:8888");
				
				if (Response!=null&&Response.containsHeader("Set-Cookie")) {
					Cookie=Response.getFirstHeader("Set-Cookie");
					request.addHeader(Cookie);
				}
				if (headerMap!=null&&headerMap.size()>0) {
					for (String key : headerMap.keySet()) {
						request.addHeader(key, headerMap.get(key));
					}
				}
				return request
						.execute()
						.returnContent()
						.asString(Consts.UTF_8);

			} else if (jsb.get("Method").equals("POST")) {

				if (jsb.get("ContentType").toString().toLowerCase().equals("application/json;charset=utf-8")) {

					String RequestBody = "";
					StringBuffer stf = new StringBuffer();
					logger.info("POST请求: " + URL);
					logger.info("POST请求测试字段: " + jsb.get("TestCaseCore"));
					logger.info("POST请求测试目的:"+jsb.get("测试目的").toString());
					
					if (jsb.has("HasJsonArrays")&&(jsb.has("HasJsonObjects")==false)) {

						logger.info("POST请求包含JsonArray数组");
						String JsonArrayTags = jsb.getString("HasJsonArrays");
						
						RequestBody = SetJsonObjectRequestBodyHasJsonArrays(jsb, JsonArrayTags, stf, RequestBody);

					}else if (jsb.has("HasJsonObjects")&&(jsb.has("HasJsonArrays")==false)) {
						
						String JsonObjectTags = jsb.getString("HasJsonObjects");
						RequestBody=SetJsonObjectRequestBodyHasJsonObjects(jsb, JsonObjectTags, stf, RequestBody);
		
					}else if (jsb.has("HasJsonArrays")&&jsb.has("HasJsonObjects")) {
						
						logger.info("POST请求包含JsonArray数组和JsonObjects");
						String JsonObjectTags = jsb.getString("HasJsonObjects");
						String JsonArrayTages = jsb.getString("HasJsonArrays");
						RequestBody=SetJsonObjectRequestMultiJson(jsb, JsonObjectTags,JsonArrayTages, stf, RequestBody);
						
					}
						else {

						RequestBody=jsb.toString();
					}
					
					request=Request.Post(URL)
							.setHeader(MasterCookieName, MasterCookieValue)
//							.connectTimeout(100)
//							.viaProxy("127.0.0.1:8888")
							.bodyString(RequestBody, ContentType.parse((String) jsb.get("ContentType")));
					
					if (Response!=null&&Response.containsHeader("Cookie")) {
						Cookie=Response.getFirstHeader("Cookie");
						request.addHeader(Cookie);
					}
					if (headerMap!=null&&headerMap.size()>0) {
						for (String key : headerMap.keySet()) {
							request.addHeader(key, headerMap.get(key));
						}
					}
					
					return request
							.execute()
							.returnContent()
							.asString(Consts.UTF_8);

				} else {

					String RequestBody = SetPOSTRequestBody(jsb, notNeededParamTags);

					logger.info("标准POST请求: " + URL);
					logger.info("POST请求测试字段: " + jsb.get("TestCaseCore"));
					logger.info("标准请求body: " + RequestBody);

					request=Request.Post(URL)
							.setHeader(MasterCookieName, MasterCookieValue)
//							.connectTimeout(100)
							.bodyString(RequestBody, ContentType.parse((String) jsb.get("ContentType")));
					
					if (Response!=null&&Response.containsHeader("Cookie")) {
						Cookie=Response.getFirstHeader("Cookie");
						request.addHeader(Cookie);
					}
					if (headerMap!=null&&headerMap.size()>0) {
						for (String key : headerMap.keySet()) {
							request.addHeader(key, headerMap.get(key));
						}
					}
					
					return request
							.execute()
							.returnContent()
							.asString(Consts.UTF_8);
				}

			} else if (jsb.get("Method").equals("UPLOAD")) {

				String exception = "上传文件时,出了一个小意外";
				
				if (jsb.has("FilePath")) {
					
					String fileNameWithPath = jsb.getString("FilePath");
					File uploadFile = new File(fileNameWithPath);
					HttpEntity entity = SetPostFileRequestWithParams(jsb);
					logger.info("上传测试字段: "+jsb.getString("TestCaseCore")+"-----"+ jsb.getString("测试目的"));
					logger.info("准备上传文件: "+ jsb.getString("FilePath"));
					
					request=Request.Post(URL)
							.setHeader(MasterCookieName, MasterCookieValue)
							.connectTimeout(20000)
							.socketTimeout(20000)
							.body(entity);
//							.viaProxy("127.0.0.1:8888");
					
					if (Response!=null&&Response.containsHeader("Cookie")) {
						Cookie=Response.getFirstHeader("Cookie");
						request.addHeader(Cookie);
					}
					if (headerMap!=null&&headerMap.size()>0) {
						for (String key : headerMap.keySet()) {
							request.addHeader(key, headerMap.get(key));
						}
					}
					
					return request
							.execute()
							.returnContent()
							.asString(Consts.UTF_8);

				} else {
					logger.info("没有找到FilePath,请检查Excel数据源");
					return exception;
				}

			} else if (jsb.get("Method").equals("DOWNLOAD")) {
				
				if (jsb.has("DownloadFilePath")) {
					
					logger.info("准备下载文件到目录: "+ jsb.getString("DownloadFilePath"));

					File File =new File(jsb.getString("DownloadFilePath")+"/result.dump");
					if (!File.exists()) {
						try {
							File.createNewFile();
						} catch (Exception e) {
							logger.warn("尝试创建下载路径失败, 请手动追加下载文件路径:"+File.getAbsolutePath());
							e.printStackTrace();
						}
					}
					
					request=Request.Get(URL)
							.setHeader(MasterCookieName, MasterCookieValue)
							.connectTimeout(20000)
							.socketTimeout(20000);
//							.viaProxy("127.0.0.1:8888")
					
					if (Response!=null&&Response.containsHeader("Cookie")) {
						Cookie=Response.getFirstHeader("Cookie");
						request.addHeader(Cookie);
					}
					if (headerMap!=null&&headerMap.size()>0) {
						for (String key : headerMap.keySet()) {
							request.addHeader(key, headerMap.get(key));
						}
					}
					
					request
					.execute()
					.saveContent(File);
					
					String FileSavePath=File.getPath();
					File.delete();
					
					return FileSavePath;
					
				} else {
					
					logger.warn("没有找到DownloadFilePath关键字段");
					return "没有找到DownloadFilePath关键字段";
					
				}
				
			}else if (jsb.get("Method").equals("DELETE")) {
				
				String params;
				params = SetGETRequestParams(jsb, notNeededParamTags);

				logger.info("DELETE请求参数:" + params);
				logger.info("DELETE请求测试字段: " + jsb.get("TestCaseCore"));
				logger.info("DELETE请求:" + URL + params);
				
				request=Request.Delete(URL + params)
						.setHeader(MasterCookieName, MasterCookieValue);
//						.connectTimeout(100);
//						.viaProxy("127.0.0.1:8888");
				
				if (Response!=null&&Response.containsHeader("Cookie")) {
					Cookie=Response.getFirstHeader("Cookie");
					request.addHeader(Cookie);
				}
				if (headerMap!=null&&headerMap.size()>0) {
					for (String key : headerMap.keySet()) {
						request.addHeader(key, headerMap.get(key));
					}
				}
				return request
						.execute()
						.returnContent()
						.asString(Consts.UTF_8);
				
			}else{
				logger.info(jsb.get("Method") + "这个方法暂时还没写好");
				return jsb.get("Method") + "这个方法暂时还没写好";
			}
		} else {
			logger.info("缺少URLFOLDER,Method,ContentType,Expected_Key,Expected_Value,TestCaseCore其中的某个或多个字段");
			return "缺少URLFOLDER,Method,ContentType,Expected_Key,Expected_Value,TestCaseCore其中的某个或多个字段";
		}

	}

	private String SetJsonObjectRequestMultiJson(JSONObject jsb, String JsonObjectTags, String JsonArrayTags,
			StringBuffer stf, String RequestBody) {
		
		if (JsonObjectTags.contains(",")){
			//有多个jsonobjects
			
			String JsonObjectNames[] = JsonObjectTags.split(",");
			List<String> JsonObjectNameList = new ArrayList<String>();
			for (int i = 0; i < JsonObjectNames.length; i++) {
				JsonObjectNameList.add(JsonObjectNames[i]);
			}
			
			logger.info("POST请求包含: " + JsonObjectNameList.size() + "个JsonObject");
			
			
			for (int i = 0; i < JsonObjectNameList.size(); i++) {
				String jsrString=jsb.get(JsonObjectNameList.get(i)).toString();
				if (!(jsrString.equals(null)||jsrString.equals("null"))) {
					JSONObject jsr = new JSONObject(jsrString);
					stf.append("\"" + JsonObjectNameList.get(i).toString() + "\":" + jsr.toString()).append(",");
				}
				
			}
			
			for (int i = 0; i < JsonObjectNameList.size(); i++) {
				jsb.remove(JsonObjectNameList.get(i));//把所有的jsonobjects全部从jsb中去掉
			}
			
		}else {
			//有一个jsonobject
			
			String jsrString = jsb.get(jsb.getString("HasJsonObjects")).toString();
			if (!(jsrString.equals(null)||jsrString.equals("null"))) {
				JSONObject jsr = new JSONObject(jsrString);
				stf.append("\"").append(jsb.getString("HasJsonObjects")).append("\"").append(":").append(jsr.toString()).append(",");
			}
			jsb.remove(jsb.getString("HasJsonObjects"));
			
		}
		
		if (JsonArrayTags.contains(",")) {
			//有多个jsonarrays
			
			String JsonArrayNames[] = JsonArrayTags.split(",");
			List<String> JsonArrayNameList = new ArrayList<String>();
			for (int i = 0; i < JsonArrayNames.length; i++) {
				JsonArrayNameList.add(JsonArrayNames[i]);
			}

			logger.info("POST请求包含: " + JsonArrayNameList.size() + "个JsonArray数组");

			for (int i = 0; i < JsonArrayNameList.size(); i++) {
				String jsrString=jsb.get(JsonArrayNameList.get(i)).toString();
				if (!(jsrString.equals(null)||jsrString.equals("null"))) {
					JSONArray jsr = new JSONArray(jsrString);
					stf.append("\"" + JsonArrayNameList.get(i).toString() + "\":" + jsr.toString()).append(",");
				}
				
			}

			stf.substring(0, stf.length() - 1);

			for (int i = 0; i < JsonArrayNameList.size(); i++) {
				jsb.remove(JsonArrayNameList.get(i));
			}
			
		}else {
			//有一个jsonarray
			
			String jsrString = jsb.get(jsb.getString("HasJsonArrays")).toString();
			if (!(jsrString.equals(null)||jsrString.equals("null"))) {
				JSONObject jsr = new JSONObject(jsrString);
				stf.append("\"").append(jsb.getString("HasJsonArrays")).append("\"").append(":").append(jsr.toString()).append(",");
			}
			jsb.remove(jsb.getString("HasJsonArrays"));
		}
		
		String AppendResult=stf.toString();
		AppendResult=AppendResult.substring(0,AppendResult.length()-1);
		
		RequestBody = jsb.toString();
		RequestBody = RequestBody.substring(0, RequestBody.length() - 1);
		RequestBody = RequestBody +","+ AppendResult + "}";
		
		return RequestBody;
	}



	private String SetJsonObjectRequestBodyHasJsonObjects(JSONObject jsb, String JsonObjectTags, StringBuffer stf,String RequestBody) {
		
		if (JsonObjectTags.contains(",")) {

			String JsonObjectNames[] = JsonObjectTags.split(",");
			List<String> JsonObjectNameList = new ArrayList<String>();
			for (int i = 0; i < JsonObjectNames.length; i++) {
				JsonObjectNameList.add(JsonObjectNames[i]);
			}

			logger.info("POST请求包含: " + JsonObjectNameList.size() + "个JsonObject");

			for (int i = 0; i < JsonObjectNameList.size(); i++) {
				String jsrString=jsb.get(JsonObjectNameList.get(i)).toString();
				if (!(jsrString.equals(null)||jsrString.equals("null"))) {
					JSONObject jsr = new JSONObject();
					stf.append("\"" + JsonObjectNameList.get(i).toString() + "\":" + jsr.toString()).append(",");
				}
				
			}

			stf.substring(0, stf.length() - 1);

			for (int i = 0; i < JsonObjectNameList.size(); i++) {
				jsb.remove(JsonObjectNameList.get(i));
			}
			
			RequestBody = jsb.toString();
			RequestBody = RequestBody.substring(0, RequestBody.length() - 1);
			RequestBody = RequestBody + stf.toString() + "}";

		} else {
			try {
				String jsrString = jsb.get(jsb.getString("HasJsonObjects")).toString();
				JSONObject jsr = new JSONObject(jsrString);
				jsb.remove(jsb.getString("HasJsonObjects"));
				RequestBody = jsb.toString();
				RequestBody = RequestBody.substring(0, RequestBody.length() - 1);
				RequestBody = RequestBody + "," + "\"" + jsb.getString("HasJsonObjects") + "\":" + jsr.toString() + "}";
			} catch (Exception e) {
				RequestBody=jsb.toString();
			}

		}
		return RequestBody;
	}

	/**
	 * 将一个含有JsonArray对象的JsonObect重构拼装成JsonObject格式的String型Body
	 * 
	 * @param jsb
	 *            传入带有JsonArray键值对的JsonObejct,是否带有JsonArray键值对由Excel中HasJsonArrays键值的Values决定
	 * @param JsonArrayTags
	 *            HasJsonArrays的键值,每个键值String由","分隔,
	 *            如HasJsonArrays:attachments,pictures
	 * @param stf
	 *            拼接字符串中间参数
	 * @param RequestBody
	 *            请求Body的String对象
	 * @return JsonObject的String型Body
	 */
	private String SetJsonObjectRequestBodyHasJsonArrays(JSONObject jsb, String JsonArrayTags, StringBuffer stf,
			String RequestBody) {

		if (JsonArrayTags.contains(",")) {

			String JsonArrayNames[] = JsonArrayTags.split(",");
			List<String> JsonArrayNameList = new ArrayList<String>();
			for (int i = 0; i < JsonArrayNames.length; i++) {
				JsonArrayNameList.add(JsonArrayNames[i]);
			}

			logger.info("POST请求包含: " + JsonArrayNameList.size() + "个JsonArray数组");

			for (int i = 0; i < JsonArrayNameList.size(); i++) {
				String jsrString=jsb.get(JsonArrayNameList.get(i)).toString();
				if (!(jsrString.equals(null)||jsrString.equals("null"))) {
					JSONArray jsr = new JSONArray(jsrString);
					stf.append("\"" + JsonArrayNameList.get(i).toString() + "\":" + jsr.toString()).append(",");
				}
				
			}

			stf.substring(0, stf.length() - 1);

			for (int i = 0; i < JsonArrayNameList.size(); i++) {
				jsb.remove(JsonArrayNameList.get(i));
			}
			RequestBody = jsb.toString();
			RequestBody = RequestBody.substring(0, RequestBody.length() - 1);
			RequestBody = RequestBody + stf.toString() + "}";

		} else {

			String jsrString = jsb.get(jsb.getString("HasJsonArrays")).toString();
			if (!(jsrString.equals(null)||jsrString.equals("null"))) {
				JSONArray jsr = new JSONArray(jsrString);
				jsb.remove(jsb.getString("HasJsonArrays"));
				RequestBody = jsb.toString();
				RequestBody = RequestBody.substring(0, RequestBody.length() - 1);
				RequestBody = RequestBody + "," + "\"" + jsb.getString("HasJsonArrays") + "\":" + jsr.toString() + "}";
			}
			

		}
		return RequestBody;
	}

	/**
	 * 从JsonObject数据中抽取必要的参数拼接成GET请求参数
	 * 
	 * @param jsb
	 *            传入JsonObject型测试数据
	 * @param KeysNotParams
	 *            在GET请求中不需要包涵的字段数组
	 * @return 返回请求参数,如: ?aa=11&bb=22
	 */
	private String SetGETRequestParams(JSONObject jsb, String[] KeysNotParams) {

		String Key;
		Set<String> set = jsb.keySet();
		List paramTags = (List) set.stream().collect(Collectors.toList());
		String Params = "?";

		for (int i = 0; i < set.size(); i++) {
			// 遍历所有jsb的Key
			Key = (String) paramTags.get(i);// 获取第Key的名字
			int flag = 0;
			for (int j = 0; j < KeysNotParams.length; j++) {
				// 每一个Key和所有的KeysNotParams都不符合
				if (!Key.equals(KeysNotParams[j])) {
					flag++;
				} else {
					break;
				}
				if (flag == KeysNotParams.length&&!(jsb.get(Key).equals(null)||jsb.get(Key).toString().equals(""))) {
					Params += Key + "=" + jsb.get(Key) + "&";
				}
			}
		}
		Params = Params.substring(0, Params.length() - 1);
		return Params;
	}

	/**
	 * 从JsonObject数据中抽取必要的参数拼接成POST请求参数
	 * 
	 * @param jsb
	 *            传入JsonObject型测试数据
	 * @param KeysNotParams
	 *            在POST请求中不需要包涵的字段数组
	 * @return 返回请求Body,如: aa=11&bb=22
	 */
	private String SetPOSTRequestBody(JSONObject jsb, String[] KeysNotParams) {

		Set<String> set = jsb.keySet();
		String Key;
		String Value;
		String Params = "";

		List paramTags = (List) set.stream().collect(Collectors.toList());

		for (int i = 0; i < set.size(); i++) {
			// 遍历所有jsb的Key
			Key = (String) paramTags.get(i);// 获取第Key的名字
			int flag = 0;// 标记每一个Key是否全部都
			for (int j = 0; j < KeysNotParams.length; j++) {
				// 每一个Key和所有的KeysNotParams都不符合
				if (!Key.equals(KeysNotParams[j])) {
					flag++;
				} else {
					break;
				}
				if (flag == KeysNotParams.length) {
					Params += Key + "=" + jsb.get(Key) + "&";
				}
			}
		}

		return Params.substring(0, Params.length() - 1);
	}

	/**
	 * 验证输入的URL是否符合HttpFluent格式标准, HttpFluent请求地址必须包含协议名称http,https等
	 * 
	 * @param URL
	 *            URL参数地址
	 * @return 返回验证后的URL地址
	 */
	public String AsureURL(String URL) {

		// 确保URL开头是符合httpfluent发送请求的标准

		if (URL.matches("^((https|http|ftp|rtsp|mms)?:\\/\\/)[^\\s]+")) {
			return URL;
		} else {
//			logger.info("URL地址有误,尝试重新编辑,如仍然报错,请确认URL地址");
			if (URL.matches(
					"^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d).*")) {
				return URL = "http://" + URL;
			} else {
				return URL = "http://" + URL;
			}
		}
	}
	
	private HttpEntity SetPostFileRequestWithParams(JSONObject jsb){
		
		Set<String> set = jsb.keySet();
		String Key;
		String Value;
		List paramTags = (List) set.stream().collect(Collectors.toList());
		
		String filePath = jsb.getString("FilePath");
		File uploadFile = new File(filePath);
		MultipartEntityBuilder multBuilder=MultipartEntityBuilder.create()
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
		        .setCharset(Charset.forName("utf-8"))
		        .addBinaryBody(jsb.optString("file", "file"), uploadFile);
//		        .(jsb.optString("file", "file"), uploadFile, ContentType.parse((String) jsb.get("ContentType")), jsb.get("FilenameWithType").toString());
		
		for (int i = 0; i < paramTags.size(); i++) {
			Key=paramTags.get(i).toString();
			Value=jsb.get(Key).toString();
			multBuilder=multBuilder.addTextBody(Key, Value);
		}
		
		HttpEntity entity = multBuilder.build();
		
		return entity;
	}

	public boolean AssertSQLMap_JsonObject(Map SqlResultMap, JSONObject jsb) {
		Iterator<String> it = SqlResultMap.keySet().iterator();
		while(it.hasNext()) {
	         String key = it.next();
	         String val = SqlResultMap.get(key).toString();
	       if (jsb.get(key).toString().equals(val)) {
			   continue;
	       }else {
			return false;
	       }
		}
		return true;
	}

	public HttpResponse GetRespoonse(JSONObject jsb) throws ClientProtocolException, IOException {
		
		pro=GetHttpfluentProperties(pro);
		
		if (jsb.has("URLFOLDER") && jsb.has("Method") && jsb.has("ContentType") && jsb.has("Expected_Key")
				&& jsb.has("Expected_Value") ){
			
			String URL = pro.getProperty("ServerIp")+jsb.getString("URLFOLDER");
			URL=AsureURL(URL);
			String[] notNeededParamTags = new String[] { "URLFOLDER", "Method", "ContentType", "Expected_Key", "Expected_Value" };
			
			if (jsb.get("Method").equals("GET")) {

				String params;
				params = SetGETRequestParams(jsb, notNeededParamTags);
				
				logger.info("GET请求参数:" + params);
				logger.info("GET请求:" + URL + params);
				
				return Request.Get(URL + params)
//						.connectTimeout(100);
//						.viaProxy("127.0.0.1:8888");
						.execute()
						.returnResponse();
				
			}else if (jsb.get("Method").equals("POST")) {
				
				String RequestBody = SetPOSTRequestBody(jsb, notNeededParamTags);
				
				logger.info("标准POST请求: " + URL);
				logger.info("标准请求body: " + RequestBody);
				
				return Request.Post(URL)
//				.connectTimeout(100)
				.bodyString(RequestBody, ContentType.parse((String) jsb.get("ContentType")))
				.execute()
				.returnResponse();
				
			} else {
				return null;
			}
			
		}else {
			return null;
		}

		
	}

	/**
	 * 比较两个map中除去不同部分的key和value之后是否完全相同
	 * @param map1  比较对象map1
	 * @param map2  比较对象map1
	 * @param RemoveList 需要从两个map中去除比较的相同部分key值数组
	 * @return
	 */
	public static boolean compareMap(Map map1, Map map2, String[] RemoveList) {
	    boolean contain = false;
	    
	    if (RemoveList!=null) {
	    	for (int i = 0; i < RemoveList.length; i++) {
				map1.remove(RemoveList[i]);
				map2.remove(RemoveList[i]);
			}	
		}
//		System.out.println("map1---->"+map1);
//		System.out.println("map2---->"+map2);
	    for (Object o : map1.keySet()) {
//	    	System.out.println(o);
	        contain = map2.containsKey(o);
	        if (contain) {
//	        	System.out.println(map1.get(o));
//	        	System.out.println(map2.get(o));
	            contain = map1.get(o).toString().equals(map2.get(o).toString());
	        }
	        if (!contain) {
	            return false;
	        }
	    }
	    return true;
	}
	/**
	 * 传入毫秒数, 返回日期类型
	 * @param jsb 测试用例JsonObject
	 * @param MillSecondsKeyName 毫秒数
	 * @return
	 */
	public static Date MillsSecondsToDate(JSONObject jsb,String MillSecondsKeyName) {
		try {
			long updatedMillis=Long.parseLong(jsb.get(MillSecondsKeyName).toString());
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(updatedMillis); 
			Date date = c.getTime();
			return date;
		} catch (Exception e) {
			return null;
		}
		
	}
	/**
	 * 根据预期的Map,与数据库返回的Map集合中的每一个进行比对, Map的Key必须和数据库中的Key相一致
	 * @param ExpectedKeyValues 预期的数据库Map键值对
	 * @param jdbc 数据库connection对象
	 * @param TableName 目标数据表名称
	 * @param Condition 查询条件
	 * @return 返回值
	 * @throws SQLException
	 */
	public boolean CheckSqlResult(Map<String, Object> ExpectedKeyValues,JdbcUtils jdbc,String TableName,String Condition) throws SQLException{
		boolean flag=true;
		StringBuffer Stf=new StringBuffer();
		Stf.append("SELECT ");
		Iterator<String> iterator = ExpectedKeyValues.keySet().iterator();
		while(iterator.hasNext()){
	            String key = iterator.next();
	            Stf.append(key);
	            Stf.append(",");
	      }
		String Sql=Stf.toString();
		Sql=Sql.substring(0, Sql.length()-1);
		Sql=Sql+" FROM "+TableName+" "+Condition;
//		System.out.println(Sql);
		List<Map<String, Object>> SqlResultList=jdbc.findModeResult(Sql, null);
		for (int i = 0; i < SqlResultList.size(); i++) {
			if (flag==false) {
				break;
			}
			Map SqlResult=SqlResultList.get(i);
//			System.out.println("SqlResult: "+SqlResult);
			if (!compareMap(ExpectedKeyValues, SqlResult, null)) {
				flag=false;
				break;
			}
		}		
		return flag;
		
	}
	
	/**
	 * 将文件保存为FileType指定的格式文件,并返回该File对象
	 * @param URL 文件的下载URL
	 * @param SaveFoleder 下载保存文件的路径
	 * @param FileType 保存的文件类型
	 * @return 返回下载完成的File对象
	 */
	public File SaveAs(String URL,String SaveFoleder,String FileType) throws ClientProtocolException, IOException {
		
		Date now = new Date(); 
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String DateStamp=dataFormat.format(now);
		File SavePathFolder=new File(SaveFoleder);
		if (!SavePathFolder.exists()) {
			try {
				SavePathFolder.createNewFile();
			} catch (IOException e) {
				logger.warn("创建下载保存路径失败, 请手动添加路径:"+SavePathFolder.getAbsolutePath());
				e.printStackTrace();
			}
		}
		File SaveFile=new File(SaveFoleder+DateStamp+FileType);
		Request.Get(URL)
		.connectTimeout(20000)
		.socketTimeout(20000)
//	.viaProxy("127.0.0.1:8888")
		.execute().saveContent(SaveFile);
		File File=new File(SaveFoleder+DateStamp+FileType);
		return File;
	}
	
	/**
	 * 将图片转换为String文本
	 * @param FileImage 图片文件源
	 * @return 返回图片的文本内容
	 * @throws TesseractException
	 */
	public String Image2String(File FileImage) throws TesseractException{
		Tesseract tesseract =new Tesseract();
    	String result=tesseract.doOCR(FileImage);
    	return result;
	}
}
