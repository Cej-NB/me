package com.cej.nc.service;

import com.cej.base.commons.vo.ResResult;
import com.cej.nc.entity.Sort;

public interface SortService {

    ResResult saveSort(Sort sort);

    ResResult updateSort(Sort sort);
}
