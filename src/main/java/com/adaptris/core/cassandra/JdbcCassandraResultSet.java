package com.adaptris.core.cassandra;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.adaptris.jdbc.JdbcResultRow;
import com.adaptris.jdbc.JdbcResultSet;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.ColumnDefinitions.Definition;

public class JdbcCassandraResultSet implements JdbcResultSet {
  
  @SuppressWarnings("unused")
  private ResultSet resultSet;
  private Iterator<Row> iterator;
  
  public JdbcCassandraResultSet(ResultSet resultSet) {
    this.resultSet = resultSet;
    this.iterator = resultSet.iterator();
  }

  @Override
  public void close() throws IOException {
    resultSet = null;
  }

  @Override
  public Iterable<JdbcResultRow> getRows() {
    return new Iterable<JdbcResultRow>() {

      @Override
      public Iterator<JdbcResultRow> iterator() {
        return new Iterator<JdbcResultRow>() {
          
          @Override
          public JdbcResultRow next() {
            Row nextRow = iterator.next();
            JdbcResultRow result = new JdbcResultRow();
            
            ColumnDefinitions columnDefinitions = nextRow.getColumnDefinitions();
            List<Definition> asList = columnDefinitions.asList();
            for(Definition definition : asList) 
              result.setFieldValue(definition.getName(), nextRow.getObject(definition.getName()));
            
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

    };
  }

}
