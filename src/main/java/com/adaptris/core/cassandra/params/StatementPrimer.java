package com.adaptris.core.cassandra.params;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public interface StatementPrimer {
  
  public PreparedStatement prepareStatement(Session session, String statement) throws Exception;

}
