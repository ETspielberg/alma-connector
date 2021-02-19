package org.unidue.ub.libintel.almaconnector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AlmaConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlmaConnectorApplication.class, args);
    }

}
