package org.huifer.crud.runner;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.huifer.crud.annotation.CacheKey;
import org.huifer.crud.interfaces.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MapperRunner implements CommandLineRunner, Ordered {

  public static Map<Class, A> om = new HashMap<>();
  public static Map<Class, MapperAndCacheInfo> mapperAndCacheInfoMap = new HashMap<>();
  @Autowired
  private ApplicationContext context;
  @Autowired
  private SqlSession sqlSession;

  public static A getA(Class a) {
    return om.get(a);
  }

  public static MapperAndCacheInfo getMapperAndCacheInfo(Class clazz) {
    return mapperAndCacheInfoMap.get(clazz);
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  @Override
  public void run(String... args) throws Exception {
    this.hh();
  }

  public void hh() {
    Configuration configuration = sqlSession.getConfiguration();
    MapperRegistry mapperRegistry = configuration.getMapperRegistry();
    Collection<Class<?>> mappers = mapperRegistry.getMappers();

    for (Class<?> mapper : mappers) {
      if (mapper.isInterface()) {

        // 获取注解信息
        CacheKey annotation = mapper.getAnnotation(CacheKey.class);

        Type[] genericInterfaces = mapper.getGenericInterfaces();

        if (genericInterfaces.length > 0) {

          Class<?> clazz = null;
          String key = null;
          A a = null;
          Class<?> mapperClazz = mapper;
          boolean isPlus = false;

          for (Type genericInterface : genericInterfaces) {
            ParameterizedType pt = (ParameterizedType) genericInterface;
            Class rawType = (Class) pt.getRawType();

            if (rawType.equals(BaseMapper.class)) {
              isPlus = true;
            }

            if (rawType.equals(A.class)) {
              Type[] r = pt.getActualTypeArguments();
              if (r.length == 2) {

                // 获取接口泛型
                Class id = (Class) r[0];
                Class type = (Class) r[1];
                clazz = type;
                Object mapper1 = sqlSession.getMapper(mapper);
                a = (A) mapper1;
                om.put(type, (A) mapper1);

                if (annotation != null) {
                  // 如果由缓存注解
                  String keyCk = annotation.key();

                  Class<?> typeC = annotation.type();
                  if (type.equals(typeC)) {
                    if (!StringUtils.isEmpty(keyCk)) {
                      key = keyCk;


                    }
                    else {
                      throw new RuntimeException("缓存key不能为空, class+ " + mapper);
                    }
                  }
                  else {
                    throw new RuntimeException("缓存注解类型和mapper类型不匹配，class = " + mapper);
                  }
                }
              }
            }

          }

          MapperAndCacheInfo mapperAndCacheInfo = new MapperAndCacheInfo();
          mapperAndCacheInfo.setClazz(clazz);
          mapperAndCacheInfo.setKey(key);
          mapperAndCacheInfo.setA(a);
          mapperAndCacheInfo.setMapperClazz(mapperClazz);
          mapperAndCacheInfo.setPlus(isPlus);

          putData(mapperAndCacheInfo);

        }
      }
    }
  }

  private void putData(MapperAndCacheInfo mapperAndCacheInfo) {
    MapperAndCacheInfo cache = mapperAndCacheInfoMap
        .get(mapperAndCacheInfo.getClazz());

    if (cache != null) {
      throw new RuntimeException("存在相同类型的" + mapperAndCacheInfo.getMapperClazz());
    }

    mapperAndCacheInfoMap.forEach(
        (k, v) -> {
          String key = v.getKey();
          if (key.equals(mapperAndCacheInfo.getKey())) {
            throw new RuntimeException(
                "存在相同的缓存键值 " + v.getMapperClazz() + "\t" + mapperAndCacheInfo.getMapperClazz());
          }
        }

    );

    mapperAndCacheInfoMap.put(mapperAndCacheInfo.getClazz(), mapperAndCacheInfo);

  }


}