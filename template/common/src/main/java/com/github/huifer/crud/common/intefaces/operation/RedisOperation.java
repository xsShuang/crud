/*
 *
 * Copyright 2020-2020 HuiFer All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.huifer.crud.common.intefaces.operation;


import com.github.huifer.crud.common.intefaces.id.IdInterface;

public interface RedisOperation {

  <T> void insert(T t, IdInterface id);


  <T> void update(IdInterface id, T t);

  void del(IdInterface id);

  <T> T byId(IdInterface id);

  Class<?> type();

  void setClass(Class<?> clazz);

  String key();
}
