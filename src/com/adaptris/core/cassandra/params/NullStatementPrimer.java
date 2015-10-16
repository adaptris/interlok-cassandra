package com.adaptris.core.cassandra.params;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * This {@link StatementPrimer} will simply use your current session object to Cassandra to prepare
 * each statement upon every execution.
 * </p>
 * <p>
 * Consider using the {@link CachedStatementPrimer} if performance is an issue.
 * </p>
 * 
 * @author Aaron
 * @config null-statement-primer
 */
@XStreamAlias("null-statement-primer")
public class NullStatementPrimer implements StatementPrimer{

  @Override
  public PreparedStatement prepareStatement(Session session, String statement) throws Exception {
    return session.prepare(statement);
  }

}
