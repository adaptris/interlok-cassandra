package com.adaptris.core.cassandra;

import java.util.EnumSet;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.DefaultMessageFactory;
import com.adaptris.core.cassandra.params.NamedParameterApplicator;
import com.adaptris.core.cassandra.params.SequentialParameterApplicator;
import com.adaptris.core.common.ConstantDataInputParameter;
import com.adaptris.core.services.jdbc.FirstRowMetadataTranslator;
import com.adaptris.core.services.jdbc.StatementParameter;
import com.adaptris.core.services.jdbc.StatementParameter.QueryType;
import com.adaptris.core.stubs.LicenseStub;

public class CassandraQueryServiceTest extends CassandraCase {
  
  private CassandraConnection connection;
  
  private CassandraQueryService service;
  
  private AdaptrisMessage message;
  
  public CassandraQueryServiceTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    connection = new CassandraConnection();
    connection.setUniqueId("CassandraConnection");
    connection.setConnectionUrl(PROPERTIES.getProperty(TESTS_HOST_KEY, "localhost"));
    connection.setKeyspace(PROPERTIES.getProperty(TESTS_KEYSPACE_KEY, "INTERLOK_KS"));
    
    service = new CassandraQueryService();
    service.setConnection(connection);
    
    FirstRowMetadataTranslator translator = new FirstRowMetadataTranslator();
    translator.setMetadataKeyPrefix("Cassandra");
    service.setResultSetTranslator(translator);
    
    message = DefaultMessageFactory.getDefaultInstance().newMessage();
  }
  
  public void tearDown() throws Exception {
    
  }
  
  public void testServiceEnterpriseLicense() throws Exception {
    assertFalse(service.isEnabled(new LicenseStub(EnumSet.of(com.adaptris.util.license.License.LicenseType.Basic))));
    assertFalse(service.isEnabled(new LicenseStub(EnumSet.of(com.adaptris.util.license.License.LicenseType.Basic, com.adaptris.util.license.License.LicenseType.Standard))));
    assertTrue(service.isEnabled(new LicenseStub(EnumSet.of(com.adaptris.util.license.License.LicenseType.Basic, com.adaptris.util.license.License.LicenseType.Standard, com.adaptris.util.license.License.LicenseType.Enterprise))));
  }
  
  public void testConnectionEnterpriseLicense() throws Exception {
    assertFalse(connection.isEnabled(new LicenseStub(EnumSet.of(com.adaptris.util.license.License.LicenseType.Basic))));
    assertFalse(connection.isEnabled(new LicenseStub(EnumSet.of(com.adaptris.util.license.License.LicenseType.Basic, com.adaptris.util.license.License.LicenseType.Standard))));
    assertTrue(connection.isEnabled(new LicenseStub(EnumSet.of(com.adaptris.util.license.License.LicenseType.Basic, com.adaptris.util.license.License.LicenseType.Standard, com.adaptris.util.license.License.LicenseType.Enterprise))));
  }
  
  public void testSimpleValueQuery() throws Exception {
    if(testsEnabled) {
      service.setStatement(new ConstantDataInputParameter("select club from liverpool_transfers where player = 'Xabi Alonso'"));
            
      startup(connection, service);
      service.doService(message);
      shutdown(connection, service);
      
      assertEquals("Real Sociedad", message.getMetadataValue("Cassandra_club"));
    } else
      System.out.println("Skipping testSimpleValueQuery()");
  }

  public void testSimpleRowQuery() throws Exception {
    if(testsEnabled) {
      service.setStatement(new ConstantDataInputParameter("select * from liverpool_transfers where player = 'Xabi Alonso'"));
      
      startup(connection, service);
      service.doService(message);
      shutdown(connection, service);
      
      assertEquals("Real Sociedad", message.getMetadataValue("Cassandra_club"));
      assertEquals("10700000", message.getMetadataValue("Cassandra_amount"));
      assertEquals("Rafael Benitez", message.getMetadataValue("Cassandra_manager"));
      assertEquals("Xabi Alonso", message.getMetadataValue("Cassandra_player"));
    } else
      System.out.println("Skipping testSimpleRowQuery()");
  }
  
  public void testSimpleValueQueryWithSimpleParameter() throws Exception {
    if(testsEnabled) {
      service.setStatement(new ConstantDataInputParameter("select club from liverpool_transfers where player = ?"));
      
      StatementParameter parameter = new StatementParameter();
      parameter.setQueryString("playername");
      parameter.setQueryClass("java.lang.String");
      parameter.setQueryType(QueryType.metadata);
      
      message.addMessageHeader("playername", "Xabi Alonso");
      
      service.getParameterList().add(parameter);
      service.setParameterApplicator(new SequentialParameterApplicator());
      
      startup(connection, service);
      service.doService(message);
      shutdown(connection, service);
      
      assertEquals("Real Sociedad", message.getMetadataValue("Cassandra_club")); 
    } else
      System.out.println("Skipping testSimpleValueQueryWithSimpleParameter()");
  }
  
  public void testSimpleValueQueryWithNamedParameter() throws Exception {
    if(testsEnabled) {
      service.setStatement(new ConstantDataInputParameter("select club from liverpool_transfers where player = #playername"));
      
      StatementParameter parameter = new StatementParameter();
      parameter.setQueryString("playername");
      parameter.setQueryClass("java.lang.String");
      parameter.setQueryType(QueryType.metadata);
      parameter.setName("playername");
      
      message.addMessageHeader("playername", "Xabi Alonso");
      
      service.getParameterList().add(parameter);
      service.setParameterApplicator(new NamedParameterApplicator());
      
      startup(connection, service);
      service.doService(message);
      shutdown(connection, service);
      
      assertEquals("Real Sociedad", message.getMetadataValue("Cassandra_club")); 
    } else
      System.out.println("Skipping testSimpleValueQueryWithNamedParameter()");
  }

  @Override
  protected Object retrieveObjectForSampleConfig() {
    service.setStatement(new ConstantDataInputParameter("SELECT * FROM myTable")); 
    return service;
  }

}
