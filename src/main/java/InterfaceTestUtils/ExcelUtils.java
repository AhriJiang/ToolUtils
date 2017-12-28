package InterfaceTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

public class ExcelUtils {
	
	/**
	 * 根据文件路径和Sheet名称返回Sheet对象
	 * @param ExcelFilePath Excel路径
	 * @param SheetName 需要获取的Sheet名称
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public XSSFSheet GetExcelSheet(String ExcelFilePath, String SheetName) throws InvalidFormatException, IOException{
		File file=new File(ExcelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFSheet Sheet = wb.getSheet(SheetName);
		return Sheet;
	}
	
	/**
	 * 从Excel的Sheet中获取单元格数据, 返回JsonObect数组
	 * 
	 * @param SheetName
	 *            Excel数据源Sheet对象
	 * @return 返回N-1行1列的Object数组,有N行Excel数据就有N-1数据(Excel第一列是Keys标签),列内存放JsonObject格式的测试数据
	 * @throws IOException
	 */

	public static Object[][] GetExcelData(XSSFWorkbook wb, String SheetName) throws IOException {

		XSSFSheet Sheet = wb.getSheet(SheetName);

		/**
		 * 获取Excel第一行的字段名称
		 */

		String[] ziduan = GetExcelKeys(Sheet);

		/**
		 * 遍历返回业务组合字段,ZiDuanIndex是一个索引数组
		 */
		int[] ZiDuanIndex = null;
		/**
		 * 遍历后返回的Array数据组
		 */
		JSONArray JsonResult = new JSONArray();

		for (int MaxRowNumber = 0; MaxRowNumber < Sheet.getLastRowNum() + 1; MaxRowNumber++) {

			JSONObject JsonDatas = new JSONObject();
			XSSFRow row = Sheet.getRow(MaxRowNumber);
			XSSFCell TagCell;

			if (MaxRowNumber == 0) {
				// 遍历第一行,获取所有字段的列坐标
				row = Sheet.getRow(0);
				ZiDuanIndex = new int[ziduan.length];
				for (int i = 0; i < ziduan.length; i++) {
					// 字段有多少个,就要遍历多少次
					for (int j = 0; j < row.getLastCellNum(); j++) {
						// 每次遍历字段,判断每一个cell是否和标签文字相同
						TagCell = row.getCell(j);
						if (TagCell.getStringCellValue().equals(ziduan[i])) {
							ZiDuanIndex[i] = TagCell.getColumnIndex();
						} else {
							continue;
						}
					}
				}
			} else {
				// 第二行开始放东西到JsonDatas
				for (int i = 0; i < ZiDuanIndex.length; i++) {
					TagCell = row.getCell(ZiDuanIndex[i]);

					if (TagCell == null) {
						JsonDatas.put(ziduan[i], JSONObject.NULL);
					} else if (TagCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && TagCell != null) {
						String ValueText = String.valueOf(TagCell.getNumericCellValue());
						if (ValueText.matches("[0-9]+\\.[0]?")) {
							ValueText = ValueText.substring(0, ValueText.indexOf("."));
							JsonDatas.put(ziduan[i], Integer.parseInt(ValueText));
						} else {
							JsonDatas.put(ziduan[i], TagCell.getNumericCellValue());
						}
					} else if (TagCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
						JsonDatas.put(ziduan[i], TagCell.getBooleanCellValue());
					} else if (TagCell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
						JsonDatas.put(ziduan[i], JSONObject.NULL);
					} else {
						TagCell.setCellType(XSSFCell.CELL_TYPE_STRING);
						if (getStringValue(TagCell).equals("") || getStringValue(TagCell).equals(null)) {
							JsonDatas.put(ziduan[i], JSONObject.NULL);
						} else {
							JsonDatas.put(ziduan[i], getStringValue(TagCell));
						}
					}
				}
				JsonResult.put(JsonDatas);
			}

		}

		/**
		 * 创建一个Jsonresult.length行, 1列的二维数组
		 */

		Object[][] b = new Object[JsonResult.length()][1];// ZiDuanIndex.length

		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < 1; j++) {// j<b[i].length
											// //如果需要将json每个值作为object列的结果

				b[i][j] = JsonResult.get(i);

				// JSONObject jb=(JSONObject) JsonResult.get(i);
				// b[i][j]=jb.get(jb.keySet().toArray()[j].toString());//将json的第j列的数据,
				// 放到object第i行,第j列中

			}
		}
		return b;

	}

	/**
	 * 获取该Sheet中所有Jooq的Insert数据
	 * 
	 * @param wb 工作簿对象
	 * @param SheetName
	 *            Sheet名称
	 * @return
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	public Object[][] GetJooqSqlDataInMatrix(String ExcelFilePath, String SheetName) throws InvalidFormatException, IOException {
		File file=new File(ExcelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(file);
		List<Object[]> records = new ArrayList<>();
		XSSFSheet Sheet = wb.getSheet(SheetName);
		XSSFRow Row;
		XSSFCell Cell;
		// try {
		int rows = Sheet.getLastRowNum();
		Row = Sheet.getRow(0);
		int columns = Row.getLastCellNum();
		// System.out.println(rows);
		// System.out.println(columns);
		List<Object> oneRecord = new ArrayList<>();
		for (int rowindex = 1; rowindex < rows + 1; rowindex++) {
			// the first column is testcasename,don't read
			Row = Sheet.getRow(rowindex);
			for (int columnindex = 0; columnindex < columns; columnindex++) {
				Cell = Row.getCell(columnindex);
				String CellText;
				if (Cell == null||Cell.getCellType()==XSSFCell.CELL_TYPE_BLANK) {
					CellText = StringUtils.trimToNull("");
				} else if (Cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && Cell != null) {
					CellText = String.valueOf(Cell.getNumericCellValue());
				} else {
					CellText = StringUtils.trimToNull(Cell.getStringCellValue());
					// System.out.println("String:"+CellText);
				}
				/**
				 * 数据库中null的处理
				 */
				oneRecord.add(CellText);
			}
			// logger.debug("current record data is:" + oneRecord);
			records.add(oneRecord.toArray(new String[oneRecord.size()]));
			oneRecord.clear();
		}
		// logger.debug("records data is :" + oneRecord);
		// } catch (Exception e) {
		// logger.error(e);
		// }

		Object[][] results = new Object[records.size()][];
		for (int i = 0; i < records.size(); i++) {
			results[i] = records.get(i);
		}
		return results;
	}

	/**
	 * 获取该Sheet中所有Jooq的Insert数据
	 * 
	 * @param wb
	 *            工作簿对象
	 * @param SheetName
	 *            Sheet名
	 * @param ColumnName
	 *            表头名
	 * @return
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	public List<Object> GetJooqSqlDataInColumn(String ExcelFilePath, String SheetName, String ColumnName) throws InvalidFormatException, IOException {
		File file=new File(ExcelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFSheet Sheet = wb.getSheet(SheetName);
		XSSFRow Row;
		XSSFCell Cell;
		/**
		 * 获取Excel第一行的字段名称
		 */
		String[] ziduan = GetExcelKeys(Sheet);
		
		/**
		 * 获取整个表格的最大列数和最大行数,ColumnNameIndex是标的index的下标
		 */
		int rows = Sheet.getLastRowNum();
		Row = Sheet.getRow(rows);
		int columns = Row.getLastCellNum();
		int ColumnNameIndex = 0;
		// System.out.println(rows);
		// System.out.println(columns);
		
		List<Object> oneRecord = new ArrayList<>();
		
		for (int i = 0; i < ziduan.length; i++) {
			if(ziduan[i].equals(ColumnName)){
				ColumnNameIndex=i;
				break;
			}
		}		
		
		for (int rowindex = 1; rowindex < rows + 1; rowindex++) {
			Row = Sheet.getRow(rowindex);
			Cell = Row.getCell(ColumnNameIndex);
			String CellText;
			if (Cell == null) {
				CellText = StringUtils.trimToNull("");
			} else if (Cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && Cell != null) {
				CellText = String.valueOf(Cell.getNumericCellValue());
			} else {
				CellText = StringUtils.trimToNull(Cell.getStringCellValue());
				// System.out.println("String:"+CellText);
			}
			oneRecord.add(CellText);
		}
		return oneRecord;
	}
	
	
	public static Object[] GetJooqSqlDataInRow(XSSFSheet Sheet,String ColumnName,String FieldValue) throws InvalidFormatException, IOException {
		
		XSSFRow Row;
		XSSFCell Cell;
		/**
		 * 获取Excel第一行的字段名称
		 */
		String[] ziduan = GetExcelKeys(Sheet);
		
		/**
		 * 获取整个表格的最大列数和最大行数,ColumnNameIndex是标的index的下标
		 */
		int rows = Sheet.getLastRowNum();
		Row = Sheet.getRow(rows);
		int columns = Row.getLastCellNum();
		/**
		 * 初始化目标行的坐标
		 */
		int ColumnNameIndex = 0;
		int ColumnValueIndex = 0;
		// System.out.println(rows);
		// System.out.println(columns);
		
		Object[] oneRow = new Object[columns];
		
		//找到目标字段名的列标签columnIndex
		for (int i = 0; i < ziduan.length; i++) {
			if(ziduan[i].equals(ColumnName)){
				ColumnNameIndex=i;
				break;
			}
		}		
		
		//找到目标字段值的行标签rowIndex
		for (int i = 0; i < rows; i++) {
			Row=Sheet.getRow(i);
			Cell=Row.getCell(ColumnNameIndex);
			if(getStringValue(Cell).equals(FieldValue)){
				ColumnValueIndex=i;
				break;
			}
		}
		
		//目标行对象
		Row=Sheet.getRow(ColumnValueIndex);
		
		for (int columeIndex = 0; columeIndex < columns ; columeIndex++) {
			Cell=Row.getCell(columeIndex);
			String CellText;
			if (Cell == null||Cell.getCellType()==XSSFCell.CELL_TYPE_BLANK) {
				CellText = StringUtils.trimToNull("");
			} else if (Cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && Cell != null) {
				CellText = String.valueOf(Cell.getNumericCellValue());
			} else {
				CellText = StringUtils.trimToNull(Cell.getStringCellValue());
				// System.out.println("String:"+CellText);
			}
			oneRow[columeIndex]=CellText;
		}
		return oneRow;
	}
	
	/**
	 * 获取Excel的Sheet中第一列的键值数组
	 * 
	 * @param Sheet
	 *            Excel的Sheet对象
	 * @return 返回第一列所有的键值数组
	 */
	public static String[] GetExcelKeys(XSSFSheet Sheet) {

		XSSFRow row = Sheet.getRow(0);
		XSSFCell KeyCell;
		String[] Keys = new String[row.getLastCellNum()];

		for (int MaxColumnNumber = 0; MaxColumnNumber < row.getLastCellNum(); MaxColumnNumber++) {

			KeyCell = row.getCell(MaxColumnNumber);
			Keys[MaxColumnNumber] = KeyCell.getStringCellValue();

		}

		return Keys;
	}

	/**
	 * 将Excel中单元格中数据输出为String数据
	 * 
	 * @param hssfCell
	 *            Excel单元格对象
	 * @return 返回单元格的String数据
	 */
	private static String getStringValue(XSSFCell hssfCell) {

		if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
			return null;
		} else {
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}

	/**
	 * 获取Excel某个Sheet的数据数量
	 * 
	 * @param WorkBook
	 *            需要验证数据行数的WorkBook对象
	 * @param SheetName
	 *            需要验证数据行数的Sheet名称
	 * @return 返回该Excel的数据条数
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public int getSheetDataCount(File file, String SheetName) throws InvalidFormatException, IOException {
		XSSFWorkbook WorkBook = new XSSFWorkbook(file);
		XSSFSheet sheet = WorkBook.getSheet(SheetName);
		int LastData = sheet.getLastRowNum() - 1;
		return LastData;
	}

}
