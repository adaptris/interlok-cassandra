package com.adaptris.core.cassandra.params;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("null-statement-primer")
public class NullStatementPrimer implements StatementPrimer{

  @Override
  public PreparedStatement prepareStatement(Session session, String statement) throws Exception {
    return session.prepare(statement);
  }

}
