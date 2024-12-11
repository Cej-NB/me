package run;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import run.entity.Seed;
import run.mapper.SeedMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class Mybatis {

    public static void main(String[] args) throws IOException {
        test();
    }


    public static void test() throws IOException {
        InputStream in = Resources.getResourceAsStream("mybatis-config.xml");

        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();

        SqlSessionFactory factory = builder.build(in);

        SqlSession session = factory.openSession();

        //清除一级缓存
        //session.clearCache();

        SeedMapper mapper = session.getMapper(SeedMapper.class);

        List<Seed> list = mapper.query();

        for (Seed seed : list) {
            System.out.println(seed);
        }

        log.info("123");
    }
}
