package com.adaptris.core.cassandra.params;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.datastax.driver.core.PreparedStatement;

public class PrimedStatement {
  
  private String stringStatement;
  
  private PreparedStatement preparedStatement;
  
  public PrimedStatement(String stringStatement, PreparedStatement preparedStatement) {
    setStringStatement(stringStatement);
    setPreparedStatement(preparedStatement);
  }

  @Override
  public boolean equals(Object object) {
    if(object instanceof PrimedStatement) {
      PrimedStatement other = (PrimedStatement) object;
      if(other.getStringStatement() != null) {
        return other.getStringStatement().equals(getStringStatement());
      }
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder(23, 49).append(getStringStatement()).hashCode();
  }
  
  public String getStringStatement() {
    return stringStatement;
  }

  public void setStringStatement(String stringStatement) {
    this.stringStatement = stringStatement;
  }

  public PreparedStatement getPreparedStatement() {
    return preparedStatement;
  }

  public void setPreparedStatement(PreparedStatement preparedStatement) {
    this.preparedStatement = preparedStatement;
  }

}
