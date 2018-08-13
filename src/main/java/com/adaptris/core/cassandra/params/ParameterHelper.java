package com.adaptris.core.cassandra.params;

import java.lang.reflect.Constructor;

import com.adaptris.core.ServiceException;

public class ParameterHelper {
  
  /**
   * Convert the given string to the corresponding query class.
   *
   * @param value the string obtained.
   * @return an Object suitable for use in the service.
   * @throws ServiceException on error.
   */
  public static Object convertToQueryClass(Object value, String queryClass) throws ServiceException {
    if (value == null) {
      return "";
    }
    else {
      if(value instanceof String) {
        value = (String) value;
        try {
          Class<?> clazz = Class.forName(queryClass);
          Object obj = null;
  
          Constructor<?> construct = clazz.getConstructor(new Class[]
          {
            String.class
          });
          obj = construct.newInstance(new Object[]
          {
            value
          });
  
          return obj;
        }
        catch (Exception e) {
          throw new ServiceException("Failed to convert input String [" + value
              + "] to type [" + queryClass + "]", e);
        }
      } else
        return value; // assume we already have the correct type, no conversion needed
    }
  }

}
