package com.yuan.dubbo.core.trace.config;

import com.yuan.dubbo.core.context.TraceContext;

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
