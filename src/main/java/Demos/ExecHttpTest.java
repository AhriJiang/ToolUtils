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

import DataBasePublicConfig.JiFengMallRedisProperties;
import InterfaceTestUtils.AssertUtils;
import InterfaceTestUtils.JdbcUtils;
import InterfaceTestUtils.SSLFulentUtils;
import redis.clients.jedis.Jedis;

public class ExecHttpTest {

	private Logger logger = Logger.getLogger(ExecHttpTest.class);
	private AssertUtils AssertUtil = new AssertUtils();

	public JSONArray execPreSql(Map<String, String> data, JdbcUtils user_jdbc) {
		
		JSONArray pre_sql_arrays;
		/**
		 * 包含4个字段: (0 启用 1 关闭) isDailyCI, isNeedRedis, isNeedPreHttpRequest,
		 * isNeedPreSql
		 */
		JSONObject preActrionFlags = new JSONObject(data.get("PRE_ACTION_FLAGS"));
		boolean isNeedPresqlFlag = preActrionFlags.get("isNeedPresql").equals("0");

		if (isNeedPresqlFlag) {

			pre_sql_arrays = new JSONArray(data.get("PRE_SQL").toString());
			JSONObject pre_sql_object;
			String pre_sql_object_sql_text;
			Map<String, String> pre_sql_object_select_result_map;
			String sql_Key;
			/**
			 * 包含5个字段: sqlType(0 select, 1 update), sqlText (sql的文本),
			 * selectedKey(select语句的选取对象), selectedKeyValue(选取出来的对象值),
			 * postPosition(HEADER, COOKIE, BODY, PATH), positionKey
			 */
			for (int i = 0; i < pre_sql_arrays.length(); i++) {

				logger.info("执行第" + (i + 1) + "组Sql");

				pre_sql_object = new JSONObject(pre_sql_arrays.get(i));
				pre_sql_object_sql_text = pre_sql_object.getString("sqlText");
				sql_Key = pre_sql_object.getString("selectedKey");

				if (pre_sql_object.get("sqlType") .equals("0")) {
					logger.info("执行select语句: " + pre_sql_object_sql_text);
					try {
						pre_sql_object_select_result_map = user_jdbc.findSimpleResult(pre_sql_object_sql_text, null);
						pre_sql_object.put("selectedKeyValue", pre_sql_object_select_result_map.get(sql_Key));
					} catch (SQLException e) {
						logger.warn("Sql执行出错: " + pre_sql_object_sql_text);
					}
				} else if (pre_sql_object.get("sqlType") .equals("1")) {
					logger.info("执行update语句: " + pre_sql_object_sql_text);
					try {
						user_jdbc.updateByPreparedStatement(pre_sql_object_sql_text, null);
					} catch (SQLException e) {
						logger.warn("Sql执行出错: " + pre_sql_object_sql_text);
					}
				}
				pre_sql_arrays.put(i, pre_sql_object);
			}

			return pre_sql_arrays;

		}else {
			return pre_sql_arrays=new JSONArray();
		}

	}

	public JSONArray execPreRedis(Map<String, String> data, JdbcUtils user_jdbc) {
		
		JSONArray pre_redis_arrays;
		/**
		 * 包含4个字段: (0 启用 1 关闭) isDailyCI, isNeedRedis, isNeedPreHttpRequest,
		 * isNeedPreSql
		 */
		JSONObject preActrionFlags = new JSONObject(data.get("PRE_ACTION_FLAGS"));
		boolean isNeedPreHttpRequest = preActrionFlags.get("isNeedPreHttpRequest").equals("0");
		boolean isNeedRedis = preActrionFlags.get("isNeedRedis").equals("0");

		if (isNeedRedis) {

			pre_redis_arrays = new JSONArray(data.get("PRE_REDIS").toString());
			/**
			 * 包含10个字段:
				host,
				port,
				timeOut,
				method(0 get 1 set),
				selectedRedisKey(获取的对象Key名称),
				selectedKeyValue,
				setKeyName,
				setKeyValue,
				postPosition,
				positionName
			 */
			JSONObject pre_redis_object;
			Jedis Jedis;
			String host;
			Integer port;
			Integer timeOut;
			String selectedRedisKey;
			String selectedKeyValue;
			String setKeyName;
			String setKeyValue;
			
			for (int i = 0; i < pre_redis_arrays.length(); i++) {

				logger.info("执行第" + (i + 1) + "组Redis请求");

				pre_redis_object = new JSONObject(pre_redis_arrays.get(i));
				selectedRedisKey=pre_redis_object.getString("selectedRedisKey");
				setKeyName=pre_redis_object.getString("setKeyName");
				setKeyValue=pre_redis_object.getString("setKeyValue");
				
				host=pre_redis_object.get("host").toString();
				port=Integer.parseInt(pre_redis_object.get("port").toString());
				timeOut=Integer.parseInt(pre_redis_object.get("timeOut").toString());
				Jedis=new Jedis(host,port,timeOut);
				
				
				if (pre_redis_object.get("method") .equals("0")) {
					selectedKeyValue=Jedis.get(selectedRedisKey);
					pre_redis_object.put(selectedRedisKey, selectedKeyValue);
				} else if (pre_redis_object.get("method") .equals("1")) {
					Jedis.set(setKeyName,setKeyValue);
				}
				pre_redis_arrays.put(i, pre_redis_object);
			}

			return pre_redis_arrays;

		}else {
			return pre_redis_arrays=new JSONArray();
		}

	}
	
	public JSONArray exePreHttp(Map<String, String> data, JdbcUtils user_jdbc) {

		JSONArray pre_Http_arrays;

		JSONObject preActrionFlags = new JSONObject(data.get("PRE_ACTION_FLAGS"));
		boolean isNeedPreHttpRequest = preActrionFlags.get("isNeedPreHttpRequest").equals("0");

		if (isNeedPreHttpRequest) {

			pre_Http_arrays = new JSONArray(data.get("PRE_HTTP_REQUEST").toString());

			return pre_Http_arrays;
			
		} else {
			return pre_Http_arrays = new JSONArray();
		}

	}
	
	public void execTestCase(Map<String, String> data, JSONArray preSqlResults, JSONArray preHttpResults,
			JSONArray preRedisResults, JdbcUtils user_jdbc)
			throws ClientProtocolException, URISyntaxException, IOException, SQLException {

		// 组装并执行http请求
		SSLFulentUtils sf = new SSLFulentUtils("0");
		String RequestResult = sf.Request(data);

		// 验证结果

		Boolean AssertPassFlag = false;

		// json验证
		if (AssertUtil.JsonPathAssert(data, RequestResult) == false) {
			AssertPassFlag = true;
		}
		// 数据库验证

		// if(AssertUtil.SqlAssert(data,RequestResult,user_jdbc)==false){
		// AssertPassFlag=true;
		// }

		if (AssertPassFlag == true) {
			Assert.fail();
		}
	}



}
