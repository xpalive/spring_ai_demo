package com.xpalive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import java.util.concurrent.*;

@Configuration
public class SchedulerConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService ollamaExecutor() {
        int corePoolSize = 6;
        int queueCapacity = 4;
        return new ThreadPoolExecutor(
                corePoolSize,
                corePoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadFactory() {
                    private int idx = 0;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("ollama-block-" + (++idx));
                        thread.setDaemon(false);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * 专门给 ollama 阻塞调用使用的调度器
     * 适合 WebFlux 环境，隔离阻塞任务，不污染全局 boundedElastic
     */
    @Bean(destroyMethod = "dispose")
    public Scheduler ollamaScheduler(ExecutorService ollamaExecutor) {
        return Schedulers.fromExecutorService(ollamaExecutor);
    }
}
