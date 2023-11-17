package com.adaptris.core.cassandra.params;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ServiceException;
import com.adaptris.core.services.jdbc.StatementParameterCollection;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * This parameter applicator will not apply any parameter values. It is typically used where no parameters are required for CQL statements.
 * </p>
 *
 * @author amcgrath
 * @config cassandra-null-parameter-applicator
 *
 */
@XStreamAlias("cassandra-null-parameter-applicator")
@AdapterComponent
public class NullParameterApplicator extends AbstractCassandraParameterApplicator {

  @Override
  public BoundStatement applyParameters(CqlSession session, AdaptrisMessage message, StatementParameterCollection parameters, String statement) throws ServiceException {
    PreparedStatement preparedStatement;
    try {
      preparedStatement = prepareStatement(session, statement);
    } catch (Exception e) {
      throw new ServiceException(e);
    }
    return preparedStatement.bind(parameters.toArray());
  }

}
