package com.adaptris.core.cassandra.params;

import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cached-statement-primer")
public class CachedStatementPrimer implements StatementPrimer {

  private transient List<PrimedStatement> statements;
  
  private int cacheLimit;
  
  public CachedStatementPrimer() {
    this.setStatements(new ArrayList<PrimedStatement>());
    this.setCacheLimit(50);
  }
  
  @Override
  public PreparedStatement prepareStatement(Session session, String statement) throws Exception {
    int statementIndex = this.getStatements().indexOf(new PrimedStatement(statement, null));
    if(statementIndex > -1)
      return this.getStatements().get(statementIndex).getPreparedStatement();

    PreparedStatement preparedStatement = session.prepare(statement);
    
    PrimedStatement primedStatement = new PrimedStatement(statement, preparedStatement);
    if(statements.size() >= this.getCacheLimit())
      this.getStatements().remove(0);
    this.getStatements().add(primedStatement);
    
    return preparedStatement;
  }

  public List<PrimedStatement> getStatements() {
    return statements;
  }

  public void setStatements(List<PrimedStatement> statements) {
    this.statements = statements;
  }

  public int getCacheLimit() {
    return cacheLimit;
  }

  public void setCacheLimit(int cacheLimit) {
    this.cacheLimit = cacheLimit;
  }

}
