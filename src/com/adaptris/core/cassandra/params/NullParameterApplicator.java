package com.adaptris.core.cassandra.params;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.services.jdbc.StatementParameterCollection;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * This parameter applicator will not apply any parameter values.  It is typically used where no parameters are required for CQL statements.
 * </p>
 * 
 * @author amcgrath
 *
 */
@XStreamAlias("cassandra-null-parameter-applicator")
public class NullParameterApplicator implements CassandraParameterApplicator {

  public NullParameterApplicator() {
  }
  
  @Override
  public BoundStatement applyParameters(Session session, AdaptrisMessage message, StatementParameterCollection parameters, String statement) {
    PreparedStatement preparedStatement = session.prepare(statement); 
    return new BoundStatement(preparedStatement);
  }

}
