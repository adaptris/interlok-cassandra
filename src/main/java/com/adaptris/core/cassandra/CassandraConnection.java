package com.adaptris.core.cassandra;

import java.net.InetSocketAddress;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.annotation.InputFieldHint;
import com.adaptris.core.AdaptrisConnectionImp;
import com.adaptris.core.CoreException;
import com.adaptris.interlok.util.Closer;
import com.adaptris.security.password.Password;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.metadata.Metadata;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.shaded.guava.common.net.HostAndPort;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Getter;
import lombok.Setter;

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

  static final int DEFAULT_PORT = 9042;
  static final String DEFAULT_DATACENTER_NAME = "datacenter1";

  @Getter
  private transient CqlSession session;

  /**
   * <p>
   * Sets the connection string to use for this Cassandra source. e.g. localhost, localhost:9042. The default port '9042' is used if the
   * port is specified.
   * </p>
   *
   * @param connectionUrl
   *          the connection string to use for this Cassandra source
   */
  @NotBlank
  @Getter
  @Setter
  private String connectionUrl;

  /**
   * <p>
   * Sets the Cassandra datacenter that is considered "local" by the load balancing policy. If left empty the default 'datacenter1' will be
   * used.
   * </p>
   *
   * @param localDatacenter
   *          the local datacenter name to use for this Cassandra node
   */
  @Getter
  @Setter
  private String localDatacenter;

  /**
   * <p>
   * Sets the Cassandra node keyspace string to use for this Cassandra node. A keyspace in Cassandra is a namespace that defines data
   * replication on nodes. A cluster contains one keyspace per node.
   * </p>
   *
   * @param keyspace
   *          the keyspace to use for this Cassandra node
   */
  @Getter
  @Setter
  private String keyspace;

  /**
   * Set the username used to access the Cassandra database.
   *
   * @param username
   */
  @Getter
  @Setter
  private String username;

  /**
   * Set the password used to access the Cassandra database.
   *
   * @param password
   *          the password which might be encoded using an available password scheme from {@link com.adaptris.security.password.Password}
   */
  @InputFieldHint(style = "PASSWORD")
  @Getter
  @Setter
  private String password;

  @Override
  protected void prepareConnection() throws CoreException {
  }

  @Override
  protected void closeConnection() {
    setSession(null);
  }

  @Override
  protected void initConnection() throws CoreException {
  }

  @Override
  protected void startConnection() throws CoreException {
    try {
      HostAndPort hostAndPort = hostAndPort();
      CqlSessionBuilder sessionBuilder = CqlSession.builder()
          .addContactPoint(InetSocketAddress.createUnresolved(hostAndPort.getHost(), hostAndPort.getPortOrDefault(DEFAULT_PORT)))
          .withLocalDatacenter(localDatacenter());

      if (StringUtils.isNoneEmpty(getUsername(), getPassword())) {
        sessionBuilder.withAuthCredentials(getUsername(), Password.decode(getPassword()));
      }

      session = sessionBuilder.withKeyspace(getKeyspace()).build();
      Metadata metadata = session.getMetadata();
      log.debug("Connected to cluster: {}", metadata.getClusterName());
      for (Node node : metadata.getNodes().values()) {
        log.debug("Datatacenter: {} Host: {} Rack: {}", node.getDatacenter(), node.getEndPoint().resolve().toString(), node.getRack());
      }
    } catch (Exception ex) {
      throw new CoreException(ex);
    }
  }

  HostAndPort hostAndPort() {
    return HostAndPort.fromString(getConnectionUrl());
  }

  private String localDatacenter() {
    return StringUtils.defaultIfBlank(getLocalDatacenter(), DEFAULT_DATACENTER_NAME);
  }

  @Override
  protected void stopConnection() {
    Closer.closeQuietly(session);
  }

  private void setSession(CqlSession session) {
    this.session = session;
  }

}
