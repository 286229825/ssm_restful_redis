package redis;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisCache implements Cache {

	//获取slf4j日志实例，用来打印一些自定义的日志
	private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

	//jedis连接池
	private static JedisConnectionFactory jedisConnectionFactory;

	//缓存实例id
	private final String id;

	//获取读写锁对象
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public RedisCache(final String id) {
		// 若缓存实例id为空，则抛出异常
		if (id == null) {
			throw new IllegalArgumentException("Cache instances require an ID");
		}
		// 使用slf4j输出日志
		logger.debug("MybatisRedisCache:id=" + id);
		this.id = id;
	}

	//在每次执行插入、删除、修改的时候都会执行这个方法来清除redis中的缓存，防止脏读
	@Override
	public void clear() {
		JedisConnection connection = null;
		try {
			connection = (JedisConnection) jedisConnectionFactory.getConnection();
			// 连接清除数据
			connection.flushDb();
			connection.flushAll();
		} catch (JedisConnectionException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Override
	public String getId() {
		return this.id;
	}

	//从redis中查数，若查不到，再去数据库中查
	@Override
	public Object getObject(Object key) {
		Object result = null;
		JedisConnection connection = null;
		try {
			// 获取redis连接
			connection = (JedisConnection) jedisConnectionFactory.getConnection();
			// 借用spring_data_redis.jar中的JdkSerializationRedisSerializer.class，并利用其反序列化方法获取值
			RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer();
			result = serializer.deserialize(connection.get(serializer.serialize(key)));
		} catch (JedisConnectionException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return result;
	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return this.readWriteLock;
	}

	@Override
	public int getSize() {
		int result = 0;
		JedisConnection connection = null;
		try {
			connection = (JedisConnection) jedisConnectionFactory.getConnection();
			result = Integer.valueOf(connection.dbSize().toString());
		} catch (JedisConnectionException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return result;
	}

	//将从数据库中查找到的数据存入redis，供以后使用
	@Override
	public void putObject(Object key, Object value) {
		JedisConnection connection = null;
		try {
			//打印日志
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>putObject:" + key + "=" + value);
			//获取redis连接
			connection = (JedisConnection) jedisConnectionFactory.getConnection();
			// 借用spring_data_redis.jar中的JdkSerializationRedisSerializer.class
			RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer();
			// 利用其序列化方法将数据写入redis服务的缓存中
			connection.set(serializer.serialize(key), serializer.serialize(value));

		} catch (JedisConnectionException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	//将指定key的数据移除
	@Override
	public Object removeObject(Object key) {
		JedisConnection connection = null;
		Object result = null;
		try {
			//获取redis连接
			connection = (JedisConnection) jedisConnectionFactory.getConnection();
			// 借用spring_data_redis.jar中的JdkSerializationRedisSerializer.class
			RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer();
			//将指定key的数据的剩余存活时间设为0
			result = connection.expire(serializer.serialize(key), 0);
		} catch (JedisConnectionException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return result;
	}

	//静态注入jedis连接池
	public static void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
		RedisCache.jedisConnectionFactory = jedisConnectionFactory;
	}
}
