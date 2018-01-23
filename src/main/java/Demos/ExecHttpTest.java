package Demos;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import InterfaceTestUtils.JdbcUtils;
import InterfaceTestUtils.SSLFulentUtils;

public class ExecHttpTest {
	
	private Logger logger = Logger.getLogger(ExecHttpTest.class);
	
	public void execTestCase(Map<String, String> data, JdbcUtils user_jdbc)
			throws ClientProtocolException, URISyntaxException, IOException{
		// 执行用户增删改sql, 0 需要执行sql, 1 不需要执行sql
		boolean isNeedExcuteUpdateSql=data.get("PRE_UPDATE_SQL_FLAG").equals("0");
		if (isNeedExcuteUpdateSql) {
			String[] Sqls=data.get("PRE_UPDATE_SQL").split("&&");
			for (int i = 0; i < Sqls.length; i++) {
				System.out.println(Sqls[i]);
				try {
					user_jdbc.updateByPreparedStatement(Sqls[i], null);
				} catch (SQLException e) {
					logger.warn(Sqls[i]+"执行出错");
				}
			}
		}
		
		// 执行用户查询Sql, 0 需要执行Sql 1 不需要执行Sql
		
		
		// 组装并执行http请求
		SSLFulentUtils sf = new SSLFulentUtils("0");
		sf.Request(data);

	}

}
