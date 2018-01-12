package com.yuan.dubbo.core.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.google.common.base.Stopwatch;
import com.twitter.zipkin.gen.Annotation;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;
import com.yuan.dubbo.core.context.TraceContext;
import com.yuan.dubbo.core.trace.TraceAgent;
import com.yuan.dubbo.core.utils.IdUtils;
import com.yuan.dubbo.core.utils.NetworkUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


@Activate(group = Constants.PROVIDER)
public class TraceProviderFilter implements Filter {

    private static final Logger logger = Logger.getLogger(TraceProviderFilter.class.getName());

    private Span startTrace(Map<String, String> attaches) {

        Long traceId = Long.valueOf(attaches.get(TraceContext.TRACE_ID_KEY));
        Long parentSpanId = Long.valueOf(attaches.get(TraceContext.SPAN_ID_KEY));

        TraceContext.start();
        TraceContext.setTraceId(traceId);
        TraceContext.setSpanId(parentSpanId);

        Span providerSpan = new Span();

        long id = IdUtils.get();
        providerSpan.setId(id);
        providerSpan.setParent_id(parentSpanId);
        providerSpan.setTrace_id(traceId);
        providerSpan.setName(TraceContext.getTraceConfig().getApplicationName());
        long timestamp = System.currentTimeMillis()*1000;
        providerSpan.setTimestamp(timestamp);

        providerSpan.addToAnnotations(
                Annotation.create(timestamp, TraceContext.ANNO_SR,
                        Endpoint.create(
                                TraceContext.getTraceConfig().getApplicationName(),
                                NetworkUtils.ip2Num(NetworkUtils.getSiteIp()),
                                TraceContext.getTraceConfig().getServerPort() )));

        TraceContext.addSpan(providerSpan);
        return providerSpan;
    }

    private void endTrace(Span span, Stopwatch watch) {

        span.addToAnnotations(
                Annotation.create(System.currentTimeMillis()*1000, TraceContext.ANNO_SS,
                        Endpoint.create(
                                span.getName(),
                                NetworkUtils.ip2Num(NetworkUtils.getSiteIp()),
                                TraceContext.getTraceConfig().getServerPort())));

        span.setDuration(watch.stop().elapsed(TimeUnit.MICROSECONDS));
        TraceAgent traceAgent=new TraceAgent(TraceContext.getTraceConfig().getZipkinUrl());

        traceAgent.send(TraceContext.getSpans());

    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if(!TraceContext.getTraceConfig().isEnabled()){
            return invoker.invoke(invocation);
        }

        Map<String, String> attaches = invocation.getAttachments();
        if (!attaches.containsKey(TraceContext.TRACE_ID_KEY)){
            return invoker.invoke(invocation);
        }
        Stopwatch watch = Stopwatch.createStarted();
        Span providerSpan= this.startTrace(attaches);

        Result result = invoker.invoke(invocation);
        this.endTrace(providerSpan,watch);

        return result;

    }
}
