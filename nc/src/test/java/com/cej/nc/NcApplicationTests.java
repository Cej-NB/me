package com.cej.nc;

import com.cej.nc.entity.Seed;
import com.cej.nc.service.es.EsBaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NcApplicationTests {

    @Autowired
    private EsBaseService esBaseService;

    @Test
    void contextLoads() {
        Seed seed = Seed.builder()
                .sName("测试测试测试")
                .build();
        esBaseService.add(seed,"2");
    }

}
