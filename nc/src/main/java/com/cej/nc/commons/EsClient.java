package com.cej.nc.commons;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;

import java.io.IOException;

public class EsClient extends ElasticsearchClient{

    private ElasticsearchTransport transport;

    public EsClient(ElasticsearchTransport transport) {
        super(transport);
        this.transport = transport;
    }

    public void closeTransport() throws IOException {
        transport.close();
    }
}
