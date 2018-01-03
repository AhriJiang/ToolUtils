package InterfaceTestUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import DataBasePublicConfig.TestCaseDBConfig;

public class JdbcUtils {
	/**
	 * @USERNAME 远程数据库用户名
	 */
	private String USERNAME;
	/**
	 * @PASSWORD 远程数据库密码
	 */
	private String PASSWORD;
//	/**
//	 * @DRIVER 远程数据库驱动类型
//	 */
//	private String DRIVER=JiFengMallMySqlProperties.DRIVER;
	/**
	 * @SSHHOST SSH服务器HOST
	 */
	private String SSHHOST;
	/**
	 * @SSHPORT SSH服务器端口号
	 */
	private int SSHPORT;
	private String SSHUSER;
	private String SSHPASSWORD;
	private String LOCALURL;
	/**
	 * @URL 远程服务器连接URL
	 */
	private String URL;
	/**
	 * @LOCALPORT  SSH服务器映射到localhost本机的端口号
	 */
	private int LOCALPORT;
	
	
	private Session session;
	private JSch jsch;
	private Connection connection;
	private PreparedStatement pstmt;
	private ResultSet resultSet;
	private DSLContext dslContext;
//	private Properties GetJDBCProperties(Properties pro){
//		try {
//			InputStream in = new FileInputStream("src/main/resources/MySql.properties");
//			pro.load(in);
//			in.close();
//		} catch (Exception e) {
//			System.out.println("没有找到MySql.properties文件");
//			e.printStackTrace();
//		}
//		return pro;
//	}

//	/**
//	 * 加载数据库驱动
//	 */
//	public JdbcUtils() {
//		
//		try{
//			Class.forName(pro.getProperty("DRIVER"));
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}	
	
	public JdbcUtils() {
		this.jsch=new JSch();
		try {
			this.session=jsch.getSession(SSHUSER, SSHHOST, SSHPORT);
		} catch (JSchException e) {
		}
	}
	
	public JdbcUtils(String db_ip, String db_port, String db_userName, String db_password, String db_baseName) {
		this.URL="jdbc:mysql://"+db_ip+":"+db_port+"/"+db_baseName;
		this.USERNAME=db_userName;
		this.PASSWORD=db_password;
	}

	/** 
     * 获得数据库的连接 
     * @return 
     */  
	public Connection getConnection(){
		
		try {  
 
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return connection;
            
        }catch (Exception e) {
        	
        	e.printStackTrace();
		}
		
		return connection;
	}
	
	/** 
     * 通过SSH隧道配置文件,获取数据库的连接 
     * @return 
	 * @throws JSchException 
     */  
	public Connection getSSHConnection() {
		
		session.setPassword(SSHPASSWORD);
		session.setConfig("StrictHostKeyChecking", "no");  
		try {
			session.connect();
		} catch (JSchException e) {
			e.printStackTrace();
		}

        System.out.println(session.getServerVersion());//这里打印SSH服务器版本信息  
        int assinged_port;
		try {
			assinged_port = session.setPortForwardingL(LOCALPORT, SSHHOST, SSHPORT);
		} catch (JSchException e1) {
			// TODO Auto-generated catch block
			assinged_port=3306;
			
		}//建立SSH和本地的端口映射关系
        System.out.println("localhost:" + assinged_port + " -> " + SSHHOST + ":" + SSHPORT);
        
		try {  
 
            this.connection = DriverManager.getConnection(LOCALURL, USERNAME, PASSWORD);
            return connection;
            
        }catch (Exception e) {
        	
        	e.printStackTrace();
		}
		
		return connection;
	}
	
	public DSLContext getdslContext(){
		dslContext = DSL.using(getConnection(),SQLDialect.MYSQL);
		return dslContext;
	}
	
	
	/** 
     * 完成对数据库 增、删、改的操作 
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */
	public boolean updateByPreparedStatement(String sql, List<Object>params)throws SQLException{
		boolean flag = false;
		int result = -1;//表示当用户执行增删改的时候所影响数据库的行数
		pstmt = connection.prepareStatement(sql);
		int index = 1;
		if(params != null && !params.isEmpty()){//判断集合的时候较为标准的格式
			for(int i=0; i<params.size(); i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		result = pstmt.executeUpdate();
		flag = result > 0 ? true : false;// ? true : false 表示  如果result>0成立, 则flag=true 否则flag=false
		return flag;
	}

	/** 
     * 查询单条记录
     * @param sql 
     * @param params  
     * @return 
     * @throws SQLException 
     */ 
	public Map<String, String> findSimpleResult(String sql, List<Object> params) throws SQLException{
		Map<String, String> map = new HashMap<String, String>();
		int index  = 1;
		pstmt = connection.prepareStatement(sql);
		if(params != null && !params.isEmpty()){
			for(int i=0; i<params.size(); i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();//返回查询结果
		ResultSetMetaData metaData = resultSet.getMetaData();
		int col_len = metaData.getColumnCount();//获得列的名称
		while(resultSet.next()){
			for(int i=0; i<col_len; i++ ){
				String cols_name = metaData.getColumnName(i+1);
				String cols_value = resultSet.getObject(cols_name).toString();
				if(cols_value == null){
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
		}
		return map;
	}

	/**查询多条记录 
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */ 
	public List<Map<String, String>> findModeResult(String sql, List<Object> params) throws SQLException{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if(params != null && !params.isEmpty()){
			for(int i = 0; i<params.size(); i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		while(resultSet.next()){
			Map<String, String> map = new HashMap<String, String>();
			for(int i=0; i<cols_len; i++){
				String cols_name = metaData.getColumnName(i+1);
				String cols_value = resultSet.getObject(cols_name).toString();
				if(cols_value == null){
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
			list.add(map);
		}

		return list;
	}

	 /**通过反射机制查询单条记录 
     * @param sql 
     * @param params 
     * @param cls 
     * @return 
     * @throws Exception 
     */ 
	public <T> T findSimpleRefResult(String sql, List<Object> params,
			Class<T> cls )throws Exception{
		T resultObject = null;
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if(params != null && !params.isEmpty()){
			for(int i = 0; i<params.size(); i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData  = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		while(resultSet.next()){
			//ͨ通过反射机制创建一个实例
			resultObject = cls.newInstance();
			for(int i = 0; i<cols_len; i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value == null){
					cols_value = "";
				}
				Field field = cls.getDeclaredField(cols_name);
				field.setAccessible(true); //打开javabean的private访问权限  
				field.set(resultObject, cols_value);
			}
		}
		return resultObject;

	}

	/**通过反射机制查询多条记录 
     * @param sql  
     * @param params 
     * @param cls 
     * @return 
     * @throws Exception 
     */  
	public <T> List<T> findMoreRefResult(String sql, List<Object> params,
			Class<T> cls )throws Exception {
		List<T> list = new ArrayList<T>();
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if(params != null && !params.isEmpty()){
			for(int i = 0; i<params.size(); i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();
		ResultSetMetaData metaData  = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();
		while(resultSet.next()){
			//ͨ通过反射机制创建一个实例 
			T resultObject = cls.newInstance();
			for(int i = 0; i<cols_len; i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value == null){
					cols_value = "";
				}
				Field field = cls.getDeclaredField(cols_name);
				field.setAccessible(true); //打开javabean的private访问权限
				field.set(resultObject, cols_value);
			}
			list.add(resultObject);
		}
		return list;
	}

	/** 
     * 释放数据库连接 
     */  
	public void releaseConn(){
		
		session.disconnect();
		
		if(resultSet != null){
			try{
				resultSet.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		if (pstmt!=null) {
			try {
				pstmt.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		if (connection!=null) {
			try {
				connection.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

}
