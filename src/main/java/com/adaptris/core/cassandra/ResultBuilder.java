package com.adaptris.core.cassandra;

import com.adaptris.jdbc.JdbcResult;
import com.adaptris.jdbc.JdbcResultSet;
import com.datastax.driver.core.ResultSet;

public class ResultBuilder {

  private JdbcResult result;

  public ResultBuilder() {
    result = new JdbcResult();
  }

  public ResultBuilder setHasResultSet(boolean hasResultSet) {
    result.setHasResultSet(hasResultSet);
    return this;
  }

  public ResultBuilder setResultSet(ResultSet resultSet) {
    result.addResultSet(mapResultSet(resultSet));
    return this;
  }

  public ResultBuilder setRowsUpdatedCount(int count) {
    result.setNumRowsUpdated(count);
    return this;
  }

  public JdbcResult build() {
    return result;
  }

  private JdbcResultSet mapResultSet(ResultSet resultSet) {
    JdbcResultSet resultReturned = new JdbcCassandraResultSet(resultSet);
    result.setHasResultSet(true);

    return resultReturned;
  }

}
