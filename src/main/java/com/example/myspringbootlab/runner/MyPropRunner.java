package com.example.myspringbootlab.runner;

import com.example.myspringbootlab.props.MyPropProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MyPropRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MyPropRunner.class);

    @Value("${myprop.username}")
    private String username;

    @Value("${myprop.port}")
    private int port;

    @Autowired
    private MyPropProperties myPropProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("MyPropProperties getUsername() : " + myPropProperties.getUsername());
//        System.out.println("MyPropProperties getPort() : " + myPropProperties.getPort());
//        System.out.println("@Value username : " + username);
//        System.out.println("@Value port : " + port);
        logger.info("username : {}", myPropProperties.getUsername());
        logger.info("port : {}", myPropProperties.getPort());

        logger.debug("@Value username : {}", username);
        logger.debug("@Value port : {}", port);

    }
}
