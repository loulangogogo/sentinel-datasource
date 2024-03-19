package io.github.loulangogogo.sentinel.datasource.redis.client;

import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import io.gitee.loulan_yxq.owner.core.collection.CollTool;
import io.gitee.loulan_yxq.owner.core.tool.StrTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/*********************************************************
 ** sentinel的redis客户端，其实就是对api客户端进行继承调整修改
 ** <br><br>
 ** Date: Created in 2024/3/19 14:06
 ** @author loulan
 ** @version 0.0.0
 *********************************************************/
@Component
@Primary
public class SentinelRedisClient extends SentinelApiClient {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 这里需要定义常量替换掉
    private static String SENTINEL_CHANNEL_PRE = "Sentinel:Channel:";
    private static String SENTINEL_CONFIG_PRE = "Sentinel:Config:";

    // fixme 2024/3/19(待修改) 这里使用枚举替换掉
    private static final String FLOW_RULE_TYPE = "flow";
    private static final String DEGRADE_RULE_TYPE = "degrade";
    private static final String PARAM_FLOW_RULE_TYPE = "paramFlow";
    private static final String SYSTEM_RULE_TYPE = "system";
    private static final String AUTH_RULE_TYPE = "authority";

    /**
     * 获取授权规则的方法
     *
     * @param app 应用服务的名称
     * @param ip  对应服务的ip【redis中不用】
     * @param app 对应服务的httpserver端口【redis中不用】
     * @return 规则数据
     * @throws
     * @author :loulan
     */
    @Override
    public List<AuthorityRuleEntity> fetchAuthorityRulesOfMachine(String app, String ip, int port) {
        return fetchRules(app, AUTH_RULE_TYPE, AuthorityRuleEntity.class);
    }

    /**
     * 设置授权规则
     *
     * @param app   应用服务的名称
     * @param ip    对应服务的ip【redis中不用】
     * @param app   对应服务的httpserver端口【redis中不用】
     * @param rules 要进行设置的规则
     * @return 设置成功与否
     * @throws
     * @author :loulan
     */
    @Override
    public boolean setAuthorityRuleOfMachine(String app, String ip, int port, List<AuthorityRuleEntity> rules) {
        AssertUtil.notEmpty(app, "应用服务名称不能为空");
        AssertUtil.notNull(rules, "授权规则不能为空");
        return setRules(app, AUTH_RULE_TYPE, rules);
    }

    /**
     * 获取系统规则的方法
     *
     * @param app 应用服务的名称
     * @param ip  对应服务的ip【redis中不用】
     * @param app 对应服务的httpserver端口【redis中不用】
     * @return 规则数据
     * @throws
     * @author :loulan
     */
    @Override
    public List<SystemRuleEntity> fetchSystemRuleOfMachine(String app, String ip, int port) {
        return fetchRules(app, SYSTEM_RULE_TYPE, SystemRuleEntity.class);
    }

    /**
     * 设置系统规则
     *
     * @param app   应用服务的名称
     * @param ip    对应服务的ip【redis中不用】
     * @param app   对应服务的httpserver端口【redis中不用】
     * @param rules 要进行设置的规则
     * @return 设置成功与否
     * @throws
     * @author :loulan
     */
    @Override
    public boolean setSystemRuleOfMachine(String app, String ip, int port, List<SystemRuleEntity> rules) {
        AssertUtil.notEmpty(app, "应用服务名称不能为空");
        AssertUtil.notNull(rules, "系统规则不能为空");
        return setRules(app, SYSTEM_RULE_TYPE, rules);
    }

    /**
     * 获取热点规则的方法
     *
     * @param app 应用服务的名称
     * @param ip  对应服务的ip【redis中不用】
     * @param app 对应服务的httpserver端口【redis中不用】
     * @return 异步可执行方法
     * @throws
     * @author :loulan
     */
    @Override
    public CompletableFuture<List<ParamFlowRuleEntity>> fetchParamFlowRulesOfMachine(String app, String ip, int port) {
        return CompletableFuture.supplyAsync(() -> {
            return fetchRules(app, PARAM_FLOW_RULE_TYPE, ParamFlowRuleEntity.class);
        });
    }

    /**
     * 设置热点规则数据
     *
     * @param app   应用服务的名称
     * @param ip    对应服务的ip【redis中不用】
     * @param app   对应服务的httpserver端口【redis中不用】
     * @param rules 要进行设置的规则
     * @return 异步可执行对象
     * @throws
     * @author :loulan
     */
    @Override
    public CompletableFuture<Void> setParamFlowRuleOfMachine(String app, String ip, int port, List<ParamFlowRuleEntity> rules) {
        AssertUtil.notEmpty(app, "应用服务名称不能为空");
        AssertUtil.notNull(rules, "热点规则不能为空");
        return CompletableFuture.runAsync(() -> {
            setRules(app, PARAM_FLOW_RULE_TYPE, rules);
        });
    }

