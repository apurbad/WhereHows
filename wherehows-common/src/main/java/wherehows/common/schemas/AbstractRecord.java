/**
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package wherehows.common.schemas;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wherehows.common.utils.StringUtil;


/**
 * Created by zechen on 9/16/15.
 */
public abstract class AbstractRecord implements Record {
  char SEPR = 0x001A;

  @Override
  public String toCsvString() {
    List<Object> allFields = fillAllFields();
    StringBuilder sb = new StringBuilder();
    for (Object o : allFields) {
      sb.append(o);
      sb.append(SEPR);
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  @Override
  public String toDatabaseValue() {
    List<Object> allFields = fillAllFields();
    StringBuilder sb = new StringBuilder();
    for (Object o : allFields) {
      sb.append(StringUtil.toDbString(o));
      sb.append(",");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  public abstract List<Object> fillAllFields();

  /**
   * return all declared fields of the class, exclude inherited fields
   * @return Field[]
   */
  public Field[] getAllFields() {
    return this.getClass().getDeclaredFields();
  }

  /**
   * return values of all declared fields as Object[]
   * @return Object[]
   * @throws IllegalAccessException
   */
  public Object[] getAllValues()
      throws IllegalAccessException {
    final Field[] fields = this.getAllFields();
    final Object[] values = new Object[fields.length];
    for (int i = 0; i < fields.length; i++) {
      fields[i].setAccessible(true);
      values[i] = fields[i].get(this);
    }
    return values;
  }

  /**
   * return the corresponding database column names to the class fields
   * @return
   */
  public String[] getDbColumnNames() {
    return null;
  }

  /**
   * return values of declared fields, and transform record, collection, map and array into string
   * Primitive data types and other structures are unchanged.
   * @return Object[]
   * @throws IllegalAccessException
   */
  public Object[] getAllValuesToString()
      throws IllegalAccessException {
    final Object[] values = getAllValues();
    for (int i = 0; i < values.length; i++) {
      values[i] = StringUtil.objectToString(values[i]);
    }
    return values;
  }

  /**
   * get the field-value map of the record
   * use db column name as field name, if not available, use class field names
   * @return Map: String-Object
   * @throws IllegalAccessException
   */
  public Map<String, Object> getFieldValueMap()
      throws IllegalAccessException {
    String[] columns = this.getDbColumnNames();
    Object[] values = this.getAllValues();

    Map<String, Object> map = new HashMap<>();
    if (columns != null && columns.length == values.length) {
      for (int i = 0; i < columns.length; i++) {
        map.put(columns[i], values[i]);
      }
    } else {
      Field[] fields = this.getAllFields();
      for (int i = 0; i < fields.length; i++) {
        map.put(fields[i].getName(), values[i]);
      }
    }
    return map;
  }
}
