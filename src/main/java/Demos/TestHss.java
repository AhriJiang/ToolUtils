package Demos;

import java.io.IOException;

import com.jcraft.jsch.JSchException;

import InterfaceTestUtils.SSHExecutor;

public class TestHss {

	public static void main(String[] args) throws JSchException, IOException, InterruptedException {
		 SSHExecutor ssh =  new SSHExecutor("root","P@ss1234","172.30.11.6",22);
		 ssh.exec("cd /usr/local/tomcat/bin /n");
		 ssh.exec("ls");
		 ssh.shell("cd /usr/local/tomcat/bin /n");
		 ssh.exec("ls");
		 String PID=ssh.ExecOutput("ps -ef|grep tomcat|grep -v grep|awk '{print $2}'");
		 System.out.println(PID);
		 ssh.exec("/usr/local/tomcat/bin/startup.sh");
//		 ssh.exec("kill -9 "+ PID);
//		 ssh.exec("./startup.sh");
		 ssh.close();
	}

}
