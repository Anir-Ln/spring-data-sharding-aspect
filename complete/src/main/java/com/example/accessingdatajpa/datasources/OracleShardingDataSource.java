package com.example.accessingdatajpa.datasources;

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

public class OracleShardingDataSource implements DataSource {
    DataSource proxyDataSource;
    DataSource directRoutingDataSource;

    private final ThreadLocal<ShardingKey> threadBoundShardingKey = new NamedThreadLocal<>("Current Sharding key");
    private final ThreadLocal<ShardingKey> threadBoundSuperShardingKey = new NamedThreadLocal<>("Current Super Sharding key");

    public void setShardingKeyForCurrentThread(ShardingKey shardingKey) {
        this.threadBoundShardingKey.set(shardingKey);
    }

    public void setSuperShardingKeyForCurrentThread(ShardingKey superShardingKey) {
        this.threadBoundSuperShardingKey.set(superShardingKey);
    }

    public void removeShardingKeyFromCurrentThread() {
        this.threadBoundShardingKey.remove();
    }

    public void removeSuperShardingKeyFromCurrentThread() {
        this.threadBoundSuperShardingKey.remove();
    }

    public void clearShardingKeysFromCurrentThread() {
        this.removeShardingKeyFromCurrentThread();
        this.removeSuperShardingKeyFromCurrentThread();
    }


    @Override
    public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
        return directRoutingDataSource.createShardingKeyBuilder();
    }

    public Connection getConnection() throws SQLException {
        try {
            return createConnectionBuilder().build();
        } catch (Exception e) {
            getParentLogger().warning("Shard connection could not be acquired, used a proxyDataSource connection instead.");
            return proxyDataSource.getConnection();
        }
    }

    public ConnectionBuilder createConnectionBuilder() throws SQLException {
        ShardingKey shardingKey = this.threadBoundShardingKey.get();
        ShardingKey superShardingKey = this.threadBoundSuperShardingKey.get();
        if (shardingKey == null) {
            proxyDataSource.createConnectionBuilder();
        }

        return directRoutingDataSource.createConnectionBuilder().shardingKey(shardingKey).superShardingKey(superShardingKey);
    }

    @Nullable
    public ShardingKey getShardingKeyForCurrentThread() {
        return this.threadBoundShardingKey.get();
    }

    @Nullable
    public ShardingKey getSuperShardingKeyForCurrentThread() {
        return this.threadBoundSuperShardingKey.get();
    }
    public void setProxyDataSource(DataSource proxyDataSource) {
        this.proxyDataSource = proxyDataSource;
    }

    public void setDirectRoutingDataSource(DataSource directRoutingDataSource) {
        this.directRoutingDataSource = directRoutingDataSource;
    }


    protected DataSource obtainTargetDataSource() {
        DataSource dataSource = this.threadBoundSuperShardingKey.get() != null ? directRoutingDataSource : proxyDataSource;
        Assert.state(dataSource != null, "No 'targetDataSource' set");
        return dataSource;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return this.createConnectionBuilder().user(username).password(password).build();
    }

    public PrintWriter getLogWriter() throws SQLException {
        return this.obtainTargetDataSource().getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        this.obtainTargetDataSource().setLogWriter(out);
    }

    public int getLoginTimeout() throws SQLException {
        return this.obtainTargetDataSource().getLoginTimeout();
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        this.obtainTargetDataSource().setLoginTimeout(seconds);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return iface.isInstance(this) ? (T) this : this.obtainTargetDataSource().unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || this.obtainTargetDataSource().isWrapperFor(iface);
    }

    public Logger getParentLogger() {
        return Logger.getLogger("global");
    }

}
