package com.adaptris.core.cassandra;

import java.net.InetSocketAddress;

import com.adaptris.core.CoreException;
import com.adaptris.core.Service;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.interlok.junit.scaffolding.services.ExampleServiceCase;
import com.adaptris.interlok.util.Closer;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.shaded.guava.common.net.HostAndPort;

public abstract class CassandraCase extends ExampleServiceCase {

  /**
   * Key in unit-test.properties that defines where example goes unless overriden {@link #setBaseDir(String)}.
   */
  public static final String BASE_DIR_KEY = "CassandraServiceCase.baseDir";
  /**
   * Key in unit-test.properties that will enable or disable the tests in this class that directly access a Cassandra instance.
   */
  public static final String TESTS_ENABLED_KEY = "CassandraServiceCase.enabled";
  /**
   * Key in unit-test.properties that specifies the host of the Cassandra instance used to tests.
   */
  public static final String TESTS_HOST_KEY = "CassandraServiceCase.host";
  /**
   * Key in unit-test.properties that specifies the port of the Cassandra instance used to tests.
   */
  public static final String TESTS_PORT_KEY = "CassandraServiceCase.port";
  /**
   * Key in unit-test.properties that specifies the Cassandra keyspace (equivalent to database name (mysql) or schema (oracle)) used for
   * these tests.
   */
  public static final String TESTS_KEYSPACE_KEY = "CassandraServiceCase.keyspace";

  private static final String DATACENTER_NAME = "datacenter1";

  protected static final String TEST_USERNAME = "dba";
  protected static final String TEST_PASSWORD = "bacon";

  protected boolean testsEnabled;

  public CassandraCase() {
    if (PROPERTIES.getProperty(BASE_DIR_KEY) != null) {
      setBaseDir(PROPERTIES.getProperty(BASE_DIR_KEY));
      testsEnabled = testEnabled();
    }
  }

  static {
    if (testEnabled()) {
      CqlSession session = null;
      try {
        HostAndPort hostAndPort = hostAndPort();
        session = CqlSession.builder()
            .addContactPoint(new InetSocketAddress(hostAndPort.getHost(), hostAndPort.getPortOrDefault(9042)))
            .withLocalDatacenter(DATACENTER_NAME)
            .withAuthCredentials(TEST_USERNAME, TEST_PASSWORD)
            .withKeyspace(PROPERTIES.getProperty(TESTS_KEYSPACE_KEY))
            .build();

        try {
          session.execute("drop table " + PROPERTIES.getProperty(TESTS_KEYSPACE_KEY) + ".liverpool_transfers");
        } catch (Exception ex) {
          // Ignored if the table doesn't exist
        }
        session.execute("create table "
            + PROPERTIES.getProperty(TESTS_KEYSPACE_KEY)
            + ".liverpool_transfers (\n"
            + "player text,\n"
            + "amount int,\n"
            + "club text,\n"
            + "manager text,\n"
            + "PRIMARY KEY(player));");

        session.execute("INSERT INTO liverpool_transfers (player, club, amount, manager) VALUES ('Djibril Cisse', 'AJ Auxerre', 14500000, 'Gerard Houllier')");
        session.execute("INSERT INTO liverpool_transfers (player, club, amount, manager) VALUES ('Emile Heskey', 'Leicester City', 11000000, 'Gerard Houllier')");
        session.execute("INSERT INTO liverpool_transfers (player, club, amount, manager) VALUES ('Xabi Alonso', 'Real Sociedad' ,10700000, 'Rafael Benitez')");
      } finally {
        Closer.closeQuietly(session);
      }
    }
  }

  private static HostAndPort hostAndPort() {
    return HostAndPort.fromString(PROPERTIES.getProperty(TESTS_HOST_KEY));
  }

  private static boolean testEnabled() {
    return PROPERTIES.getProperty(TESTS_ENABLED_KEY, "false").equalsIgnoreCase("true");
  }

  protected void shutdown(Service... services) {
    for (Service service : services) {
      LifecycleHelper.stop(service);
      LifecycleHelper.close(service);
    }
  }

  protected void startup(Service... services) throws CoreException {
    for (Service service : services) {
      LifecycleHelper.init(service);
      LifecycleHelper.start(service);
    }
  }

}
