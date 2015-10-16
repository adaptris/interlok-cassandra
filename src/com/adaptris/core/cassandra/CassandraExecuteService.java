package com.adaptris.core.cassandra;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.adaptris.annotation.AutoPopulated;
import com.adaptris.core.AdaptrisConnection;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.ServiceException;
import com.adaptris.core.ServiceImp;
import com.adaptris.core.cassandra.params.CassandraParameterApplicator;
import com.adaptris.core.cassandra.params.NullParameterApplicator;
import com.adaptris.core.cassandra.params.NullStatementPrimer;
import com.adaptris.core.cassandra.params.StatementPrimer;
import com.adaptris.core.services.jdbc.StatementParameterList;
import com.adaptris.interlok.config.DataInputParameter;
import com.adaptris.util.license.License;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Session;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cassandra-execute-service")
public class CassandraExecuteService extends ServiceImp {
  
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
  
  public CassandraExecuteService() {
    this.setParameterApplicator(new NullParameterApplicator());
    this.setParameterList(new StatementParameterList());
    this.setStatementPrimer(new NullStatementPrimer());
  }

  @Override
  public void doService(AdaptrisMessage message) throws ServiceException {
    try {
      Session session = (this.getConnection().retrieveConnection(CassandraConnection.class)).getSession();
      BoundStatement boundStatement = this.getParameterApplicator().applyParameters(session, message, this.getParameterList(), this.getStatement().extract(message));
      session.execute(boundStatement);
    } catch(Exception ex) {
      throw new ServiceException(ex);
    }
  }

  @Override
  public boolean isEnabled(License license) throws CoreException {
    return license.isEnabled(License.LicenseType.Enterprise);
  }

  @Override
  public void init() throws CoreException {    
    this.getParameterApplicator().setStatementPrimer(this.getStatementPrimer());
  }

  @Override
  public void close() {    
  }

  public AdaptrisConnection getConnection() {
    return connection;
  }

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
