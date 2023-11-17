package com.adaptris.core.cassandra;

import java.io.IOException;
import java.util.Iterator;

import com.adaptris.jdbc.JdbcResultRow;
import com.adaptris.jdbc.JdbcResultSet;
import com.adaptris.jdbc.ParameterValueType;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class JdbcCassandraResultSet implements JdbcResultSet {

  @SuppressWarnings("unused")
  private ResultSet resultSet;
  private Iterator<Row> iterator;

  public JdbcCassandraResultSet(ResultSet resultSet) {
    this.resultSet = resultSet;
    iterator = resultSet.iterator();
  }

  @Override
  public void close() throws IOException {
    resultSet = null;
  }

  @Override
  public Iterable<JdbcResultRow> getRows() {
    return () -> new Iterator<>() {

      @Override
      public JdbcResultRow next() {
        Row nextRow = iterator.next();
        JdbcResultRow result = new JdbcResultRow();

        ColumnDefinitions columnDefinitions = nextRow.getColumnDefinitions();
        for (ColumnDefinition definition : columnDefinitions) {
          result.setFieldValue(definition.getName().asInternal(), nextRow.getObject(definition.getName()), (ParameterValueType) null);
        }

        return result;
      }

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public void remove() {
        iterator.remove();
      }
    };
  }

}
