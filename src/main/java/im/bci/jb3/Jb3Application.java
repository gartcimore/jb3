package im.bci.jb3;

import java.util.concurrent.Executor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

    @Bean(name = "botExecutor")
    public Executor botExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(1);
        return executor;
    }

    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(4);
        return executor;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Jb3Application.class, args);
    }
}
