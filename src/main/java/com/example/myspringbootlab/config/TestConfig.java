package com.example.myspringbootlab.config;

import com.example.myspringbootlab.env.MyEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig {
    @Bean
    public MyEnvironment myEnvironment(){
        return myEnvironment().builder()
                .mode("테스트환경")
                .build();
    }
}
