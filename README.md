# dubbo-zikpin


项目中引入监控

   <bean id="enableTraceAutoConfiguration" class="com.yuan.dubbo.core.trace.config.EnableTraceAutoConfiguration">
        <property name="traceConfig" ref="traceConfig"></property>
    </bean>

    <bean id="traceConfig" class="com.yuan.dubbo.core.trace.config.TraceConfig">
        <property name="zipkinUrl" value="http://127.0.0.1:9411"></property>
        <property name="serverPort" value="8080"></property>
        <property name="applicationName" value="dubbo_client"></property>
    </bean>