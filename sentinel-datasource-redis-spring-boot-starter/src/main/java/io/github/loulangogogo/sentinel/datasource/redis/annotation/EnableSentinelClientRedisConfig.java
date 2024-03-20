package io.github.loulangogogo.sentinel.datasource.redis.annotation;

import io.github.loulangogogo.sentinel.datasource.redis.config.SentinelRedisConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/*********************************************************
 ** 使能sentinel客户端的redis配置
 ** <br><br>
 ** Date: Created in 2024/3/20 10:32
 ** @author loulan
 ** @version 0.0.0
 *********************************************************/
@Target(value = {ElementType.TYPE})  //作用与类
@Retention(RetentionPolicy.RUNTIME)  //运行时注解
@Documented                          //可以被javadoc识别处理
@Import(SentinelRedisConfiguration.class)
public @interface EnableSentinelClientRedisConfig {
}
