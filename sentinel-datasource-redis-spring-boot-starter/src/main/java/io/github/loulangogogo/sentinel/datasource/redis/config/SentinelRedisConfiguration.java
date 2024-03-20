package io.github.loulangogogo.sentinel.datasource.redis.config;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.redis.RedisDataSource;
import com.alibaba.csp.sentinel.datasource.redis.config.RedisConnectionConfig;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import io.gitee.loulan_yxq.owner.json.tool.JsonTool;
import io.github.loulangogogo.sentinel.datasource.redis.base.RedisKey;
import io.github.loulangogogo.sentinel.datasource.redis.enums.RuleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/*********************************************************
 ** sentinel的配置
 ** <br><br>
 ** Date: Created in 2024/3/19 14:51
 ** @author loulan
 ** @version 0.0.0
 *********************************************************/
@Configuration
@Slf4j
public class SentinelRedisConfiguration implements SmartInitializingSingleton {

    @Value("${spring.application.name}")
    public String applicationName;
    @Value("${spring.redis.host}")
    public String redisHost;
    @Value("${spring.redis.port}")
    public Integer redisPort;
    @Value("${spring.redis.database}")
    public Integer redisDatabase;
    @Value("${spring.redis.password}")
    public String redisPassword;


    /**
     * 这个配置如何和springcloud的sentinel使用的话，要在SentinelAutoConfiguration初始化完成之后执行，
     * 否则会出现无法注册到控制台上的情况。
     *
     * @author :loulan
     */
    @Override
    public void afterSingletonsInstantiated() {
        RedisConnectionConfig config = RedisConnectionConfig.builder()
                .withHost(redisHost)
                .withPort(redisPort)
                .withDatabase(redisDatabase)
                .withPassword(redisPassword)
                .build();
        // 添加注册流控规则的配置
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new RedisDataSource<>(config,
                RedisKey.SENTINEL_CONFIG_PRE + applicationName + ":" + RuleTypeEnum.FLOW.getName(),
                RedisKey.SENTINEL_CHANNEL_PRE + applicationName + ":" + RuleTypeEnum.FLOW.getName(),
                source -> JsonTool.parseList(source, FlowRule.class));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new RedisDataSource<>(config,
                RedisKey.SENTINEL_CONFIG_PRE + applicationName + ":" + RuleTypeEnum.DEGRADE.getName(),
                RedisKey.SENTINEL_CHANNEL_PRE + applicationName + ":" + RuleTypeEnum.DEGRADE.getName(),
                source -> JsonTool.parseList(source, DegradeRule.class));
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());

        ReadableDataSource<String, List<ParamFlowRule>> paramFlowRuleDataSource = new RedisDataSource<>(config,
                RedisKey.SENTINEL_CONFIG_PRE + applicationName + ":" + RuleTypeEnum.PARAM_FLOW.getName(),
                RedisKey.SENTINEL_CHANNEL_PRE + applicationName + ":" + RuleTypeEnum.PARAM_FLOW.getName(),
                source -> JsonTool.parseList(source, ParamFlowRule.class));
        ParamFlowRuleManager.register2Property(paramFlowRuleDataSource.getProperty());

        ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new RedisDataSource<>(config,
                RedisKey.SENTINEL_CONFIG_PRE + applicationName + ":" + RuleTypeEnum.SYSTEM.getName(),
                RedisKey.SENTINEL_CHANNEL_PRE + applicationName + ":" + RuleTypeEnum.SYSTEM.getName(),
                source -> JsonTool.parseList(source, SystemRule.class));
        SystemRuleManager.register2Property(systemRuleDataSource.getProperty());

        ReadableDataSource<String, List<AuthorityRule>> authRuleDataSource = new RedisDataSource<>(config,
                RedisKey.SENTINEL_CONFIG_PRE + applicationName + ":" + RuleTypeEnum.AUTH.getName(),
                RedisKey.SENTINEL_CHANNEL_PRE + applicationName + ":" + RuleTypeEnum.AUTH.getName(),
                source -> JsonTool.parseList(source, AuthorityRule.class));
        AuthorityRuleManager.register2Property(authRuleDataSource.getProperty());
    }
}