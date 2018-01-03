package Demos;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import DataBasePublicConfig.TestCaseDBConfig;
import InterfaceTestUtils.JdbcUtils;

import org.testng.annotations.Parameters;

public class StandardInterfaceTest {

	String sql;
	JdbcUtils user_jdbc;

	@Parameters({ "sql", "db_ip", "db_port", "db_userName", "db_password", "db_baseName" })
	@BeforeClass()
	public void BeforeRun(String sql, String user_db_ip, String user_db_port, String user_db_userName,
			String user_db_password, String user_db_baseName) throws Exception {
		user_jdbc = new JdbcUtils(user_db_ip, user_db_port, user_db_userName, user_db_password, user_db_baseName);
		user_jdbc.getConnection();
		this.sql = sql;
	}

	@DataProvider(name = "caseData")
	private Iterator<Object[]> caseData() throws SQLException, ClassNotFoundException {
		return new DataProvider_forDB(TestCaseDBConfig.DB_IP, TestCaseDBConfig.DB_PORT, TestCaseDBConfig.DB_USERNAME,
				TestCaseDBConfig.DB_PASSWORD, TestCaseDBConfig.DB_BASENAME, sql);
	}

	@Test(dataProvider = "caseData")
	public void Test(Map<String, String> data) {
		new ExecHttpTest().execTestCase(data);
	}

	@AfterClass()
	public void AfterRun() {
		user_jdbc.releaseConn();
	}
}