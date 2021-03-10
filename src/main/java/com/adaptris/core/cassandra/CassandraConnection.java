package com.adaptris.core.cassandra;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.annotation.InputFieldHint;
import com.adaptris.core.AdaptrisConnectionImp;
import com.adaptris.core.CoreException;
import com.adaptris.interlok.util.Closer;
import com.adaptris.security.password.Password;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * This connection is solely to be used with Adaptris cassandra components.
 * </p>
 * <p>
 * Cassandra developer guides, suggest only a single session is required per application, therefore it is suggested to configure a single
 * instance of this connection in the shared objects area. <br/>
 * Example:
 *
 * <pre>
 * {@code
 *   <adapter>
 *     <shared-components>
 *       <connections>
 *         <cassandra-connection>
 *           <unique-id>single-cassandra-connection</unique-id>
 *           <connection-url>localhost</connection-url>
 *           <keyspace>a_keyspace</keyspace>
 *         </cassandra-connection>
 *       </connections>
 *     </shared-components>
 *     ...
 * }
 * </pre>
 * </p>
 * <p>
 * Then in each cassandra service instance, simply reference the shared connection;
 *
 * <pre>
 * {@code
 *   <cassandra-query-service>
 *     <connection class="shared-connection">
 *       <lookup-name>single-cassandra-connection</lookup-name>
 *     </connection>
 *  ...
 * }
 * </pre>
 * </p>
 *
 * @author amcgrath
 * @config cassandra-connection
 */
@XStreamAlias("cassandra-connection")
@AdapterComponent
@ComponentProfile(summary = "Connect to a Cassandra database", tag = "connections,cassandra")
@DisplayOrder(order = { "connectionUrl", "keyspace", "username", "password" })
public class CassandraConnection extends AdaptrisConnectionImp {

  private transient Cluster cluster;

  private transient Session session;

  private String connectionUrl;

  private String keyspace;

  private String username;
  @InputFieldHint(style = "PASSWORD")
  private String password;

  @Override
  protected void prepareConnection() throws CoreException {
  }

  @Override
  protected void closeConnection() {
    cluster = null;
  }

  @Override
  protected void initConnection() throws CoreException {
  }

  @Override
  protected void startConnection() throws CoreException {
    try {
      cluster = Cluster.builder()
          .addContactPoint(getConnectionUrl())
          .withCredentials(getUsername(), Password.decode(getPassword()))
          .build();
      Metadata metadata = cluster.getMetadata();
      log.debug("Connected to cluster: {}", metadata.getClusterName());
      for (Host host : metadata.getAllHosts()) {
        log.debug("Datatacenter: {} Host: {} Rack: {}", host.getDatacenter(), host.getEndPoint().resolve().getAddress(), host.getRack());
      }
      setSession(getCluster().connect(getKeyspace()));
    } catch (Exception ex) {
      throw new CoreException(ex);
    }
  }

  @Override
  protected void stopConnection() {
    Closer.closeQuietly(cluster);
  }

  public String getConnectionUrl() {
    return connectionUrl;
  }

  /**
   * <p>
   * Sets the connection string to use for this Cassandra source. e.g. localhost
   * </p>
   *
   * @param connectionUrl
   *          the connection string to use for this Cassandra source
   */
  public void setConnectionUrl(String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  public Cluster getCluster() {
    return cluster;
  }

  public void setCluster(Cluster cluster) {
    this.cluster = cluster;
  }

  public String getKeyspace() {
    return keyspace;
  }

  /**
   * <p>
   * Sets the Cassandra node keyspace string to use for this Cassandra node. A keyspace in Cassandra is a namespace that defines data
   * replication on nodes. A cluster contains one keyspace per node.
   * </p>
   *
   * @param keyspace
   *          the keyspace to use for this Cassandra node
   */
  public void setKeyspace(String keyspace) {
    this.keyspace = keyspace;
  }

  public String getUsername() {
    return username;
  }

  /**
   * Set the username used to access the Cassandra database.
   *
   * @param username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  /**
   * Set the password used to access the Cassandra database.
   *
   * @param password
   *          the password which might be encoded using an available password scheme from {@link com.adaptris.security.password.Password}
   */
  public void setPassword(String password) {
    this.password = password;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

}
