package com.adaptris.core.cassandra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.DefaultMessageFactory;
import com.adaptris.core.cassandra.params.CachedStatementPrimer;
import com.adaptris.core.cassandra.params.SequentialParameterApplicator;
import com.adaptris.core.common.ConstantDataInputParameter;
import com.adaptris.core.services.jdbc.FirstRowMetadataTranslator;
import com.adaptris.core.services.jdbc.StatementParameter;
import com.adaptris.core.services.jdbc.StatementParameterImpl.QueryType;

public class CassandraExecuteServiceTest extends CassandraCase {
  
  private CassandraConnection connection;
  
  private CassandraExecuteService service;
  
  private CassandraQueryService verifyService;
  
  private AdaptrisMessage message;

  @Before
  public void setUp() throws Exception {
    connection = new CassandraConnection();
    connection.setUniqueId("CassandraConnection");
    connection.setConnectionUrl(PROPERTIES.getProperty(TESTS_HOST_KEY, "localhost"));
    connection.setKeyspace(PROPERTIES.getProperty(TESTS_KEYSPACE_KEY, "INTERLOK_KS"));
    connection.setUsername(TEST_USERNAME);
    connection.setPassword(TEST_PASSWORD);
    
    service = new CassandraExecuteService();
    service.setConnection(connection);
    
    verifyService = new CassandraQueryService();
    verifyService.setConnection(connection);
    
    FirstRowMetadataTranslator translator = new FirstRowMetadataTranslator();
    translator.setMetadataKeyPrefix("Cassandra");
    verifyService.setResultSetTranslator(translator);
    
    message = DefaultMessageFactory.getDefaultInstance().newMessage();
  }
  
  @Test
  public void testSimpleInsert() throws Exception {
    if(testsEnabled) {
      ConstantDataInputParameter insertStatement = new ConstantDataInputParameter(
          "insert into liverpool_transfers(player, amount, club, manager) "
          + "values ('Fred', 10, 'Accrington Stanley', 'Brendan Rogers')");
      service.setStatement(insertStatement);
      
      ConstantDataInputParameter verifyStatement = new ConstantDataInputParameter(
          "select * from liverpool_transfers where player = 'Fred'");
      verifyService.setStatement(verifyStatement);
      
      startup(connection, service, verifyService);
      service.doService(message);
      
      verifyService.doService(message);
      shutdown(connection, service, verifyService);
    
      assertEquals("Accrington Stanley", message.getMetadataValue("Cassandra_club"));
    }
  }
  
  @Test
  public void testSimpleInsertAndDelete() throws Exception {
    if(testsEnabled) {
      ConstantDataInputParameter deleteStatement = new ConstantDataInputParameter(
          "delete from liverpool_transfers where player = 'Fred'");
      
      ConstantDataInputParameter insertStatement = new ConstantDataInputParameter(
          "insert into liverpool_transfers(player, amount, club, manager) "
          + "values ('Fred', 10, 'Accrington Stanley', 'Brendan Rogers')");
      service.setStatement(insertStatement);
      
      ConstantDataInputParameter verifyStatement = new ConstantDataInputParameter(
          "select * from liverpool_transfers where player = 'Fred'");
      verifyService.setStatement(verifyStatement);
      
      startup(connection, service, verifyService);
      service.doService(message);
      
      verifyService.doService(message);
      assertEquals("Accrington Stanley", message.getMetadataValue("Cassandra_club"));
      
      service.setStatement(deleteStatement);
      service.doService(message);
      
      AdaptrisMessage deleteServiceMessage = DefaultMessageFactory.getDefaultInstance().newMessage();
      verifyService.doService(deleteServiceMessage);
      assertNull(deleteServiceMessage.getMetadataValue("Cassandra_club"));
      
      shutdown(connection, service, verifyService);
    }
  }
  
  @Test
  public void testSimpleInsertWithParameters() throws Exception {
    if(testsEnabled) {
      ConstantDataInputParameter insertStatement = new ConstantDataInputParameter(
          "insert into liverpool_transfers(player, amount, club, manager) "
          + "values (?, ?, ?, ?)");
      message.addMessageHeader("player", "Fred");
      message.addMessageHeader("amount", "10");
      message.addMessageHeader("club", "Accrington Stanley");
      message.addMessageHeader("manager", "Brendan Rogers");
      service.setStatement(insertStatement);
      
      StatementParameter playerParameter = new StatementParameter();
      playerParameter.setQueryString("player");
      playerParameter.setQueryClass("java.lang.String");
      playerParameter.setQueryType(QueryType.metadata);
      
      StatementParameter amountParameter = new StatementParameter();
      amountParameter.setQueryString("amount");
      amountParameter.setQueryClass("java.lang.Integer");
      amountParameter.setQueryType(QueryType.metadata);
      
      StatementParameter clubParameter = new StatementParameter();
      clubParameter.setQueryString("club");
      clubParameter.setQueryClass("java.lang.String");
      clubParameter.setQueryType(QueryType.metadata);
      
      StatementParameter managerParameter = new StatementParameter();
      managerParameter.setQueryString("manager");
      managerParameter.setQueryClass("java.lang.String");
      managerParameter.setQueryType(QueryType.metadata);
      
      service.getParameterList().add(playerParameter);
      service.getParameterList().add(amountParameter);
      service.getParameterList().add(clubParameter);
      service.getParameterList().add(managerParameter);
      
      service.setParameterApplicator(new SequentialParameterApplicator());
      
      ConstantDataInputParameter verifyStatement = new ConstantDataInputParameter(
          "select * from liverpool_transfers where player = 'Fred'");
      verifyService.setStatement(verifyStatement);
      
      startup(connection, service, verifyService);
      service.doService(message);
      
      verifyService.doService(message);
      shutdown(connection, service, verifyService);
    
      assertEquals("Fred", message.getMetadataValue("Cassandra_player"));
      assertEquals("Accrington Stanley", message.getMetadataValue("Cassandra_club"));
      assertEquals("10", message.getMetadataValue("Cassandra_amount"));
      assertEquals("Brendan Rogers", message.getMetadataValue("Cassandra_manager"));
    }
  }
  
  @Override
  protected Object retrieveObjectForSampleConfig() {
    ConstantDataInputParameter insertStatement = new ConstantDataInputParameter(
        "insert into liverpool_transfers(player, amount, club, manager) "
        + "values (?, 10, 'Accrington Stanley', 'Brendan Rogers')");
    service.setStatement(insertStatement);
    service.setParameterApplicator(new SequentialParameterApplicator());
    StatementParameter parameter = new StatementParameter("Fred", "java.lang.String", QueryType.constant);
    
    CachedStatementPrimer statementPrimer = new CachedStatementPrimer();
    statementPrimer.setCacheLimit(25);
    service.setStatementPrimer(statementPrimer);
    service.getParameterList().add(parameter);
    return service;
  }

}
