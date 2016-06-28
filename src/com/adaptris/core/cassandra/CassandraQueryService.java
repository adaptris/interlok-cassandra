package com.adaptris.core.cassandra;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.adaptris.annotation.AutoPopulated;
import com.adaptris.core.AdaptrisConnection;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.ServiceException;
import com.adaptris.core.ServiceImp;
import com.adaptris.core.cassandra.params.CachedStatementPrimer;
import com.adaptris.core.cassandra.params.CassandraParameterApplicator;
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
 * This service allows us to fire CQL (Cassandra Query Language) queries at a Cassandra cluster, the results of which can be stored into the 
 * {@Link AdaptrisMessage}.
 * </p>
 * <p>
 * Specify the source of the CQL statement by configuring a {@link DataInputParameter<String>}.  
 * Note that the CQL statement can contain parameters in one of 2 forms; the standard SQL form, using the character "?", or you can use named parameters.
 * <br/>
 * If you configure any parameters, using the standard SQL form, then you will need to configure a {@link SequentialParameterApplicator}, or should you wish to name your parameters
 * for ease of configuration, especially when statements contain many parameters, then you will need to configure a {@link NamedParameterApplicator}.
 * </p>
 * <p>
 * To configure the values of the parameters configure a {@link StatementParameterList}.
 * </p>
 * <p>
 * You may also configure a {@link StatementPrimer}.  Statement Primers are used to prepare a CQL statement before it is executed.
 * <br/>
 * Especially useful may be the {@link CachedStatementPrimer}.  The default value for this service is the {@link NullStatementPrimer}.
 * </p>
 * <p>
 * Finally the results of the query can be stored in the {@link AdaptrisMessage}, the format and location of which can be configured using {@link ResultSetTranslator}.
 * </p>
 * 
 * @author amcgrath
 * @config cassandra-query-service
 * @license ENTERPRISE
 */
@XStreamAlias("cassandra-query-service")
public class CassandraQueryService extends ServiceImp {

  @NotNull
  @Valid
  private AdaptrisConnection connection;
  @NotNull
  @AutoPopulated
  @Valid
  private ResultSetTranslator resultSetTranslator;
  @NotNull
  @Valid
  private DataInputParameter<String> statement;
  @NotNull
  @Valid
  @AutoPopulated
  private CassandraParameterApplicator parameterApplicator;
  @NotNull
  @Valid
  @AutoPopulated
  private StatementParameterList parameterList;
  @NotNull
  @Valid
  @AutoPopulated
  private StatementPrimer statementPrimer;
    
  public CassandraQueryService() {
    this.setParameterApplicator(new NullParameterApplicator());
    this.setParameterList(new StatementParameterList());
    this.setResultSetTranslator(new XmlPayloadTranslator());
    this.setStatementPrimer(new NullStatementPrimer());
  }
  
  @Override
  public void doService(AdaptrisMessage message) throws ServiceException {
    try {          
      Session session = (((CassandraConnection) connection).getSession());
      BoundStatement boundStatement = this.getParameterApplicator().applyParameters(session, message, this.getParameterList(), this.getStatement().extract(message));
      ResultSet results = session.execute(boundStatement);
      
      JdbcResult result = new ResultBuilder().setHasResultSet(true).setResultSet(results).build();
      resultSetTranslator.translate(result, message);
    } catch(Exception ex) {
      throw new ServiceException(ex);
    }
  }

  @Override
  public void prepare() throws CoreException {
  }


  @Override
  protected void initService() throws CoreException {
    this.getParameterApplicator().setStatementPrimer(this.getStatementPrimer());
  }

  @Override
  protected void closeService() {
  }

  @Override
  public void start() throws CoreException {
  }
  
  @Override
  public void stop() {
  }
  
  public AdaptrisConnection getConnection() {
    return connection;
  }

  public void setConnection(AdaptrisConnection connection) {
    this.connection = connection;
  }

  public ResultSetTranslator getResultSetTranslator() {
    return resultSetTranslator;
  }

  public void setResultSetTranslator(ResultSetTranslator resultSetTranslator) {
    this.resultSetTranslator = resultSetTranslator;
  }

  public DataInputParameter<String> getStatement() {
    return statement;
  }

  public void setStatement(DataInputParameter<String> statement) {
    this.statement = statement;
  }

  public CassandraParameterApplicator getParameterApplicator() {
    return parameterApplicator;
  }

  public void setParameterApplicator(
      CassandraParameterApplicator parameterApplicator) {
    this.parameterApplicator = parameterApplicator;
  }

  public StatementParameterList getParameterList() {
    return parameterList;
  }

  public void setParameterList(StatementParameterList parameterList) {
    this.parameterList = parameterList;
  }

  public StatementPrimer getStatementPrimer() {
    return statementPrimer;
  }

  public void setStatementPrimer(StatementPrimer statementPrimer) {
    this.statementPrimer = statementPrimer;
  }

}
