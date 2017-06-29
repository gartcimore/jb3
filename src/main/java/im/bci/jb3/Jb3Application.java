package im.bci.jb3;

import java.util.concurrent.Executor;
import okhttp3.OkHttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

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

    @Bean(name = "mouleExecutor")
    public Executor botExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(1);
        return executor;
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Component
    public static class ThreadScopeRegister implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            beanFactory.registerScope("thread", new SimpleThreadScope());
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Jb3Application.class, args);
    }
}
