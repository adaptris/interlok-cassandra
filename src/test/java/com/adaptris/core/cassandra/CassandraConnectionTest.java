package com.adaptris.core.cassandra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class CassandraConnectionTest {

  @Test
  public void testHostnameAndDefaultPort() throws Exception {
    CassandraConnection tmpConnection = new CassandraConnection();
    tmpConnection.setConnectionUrl("localhost");

    assertEquals("localhost", tmpConnection.hostAndPort().getHost());
  }

  @Test
  public void testHostnameAndDifferntPort() throws Exception {
    CassandraConnection tmpConnection = new CassandraConnection();
    tmpConnection.setConnectionUrl("localhost:9043");

    assertEquals("localhost", tmpConnection.hostAndPort().getHost());
    assertEquals(9043, tmpConnection.hostAndPort().getPort());
  }

  @Test
  public void testHostnameAndInvalidPort() throws Exception {
    CassandraConnection tmpConnection = new CassandraConnection();
    tmpConnection.setConnectionUrl("localhost:Invalid");

    assertThrows(IllegalArgumentException.class, () -> tmpConnection.hostAndPort().getHost());
  }

}
