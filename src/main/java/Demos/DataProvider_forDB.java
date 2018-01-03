package Demos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * 数据源 Case_DB数据库
 *
 * @author yongda.chen
 */
public class DataProvider_forDB implements Iterator<Object[]> {
	
	List<Map<String, String>> list;
	ResultSet rs;
    ResultSetMetaData rd;
	
	
	public DataProvider_forDB(String dbIp, String dbPort, String dbUsername, String dbPassword, String dbBasename,
			String sql) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = String.format("jdbc:mysql://%s:%s/%s", dbIp, dbPort, dbBasename);
		Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
        Statement createStatement = conn.createStatement();
        rs = createStatement.executeQuery(sql);
        rd = rs.getMetaData();

	}

    @Override
    public boolean hasNext() {
        boolean flag = false;
        try {
            flag = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public Object[] next() {
        Map<String, String> data = new HashMap<String, String>();
        try {
            for (int i = 1; i <= rd.getColumnCount(); i++) {
                data.put(rd.getColumnName(i), rs.getString(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Object r[] = new Object[1];
        r[0] = data;
        return r;
    }

    @Override
    public void remove() {
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
