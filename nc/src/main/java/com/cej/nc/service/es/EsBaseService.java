package com.cej.nc.service.es;

import com.cej.nc.entity.Seed;

public interface EsBaseService {

    void add(Seed seed,String id);

    void delete();

    void update();
}
