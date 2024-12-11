package com.cej.nc.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.cej.nc.commons.EsClient;
import com.cej.nc.config.property.EsProperties;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {

    @Bean
    public EsClient getElasticsearchClient(EsProperties esProperties) {
        //创建rest 客户端
        RestClient restClient = RestClient.builder(
                        new HttpHost(esProperties.getHost(),esProperties.getPort(),"http"))
                .build();
        //创建传输层
        RestClientTransport restClientTransport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );
        //创建client客户端
        return new EsClient(restClientTransport);
    }
}
