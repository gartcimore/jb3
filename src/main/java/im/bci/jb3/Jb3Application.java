package im.bci.jb3;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
@EnableAutoConfiguration
@EnableAsync
@ComponentScan
@EnableSpringDataWebSupport
@EnableScheduling
public class Jb3Application implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("jb3 or not to be!");
    }
    
    @Bean(name = "webdirectcoinExecutor")
    public ScheduledExecutorService webdirectcoinExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean(name = "mouleExecutor")
    public ScheduledExecutorService mouleExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean(name = "mouleScheduler")
    public TaskScheduler mouleScheduler(@Qualifier("mouleExecutor") ScheduledExecutorService executor) {
        return new ConcurrentTaskScheduler(executor);
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();

    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Jb3Application.class,
                args);
    }
}
