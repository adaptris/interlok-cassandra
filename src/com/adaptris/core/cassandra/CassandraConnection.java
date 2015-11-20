package com.adaptris.core.cassandra;

import com.adaptris.core.AdaptrisConnectionImp;
import com.adaptris.core.CoreException;
import com.adaptris.core.licensing.License;
import com.adaptris.core.licensing.License.LicenseType;
import com.adaptris.core.licensing.LicenseChecker;
import com.adaptris.core.licensing.LicensedComponent;
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
 * Cassandra developer guides, suggest only a single session is required per application, therefore it is suggested to configure a single instance of this
 * connection in the shared objects area.
 * <br/>
 * Example:
 * <pre>
 * {@code
 *   <adapter>
 *     <shared-components>
 *       <connections>
 *         <cassandra-connection>
 *           <unique-id>single-cassandra-connection</unique-id>
 *         </cassandra-connection>
 *       </connections>
 *     </shared-components>
 *     ...
 * }
 * </pre>
 * </p>
 * <p>
 * Then in each cassandra service instance, simply reference the shared connection;
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
 * @license ENTERPRISE
 */
@XStreamAlias("cassandra-connection")
public class CassandraConnection extends AdaptrisConnectionImp implements LicensedComponent {

  private transient Cluster cluster;
  
  private transient Session session;

  private String connectionUrl;
  
  private String keyspace;
  
  private String username;
  
  private String password;

  public CassandraConnection() {

  }

  @Override
  public boolean isEnabled(License license) {
    return license.isEnabled(LicenseType.Enterprise);
  }


  @Override
  protected void prepareConnection() throws CoreException {
    LicenseChecker.newChecker().checkLicense(this);
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
          .addContactPoint(this.getConnectionUrl())
          .withCredentials(this.getUsername(), Password.decode(this.getPassword()))
          .build();
      Metadata metadata = cluster.getMetadata();
      log.debug("Connected to cluster: " + metadata.getClusterName());
      for (Host host : metadata.getAllHosts()) {
        log.debug("Datatacenter: " + host.getDatacenter() + " Host: " + host.getAddress() + " Rack: " + host.getRack());
      }
      this.setSession(this.getCluster().connect(this.getKeyspace()));
    } catch (Exception ex) {
      throw new CoreException(ex);
    }
  }

  @Override
  protected void stopConnection() {
    try {
      cluster.close();
    } catch (Exception ex) {
      // ignore.
    }
  }

  public String getConnectionUrl() {
    return connectionUrl;
  }

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

  public void setKeyspace(String keyspace) {
    this.keyspace = keyspace;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

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
