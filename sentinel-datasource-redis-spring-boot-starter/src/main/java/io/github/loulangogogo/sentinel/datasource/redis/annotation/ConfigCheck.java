package io.github.loulangogogo.sentinel.datasource.redis.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MissingRequiredPropertiesException;

import javax.annotation.PostConstruct;
import javax.el.PropertyNotFoundException;

/*********************************************************
 ** 配置检查，确定使用的配置是否已经配置过了
 ** <br><br>
 ** Date: Created in 2024/3/20 14:14
 ** @author loulan
 ** @version 0.0.0
 *********************************************************/
@Configuration
public class ConfigCheck {

    @Autowired
    private Environment env;

    /**
     * 检查redis是否已经配置过了
     * @author     :loulan
     * */
    @PostConstruct
    public void checkRedis() {
        if (!env.containsProperty("spring.redis.host") ||
                !env.containsProperty("spring.redis.port")) {
            // 如果没有这两个配置，那么跑出异常
            throw new PropertyNotFoundException("请引入【spring-boot-starter-data-redis】依赖，并配置redis");
        }
    }
}
