package pers.qh.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(MyThreadProperties.class)
@Configuration
public class MyThreadPool {
    @Autowired
    private MyThreadProperties myThreadProperties;
    @Bean
    public ThreadPoolExecutor myPoolExecutor(){
        return new ThreadPoolExecutor(
                myThreadProperties.getCorePoolSize(),
                myThreadProperties.getMaximumPoolSize(),
                myThreadProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(myThreadProperties.getQueueLength()),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
