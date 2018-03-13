package InterfaceTestUtils;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

public class AssertUtils {

	private Logger logger = Logger.getLogger(AssertUtils.class);

	/**
	 * 返回两个数组之间相同的集合,两边都去重
	 * 
	 * @param <T>
	 * @param a
	 *            String数组A
	 * @param b
	 *            String数组B
	 * @return
	 */
	public static <T> Set<T> CompareSameUnique(T[] a, T[] b) {

		Set<T> same = new HashSet<T>(); // 用来存放两个数组中相同的元素
		Set<T> temp = new HashSet<T>(); // 用来存放数组a中的元素

		for (int i = 0; i < a.length; i++) {
			temp.add(a[i]); // 把数组a中的元素放到Set中，可以去除重复的元素
		}

		for (int j = 0; j < b.length; j++) {
			// 把数组b中的元素添加到temp中
			// 如果temp中已存在相同的元素，则temp.add（b[j]）返回false
			if (!temp.add(b[j]))
				same.add(b[j]);
		}
		return same;
	}

	/**
	 * 返回两个数组之间相同的集合,两边不去重
	 * 
	 * @param <T>
	 * @param a
	 *            String数组A
	 * @param b
	 *            String数组B
	 * @return
	 */
	public static <T> List<T> CompareSame(T[] t1, T[] t2) {

		List<T> list1 = Arrays.asList(t1);
		List<T> same = new ArrayList<T>();
		for (T t : t2) {
			if (list1.contains(t)) {
				same.add(t);
			}
		}
		return same;
	}

	/**
	 * 返回两个数组之间不相同的集合,两边不去重
	 * 
	 * @param <T>
	 * @param a
	 *            String数组A
	 * @param b
	 *            String数组B
	 * @return
	 */
	public static <T> List<T> CompareDiff(T[] t1, T[] t2) {
		List<T> list1 = Arrays.asList(t1);
		List<T> list2 = Arrays.asList(t2);
		List<T> diff = new ArrayList<T>();

		for (T t : t1) {
			// System.out.println("list2是否包含"+t+":"+list2.contains(t));
			if (!list2.contains(t)) {
				diff.add(t);
			}
		}

		for (T t : t2) {
			// System.out.println("list1是否包含"+t+":"+list1.contains(t));
			if (!list1.contains(t)) {
				diff.add(t);
			}
		}

		return diff;
	}

	/**
	 * 比较两个map中除去不同部分的key和value之后是否完全相同
	 * 
	 * @param map1
	 *            比较对象map1
	 * @param map2
	 *            比较对象map1
	 * @param RemoveList
	 *            需要从两个map中去除比较的相同部分key值数组
	 * @return
	 */
	public static boolean compareMap(Map<String, Object> map1, Map<?, ?> map2, String[] RemoveList) {
		boolean contain = false;

		if (RemoveList != null) {
			for (int i = 0; i < RemoveList.length; i++) {
				map1.remove(RemoveList[i]);
				map2.remove(RemoveList[i]);
			}
		}
		// System.out.println("map1---->"+map1);
		// System.out.println("map2---->"+map2);
		for (Object o : map1.keySet()) {
			// System.out.println(o);
			contain = map2.containsKey(o);
			if (contain) {
				// System.out.println(map1.get(o));
				// System.out.println(map2.get(o));
				contain = map1.get(o).toString().equals(map2.get(o).toString());
			}
			if (!contain) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 根据预期的Map,与数据库返回的Map集合中的每一个进行比对, Map的Key必须和数据库中的Key相一致
	 * 
	 * @param ExpectedKeyValues
	 *            预期的数据库Map键值对
	 * @param jdbc
	 *            数据库connection对象
	 * @param TableName
	 *            目标数据表名称
	 * @param Condition
	 *            查询条件
	 * @return 返回值
	 * @throws SQLException
	 */
	public boolean CheckSqlResult(Map<String, Object> ExpectedKeyValues, JdbcUtils jdbc, String TableName,
			String Condition) throws SQLException {
		boolean flag = true;
		StringBuffer Stf = new StringBuffer();
		Stf.append("SELECT ");
		Iterator<String> iterator = ExpectedKeyValues.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Stf.append(key);
			Stf.append(",");
		}
		String Sql = Stf.toString();
		Sql = Sql.substring(0, Sql.length() - 1);
		Sql = Sql + " FROM " + TableName + " " + Condition;
		// System.out.println(Sql);
		List<Map<String, String>> SqlResultList = jdbc.findModeResult(Sql, null);
		for (int i = 0; i < SqlResultList.size(); i++) {
			if (flag == false) {
				break;
			}
			Map<?, ?> SqlResult = SqlResultList.get(i);
			// System.out.println("SqlResult: "+SqlResult);
			if (!compareMap(ExpectedKeyValues, SqlResult, null)) {
				flag = false;
				break;
			}
		}
		return flag;

	}

	/**
	 * 返回两个Sql.Date日期类型之间相差的天数
	 * 
	 * @param Date1
	 *            第一个日期
	 * @param Date2
	 *            第二个日期
	 * @return 返回相差天数, 可能为负
	 */
	public static int DaysSubOf2Date(Date Date1, Date Date2) {

		Calendar aCalendar = Calendar.getInstance();

		aCalendar.setTime(Date1);

		int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

		aCalendar.setTime(Date2);

		int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);

		int Sub = day2 - day1;

		return Sub;

	}

