package com.cej.nc.service;

import com.cej.base.commons.vo.ResResult;
import com.cej.nc.entity.Seed;

public interface SeedService {

    ResResult query(Seed seed);

    ResResult saveSeed(Seed seed);

    ResResult delete(Seed seed);

    ResResult getAll();
}
