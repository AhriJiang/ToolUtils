package Demos;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HttpDemo {
	
	Map<String,String> cookie;
	RequestConfig requestConfig=RequestConfig.custom()
			.setProxy(new HttpHost("127.0.0.1",8888))
			.setConnectTimeout(10000)
			.setConnectionRequestTimeout(10000)
			.build();
	
	@BeforeClass(groups = { "testcase" })
	public void BeforeRun() throws Exception {
		
	}

	@Test
	public void JsoupGet() throws Exception {
		//上课时候的一个综合案例
		HttpFluent hf=new HttpFluent();
		String body=hf.GetByProxyOrNot(false,"","https://passport.banggo.com/CASServer/custom/registryPage.do");
		
		Document doc=Jsoup.parse(body);
		String vkey=doc.getElementById("vKey").val();
		String vvalue=doc.getElementById("vValue").val();
		System.out.println(vkey);
		System.out.println(vvalue);
		
		Map map = new HashMap<String, String>();
		map.put(vkey, vvalue);
		map.put("uid", "test123456789012314122");
		
		body=hf.PostByMaps(false,"","https://passport.banggo.com/CASServer/custom/checkUidUniqueAjax.do", map);
		System.out.println(body);
		
		JSONObject jb=new JSONObject(body);
		vkey=jb.getString("CASCheckKey");
		vvalue=jb.getString("CASCheckVale");
		
		System.out.println(vkey);
		System.out.println(vvalue);
		map.clear();
		map.put(vkey, vvalue);
		map.put("uid", "test123456789012314122");
		body=hf.PostByMaps(false,"","https://passport.banggo.com/CASServer/custom/checkUidUniqueAjax.do", map);
		System.out.println(body);
		hf.LRString(body, "用户", "用",0);

	}

	
	@Test
	public void JsoupGetDoc() throws Exception {
		Connection conn =Jsoup.connect("http://192.168.10.201/php/html.php");
		conn.proxy("127.0.0.1",8888);
		Document docu=conn.get();
		
	}
	
	@Test
	public void JsoupPost() throws Exception {
		Connection conn =Jsoup.connect("http://192.168.10.201/php/html.php");
		conn.proxy("127.0.0.1",8888);
		Document doc=conn.post();
	}
	
	@Test
	public String GetBangGo() throws IOException{
		Connection conn =Jsoup.connect("https://passport.banggo.com/CASServer/custom/registryPage.do");
		conn.proxy("127.0.0.1",8888);
		conn.method(Method.GET);//设置Jsoup的访问方法
		Response response=conn.execute();
		cookie = response.cookies();
		return response.body();
	}
	
	@Test
	public void BangGoAjax(){
		Connection conn =Jsoup.connect("https://passport.banggo.com/CASServer/custom/checkUidUniqueAjax.do");
		conn.proxy("127.0.0.1",8888);
		conn.method(Method.POST);


	}

	@Test
	public void HttpClientGet() throws Exception{
		HttpclientDemo.sendHttpGet("http://192.168.10.201/php/html.php");
	}

	@AfterClass
	public void AfterRun() {
		
	}
	
	@DataProvider
	public Object[][] ExcelDataProvidorDemo() {
		//1.每个dataprovidor就是一个业务字段组合
		//2.将excel文件路径,sheet名称写入配置文件中
		//3.dataprovodir中配置, 请求方法, 请求URL, 需要遍历的字段参数
		JSONArray json = null;
		return new Object[][] {
			
				
				new Object[] { "HSI"},
};
	}
	
}
