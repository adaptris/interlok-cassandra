package com.adaptris.core.cassandra;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.adaptris.annotation.AutoPopulated;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.ServiceException;
import com.adaptris.core.ServiceImp;
import com.adaptris.core.services.jdbc.ResultSetTranslator;
import com.adaptris.interlok.config.DataInputParameter;
import com.adaptris.jdbc.JdbcResult;
import com.adaptris.util.license.License;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cassandra-query-service")
public class CassandraQueryService extends ServiceImp {

  @NotNull
  @Valid
  private CassandraConnection connection;
  @NotNull
  @AutoPopulated
  @Valid
  private ResultSetTranslator resultSetTranslator;
  @NotNull
  @Valid
  private DataInputParameter<String> statement;
  
  private transient Session session;
  
  public CassandraQueryService() {
    
  }
  
  @Override
  public void doService(AdaptrisMessage message) throws ServiceException {
    try {          
      ResultSet results = this.getSession().execute(this.getStatement().extract(message));
      JdbcResult result = new ResultBuilder().setHasResultSet(true).setResultSet(results).build();
      resultSetTranslator.translate(result, message);
    } catch(Exception ex) {
      throw new ServiceException(ex);
    }
  }

  @Override
  public boolean isEnabled(License license) throws CoreException {
    return license.isEnabled(License.LicenseType.Enterprise);
  }

  @Override
  public void close() {
    this.getConnection().close();
  }

  @Override
  public void start() throws CoreException {
    this.getConnection().start();
    this.setSession(connection.getCluster().connect(connection.getKeyspace()));
  }
  
  @Override
  public void stop() {
    this.getSession().close();
    this.getConnection().stop();
  }
  
  @Override
  public void init() throws CoreException {
    this.getConnection().init();
  }

  public CassandraConnection getConnection() {
    return connection;
  }

  public void setConnection(CassandraConnection connection) {
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

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

}
