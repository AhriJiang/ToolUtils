package InterfaceTestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class SSLFulentUtils {
	
	public static Logger logger = Logger.getLogger(SSLFulentUtils.class);
	private HttpClientBuilder clientBuilder;
	private HttpResponse Response;
	private String ServerIp;
	private Properties pro=new Properties();
	
	
	/**
	 * 构造SSLFluent实例
	 * @param proxyFlag 0:不使用代理, 1:使用代理
	 */
	public SSLFulentUtils(String proxyFlag){
//		this.pro=GetHttpfluentProperties(pro);
		this.ServerIp=pro.getProperty("ServerIp");
		this.clientBuilder=BuildConnection(proxyFlag);
	}
	
	
	
	
	/**
	 * 获取参数Response的Header信息,构造SSLFluent实例
	 * @param proxyFlag 0:不使用代理
	 * @param Response 外部响应实例
	 */
	public SSLFulentUtils(String proxyFlag,HttpResponse Response){
		this.pro=GetHttpfluentProperties(pro);
		this.ServerIp=pro.getProperty("ServerIp");
		this.Response=Response;
		this.clientBuilder=BuildConnection(proxyFlag);
	}
	
	/**
	 * 构建SSL客户端
	 * @param proxyFlag 代理开关 0:不使用代理
	 * @return HttpClient构造器
	 */
	private HttpClientBuilder BuildConnection(String proxyFlag){
		
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();  
		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();  
		registryBuilder.register("http", plainSF);
		
		//指定信任密钥存储对象和连接套接字工厂  
		try {
		    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());  
			SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, new AnyTrustStrategy()).build();
			LayeredConnectionSocketFactory sslSF= new SSLConnectionSocketFactory(sslContext);
//		    LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
			registryBuilder.register("https", sslSF);   
		    
		} catch (KeyStoreException e) {  
		    throw new RuntimeException(e);  
		} catch (KeyManagementException e) {  
		    throw new RuntimeException(e);  
		} catch (NoSuchAlgorithmException e) {  
		    throw new RuntimeException(e);  
		}  
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();  
		//设置连接管理器  
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);  
		//connManager.setDefaultConnectionConfig(null);  
		//connManager.setDefaultSocketConfig(null);
		
		//构建客户端  
		HttpClientBuilder ClientBuilder= HttpClientBuilder.create();
		if (proxyFlag.equals("1")) {
			System.out.println("正在使用调试模式,请启动filder本地监听端口8888");
			ClientBuilder.setProxy(new HttpHost("127.0.0.1", 8888));
		}
		
		ClientBuilder=ClientBuilder
				.setConnectionManager(connManager);
		if (Response!=null&&Response.containsHeader("ContentType")) {
			Collection<Header> HeaderCollection=new ArrayList();
			Header[] Headers=Response.getAllHeaders();
			for (int i = 0; i < Headers.length; i++) {
				HeaderCollection.add(Headers[i]);
			}
			ClientBuilder=ClientBuilder.setDefaultHeaders(HeaderCollection);
		}
		
		return ClientBuilder;
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
	public String Request(JSONObject jsb,String CookieCharactor) throws URISyntaxException, ClientProtocolException, IOException {
		
		if (jsb.has("URLFOLDER") && jsb.has("Method") && jsb.has("ContentType") && jsb.has("Expected_Key")
				&& jsb.has("Expected_Value") && jsb.has("TestCaseCore")) {
			
			String Folder=jsb.getString("URLFOLDER");
			
			String[] notNeededParamTags = new String[] { "URLFOLDER", "Method", "ContentType", "TestCaseCore",
					"Expected_Key", "Expected_Value", "正/反例", "测试字段", "测试目的", "HasJsonArrays" };
			
			//Get请求
			if (jsb.get("Method").equals("GET")) {
				
				String params;
				params = SetGETRequestParams(jsb, notNeededParamTags);
				
				//设置Uri
				URI uri=new URI("https", ServerIp, Folder, params);
				
				//日志打印
				logger.info("GET请求参数:" + params);
				logger.info("GET请求测试字段: " + jsb.get("TestCaseCore"));
				logger.info("测试目的:"+ jsb.get("测试目的"));
				logger.info("GET请求:" + uri.toString() + params);
				
				HttpGet request=new HttpGet(uri);
				
				return GetRequestEntity(request);
				
			}
			//Post请求
			else if (jsb.get("Method").equals("POST")) {
				
				//设置Uri
				URI uri=new URI("https", ServerIp, Folder);
				
				//json格式Post请求
				if (jsb.get("ContentType").toString().toLowerCase().equals("application/json;charset=utf-8")) {
					
					//日志打印
					logger.info("POST请求: " + uri.toString());
					logger.info("POST请求测试字段: " + jsb.get("TestCaseCore"));
					logger.info("POST请求测试目的:"+jsb.get("测试目的").toString());
					
					//请求body内容
					String RequestBody = "";
					//stringBuffer过渡拼接body
					StringBuffer stf = new StringBuffer();
					
					HttpPost request=new HttpPost(uri);
					request.setHeader("ContentType", "application/json;charset=utf-8");
					
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
					
					StringEntity entity=new StringEntity(RequestBody, "utf-8");
					request.setEntity(entity);
					
					return GetRequestEntity(request);
					
				}else {
					
					String RequestBody = SetPOSTRequestBody(jsb, notNeededParamTags);
					
					//日志打印
					logger.info("标准POST请求: " + uri.toString());
					logger.info("POST请求测试字段: " + jsb.get("TestCaseCore"));
					logger.info("标准请求body: " + RequestBody);
					
					HttpPost request=new HttpPost(uri);
					StringEntity entity=new StringEntity(RequestBody, "utf-8");
					request.setEntity(entity);
					
					return GetRequestEntity(request);
				}
			}else if(jsb.get("Method").equals("UPLOAD")){
				
				String exception = "上传文件时,出了一个小意外";
				URI uri=new URI("https", ServerIp, Folder);
				
				if (jsb.has("FilePath")) {
					
					String fileNameWithPath = jsb.getString("FilePath");
					File uploadFile = new File(fileNameWithPath);
					HttpEntity entity = SetPostFileRequestWithParams(jsb);
					logger.info("上传测试字段: "+jsb.getString("TestCaseCore")+"-----"+ jsb.getString("测试目的"));
					logger.info("准备上传文件: "+ jsb.getString("FilePath"));
					
					HttpPost request=new HttpPost(uri);
					clientBuilder=clientBuilder.setConnectionTimeToLive(1, TimeUnit.MINUTES);
					
					return GetRequestEntity(request);

				} else {
					logger.info("没有找到FilePath,请检查Excel数据源");
					return exception;
				}
			}else if (jsb.get("Method").equals("DOWNLOAD")) {
				
				String params;
				params = SetGETRequestParams(jsb, notNeededParamTags);

				URI uri=new URI("https", ServerIp, Folder, params);
				
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
					
					HttpGet request=new HttpGet(uri);
					SaveAsFile(request, File);
					
					String FileSavePath=File.getPath();
					File.delete();
					
					return FileSavePath;
				}else {
					
					logger.warn("没有找到DownloadFilePath关键字段");
					return "没有找到DownloadFilePath关键字段";
					
				}
				
			}else if(jsb.get("Method").equals("DELETE")){
				
				String params;
				params = SetGETRequestParams(jsb, notNeededParamTags);
				
				URI uri=new URI("https", ServerIp, Folder, params);
				
				logger.info("DELETE请求参数:" + params);
				logger.info("DELETE请求测试字段: " + jsb.get("TestCaseCore"));
				logger.info("DELETE请求:" + uri.toString());
				
				HttpDelete request=new HttpDelete(uri);
				return GetRequestEntity(request);
				
			}else{
				logger.info(jsb.get("Method") + "这个方法暂时还没写好");
				return jsb.get("Method") + "这个方法暂时还没写好";
			}
			
		}else {
			logger.info("缺少URLFOLDER,Method,ContentType,Expected_Key,Expected_Value,TestCaseCore其中的某个或多个字段");
			return "缺少URLFOLDER,Method,ContentType,Expected_Key,Expected_Value,TestCaseCore其中的某个或多个字段";
		}

	}

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
	
	private Properties GetHttpfluentProperties(Properties pro){
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
	 * 根据不同请求构建方式,返回响应的String文本
	 * @param request HttpRequestBase型请求
	 * @return 返回requet的String文本
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private String GetRequestResponse(HttpRequestBase request) throws ClientProtocolException, IOException {
		
//		return EntityUtils.toString(entity);
		return clientBuilder
				.build()
				.execute(request)
				.getEntity()
				.toString();
	}
	
	/**
	 * 获取Download请求的文件, 并保存下来
	 * @param request Download请求
	 * @param File 保存的文件对象
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private void SaveAsFile(HttpRequestBase request,File File) throws ClientProtocolException, IOException {
		HttpResponse response=clientBuilder.build().execute(request);
		final StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        final FileOutputStream out = new FileOutputStream(File);
        try {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                entity.writeTo(out);
            }
        } finally {
            out.close();
        }
	}
	
	/**
	 * 设置上传文件的Entity
	 * @param jsb 参数Json
	 * @return 上传请求的HttpEntity
	 */
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

	public String Request(Map<String, String> data) throws URISyntaxException, ClientProtocolException, IOException {
		
		String Folder=data.get("API_CATALOG");
		String HttpMethod=data.get("HTTP_METHOD");
		String ServerIp=data.get("INV_FIELD");
		String UploadPath=data.get("UPLOAD_FILE_PATH");
		String DownloadPath=data.get("DOWNLOAD_FILE_PATH");
		//自定义的万能Cookie
		String MasterCookie=data.get("MASTER_COOKIE");
		//Get请求
		if (HttpMethod.equals("0")) {
			
			String params;
			params = SetGETRequestParams(data);
			
			//设置Uri
			URI uri=new URI("https", ServerIp, Folder, params);
			
			//日志打印
			logger.info("GET请求参数:" + params);
			logger.info("GET请求测试字段: " + data.get("TEST_PURPOSE"));
			logger.info("测试描述:"+ data.get("TEST_CASE_DESCRIPTION"));
			logger.info("GET请求:" + uri.toString() + params);
			
			HttpGet request=new HttpGet(uri);
			request.setHeader("Cookie",MasterCookie);
			
			return GetRequestEntity(request);
			
		}
		//Post请求
		else if (HttpMethod.equals("1")) {
			
			//设置Uri
			URI uri=new URI("https", ServerIp, Folder,"");
			
			//json格式Post请求
			if (data.get("CONTENT_TYPE").equals("1")) {
				
				//日志打印
				logger.info("POST请求: " + uri.toString());
				logger.info("POST请求测试字段: " + data.get("TEST_PURPOSE"));
				logger.info("POST请求测试目的:"+data.get("TEST_CASE_DESCRIPTION"));
				
				String[] testKeys=data.get("TEST_CASE_KEY_SET").split("&&");
				String[] testValues=data.get("TEST_CASE_VALUE_SET").split("&&");
				
				JSONObject jsb=new JSONObject();
				for (int i = 0; i < testKeys.length; i++) {
					jsb.put(testKeys[i], testValues[i]);
				}
				
				HttpPost request=new HttpPost(uri);
				request.setHeader("ContentType", "application/json;charset=utf-8");
				request.setHeader("Cookie",MasterCookie);
				String RequestBody=jsb.toString();
				StringEntity entity=new StringEntity(RequestBody, "utf-8");
				request.setEntity(entity);
				
				return GetRequestEntity(request);
				
			}else {
				
				String RequestBody = SetPOSTRequestBody(data);
				
				//日志打印
				logger.info("POST请求: " + uri.toString());
				logger.info("POST请求测试字段: " + data.get("TEST_PURPOSE"));
				logger.info("POST请求测试目的:"+data.get("TEST_CASE_DESCRIPTION"));
				logger.info("标准请求body: " + RequestBody);
				
				HttpPost request=new HttpPost(uri);
				StringEntity entity=new StringEntity(RequestBody, "utf-8");
				request.setEntity(entity);
				
				return GetRequestEntity(request);
			}
		}
		//UPLOAD
		else if(HttpMethod.equals("2")){
			
			String exception = "上传文件时,出了一个小意外";
			URI uri=new URI("https", ServerIp, Folder);
			
				File uploadFile = new File(UploadPath);
				HttpEntity entity = SetPostFileRequestWithParams(data);
				logger.info("上传测试字段: "+data.get("TEST_PURPOSE")+"-----"+ data.get("TEST_CASE_DESCRIPTION"));
				logger.info("准备上传文件: "+ data.get("UPLOAD_FILE_PATH"));
				
				HttpPost request=new HttpPost(uri);
				request.setHeader("Cookie", MasterCookie);
				
				clientBuilder=clientBuilder.setConnectionTimeToLive(1, TimeUnit.MINUTES);
				
				return GetRequestEntity(request);

		}
		//DOWNLOAD
		else if (HttpMethod.equals("3")) {
			
			String params;
			params = SetGETRequestParams(data);

			URI uri=new URI("https", ServerIp, Folder, params);
			
			if (DownloadPath!=null) {
				
				logger.info("准备下载文件到目录: "+ DownloadPath);

				File File =new File(DownloadPath+"/result.dump");
				if (!File.exists()) {
					try {
						File.createNewFile();
					} catch (Exception e) {
						logger.warn("尝试创建下载路径失败, 请手动追加下载文件路径:"+File.getAbsolutePath());
						e.printStackTrace();
					}
				}
				
				HttpGet request=new HttpGet(uri);
				SaveAsFile(request, File);
				
				String FileSavePath=File.getPath();
				File.delete();
				
				return FileSavePath;
			}else {
				
				logger.warn("没有找到DownloadFilePath关键字段");
				return "没有找到DownloadFilePath关键字段";
				
			}
			
		}else if(HttpMethod.equals("4")){
			
			String params;
			params = SetGETRequestParams(data);
			
			URI uri=new URI("https", ServerIp, Folder, params);
			
			logger.info("DELETE请求参数:" + params);
			logger.info("DELETE请求测试字段: " + data.get("TEST_PURPOSE"));
			logger.info("DELETE请求:" + uri.toString());
			
			HttpDelete request=new HttpDelete(uri);
			return GetRequestResponse(request);
			
		}else{
			logger.info("方法暂时还没未支持");
			return "方法暂时还没未支持";
		}	
		
	}
	/**
	 * 获取请求响应body主体,并且销毁静态变量
	 * @param request http请求
	 * @return 返回主体文本
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private String GetRequestEntity(HttpRequestBase request) throws ClientProtocolException, IOException {
		
		HttpEntity entity= clientBuilder.build().execute(request).getEntity();
		String RequestBody=EntityUtils.toString(entity);
		EntityUtils.consume(entity);
		
		return RequestBody;
	}




	private HttpEntity SetPostFileRequestWithParams(Map<String, String> data) {
		Set<String> set = data.keySet();
		String Key;
		String Value;
		List paramTags = (List) set.stream().collect(Collectors.toList());
		//在服务器位置上的文件存放路径
		String filePath = data.get("UPLOAD_FILE_PATH");
		File uploadFile = new File(filePath);
		MultipartEntityBuilder multBuilder=MultipartEntityBuilder.create()
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
		        .setCharset(Charset.forName("utf-8"))
		        .addBinaryBody("file", uploadFile);
//		        .(jsb.optString("file", "file"), uploadFile, ContentType.parse((String) jsb.get("ContentType")), jsb.get("FilenameWithType").toString());
		
		for (int i = 0; i < paramTags.size(); i++) {
			Key=paramTags.get(i).toString();
			Value=data.get(Key).toString();
			multBuilder=multBuilder.addTextBody(Key, Value);
		}
		
		HttpEntity entity = multBuilder.build();
		
		return entity;
	}




	/**
	 * 从JsonObject数据中抽取必要的参数拼接成POST请求参数
	 * 
	 * @param data
	 *            传入数据库data
	 * @return 返回请求Body,如: aa=11&bb=22
	 */
	private String SetPOSTRequestBody(Map<String, String> data) {
		String params="";
		String[] keys=data.get("TEST_CASE_KEY_SET").split("&&");
		String[] values=data.get("TEST_CASE_VALUE_SET").split("&&");
		for (int i = 0; i < keys.length; i++) {
			params=params+keys[i]+"="+values[i]+"&";
		}
		return params.substring(0, params.length() - 1);
	}




	private String SetGETRequestParams(Map<String, String> data) {
		String params="?";
		String[] keys=data.get("TEST_CASE_KEY_SET").split(",");
		String[] values=data.get("TEST_CASE_VALUE_SET").split(",");
		for (int i = 0; i < keys.length; i++) {
			params=params+keys[i]+"="+values[i];
		}
		return params;
	}
}
