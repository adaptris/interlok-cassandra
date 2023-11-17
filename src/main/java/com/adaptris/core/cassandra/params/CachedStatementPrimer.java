package com.adaptris.core.cassandra.params;

import java.util.ArrayList;
import java.util.List;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * This {@link StatementPrimer} will cache a number of CQL statements, therefore not needing to prepare a statement that has already been
 * prepared.
 * </p>
 * <p>
 * Use this StatementPrimer when you know you will execute identical statements over and over.
 * </p>
 * <p>
 * Finally you can configure the number of cached statements (the default being 50) by configuring the "cache-limit";
 *
 * <pre>
 * {@code
 <cached-statement-primer>
   <cache-limit>50</cache-limit>
 </cached-statement-primer>
 * }
 * </pre>
 * </p>
 *
 * @author Aaron
 * @config cached-statement-primer
 *
 */
@XStreamAlias("cached-statement-primer")
@AdapterComponent
@ComponentProfile(summary = "Cache a number of CQL statements", tag = "cassandra,cache")
public class CachedStatementPrimer implements StatementPrimer {

  private transient List<PrimedStatement> statements;

  private int cacheLimit;

  public CachedStatementPrimer() {
    setStatements(new ArrayList<>());
    setCacheLimit(50);
  }

  @Override
  public PreparedStatement prepareStatement(CqlSession session, String statement) throws Exception {
    int statementIndex = getStatements().indexOf(new PrimedStatement(statement, null));
    if (statementIndex > -1) {
      return getStatements().get(statementIndex).getPreparedStatement();
    }

    PreparedStatement preparedStatement = session.prepare(statement);

    PrimedStatement primedStatement = new PrimedStatement(statement, preparedStatement);
    if (statements.size() >= getCacheLimit()) {
      getStatements().remove(0);
    }
    getStatements().add(primedStatement);

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

  /**
   * Set the max number of statements to keep in the cache
   *
   * @param cacheLimit
   */
  public void setCacheLimit(int cacheLimit) {
    this.cacheLimit = cacheLimit;
  }

}
