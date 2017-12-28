package Demos;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.UpdateQuery;
import org.jooq.impl.DSL;
import org.omg.CORBA.portable.IDLEntity;

import com.mysql.jdbc.Connection;

import InterfaceTestUtils.ExcelUtils;
import InterfaceTestUtils.JdbcUtils;


import org.jooq.SelectQuery;

public class JooqDemo {
	public DSLContext dslContext= null;  
    //获取DSLContext对象  
	public DSLContext getdslContext() throws SQLException {
		String userName = "juntao";
		String password = "jjt20030439";
		String url = "jdbc:mysql://localhost:3306/myschool";
		Connection conn = (Connection) DriverManager.getConnection(url, userName, password);
		JdbcUtils jdbc = new JdbcUtils();
		// dslContext = DSL.using(jdbc.getConnection(),SQLDialect.MYSQL);
		dslContext = DSL.using(conn,SQLDialect.MYSQL);
		return dslContext;
	}

    //简单实体查询  
    public void select(String add) throws SQLException  
    {  
        DSLContext getdslContext = getdslContext();  
        Table<Record> table = DSL.table("shangfox_user");  
        SelectQuery<Record> selectQuery = getdslContext.selectQuery(table);//获取查询对象  
        Condition eq = DSL.field("username").eq(add);//查询条件  
        selectQuery.addConditions(eq);//添加查询条件  
        Result<Record> fetch = selectQuery.fetch();  
        for (Object aResult : fetch) {  
            Record record = (Record) aResult;  
            System.out.println(record);  
            System.out.println(record.getValue("username"));  
        }  
      }  
    //实体更新  
    public void update(String name) throws SQLException  
    {  
        DSLContext getdslContext = getdslContext();  
        Table<Record> table = DSL.table("shangfox_user");  
        UpdateQuery<Record> updateQuery = getdslContext.updateQuery(table);//获取更新对象  
        updateQuery.addValue(DSL.field("email"), "new-email");//更新email字段的值为new-email  
        Condition eq = DSL.field("username").eq(name);//更新username为name的email字段  
        updateQuery.addConditions(eq);  
        int execute = updateQuery.execute();  
        System.out.println(execute);  
        select("shangfox1");  
    }  
    //原生态的sql查询  
    public void getVal() throws SQLException  
    {  
    	
    	ExcelUtils excel=new ExcelUtils();
        DSLContext getdslContext = getdslContext();
        
        
        Table<Record> table = DSL.table("shangfox_wish");//表名 
        Result<Record> fetch = getdslContext.select().from(table).where("statu = 0").and("id > 4340").orderBy(DSL.field("time").asc()).fetch();  
        for (Object aResult : fetch) {  
            Record record = (Record) aResult;  
            System.out.println(record);  
        }  
        /*Map<String, Object> fetchAnyMap = orderBy.fetchAnyMap(); 
        Set<String> keySet = fetchAnyMap.keySet(); 
        for(String s:keySet) 
        { 
            System.out.println("key--"+s+"--val:"+fetchAnyMap.get(s)); 
        }*/  
    }  
    //验证DSL.exists方法  
    public void exits() throws SQLException  
    {  
        DSLContext getdslContext = getdslContext();  
        
        Condition condition = DSL.exists(DSL.select(DSL.field("username1")));  
        Table<Record> table = DSL.table("shangfox_user");  
        SelectQuery<Record> selectQuery = getdslContext.selectQuery(table);  
        selectQuery.addConditions(condition);
        Result<Record> fetch = selectQuery.fetch();  
        for (Object aResult : fetch) {  
            Record record = (Record) aResult;  
            System.out.println(record);  
            System.out.println(record.getValue("username"));  
        }  
    }  
    public static void main(String[] args) throws InvalidFormatException, IOException, SQLException {  
        JooqDemo JooqDemo = new JooqDemo();  
//        jooqDao.select("shangfox");  
//        jooqDao.update("shangfox1");  
//        jooqDao.exits();  
        
        //获取jdbc链接,返回jooq实例
        DSLContext dslContext=JooqDemo.getdslContext();
        //获取Excel数据
        ExcelUtils excelUtil=new ExcelUtils();
        String ExcelFilePath="src/main/resources/Jooq.xlsx";//excel路径
        Object[][] JooqDatas=excelUtil.GetJooqSqlDataInMatrix(ExcelFilePath, "Sheet1");
        //获取一个insert的table对象
//        SkuGrpInfo SkuGrpInfo=SkuTables.Tables.SKU_GRP_INFO;
        //jooqInsert
        for (int i = 0; i < JooqDatas.length; i++) {
        	Object[] oneSql=JooqDatas[i];
//            System.out.println(dslContext.insertInto(SkuGrpInfo).values(oneSql));
		}
        //全体insert
        //写一个方法去获取excel特定表头的数组
        //遍历这个数组唯一标记, 通过jooq的update方法去更新这些数据
        List list=excelUtil.GetJooqSqlDataInColumn(ExcelFilePath, "Sheet1", "B");
        for (int i = 0; i < list.size(); i++) {
        	System.out.println(list.get(i));
		}
        Map map=new HashMap<String, String>();
//        dslContext.update(SkuGrpInfo).set(map).where().execute();
//        dslContext.fetchExists(SkuGrpInfo, SkuGrpInfo.ID.eq((long) 1));
    }  
}
