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
import io.gitee.loulan_yxq.owner.core.tool.ObjectTool;
import io.gitee.loulan_yxq.owner.core.tool.StrTool;
import io.gitee.loulan_yxq.owner.json.tool.JsonTool;
import io.github.loulangogogo.sentinel.datasource.redis.base.RedisKey;
import io.github.loulangogogo.sentinel.datasource.redis.enums.RuleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.el.PropertyNotFoundException;
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

    @Autowired
    private Environment env;


    /**
     * 这个配置如何和springcloud的sentinel使用的话，要在SentinelAutoConfiguration初始化完成之后执行，
     * 否则会出现无法注册到控制台上的情况。
     *
     * @author :loulan
     */
    @Override
    public void afterSingletonsInstantiated() {

        String appName = createAppName();
        RedisConnectionConfig config = createRedisConnectionConfig();

        // 添加注册流控规则的配置
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new RedisDataSource<>(config,
                RedisKey.SENTINEL_CONFIG_PRE + appName + ":" + RuleTypeEnum.FLOW.getName(),
                RedisKey.SENTINEL_CHANNEL_PRE + appName + ":" + RuleTypeEnum.FLOW.getName(),
                source -> JsonTool.parseList(source, FlowRule.class));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new RedisDataSource<>(config,
                RedisKey.SENTINEL_CONFIG_PRE + appName + ":" + RuleTypeEnum.DEGRADE.getName(),
                RedisKey.SENTINEL_CHANNEL_PRE + appName + ":" + RuleTypeEnum.DEGRADE.getName(),
                source -> JsonTool.parseList(source, DegradeRule.class));
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());

        ReadableDataSource<String, List<ParamFlowRule>> paramFlowRuleDataSource = new RedisDataSource<>(config,
                RedisKey.SENTINEL_CONFIG_PRE + appName + ":" + RuleTypeEnum.PARAM_FLOW.getName(),
                RedisKey.SENTINEL_CHANNEL_PRE + appName + ":" + RuleTypeEnum.PARAM_FLOW.getName(),
                source -> JsonTool.parseList(source, ParamFlowRule.class));
        ParamFlowRuleManager.register2Property(paramFlowRuleDataSource.getProperty());

        ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new RedisDataSource<>(config,
                RedisKey.SENTINEL_CONFIG_PRE + appName + ":" + RuleTypeEnum.SYSTEM.getName(),
                RedisKey.SENTINEL_CHANNEL_PRE + appName + ":" + RuleTypeEnum.SYSTEM.getName(),
                source -> JsonTool.parseList(source, SystemRule.class));
        SystemRuleManager.register2Property(systemRuleDataSource.getProperty());

        ReadableDataSource<String, List<AuthorityRule>> authRuleDataSource = new RedisDataSource<>(config,
                RedisKey.SENTINEL_CONFIG_PRE + appName + ":" + RuleTypeEnum.AUTH.getName(),
                RedisKey.SENTINEL_CHANNEL_PRE + appName + ":" + RuleTypeEnum.AUTH.getName(),
                source -> JsonTool.parseList(source, AuthorityRule.class));
        AuthorityRuleManager.register2Property(authRuleDataSource.getProperty());
    }

    /**
     * 创建获取应用名称
     *
     * @return 应用的名称
     * @author :loulan
     */
    private String createAppName() {
        String applicationName = env.getProperty("spring.application.name");
        String projectName = env.getProperty("project.name");
        if (StrTool.isEmpty(applicationName) && StrTool.isEmpty(projectName)) {
            throw new PropertyNotFoundException("springboot项目请配置【spring.application.name】。sentinel-dashboard的项目请配置【project.name】");
        }
        return StrTool.isNotEmpty(applicationName) ? applicationName : projectName;
    }

    /**
     * 创建sentinel的redis连接配置
     *
     * @return sentinel的redis连接配置对象
     * @author :loulan
     */
    private RedisConnectionConfig createRedisConnectionConfig() {
        // host和port是必须存在的，database和password需要进行判断
        String redisHost = env.getProperty("spring.redis.host");
        Integer redisPort = env.getProperty("spring.redis.port", Integer.class);
        Integer redisDatabase = env.getProperty("spring.redis.database", Integer.class);
        String redisPassword = env.getProperty("spring.redis.password");

        RedisConnectionConfig.Builder redisConnectionBuilder = RedisConnectionConfig.builder()
                .withHost(redisHost)
                .withPort(redisPort);
        if (ObjectTool.isNotNull(redisDatabase)) redisConnectionBuilder.withDatabase(redisDatabase);
        if (StrTool.isNotEmpty(redisPassword)) redisConnectionBuilder.withPassword(redisPassword);

        return redisConnectionBuilder.build();
    }
}