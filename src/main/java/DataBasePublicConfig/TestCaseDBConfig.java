package DataBasePublicConfig;

import org.jooq.SQLDialect;

public class TestCaseDBConfig {

	public static final String DB_IP="localhost";
	public static final String DB_PORT="3306";
	public static final String DB_USERNAME="juntao";
	public static final String DB_PASSWORD="jjt20030439";
	public static final String DB_BASENAME="project_t";
	public static final String URL="jdbc:mysql://172.16.101.254:8066/MasterSlave?maxAllowedPacket=10240";
	public static final SQLDialect DB_DIALECT = SQLDialect.MYSQL;
	public static final String SSHHOST = "163.53.94.238";
	public static final int SSHPORT = 22;
	public static final String SSHUSER = "qappsom";
	public static final String SSHPASSWORD = "Appsvr-123";
	public static final int LOCALPORT = 3306;
	
}
