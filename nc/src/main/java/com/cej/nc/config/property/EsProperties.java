package com.cej.nc.config.property;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class EsProperties {

    @Value("${es.host}")
    public String host;
    @Value("${es.port}")
    public Integer port;
}
