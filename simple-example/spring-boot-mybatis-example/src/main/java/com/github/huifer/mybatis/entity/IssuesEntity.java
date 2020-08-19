package com.github.huifer.mybatis.entity;


import com.github.huifer.crud.common.annotation.CacheKey;
import com.github.huifer.crud.common.intefaces.BaseEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@CacheKey(key = "tt", type = IssuesEntity.class, idFiled = "newTitle")
public class IssuesEntity implements BaseEntity {


  private Integer id;
  private String newTitle;

  private String ooo() {
    return "OOO" + this.newTitle;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNewTitle() {
    return newTitle;
  }

  public void setNewTitle(String newTitle) {
    this.newTitle = newTitle;
  }
}