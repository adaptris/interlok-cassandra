package com.adaptris.core.cassandra.params;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ServiceException;
import com.adaptris.core.services.jdbc.StatementParameterCollection;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;

/**
 * <p>
 * Implementations of this class define how parameter values will be sourced for Cassandra services.
 * </p>
 *
 * @author amcgrath
 *
 */
public interface CassandraParameterApplicator {

  public BoundStatement applyParameters(CqlSession session, AdaptrisMessage message, StatementParameterCollection parameters, String statement) throws ServiceException;

  public void setStatementPrimer(StatementPrimer statementPrimer);

}
