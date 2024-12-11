package com.cej.nc;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

public class MainTest {
    public static void main(String[] args) throws IOException {

        //创建rest 客户端
        RestClient restClient = RestClient.builder(
                new HttpHost("172.18.241.67",9200,"http"))
                .build();
        //创建传输层
        RestClientTransport restClientTransport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );
        //创建client客户端
        ElasticsearchClient client = new ElasticsearchClient(restClientTransport);
        //创建文档
        Xdd xdd = Xdd.builder().sName("我叫饿").pid("2").pos("4").sType("4").build();
        //IndexResponse response = client.index(s ->s.index("nc").id("4").document(xdd));
        DeleteResponse response =client.delete(s -> s.index("nc").id("3"));

        restClientTransport.close();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Xdd {
        String sName;
        String pos;
        String sType;
        String pid;
        String star;
    }
}
