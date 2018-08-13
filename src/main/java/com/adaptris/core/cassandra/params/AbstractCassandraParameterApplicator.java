package com.adaptris.core.cassandra.params;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public abstract class AbstractCassandraParameterApplicator implements CassandraParameterApplicator {

  private transient StatementPrimer statementPrimer;
  
  public AbstractCassandraParameterApplicator() {
    this.setStatementPrimer(new NullStatementPrimer());
  }
  
  protected PreparedStatement prepareStatement(Session session, String statement) throws Exception {
    return this.getStatementPrimer().prepareStatement(session, statement);
  }

  public StatementPrimer getStatementPrimer() {
    return statementPrimer;
  }

  public void setStatementPrimer(StatementPrimer statementPrimer) {
    this.statementPrimer = statementPrimer;
  }
  
}
