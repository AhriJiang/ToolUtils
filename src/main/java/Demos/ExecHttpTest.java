package Demos;

import java.util.Map;

import InterfaceTestUtils.JdbcUtils;
import InterfaceTestUtils.SSLFulentUtils;

public class ExecHttpTest {

	public void execTestCase(Map<String, String> data, JdbcUtils user_jdbc) {
		//执行用户sql
		
		//组装并执行http请求
		SSLFulentUtils sf=new SSLFulentUtils("0");
		sf.Request(data);
	}

}
