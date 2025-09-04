package org.example.swaggermultiapplication.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "org.example.moduleA.controller",
                "org.example.moduleB.controller",
                "org.example.moduleC.controller",
                "org.example.swaggermultiapplication.common"
        }
)
public class SwaggerMultiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SwaggerMultiApplication.class, args);
    }
}
