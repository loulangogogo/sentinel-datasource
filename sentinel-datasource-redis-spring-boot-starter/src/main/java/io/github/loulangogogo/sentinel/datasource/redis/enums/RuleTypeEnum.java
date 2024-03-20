package io.github.loulangogogo.sentinel.datasource.redis.enums;

/*********************************************************
 ** 规则类型的枚举
 ** <br><br>
 ** Date: Created in 2024/3/19 23:21
 ** @author loulan
 ** @version 0.0.0
 *********************************************************/
public enum RuleTypeEnum {

    // 流控
    FLOW("flow"),
    // 熔断
    DEGRADE("degrade"),
    // 热点
    PARAM_FLOW("paramFlow"),
    // 系统
    SYSTEM("system"),
    // 授权
    AUTH("authority");
    
    
    private String name;

    private RuleTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
