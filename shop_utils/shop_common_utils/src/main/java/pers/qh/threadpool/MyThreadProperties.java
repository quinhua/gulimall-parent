package pers.qh.threadpool;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thread.pool")
//public class MyThreadProperties implements InitializingBean {
public class MyThreadProperties {
    private int corePoolSize=16;
    private int maximumPoolSize=32;
    private long keepAliveTime=50;
    private int queueLength=100;

//    public static int CORE_POOL_SIZE;
//    public static int MAX_NUM_POOL_SIZE;
//    public static long KEEP_ALIVE_TIME;
//    public static int QUEUE_LENGTH;
//
//    @Override
//    public void afterPropertiesSet() {
//        CORE_POOL_SIZE=corePoolSize;
//        MAX_NUM_POOL_SIZE=maximumPoolSize;
//        KEEP_ALIVE_TIME=keepAliveTime;
//        QUEUE_LENGTH=queueLength;
//    }
}
