package com.cej.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cej.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
