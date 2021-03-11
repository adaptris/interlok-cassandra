package com.adaptris.core.cassandra;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.adaptris.annotation.AutoPopulated;
import com.adaptris.core.AdaptrisConnection;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ConnectedService;
import com.adaptris.core.CoreException;
import com.adaptris.core.ServiceException;
import com.adaptris.core.ServiceImp;
import com.adaptris.core.cassandra.params.CassandraParameterApplicator;
import com.adaptris.core.cassandra.params.NullParameterApplicator;
import com.adaptris.core.cassandra.params.NullStatementPrimer;
import com.adaptris.core.cassandra.params.StatementPrimer;
import com.adaptris.core.services.jdbc.StatementParameterList;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.interlok.config.DataInputParameter;
import com.datastax.driver.core.Session;

/**
 * <p>
 * Abstract the common behaviour for Cassandra connected services
 * </p>
 */
public abstract class CassandraServiceImp extends ServiceImp implements ConnectedService {

  @NotNull
  @Valid
  private AdaptrisConnection connection;
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

  public CassandraServiceImp() {
    setParameterApplicator(new NullParameterApplicator());
    setParameterList(new StatementParameterList());
    setStatementPrimer(new NullStatementPrimer());
  }

  @Override
  public void doService(AdaptrisMessage message) throws ServiceException {
    try {
      Session session = getConnection().retrieveConnection(CassandraConnection.class).getSession();
      doCassandraService(session, message);
    } catch(Exception ex) {
      throw new ServiceException(ex);
    }
  }

  protected abstract void doCassandraService(Session session, AdaptrisMessage message) throws Exception;

  @Override
  public void prepare() throws CoreException {
  }

  @Override
  protected void initService() throws CoreException {
    LifecycleHelper.init(connection);
    getParameterApplicator().setStatementPrimer(getStatementPrimer());
  }

  @Override
  public void start() throws CoreException {
    super.start();
    LifecycleHelper.start(connection);
  }

  @Override
  protected void closeService() {
    LifecycleHelper.close(connection);
  }

  @Override
  public void stop() {
    super.stop();
    LifecycleHelper.stop(connection);
  }

  @Override
  public AdaptrisConnection getConnection() {
    return connection;
  }

  @Override
  public void setConnection(AdaptrisConnection connection) {
    this.connection = connection;
  }

  public CassandraParameterApplicator getParameterApplicator() {
    return parameterApplicator;
  }

  public void setParameterApplicator(CassandraParameterApplicator parameterApplicator) {
    this.parameterApplicator = parameterApplicator;
  }

  public StatementParameterList getParameterList() {
    return parameterList;
  }

  public void setParameterList(StatementParameterList parameterList) {
    this.parameterList = parameterList;
  }

  public DataInputParameter<String> getStatement() {
    return statement;
  }

  public void setStatement(DataInputParameter<String> statement) {
    this.statement = statement;
  }

  public StatementPrimer getStatementPrimer() {
    return statementPrimer;
  }

  public void setStatementPrimer(StatementPrimer statementPrimer) {
    this.statementPrimer = statementPrimer;
  }

}
