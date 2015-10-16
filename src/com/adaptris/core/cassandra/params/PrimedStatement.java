package com.adaptris.core.cassandra.params;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.datastax.driver.core.PreparedStatement;

public class PrimedStatement {
  
  private String stringStatement;
  
  private PreparedStatement preparedStatement;
  
  public PrimedStatement() {
  }
  
  public PrimedStatement(String stringStatement, PreparedStatement preparedStatement) {
    this.setStringStatement(stringStatement);
    this.setPreparedStatement(preparedStatement);
  }

  public boolean equals(Object object) {
    if(object instanceof PrimedStatement) {
      PrimedStatement other = (PrimedStatement) object;
      if(other.getStringStatement() != null)
        return other.getStringStatement().equals(this.getStringStatement());
    }
    return false;
  }
  
  public int hashCode() {
    return new HashCodeBuilder(23, 49).append(this.getStringStatement()).hashCode();
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
