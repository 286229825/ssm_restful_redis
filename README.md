
整合 Spring、SpringMVC、MyBatis，使用 Redis 作为缓存，并使用 RestFul 风格开发。


整合 Redis ：

    首先需要引入 java 的 redis 驱动的依赖：

      <!-- 添加jedis依赖 -->
      <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>2.8.0</version>
      </dependency>
      <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-redis</artifactId>
        <version>1.6.2.RELEASE</version>
      </dependency>

    第一步：在 spring 的 配置文件 applicationContext.xml 中加入 redis 的配置

    第二步：创建实现了 mybatis 的 Cache 接口的自定义缓存类（redis.RedisCache.java），还创建一个帮助自定义缓存类静态注入 redis 连接池的中间类（redis.RedisCacheTransfer.java）

    第三步：在 mybatis 的 sqlMapConfig.xml 文件中开启 mybatis 的二级缓存，默认就是开启的，所以这一步可以跳过

    第四步：在需要使用 redis 的 mapper.xml 文件中开启 mybatis 的二级缓存，使用以下标签来开启：

        <!-- 开启使用redis作为mybatis的二级缓存，type的值为实现了Cache接口的自定义缓存类 -->
        <cache type="redis.RedisCache"></cache>
        
        
        
使用 RestFul 风格的 URL：

    因为 restful 要用到 json，所以需要额外添加 json 的依赖
		<!-- 添加json依赖 -->
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
			<version>2.8.5</version>
		</dependency>
