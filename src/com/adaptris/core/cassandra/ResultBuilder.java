package com.adaptris.core.cassandra;

import java.util.List;

import com.adaptris.jdbc.JdbcResult;
import com.adaptris.jdbc.JdbcResultRow;
import com.adaptris.jdbc.JdbcResultSet;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

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
    result.addResultSet(this.mapResultSet(resultSet));
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
    JdbcResultSet resultReturned = new JdbcResultSet();
    result.setHasResultSet(true);
    
    for (Row resultRow : resultSet) {
      JdbcResultRow row = new JdbcResultRow();
      
      ColumnDefinitions columnDefinitions = resultRow.getColumnDefinitions();
      List<Definition> asList = columnDefinitions.asList();
      for(Definition definition : asList) 
        row.setFieldValue(definition.getName(), resultRow.getObject(definition.getName()));
      
      resultReturned.addRow(row);
    }

    return resultReturned;
  }

}
