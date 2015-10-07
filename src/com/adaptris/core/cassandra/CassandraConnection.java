package com.adaptris.core.cassandra;

import com.adaptris.core.AdaptrisConnectionImp;
import com.adaptris.core.CoreException;
import com.adaptris.util.license.License;
import com.adaptris.util.license.License.LicenseType;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cassandra-connection")
public class CassandraConnection extends AdaptrisConnectionImp {

  private transient Cluster cluster;

  private String connectionUrl;
  
  private String keyspace;

  public CassandraConnection() {

  }

  @Override
  public boolean isEnabled(License license) throws CoreException {
    return license.isEnabled(LicenseType.Enterprise);
  }

  @Override
  protected void closeConnection() {
    try {
      cluster.close();
    } catch (Exception ex) {
      // ignore.
    }
  }

  @Override
  protected void initConnection() throws CoreException {

  }

  @Override
  protected void startConnection() throws CoreException {
    cluster = Cluster.builder().addContactPoint(this.getConnectionUrl()).build();
    Metadata metadata = cluster.getMetadata();
    log.debug("Connected to cluster: %s\n", metadata.getClusterName());
    for (Host host : metadata.getAllHosts()) {
      log.debug("Datatacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(), host.getRack());
    }
  }

  @Override
  protected void stopConnection() {
    cluster = null;
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

}
