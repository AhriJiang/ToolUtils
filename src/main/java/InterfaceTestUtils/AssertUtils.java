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

public class AssertUtils {
	/**
	 * 返回两个数组之间相同的集合,两边都去重
	 * @param <T>
	 * @param a String数组A
	 * @param b String数组B
	 * @return
	 */
	public static <T> Set<T> CompareSameUnique(T[] a, T[] b){  
        
	      Set<T> same = new HashSet<T>();  //用来存放两个数组中相同的元素  
	      Set<T> temp = new HashSet<T>();  //用来存放数组a中的元素  
	        
	      for (int i = 0; i < a.length; i++) {  
	          temp.add( a[i]);   //把数组a中的元素放到Set中，可以去除重复的元素  
	      }  
	        
	      for (int j = 0; j < b.length; j++) {  
	        //把数组b中的元素添加到temp中  
	        //如果temp中已存在相同的元素，则temp.add（b[j]）返回false  
	        if(!temp.add( b[j]))  
	            same.add( b[j]);  
	    }  
	    return same;   
	  }
	
	/**
	 * 返回两个数组之间相同的集合,两边不去重
	 * @param <T>
	 * @param a String数组A
	 * @param b String数组B
	 * @return
	 */
	public static <T> List<T> CompareSame(T[] t1, T[] t2){  
        
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
	 * @param <T>
	 * @param a String数组A
	 * @param b String数组B
	 * @return
	 */
	public static <T> List<T> CompareDiff(T[] t1, T[] t2) {    
	      List<T> list1 = Arrays.asList(t1);
	      List<T> list2 = Arrays.asList(t2);  
	      List<T> diff = new ArrayList<T>();
	      
	      for (T t : t1) {
//	    	  System.out.println("list2是否包含"+t+":"+list2.contains(t));
	          if (!list2.contains(t)) {
	              diff.add(t);    
	          }    
	      }
	      
	      for (T t : t2) {
//	    	  System.out.println("list1是否包含"+t+":"+list1.contains(t));
	          if (!list1.contains(t)) {
	              diff.add(t);    
	          }    
	      }
	      
	      return diff;    
	  }   
	
	/**
	 * 比较两个map中除去不同部分的key和value之后是否完全相同
	 * @param map1  比较对象map1
	 * @param map2  比较对象map1
	 * @param RemoveList 需要从两个map中去除比较的相同部分key值数组
	 * @return
	 */
	public static boolean compareMap(Map<String, Object> map1, Map<?, ?> map2, String[] RemoveList) {
	    boolean contain = false;
	    
	    if (RemoveList!=null) {
	    	for (int i = 0; i < RemoveList.length; i++) {
				map1.remove(RemoveList[i]);
				map2.remove(RemoveList[i]);
			}	
		}
//		System.out.println("map1---->"+map1);
//		System.out.println("map2---->"+map2);
	    for (Object o : map1.keySet()) {
//	    	System.out.println(o);
	        contain = map2.containsKey(o);
	        if (contain) {
//	        	System.out.println(map1.get(o));
//	        	System.out.println(map2.get(o));
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
	 * @param ExpectedKeyValues 预期的数据库Map键值对
	 * @param jdbc 数据库connection对象
	 * @param TableName 目标数据表名称
	 * @param Condition 查询条件
	 * @return 返回值
	 * @throws SQLException
	 */
	public boolean CheckSqlResult(Map<String, Object> ExpectedKeyValues,JdbcUtils jdbc,String TableName,String Condition) throws SQLException{
		boolean flag=true;
		StringBuffer Stf=new StringBuffer();
		Stf.append("SELECT ");
		Iterator<String> iterator = ExpectedKeyValues.keySet().iterator();
		while(iterator.hasNext()){
	            String key = iterator.next();
	            Stf.append(key);
	            Stf.append(",");
	      }
		String Sql=Stf.toString();
		Sql=Sql.substring(0, Sql.length()-1);
		Sql=Sql+" FROM "+TableName+" "+Condition;
//		System.out.println(Sql);
		List<Map<String, String>> SqlResultList=jdbc.findModeResult(Sql, null);
		for (int i = 0; i < SqlResultList.size(); i++) {
			if (flag==false) {
				break;
			}
			Map<?, ?> SqlResult=SqlResultList.get(i);
//			System.out.println("SqlResult: "+SqlResult);
			if (!compareMap(ExpectedKeyValues, SqlResult, null)) {
				flag=false;
				break;
			}
		}		
		return flag;
		
	}
	
	/**
	 * 返回两个Sql.Date日期类型之间相差的天数
	 * @param Date1 第一个日期
	 * @param Date2 第二个日期
	 * @return 返回相差天数, 可能为负
	 */
	public static int DaysSubOf2Date(Date Date1, Date Date2) {

	       Calendar aCalendar = Calendar.getInstance();

	       aCalendar.setTime(Date1);

	       int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

	       aCalendar.setTime(Date2);

	       int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       
	       int Sub=day2-day1;
	       
	       return Sub;
	       
	   }
	
}
