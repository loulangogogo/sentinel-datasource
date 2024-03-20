package io.github.loulangogogo.sentinel.datasource.redis.base;

/*********************************************************
 ** 定义常量key
 ** <br><br>
 ** Date: Created in 2024/3/20 09:14
 ** @author loulan
 ** @version 0.0.0
 *********************************************************/
public interface RedisKey {

    // 订阅通道前缀
    String SENTINEL_CHANNEL_PRE = "Sentinel:Channel:";
    // 配置数据前缀
    String SENTINEL_CONFIG_PRE = "Sentinel:Config:";
}
