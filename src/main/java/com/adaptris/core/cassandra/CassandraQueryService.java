package com.adaptris.core.cassandra;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ConnectedService;
import com.adaptris.core.cassandra.params.CachedStatementPrimer;
import com.adaptris.core.cassandra.params.NullParameterApplicator;
import com.adaptris.core.cassandra.params.NullStatementPrimer;
import com.adaptris.core.cassandra.params.StatementPrimer;
import com.adaptris.core.services.jdbc.ResultSetTranslator;
import com.adaptris.core.services.jdbc.StatementParameterList;
import com.adaptris.core.services.jdbc.XmlPayloadTranslator;
import com.adaptris.interlok.config.DataInputParameter;
import com.adaptris.jdbc.JdbcResult;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * Built for Cassandra version 3.0+
 * </p>
 * <p>
 * This service allows us to fire CQL (Cassandra Query Language) queries at a Cassandra cluster, the results of which can be stored
 * into the {@link AdaptrisMessage}.
 * </p>
 * <p>
 * Specify the source of the CQL statement by configuring a {@link DataInputParameter}. Note that the CQL statement can contain
 * parameters in one of 2 forms; the standard SQL form, using the character "?", or you can use named parameters. <br/>
 * If you configure any parameters, using the standard SQL form, then you will need to configure a
 * {@link com.adaptris.core.cassandra.params.SequentialParameterApplicator}, or should you wish to name your parameters for ease of
 * configuration, especially when statements contain many parameters, then you will need to configure a
 * {@link com.adaptris.core.cassandra.params.NamedParameterApplicator}.
 * </p>
 * <p>
 * To configure the values of the parameters configure a {@link StatementParameterList}.
 * </p>
 * <p>
 * You may also configure a {@link StatementPrimer}. Statement Primers are used to prepare a CQL statement before it is executed.
 * <br/>
 * Especially useful may be the {@link CachedStatementPrimer}. The default value for this service is the
 * {@link NullStatementPrimer}.
 * </p>
 * <p>
 * Finally the results of the query can be stored in the {@link AdaptrisMessage}, the format and location of which can be configured
 * using {@link ResultSetTranslator}.
 * </p>
 *
 * @author amcgrath
 * @config cassandra-query-service
 */
@XStreamAlias("cassandra-query-service")
@AdapterComponent
@ComponentProfile(summary = "Execute CQL to query data from the databases tables", recommended = {
    CassandraConnection.class }, tag = "cassandra")
@DisplayOrder(order = { "connection", "statement" })
public class CassandraQueryService extends CassandraServiceImp implements ConnectedService {

  @NotNull
  @AutoPopulated
  @Valid
  private ResultSetTranslator resultSetTranslator;

  public CassandraQueryService() {
    setParameterApplicator(new NullParameterApplicator());
    setParameterList(new StatementParameterList());
    setResultSetTranslator(new XmlPayloadTranslator());
    setStatementPrimer(new NullStatementPrimer());
  }

  @Override
  public void doCassandraService(Session session, AdaptrisMessage message) throws Exception {
    BoundStatement boundStatement = getParameterApplicator().applyParameters(session, message, getParameterList(), getStatement().extract(message));
    ResultSet results = session.execute(boundStatement);

    JdbcResult result = new ResultBuilder().setHasResultSet(true).setResultSet(results).build();
    resultSetTranslator.translate(result, message);
  }

  public ResultSetTranslator getResultSetTranslator() {
    return resultSetTranslator;
  }

  public void setResultSetTranslator(ResultSetTranslator resultSetTranslator) {
    this.resultSetTranslator = resultSetTranslator;
  }

}
