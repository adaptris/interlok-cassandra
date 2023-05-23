package com.adaptris.core.cassandra;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.DefaultMessageFactory;
import com.adaptris.core.cassandra.params.CachedStatementPrimer;
import com.adaptris.core.cassandra.params.NamedParameterApplicator;
import com.adaptris.core.cassandra.params.SequentialParameterApplicator;
import com.adaptris.core.common.ConstantDataInputParameter;
import com.adaptris.core.services.jdbc.FirstRowMetadataTranslator;
import com.adaptris.core.services.jdbc.StatementParameter;
import com.adaptris.core.services.jdbc.StatementParameterImpl.QueryType;

public class CassandraQueryServiceTest extends CassandraCase {

  private CassandraConnection connection;

  private CassandraQueryService service;

  private AdaptrisMessage message;

  @BeforeEach
  public void setUp() throws Exception {
    connection = new CassandraConnection();
    connection.setUniqueId("CassandraConnection");
    connection.setConnectionUrl(PROPERTIES.getProperty(TESTS_HOST_KEY, "localhost"));
    connection.setKeyspace(PROPERTIES.getProperty(TESTS_KEYSPACE_KEY, "INTERLOK_KS"));
    connection.setUsername(TEST_USERNAME);
    connection.setPassword(TEST_PASSWORD);

    service = new CassandraQueryService();
    service.setConnection(connection);

    FirstRowMetadataTranslator translator = new FirstRowMetadataTranslator();
    translator.setMetadataKeyPrefix("Cassandra");
    service.setResultSetTranslator(translator);

    message = DefaultMessageFactory.getDefaultInstance().newMessage();
  }

  @Test
  public void testSimpleValueQuery() throws Exception {
    if (testsEnabled) {
      service.setStatement(new ConstantDataInputParameter("select club from liverpool_transfers where player = 'Xabi Alonso'"));

      startup(service);
      service.doService(message);
      shutdown(service);

      assertEquals("Real Sociedad", message.getMetadataValue("Cassandra_club"));
    } else {
      System.out.println("Skipping testSimpleValueQuery()");
    }
  }

  @Test
  public void testSimpleRowQuery() throws Exception {
    if (testsEnabled) {
      service.setStatement(new ConstantDataInputParameter("select * from liverpool_transfers where player = 'Xabi Alonso'"));

      startup(service);
      service.doService(message);
      shutdown(service);

      assertEquals("Real Sociedad", message.getMetadataValue("Cassandra_club"));
      assertEquals("10700000", message.getMetadataValue("Cassandra_amount"));
      assertEquals("Rafael Benitez", message.getMetadataValue("Cassandra_manager"));
      assertEquals("Xabi Alonso", message.getMetadataValue("Cassandra_player"));
    } else {
      System.out.println("Skipping testSimpleRowQuery()");
    }
  }

  @Test
  public void testSimpleValueQueryWithSimpleParameter() throws Exception {
    if (testsEnabled) {
      service.setStatement(new ConstantDataInputParameter("select club from liverpool_transfers where player = ?"));

      StatementParameter parameter = new StatementParameter();
      parameter.setQueryString("playername");
      parameter.setQueryClass("java.lang.String");
      parameter.setQueryType(QueryType.metadata);

      message.addMessageHeader("playername", "Xabi Alonso");

      service.getParameterList().add(parameter);
      service.setParameterApplicator(new SequentialParameterApplicator());

      startup(service);
      service.doService(message);
      shutdown(service);

      assertEquals("Real Sociedad", message.getMetadataValue("Cassandra_club"));
    } else {
      System.out.println("Skipping testSimpleValueQueryWithSimpleParameter()");
    }
  }

  @Test
  public void testSimpleValueQueryWithNamedParameter() throws Exception {
    if (testsEnabled) {
      service.setStatement(new ConstantDataInputParameter("select club from liverpool_transfers where player = #playername"));

      StatementParameter parameter = new StatementParameter();
      parameter.setQueryString("playername");
      parameter.setQueryClass("java.lang.String");
      parameter.setQueryType(QueryType.metadata);
      parameter.setName("playername");

      message.addMessageHeader("playername", "Xabi Alonso");

      service.getParameterList().add(parameter);
      service.setParameterApplicator(new NamedParameterApplicator());

      startup(service);
      service.doService(message);
      shutdown(service);

      assertEquals("Real Sociedad", message.getMetadataValue("Cassandra_club"));
    } else {
      System.out.println("Skipping testSimpleValueQueryWithNamedParameter()");
    }
  }

  @Override
  protected Object retrieveObjectForSampleConfig() {
    service.setStatement(new ConstantDataInputParameter("SELECT * FROM myTable"));
    CachedStatementPrimer statementPrimer = new CachedStatementPrimer();
    statementPrimer.setCacheLimit(25);
    service.setStatementPrimer(statementPrimer);
    return service;
  }

}
