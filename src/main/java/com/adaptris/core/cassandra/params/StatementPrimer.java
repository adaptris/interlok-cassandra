package com.adaptris.core.cassandra.params;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

public interface StatementPrimer {

  public PreparedStatement prepareStatement(CqlSession session, String statement) throws Exception;

}
