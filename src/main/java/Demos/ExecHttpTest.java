package Demos;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import InterfaceTestUtils.AssertUtils;
import InterfaceTestUtils.JdbcUtils;
import InterfaceTestUtils.SSLFulentUtils;

public class ExecHttpTest {
	
	private Logger logger = Logger.getLogger(ExecHttpTest.class);
	private AssertUtils AssertUtil=new AssertUtils();
	
	public void execTestCase(Map<String, String> data, JdbcUtils user_jdbc)
			throws ClientProtocolException, URISyntaxException, IOException{
		// 执行用户增删改sql, 0 需要执行sql, 1 不需要执行sql
		
		boolean isNeedExcuteUpdateSql=data.get("PRE_UPDATE_SQL_FLAG").equals("0");
		if (isNeedExcuteUpdateSql) {
			String[] Sqls=data.get("PRE_UPDATE_SQL").split("&&");
			for (int i = 0; i < Sqls.length; i++) {
				logger.info("执行第"+(i+1)+"条Sql:"+Sqls[i]);
				try {
					user_jdbc.updateByPreparedStatement(Sqls[i], null);
				} catch (SQLException e) {
					logger.warn("第"+(i+1)+"条Sql执行出错");
				}
			}
		}
		
		// 执行用户查询Sql, 0 需要执行Sql 1 不需要执行Sql
		
		
		// 组装并执行http请求
		SSLFulentUtils sf = new SSLFulentUtils("0");
		String RequestResult=sf.Request(data);
		
		// 验证结果
		
		Boolean AssertPassFlag=false;
		
		// json验证
		if (AssertUtil.JsonPathAssert(data,RequestResult)==false) {
			AssertPassFlag=true;
		}
		// html验证
		if (AssertUtil.HtmlTextAssert(data,RequestResult)==false) {
			AssertPassFlag=true;
		}
		// 数据库验证
		
		
		
		if (AssertPassFlag==true) {
			Assert.fail();
		}
	}

}
