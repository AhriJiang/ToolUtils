package InterfaceTestUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcraft.jsch.JSchException;

public class JDBCtest {

	public static void main(String[] args) throws SQLException, JSchException {
		// TODO Auto-generated method stub
		JdbcUtils jdbcUtils = new JdbcUtils();
		jdbcUtils.getConnection();

		/*******************增*********************/
		/*		String sql = "insert into userinfo (username, pswd) values (?, ?), (?, ?), (?, ?)";
		List<Object> params = new ArrayList<Object>();
		params.add("小明"); 
        params.add("123xiaoming"); 
        params.add("张三"); 
        params.add("zhangsan"); 
        params.add("李四"); 
        params.add("lisi000"); 
		try {
			boolean flag = jdbcUtils.updateByPreparedStatement(sql, params);
			System.out.println(flag);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


		/*******************删*********************/
		//删除名字为张三的记录  
		/*		String sql = "delete from userinfo where username = ?";
		List<Object> params = new ArrayList<Object>();
		params.add("张三");
		boolean flag = jdbcUtils.updateByPreparedStatement(sql, params);*/

		/*******************改*********************/  
        //将名字为李四的密码改了 
		/*		String sql = "update userinfo set pswd = ? where username = ? ";
		List<Object> params = new ArrayList<Object>();
		params.add("lisi88888"); 
        params.add("李四"); 
		boolean flag = jdbcUtils.updateByPreparedStatement(sql, params);
		System.out.println(flag);*/

		/*******************查*********************/
		//不利用反射查询单个记录
        String sql2 = "SELECT * from vndr_info_chk where VENDOR_NO=999888;"; 
        List<Object> params=new ArrayList<Object>();
        params.add(999888);
        Map<String, String> map = jdbcUtils.findSimpleResult(sql2, null);
        System.out.println(map.get("VENDOR_NO"));		
        jdbcUtils.releaseConn();
	    //不利用反射查询多个记录  
        /*      String sql2 = "select * from userinfo where id = 1"; 
        List<Map<String, Object>> list = jdbcUtils.findModeResult(sql2, null); 
        System.out.println(list);*/ 

		//利用反射查询 单条记录  
		/*		String sql = "select * from userinfo where username = ? ";
		List<Object> params = new ArrayList<Object>();
		params.add("李四");
		UserInfo userInfo;
		try {
			userInfo = jdbcUtils.findSimpleRefResult(sql, params, UserInfo.class);
			System.out.print(userInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


	}

}