    /**
     * 获取熔断规则的方法
     *
     * @param app 应用服务的名称
     * @param ip  对应服务的ip【redis中不用】
     * @param app 对应服务的httpserver端口【redis中不用】
     * @return 规则数据
     * @throws
     * @author :loulan
     */
    @Override
    public List<DegradeRuleEntity> fetchDegradeRuleOfMachine(String app, String ip, int port) {
        return fetchRules(app, DEGRADE_RULE_TYPE, DegradeRuleEntity.class);
    }

    /**
     * 设置熔断规则数据
     *
     * @param app   应用服务的名称
     * @param ip    对应服务的ip【redis中不用】
     * @param app   对应服务的httpserver端口【redis中不用】
     * @param rules 要进行设置的规则
     * @return 设置成功与否
     * @throws
     * @author :loulan
     */
    @Override
    public boolean setDegradeRuleOfMachine(String app, String ip, int port, List<DegradeRuleEntity> rules) {
        AssertUtil.notEmpty(app, "应用服务名称不能为空");
        AssertUtil.notNull(rules, "熔断规则不能为空");
        return setRules(app, DEGRADE_RULE_TYPE, rules);
    }


    /**
     * 获取流控规则的方法
     *
     * @param app 应用服务的名称
     * @param ip  对应服务的ip【redis中不用】
     * @param app 对应服务的httpserver端口【redis中不用】
     * @return 规则数据
     * @throws
     * @author :loulan
     */
    @Override
    public List<FlowRuleEntity> fetchFlowRuleOfMachine(String app, String ip, int port) {
        return fetchRules(app, FLOW_RULE_TYPE, FlowRuleEntity.class);
    }

    /**
     * 设置留空规则到机器
     *
     * @param app   应用服务的名称
     * @param ip    对应服务的ip【redis中不用】
     * @param app   对应服务的httpserver端口【redis中不用】
     * @param rules 要进行设置的规则
     * @return
     * @throws
     * @author :loulan
     */
    @Override
    public boolean setFlowRuleOfMachine(String app, String ip, int port, List<FlowRuleEntity> rules) {
        AssertUtil.notEmpty(app, "应用服务名称不能为空");
        AssertUtil.notNull(rules, "流控规则不能为空");
        return setRules(app, FLOW_RULE_TYPE, rules);
    }

    /**
     * 异步设置流控规则到机器
     *
     * @param app   应用服务的名称
     * @param ip    对应服务的ip【redis中不用】
     * @param app   对应服务的httpserver端口【redis中不用】
     * @param rules 要进行设置的规则
     * @return
     * @throws
     * @author :loulan
     */
    @Override
    public CompletableFuture<Void> setFlowRuleOfMachineAsync(String app, String ip, int port, List<FlowRuleEntity> rules) {
        AssertUtil.notEmpty(app, "应用服务名称不能为空");
        AssertUtil.notNull(rules, "流控规则不能为空");
        // fixme 2024/3/19(待修改) 这是什么鬼东西
        return CompletableFuture.runAsync(() -> {
            setFlowRuleOfMachine(app, ip, port, rules);
        });
    }

    /**
     * 获取规则数据
     *
     * @param app  应用服务名称
     * @param type 规则类型
     * @param clzz 规则的类
     * @return 规则集合数据
     * @throws
     * @author :loulan
     */
    private <T> List<T> fetchRules(String app, String type, Class<T> clzz) {
        String rulesJson = redisTemplate.opsForValue().get(SENTINEL_CONFIG_PRE + app + ":" + type);
        if (StrTool.isEmpty(rulesJson)) {
            return CollTool.list();
        }
        return JSON.parseArray(rulesJson, clzz);
    }

    /**
     * 设置规则数据
     *
     * @param app   应用服务名称
     * @param type  规则类型
     * @param rules 规则数据
     * @return 设置是否成功
     * @throws
     * @author :loulan
     */
    private boolean setRules(String app, String type, List rules) {
        AssertUtil.notEmpty(app, "应用服务名称不能为空");
        AssertUtil.notNull(rules, "规则不能为空");
        String rulesJson = JSON.toJSONString(rules);
        redisTemplate.opsForValue().set(SENTINEL_CONFIG_PRE + app + ":" + type, rulesJson);
        redisTemplate.convertAndSend(SENTINEL_CHANNEL_PRE + app + ":" + type, rulesJson);
        return true;
    }
}
