package InterfaceTestUtils;


import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SSHExecutor {

	private static final long INTERVAL = 100L;
	private static final int SESSION_TIMEOUT = 30000;
	private static final int CHANNEL_TIMEOUT = 3000;
	private JSch jsch = null;
	private Session session = null;
	
	/**
	 * SSHExecutor外部传参构造SSH对象
	 * @param User 用户名
	 * @param Password 密码
	 * @param Host 服务器IP地址
	 * @param Port 服务器端口号
	 * @return
	 * @throws JSchException
	 */
	public SSHExecutor(String User,String Password,String Host,Integer Port) throws JSchException {
		jsch = new JSch();
		session = jsch.getSession(User,Host,Port);
		session.setPassword(Password);
		session.setUserInfo((UserInfo) new MyUserInfo());
		session.connect(SESSION_TIMEOUT);
	}


	/*
	 * 注意编码转换
	 */
	public long shell(String cmd, String outputFileName) throws JSchException, IOException, InterruptedException {
		long start = System.currentTimeMillis();
		Channel channel = session.openChannel("shell");
		PipedInputStream pipeIn = new PipedInputStream();
		PipedOutputStream pipeOut = new PipedOutputStream(pipeIn);
		FileOutputStream fileOut = new FileOutputStream(outputFileName, true);
		channel.setInputStream(pipeIn);
		channel.setOutputStream(fileOut);
		channel.connect(CHANNEL_TIMEOUT);

		pipeOut.write(cmd.getBytes());
		Thread.sleep(INTERVAL);
		pipeOut.close();
		pipeIn.close();
		fileOut.close();
		channel.disconnect();
		return System.currentTimeMillis() - start;
	}
	
	public long shell(String cmd, File File) throws JSchException, IOException, InterruptedException {
		long start = System.currentTimeMillis();
		Channel channel = session.openChannel("shell");
		PipedInputStream pipeIn = new PipedInputStream();
		PipedOutputStream pipeOut = new PipedOutputStream(pipeIn);
		FileOutputStream fileOut = new FileOutputStream(File,true);
		channel.setInputStream(pipeIn);
		channel.setOutputStream(fileOut);
		channel.connect(CHANNEL_TIMEOUT);
		
		pipeOut.write(cmd.getBytes());
		Thread.sleep(INTERVAL);
		pipeOut.close();
		pipeIn.close();
		fileOut.close();
		channel.disconnect();
		return System.currentTimeMillis() - start;
	}
	
	public long shell(String cmd) throws JSchException, IOException, InterruptedException {
		long start = System.currentTimeMillis();
		Channel channel = session.openChannel("shell");
		PipedInputStream pipeIn = new PipedInputStream();
		PipedOutputStream pipeOut = new PipedOutputStream(pipeIn);
		channel.setInputStream(pipeIn);
		channel.connect(CHANNEL_TIMEOUT);
		
		pipeOut.write(cmd.getBytes());
		Thread.sleep(INTERVAL);
		pipeOut.close();
		pipeIn.close();
		channel.disconnect();
		return System.currentTimeMillis() - start;
	}
	
	public int exec(String cmd) throws IOException, JSchException, InterruptedException {
		ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand(cmd);
		channelExec.setInputStream(null);
		channelExec.setErrStream(System.err);
		InputStream in = channelExec.getInputStream();
		channelExec.connect();

		int res = -1;
		StringBuffer buf = new StringBuffer(1024);
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				buf.append(new String(tmp, 0, i));
			}
			if (channelExec.isClosed()) {
				res = channelExec.getExitStatus();
				System.out.println(format("Exit-status: %d", res));
				break;
			}
			TimeUnit.MILLISECONDS.sleep(100);
		}
		System.out.println(buf.toString());
		channelExec.disconnect();
		return res;
	}
	
	public String ExecOutput(String cmd) throws IOException, JSchException, InterruptedException {
		String Output;
		ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
		channelExec.setCommand(cmd);
		channelExec.setInputStream(null);
		channelExec.setErrStream(System.err);
		InputStream in = channelExec.getInputStream();
		channelExec.connect();

		int res = -1;
		StringBuffer buf = new StringBuffer(1024);
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				buf.append(new String(tmp, 0, i));
			}
			if (channelExec.isClosed()) {
				res = channelExec.getExitStatus();
				System.out.println(format("Exit-status: %d", res));
				break;
			}
			TimeUnit.MILLISECONDS.sleep(100);
		}
		Output=buf.toString();
		System.out.println(Output);
		channelExec.disconnect();
		return Output;
	}
	
	public Session getSession() {
		return session;
	}

	public void close() {
		getSession().disconnect();
	}

	

	/*
	 * 自定义UserInfo
	 */
	private class MyUserInfo implements UserInfo {

		public String getPassphrase() {
			return null;
		}

		public String getPassword() {
			return null;
		}

		public boolean promptPassword(String s) {
			return false;
		}

		public boolean promptPassphrase(String s) {
			return false;
		}

		public boolean promptYesNo(String s) {
			System.out.println(s);
			System.out.println("true");
			return true;
		}

		public void showMessage(String s) {
		}
	}
}
