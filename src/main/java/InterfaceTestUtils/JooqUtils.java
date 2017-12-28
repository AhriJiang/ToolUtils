package InterfaceTestUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import DataBasePublicConfig.JiFengMallMySqlProperties;

public class JooqUtils {

	private String USERNAME = JiFengMallMySqlProperties.USERNAME;
	private String PASSWORD = JiFengMallMySqlProperties.PASSWORD;
	private String URL = JiFengMallMySqlProperties.URL;
	private SQLDialect DIALECT = JiFengMallMySqlProperties.DIALECT;
	private Connection connection;
	public DSLContext dslContext;

	public JooqUtils(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	/**
	 * 获取数据库连接,根据JiFengMallMySqlProperties配置类构造,获取连接同时实例化DSLContext
	 * 
	 * @return 返回DSLContext实例
	 * @throws SQLException
	 */
	public DSLContext getConnection() throws SQLException {

		this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		this.dslContext = DSL.using(connection, DIALECT);
		return dslContext;

	}

	/**
	 * 断开数据库连接
	 */
	public void releaseConnection() {
		dslContext.close();
	}

	/**
	 * 从Table中选取任意数据,返回数据条数
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            JooqTable泛型
	 * @return 查询结果数量
	 */
	public Integer SelectFromTable(Table<?> Table) {
		Integer SelectCount = dslContext.select().from(Table).execute();
		return SelectCount;
	}

	/**
	 * 获取某个Field值的集合,返回List对象
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            表名
	 * @param Filed
	 *            列名
	 * @return 该列的值的List对象集合
	 */
	public List<T> SelectValuesList(Table<?> Table, Field<T> Filed) {

		List<T> Result = dslContext.select().from(Table).fetch(Filed);
		return Result;
	}

	/**
	 * 获取某个Field值的集合,返回Array对象
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            表名
	 * @param Filed
	 *            列名
	 * @return 该列的值的Array对象集合
	 */
	public T[] SelectValuesArray(Table<?> Table, Field<T> Filed) {

		T[] Result = dslContext.select().from(Table).fetch().intoArray(Filed);
		return Result;

	}

	/**
	 * 获取某个Field值的集合,返回List对象
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            表名
	 * @param Filed
	 *            列名
	 * @param Condition
	 *            where条件
	 * @return 该列的值的List对象集合
	 */
	public List<T> SelectFieldValues(Table<?> Table, Field<T> Filed, String Condition) {

		List<T> Result = dslContext.selectFrom(Table).where(Condition).fetch(Filed);
		return Result;

	}

	/**
	 * 获取某个Field值的集合,返回List对象
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            表名
	 * @param Filed
	 *            列名
	 * @return 该列的值的List对象集合
	 */
	public <T> List<?> SelectFieldValues(TableImpl<?> Table, Field<T> Filed) {

		List<T> Result = dslContext.selectFrom(Table).fetch(Filed);
		return Result;

	}

	/**
	 * 获取某个Field值的集合,返回List对象
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            表名
	 * @param Filed
	 *            列名
	 * @param Condition
	 *            where条件
	 * @return 该列的值的List对象集合
	 */
	public <T> List<T> SelectFieldValues(TableImpl<?> Table,Field<T> Filed, Condition Condition) {

		List<T> Result = dslContext.selectFrom(Table).where(Condition).fetch(Filed);
		return Result;

	}

	/**
	 * 获取某一个Field的值
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            表对象
	 * @param Filed
	 *            列名
	 * @param Condition
	 *            where条件
	 * @return 返回指定Field的类型对象
	 */
	public <T> T SelectOneValue(TableImpl<?> Table, Field<T> Filed, Condition Condition) {

		return dslContext.selectFrom(Table).where(Condition).fetchOne(Filed);

	}

	/**
	 * 获取某一个Field的值
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            表对象
	 * @param Filed
	 *            列名
	 * @param Condition
	 *            where条件
	 * @return 返回指定Field的类型对象
	 */
	public <T> T SelectOneValue(TableImpl<?> Table, Field<T> Filed, String Condition) {

		return dslContext.selectFrom(Table).where(Condition).fetchOne(Filed);

	}
	
	/**
	 * 获取某一个Field的平均值
	 * 
	 * @param Table
	 *            表对象
	 * @param Filed
	 *            列名
	 * @param Condition
	 *            where条件
	 * @return 返回指定Field的类型对象的平均值
	 */
	public Number SelectAvg(TableImpl<?> Table, Field<? extends Number> Filed, Condition Condition) {
		return dslContext.selectFrom(Table).where(Condition).fetchOne(DSL.avg(Filed));
	}

	/**
	 * 获取某一个Field的总计和
	 * 
	 * @param Table
	 *            表对象
	 * @param Filed
	 *            列名
	 * @param Condition
	 *            where条件
	 * @return 返回指定Field的类型对象的平均值
	 */
	public Number SelectSum(TableImpl<?> Table, Field<? extends Number> Filed, Condition Condition) {
		return dslContext.selectFrom(Table).where(Condition).fetchOne(DSL.sum(Filed));
	}

	/**
	 * 获取某一个Field的计数和
	 * 
	 * @param Table
	 *            表对象
	 * @param Filed
	 *            列名
	 * @param Condition
	 *            where条件
	 * @return 返回指定Field的类型对象的平均值
	 */
	public Integer SelectCount(Table<?> Table, Condition Condition) {
		return dslContext.fetchCount(Table, Condition);
	}

	/**
	 * 获取某一个Field的最大值
	 * 
	 * @param Table
	 *            表对象
	 * @param Filed
	 *            列名
	 * @param Condition
	 *            where条件
	 * @return 返回指定Field的类型对象的平均值
	 */
	public <T> T SelectMax(TableImpl<?> Table, Field<T> Filed, Condition Condition) {
		return dslContext.selectFrom(Table).where(Condition).fetchOne(DSL.max(Filed));
	}

	/**
	 * 获取某一个Field的最小值
	 * 
	 * @param Table
	 *            表对象
	 * @param Filed
	 *            列名
	 * @param Condition
	 *            where条件
	 * @return 返回指定Field的类型对象的平均值
	 */
	public <T> T SelectMin(TableImpl<?> Table, Field<T> Filed, Condition Condition) {
		return dslContext.selectFrom(Table).where(Condition).fetchOne(DSL.min(Filed));
	}

	/**
	 * 获取某个Field值的集合,返回List对象,注解有使用demo
	 * 
	 * Condition ConditionA=SkuGrpInfo.BRAND_CN.eq(""); Condition
	 * ConditionB=SkuGrpInfo.BRAND_CN.eq("");
	 * 
	 * Collection<Condition> Conditions =new ArrayList<Condition>() ;
	 * 
	 * Conditions.add(ConditionA); Conditions.add(ConditionB);
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            表名
	 * @param Filed
	 *            列名
	 * @param Conditions
	 *            多重where条件,注解有使用demo
	 * @return 该列的值的List对象集合
	 */
	public <T> List<T> SelectFieldValuesWithConditions(TableImpl<?> Table, TableField Filed,
			Collection<? extends Condition> Conditions) {

		List<T> Result = dslContext.selectFrom(Table).where(Conditions).fetch(Filed);
		return Result;

	}
	
	/**
	 * 根据多条件获取某一个Field的值
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            表对象
	 * @param Filed
	 *            列名
	 * @param Conditions
	 *            多where条件
	 * @return 返回指定Field的类型对象
	 */
	public <T> T SelectOneValue(TableImpl<?> Table, Field<T> Filed, Collection<? extends Condition> Conditions) {

		return dslContext.selectFrom(Table).where(Conditions).fetchOne(Filed);

	}

	/**
	 * 根据传入的Sheet和特定的列名和值,像指定的Table插入一条数据
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Sheet
	 *            sheet对象
	 * @param ExcelFieldName
	 *            Excel指定列名
	 * @param ExcelFieldValue
	 *            Excel指定值
	 * @param Table
	 *            目标Table对象
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public void Insert(XSSFSheet Sheet, String ExcelFieldName, String ExcelFieldValue, Table<?> Table)
			throws InvalidFormatException, IOException {
		dslContext.insertInto(Table).values(ExcelUtils.GetJooqSqlDataInRow(Sheet, ExcelFieldName, ExcelFieldValue))
				.execute();
	}

	/**
	 * 根据传入ExcelFilePath和SheetName,将该Sheet内所有数据插入对象Table中
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param ExcelFilePath
	 *            数据源文件路径
	 * @param SheetName
	 *            数据源Sheet
	 * @param Table
	 *            数据插入的对象Table
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public void Insert(String ExcelFilePath, String SheetName, TableImpl<?> Table)
			throws InvalidFormatException, IOException {
		ExcelUtils excelUtil = new ExcelUtils();
		Object[][] JooqDatas = excelUtil.GetJooqSqlDataInMatrix(ExcelFilePath, SheetName);
		for (int i = 0; i < JooqDatas.length; i++) {
			dslContext.insertInto(Table).values(JooqDatas[i]).execute();
		}

	}

	public void DeleteWhere(Table<?> Table, Condition Condition) {
		dslContext.deleteFrom(Table).where(Condition).execute();
	}

	public void DeleteWhere(Table<?> Table, String Condition) {
		dslContext.deleteFrom(Table).where(Condition).execute();
	}

	/**
	 * 从Excel特定的Sheet中根据指定Column删除测试数据
	 * 
	 * @param dslContext
	 *            Jooq驱动核心
	 * @param Table
	 *            目标表对象
	 * @param ExcelFilePath
	 *            Excel路径
	 * @param SheetName
	 *            Sheet名称
	 * @param ColumnName
	 *            列名称
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void DeleteWhere(Table<?> Table, String ExcelFilePath, String SheetName, String ColumnName)
			throws InvalidFormatException, IOException {
		ExcelUtils excelUtil = new ExcelUtils();
		List<Object> ColumnNameList = excelUtil.GetJooqSqlDataInColumn(ExcelFilePath, SheetName, ColumnName);
		for (int i = 0; i < ColumnNameList.size(); i++) {
			DeleteWhere(Table, ColumnName + "='" + ColumnNameList.get(i).toString() + "'");
		}

	}

	public void DeleteAny(Table<?> Table) {
		dslContext.deleteFrom(Table).execute();
	}

	public void SetFieldToNull(TableImpl<?> Table, Field<T> Filed, Condition Condition){
		dslContext.update(Table).set(Filed, DSL.castNull(Filed)).execute();
	}
}
