package org.unidue.ub.libintel.almaconnector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class AlmaConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlmaConnectorApplication.class, args);
    }

}
