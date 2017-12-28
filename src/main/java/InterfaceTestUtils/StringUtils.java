package InterfaceTestUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

public class StringUtils {
	/**
	 * 将String行,转换成yyyy-MM-dd HH:mm:ss的Timestamp（java.sql.Timestamp）时间戳对象
	 * @param dateString 传入的String参数
	 * @return Sql时间戳对象
	 * @throws java.text.ParseException
	 */
	public java.sql.Timestamp String2Time(String dateString) throws java.text.ParseException {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设定格式
		// dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss",
		// Locale.ENGLISH);
		dateFormat.setLenient(false);
		java.util.Date timeDate = dateFormat.parse(dateString);// util类型
		java.sql.Timestamp dateTime = new java.sql.Timestamp(timeDate.getTime());// Timestamp类型,timeDate.getTime()返回一个long型
		return dateTime;
	}

	/**
	 * method 将字符串类型的日期转换为一个Date（java.sql.Date） dateString 需要转换为Date的字符串
	 * @param dateString 传入的String参数
	 * @return 返回Sql日期对象
	 * @throws java.text.ParseException
	 * dataTime Date
	 */
	public java.sql.Date String2Date(String dateString) throws java.lang.Exception {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		java.util.Date timeDate = dateFormat.parse(dateString);// util类型
		java.sql.Date dateTime = new java.sql.Date(timeDate.getTime());// sql类型
		return dateTime;
	}
	
	/**
	 * 将String行,转换成yyyy-MM-dd HH:mm:ss的Timestamp（java.sql.Timestamp）时间戳对象,自定义format时间戳格式
	 * @param dateString 传入的String参数
	 * @param format 传入format格式
	 * @return Sql时间戳对象
	 * @throws java.text.ParseException
	 */
	public java.sql.Timestamp String2Time(String dateString,String format) throws java.text.ParseException {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat(format);// 设定格式
		// dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss",
		// Locale.ENGLISH);
		dateFormat.setLenient(false);
		java.util.Date timeDate = dateFormat.parse(dateString);// util类型
		java.sql.Timestamp dateTime = new java.sql.Timestamp(timeDate.getTime());// Timestamp类型,timeDate.getTime()返回一个long型
		return dateTime;
	}
	
	/**
	 * method 将字符串类型的日期转换为一个Date（java.sql.Date） dateString 需要转换为Date的字符串,自定义format时间戳格式
	 * @param dateString 传入的String参数
	 * @param format 传入format格式
	 * @return Sql时间戳Date对象
	 * @throws java.text.ParseException
	 * dataTime Date
	 */
	public java.sql.Date String2Date(String dateString,String format) throws java.lang.Exception {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		java.util.Date timeDate = dateFormat.parse(dateString);// util类型
		java.sql.Date dateTime = new java.sql.Date(timeDate.getTime());// sql类型
		return dateTime;
	}

	/**
	 * 根据毫秒数,返回该毫秒数的Sql.Date对象,默认返回yyyy-MM-dd的格式
	 * @param TimeMillis 毫秒数
	 * @return 返回yyyy-MM-dd的格式
	 */
	public java.sql.Date Millis2Date(long TimeMillis) {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		java.sql.Date dateTime = new java.sql.Date(TimeMillis);// sql类型
		return dateTime;
	}	

	
	/**
	 * 根据毫秒数,返回该毫秒数的Sql.Date对象,返回Format的格式
	 * @param TimeMillis 毫秒数
	 * @param Format 日期类型格式
	 * @return 返回Format的格式
	 */
	public java.sql.Date Millis2Date(long TimeMillis,String Format) {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat(Format);
		dateFormat.setLenient(false);
		java.sql.Date dateTime = new java.sql.Date(TimeMillis);// sql类型
		return dateTime;
	}
	/**
	 * 将String型毫秒数直接转换为TimeStamp类型
	 * @param millsString String型毫秒数
	 * @return
	 */
	public java.sql.Timestamp StringMills2Time(String millsString){
		long mills=Long.parseLong(millsString);
		java.sql.Timestamp dateTime = new java.sql.Timestamp(mills);// Timestamp类型,timeDate.getTime()返回一个long型
		return dateTime;
	}
	
	
	/**
	 * 根据String文本类型,切分成String数组
	 * @param ArrayText String文本对象
	 * @return 返回String[]数组对象
	 */
	public List<String> String2StringList(String ArrayText){
		List<String> result;
		if (ArrayText.contains(",")) {
			String[] sa=ArrayText.split(",");
			result=Arrays.asList(sa);
			return result;
		}else {
			String[] sa={ArrayText};
			result=Arrays.asList(sa);
			return result;
		}
		
	}
	
}
