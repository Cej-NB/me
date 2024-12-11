package run.mapper;

import org.apache.ibatis.annotations.Mapper;
import run.entity.Seed;

import java.util.List;

@Mapper
public interface SeedMapper {
    List<Seed> query();
}
