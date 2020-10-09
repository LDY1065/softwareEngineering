package com.gdutinformationsafety.fouroperations.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "myfile")
public class testFileProperties {
    private String testDir;
    private String zipDir;
    private String compareDir;
}
