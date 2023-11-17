package com.adaptris.core.cassandra.params;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

public abstract class AbstractCassandraParameterApplicator implements CassandraParameterApplicator {

  private transient StatementPrimer statementPrimer;

  public AbstractCassandraParameterApplicator() {
    setStatementPrimer(new NullStatementPrimer());
  }

  protected PreparedStatement prepareStatement(CqlSession session, String statement) throws Exception {
    return getStatementPrimer().prepareStatement(session, statement);
  }

  public StatementPrimer getStatementPrimer() {
    return statementPrimer;
  }

  @Override
  public void setStatementPrimer(StatementPrimer statementPrimer) {
    this.statementPrimer = statementPrimer;
  }

}
