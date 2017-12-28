package Demos;

import DataBasePublicConfig.JiFengMallRedisProperties;
import redis.clients.jedis.Jedis;

public class TestJedis {
	public static void main(String[] args) {
		Jedis Jedis = new Jedis("172.30.11.177",JiFengMallRedisProperties.ServerPort, JiFengMallRedisProperties.TimeOut);
		Jedis.set("TOK:20170621", "287240578125602816");
		System.out.println(Jedis.exists("TOK:20170621"));;
	}
}
