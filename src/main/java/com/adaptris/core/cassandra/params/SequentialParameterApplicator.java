package com.adaptris.core.cassandra.params;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ServiceException;
import com.adaptris.core.services.jdbc.JdbcStatementParameter;
import com.adaptris.core.services.jdbc.StatementParameter;
import com.adaptris.core.services.jdbc.StatementParameterCollection;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * {@link CassandraParameterApplicator} implementation that applies parameters sequentially.
 *
 * <p>
 * This applies {@link StatementParameter} instances in the order that they are declared in adapter configuration and is the default
 * {@link CassandraParameterApplicator} implementation
 * </p>
 *
 * @config cassandra-sequential-parameter-applicator
 *
 */
@XStreamAlias("cassandra-sequential-parameter-applicator")
@AdapterComponent
@ComponentProfile(summary = "Helps to use sequencial parameters in CQL statements", tag = "cassandra")
public class SequentialParameterApplicator extends AbstractCassandraParameterApplicator {

  @Override
  public BoundStatement applyParameters(Session session, AdaptrisMessage message, StatementParameterCollection parameters, String statement) throws ServiceException {
    Object[] parameterArray = new Object[parameters.size()];
    int counter = 0;

    for(JdbcStatementParameter jdbcParam : parameters) {
      StatementParameter sParam = (StatementParameter) jdbcParam;
      parameterArray[counter] = ParameterHelper.convertToQueryClass(sParam.getQueryValue(message), sParam.getQueryClass());
      counter ++;
    }

    PreparedStatement preparedStatement;
    try {
      preparedStatement = prepareStatement(session, statement);
    } catch (Exception e) {
      throw new ServiceException(e);
    }
    BoundStatement boundStatement = new BoundStatement(preparedStatement);

    return boundStatement.bind(parameterArray);
  }

}
