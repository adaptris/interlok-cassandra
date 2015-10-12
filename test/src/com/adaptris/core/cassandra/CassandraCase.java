package com.adaptris.core.cassandra;

import com.adaptris.core.AdaptrisConnection;
import com.adaptris.core.CoreException;
import com.adaptris.core.Service;
import com.adaptris.core.ServiceCase;
import com.adaptris.core.util.LifecycleHelper;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public abstract class CassandraCase extends ServiceCase {
  
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
   * Key in unit-test.properties that specifies the Cassandra keyspace (equivalent to database name (mysql) or schema (oracle)) used for these tests.
   */
  public static final String TESTS_KEYSPACE_KEY = "CassandraServiceCase.keyspace";

  protected boolean testsEnabled;
  
  public CassandraCase(String name) {
    super(name);
    
    if (PROPERTIES.getProperty(BASE_DIR_KEY) != null) {
      setBaseDir(PROPERTIES.getProperty(BASE_DIR_KEY));
      testsEnabled = PROPERTIES.getProperty(TESTS_ENABLED_KEY, "false").equalsIgnoreCase("true");
    }
  }
  
  static {
    if(PROPERTIES.getProperty(TESTS_ENABLED_KEY, "false").equalsIgnoreCase("true")) {
      Cluster cluster = null;
      Session session = null;
      try {
        cluster = Cluster.builder()
            .addContactPoint(PROPERTIES.getProperty(TESTS_HOST_KEY))
            .build();
        
        session = cluster.connect(PROPERTIES.getProperty(TESTS_KEYSPACE_KEY));
        
        try {
          session.execute("drop table " + PROPERTIES.getProperty(TESTS_KEYSPACE_KEY) + ".liverpool_transfers");
        } catch (Exception ex) {
          // ignored if the table doesn;t exist
        }
        session.execute("create table " + PROPERTIES.getProperty(TESTS_KEYSPACE_KEY) + ".liverpool_transfers (\n"
            + "player text,\n"
            + "amount int,\n"
            + "club text,\n"
            + "manager text,\n"
            + "PRIMARY KEY(player));");
        
        session.execute("INSERT INTO liverpool_transfers (player, club, amount, manager) VALUES ('Djibril Cisse', 'AJ Auxerre',14500000,'Gerard Houllier')");
        session.execute("INSERT INTO liverpool_transfers (player, club, amount, manager) VALUES ('Emile Heskey', 'Leicester City',11000000,'Gerard Houllier')");
        session.execute("INSERT INTO liverpool_transfers (player, club, amount, manager) VALUES ('Xabi Alonso', 'Real Sociedad',10700000,'Rafael Benitez')");
        
      } finally {
        session.close();
        cluster.close();
      }
    }
  }
  
  protected void shutdown(AdaptrisConnection connection, Service... services) {
    LifecycleHelper.stop(connection);
    LifecycleHelper.close(connection);
    
    for(Service service : services) {
      LifecycleHelper.stop(service);
      LifecycleHelper.close(service);
    }
  }

  protected void startup(AdaptrisConnection connection, Service... services) throws CoreException {
    LifecycleHelper.init(connection);
    LifecycleHelper.start(connection);
    
    for(Service service : services) {
      LifecycleHelper.stop(service);
      LifecycleHelper.close(service);
    }
  }

}
