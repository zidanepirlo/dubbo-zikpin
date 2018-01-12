package com.yuan.dubbo.core.trace.config;

import com.yuan.dubbo.core.context.TraceContext;

/*
* 日志追踪自动配置开关
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
public class EnableTraceAutoConfiguration {

    private TraceConfig traceConfig;

    public TraceConfig getTraceConfig() {
        return traceConfig;
    }

    public void setTraceConfig(TraceConfig traceConfig) {
        this.traceConfig = traceConfig;
        TraceContext.init(this.traceConfig);
    }
}
