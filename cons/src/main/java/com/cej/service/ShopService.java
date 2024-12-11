package com.cej.service;

import com.cej.commons.vo.ResResult;
import com.cej.entity.Shop;

public interface ShopService {

    ResResult queryById(String id);

    ResResult updateById(String id);

}