	/**
	 * 根据Sql数据中的jsonPath数据,验证返回值的
	 * 
	 * @param Sqldata
	 * @param responseJson
	 * @return 验证成功返回true,验证失败返回false
	 */
	public boolean JsonPathAssert(Map<String, String> Sqldata, String responseJson) {

		boolean Assertflag = false;

		ReadContext ctx = JsonPath.parse(responseJson);
		JSONArray jsonPathArray = new JSONArray(Sqldata.get("ASSERT_JSON"));

		for (int i = 0; i < jsonPathArray.length(); i++) {
			JSONObject json = jsonPathArray.getJSONObject(i);
			String userJpath = json.getString("jpath");
			String userJpathValue = json.get("value").toString();
			String acturalValue = ctx.read(userJpath).toString();
			logger.info("JsonPath路径:" + userJpath+" 预期Json返回值:" + userJpathValue+" 实际Json返回值:" + acturalValue);
			if (acturalValue.equals(userJpathValue)) {
				Assertflag = true;
			} else {
				Assertflag = false;
				break;
			}
		}
		return Assertflag;

	}

//	/**
//	 * 根据Sql数据中的html文本 参数说明: left_border 文本左边界, right_border 文本右边界, index 匹配第几项
//	 * 默认为0 匹配第1项 实例:
//	 * [{"left_border":"ab","right_border":"c","index":"0"},{"left_border":"ab","right_border":"c","index":"1"}]
//	 * 
//	 * @param Sqldata
//	 * @param responseJson
//	 * @return 验证成功返回true,验证失败返回false
//	 */
//	public boolean HtmlTextAssert(Map<String, String> Sqldata, String responseJson) {
//
//		boolean Assertflag = false;
//
//		// 没考虑多个验证点,需要添加
//
//		JSONArray HtmlJsonArray = new JSONArray(Sqldata.get("ASSERT_HTML_TEXT"));
//
//		for (int i = 0; i < HtmlJsonArray.length(); i++) {
//
//			JSONObject HtmlJson = HtmlJsonArray.getJSONObject(i);
//
//			String LBorder = HtmlJson.get("left_border").toString();
//			String RBorder = HtmlJson.get("right_border").toString();
//
//			int count = 0;
//			int index = Integer.parseInt(HtmlJson.get("index").toString());
//
//			String RegRule = LBorder + ".*?" + RBorder;
//			logger.info("文本匹配第" + (i + 1) + "组,文本匹配的规则:" + RegRule + ", 匹配index=" + index);
//			Pattern pattern = Pattern.compile(RegRule);
//			Matcher RegResult = pattern.matcher(responseJson);
//
//			while (RegResult.find()) {
//
//				if (!(count < index)) {
//					String FindResult = RegResult.group(0);
//					logger.info("匹配到预期结果文本:" + FindResult);
//					Assertflag = true;
//				}
//				count++;
//			}
//			if (Assertflag == false) {
//				break;
//			}
//
//		}
//		return Assertflag;
//
//	}
//
	public boolean SqlAssert(Map<String, String> data, String requestResult, JdbcUtils user_jdbc) throws SQLException {

		boolean Assertflag = false;
		// [{"Sql":"","ExpectedKey":"","ExpectedValue":""},{"Sql":"","ExpectedKey":"","ExpectedValue":""}]
		JSONArray SqlJsonArray = new JSONArray(data.get("ASSERT_SQL"));
		String SqlString;
		JSONObject SqlJsonObject;
		String ExpecteKey;
		String ExpectedValue;
		String ResultValue;
		Map<String, String> SqlResultMap;
		int MaxAssertLength;

		for (int i = 0; i < SqlJsonArray.length(); i++) {

			MaxAssertLength = SqlJsonArray.length() + 1;
			logger.info("当前Sql验证循环:" + (i + 1) + "/" + MaxAssertLength);

			SqlJsonObject = SqlJsonArray.getJSONObject(i);
			SqlString = SqlJsonObject.getString("Sql");
			SqlResultMap = user_jdbc.findModeResult(SqlString, null).get(0);

			ExpecteKey = SqlJsonObject.getString("ExpectedKey");
			ExpectedValue = SqlJsonObject.getString("ExpectedValue");
			ResultValue = SqlResultMap.get(ExpecteKey).toString();
			logger.info("预期" + ExpecteKey + "的值为:" + ExpectedValue);
			logger.info("实际数据库值为:" + ResultValue);

			if (ExpectedValue.equals(ResultValue)) {
				Assertflag = true;
				logger.info("第" + (i + 1) + "次循环验证成功,继续下一环Sql验证");
			} else {
				Assertflag = false;
				logger.info("第" + (i + 1) + "次循环验证失败,Sql验证总体失败");
				break;
			}

		}

		return Assertflag;
	}

}
