package com.github.huifer.crud.service.factory;

import com.github.huifer.crud.daotype.DaoType;
import com.github.huifer.crud.interfaces.CrudTemplate;
import com.github.huifer.crud.interfaces.DaoTypeLabel;
import com.github.huifer.crud.interfaces.operation.DbOperation;
import com.github.huifer.crud.interfaces.operation.RedisOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("operationFactoryImpl")
public class OperationFactoryImpl implements OperationFactory {


  @Autowired
  @Qualifier("crudHashTemplateForRedis")
  private RedisOperation redisOperation;

  @Autowired
  private ApplicationContext context;


  @Override
  public OperationCollection factory(DaoType daoType) {
    OperationCollection operationCollection = new OperationCollection();

    operationCollection.setRedisOperation(redisOperation);

    Map<String, DbOperation> beansOfType = context.getBeansOfType(DbOperation.class);
    CrudTemplate dbOperation = null;
    for (Map.Entry<String, DbOperation> entry : beansOfType.entrySet()) {
      DbOperation v = entry.getValue();
      boolean equals = v.DAO_TYPE().equals(daoType);
      if (equals) {
        dbOperation = (CrudTemplate) v;
        break;
      }
    }

    operationCollection.setDbOperation(dbOperation);

    return operationCollection;
  }
}