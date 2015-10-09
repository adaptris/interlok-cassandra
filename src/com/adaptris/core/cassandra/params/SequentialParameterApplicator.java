package com.adaptris.core.cassandra.params;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ServiceException;
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
 * 
 */
@XStreamAlias("cassandra-sequential-parameter-applicator")
public class SequentialParameterApplicator implements CassandraParameterApplicator {
  
  public SequentialParameterApplicator() {  
  }

  @Override
  public BoundStatement applyParameters(Session session, AdaptrisMessage message, StatementParameterCollection parameters, String statement) throws ServiceException {
    Object[] parameterArray = new Object[parameters.size()];
    int counter = 0;
    for(StatementParameter sParam : parameters) {
      parameterArray[counter] = sParam.convertToQueryClass(sParam.getQueryValue(message));
      counter ++;
    }
    
    PreparedStatement preparedStatement = session.prepare(statement); 
    BoundStatement boundStatement = new BoundStatement(preparedStatement);
    
    return boundStatement.bind(parameterArray);
  }
  
}
