package com.cej.nc.service.es.impl;

import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.cej.nc.commons.EsClient;
import com.cej.nc.entity.Seed;
import com.cej.nc.service.es.EsBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class EsBaseServiceImpl implements EsBaseService {

    @Autowired
    private EsClient esClient;

    @Override
    public void add(Seed seed,String id) {
        try {
            IndexResponse response = esClient.index(s -> s.index("nc").id(id).document(seed));
            esClient.closeTransport();
        }catch (Exception e){
            log.info(Arrays.toString(e.getStackTrace()));
        }

    }

    @Override
    public void delete() {

    }

    @Override
    public void update() {

    }
}
