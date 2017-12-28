package DataBasePublicConfig;

import org.jooq.SQLDialect;

public class JiFengMallMySqlProperties {
	
	public static final String USERNAME="galedb";
	public static final String PASSWORD="Appsvr-123";
	public static final String DRIVER="com.mysql.jdbc.Driver";
	public static final String URL="jdbc:mysql://172.16.101.254:8066/MasterSlave?maxAllowedPacket=10240";
	public static final String DefaultPlatFormCode="cmbmb";
	public static final String DefaultCategory="31";
	public static final String DefaultChannelCode="cmbmbA";
	public static final String DefaultBizmode="7";
	public static final String DefalutPlatformUid="123";
	public static final String SpCookieVenderNo="668887";
	public static final String DefaultOtherPlatFormCode="mucfc";
	public static final String DefaultOtherChannelCode="mucfcP";
	public static final String DefaultOtherBizmode="1";
	public static final String DefalutOtherPlatformUid="321";
	public static final SQLDialect DIALECT = SQLDialect.MYSQL;
	public static final String SSHHOST = "163.53.94.238";
	public static final int SSHPORT = 22;
	public static final String SSHUSER = "qappsom";
	public static final String SSHPASSWORD = "Appsvr-123";
	public static final int LOCALPORT = 3306;
	public static final String LOCALURL = "jdbc:mysql://localhost:"+LOCALPORT+"/MasterSlave?maxAllowedPacket=10240";
	
}
