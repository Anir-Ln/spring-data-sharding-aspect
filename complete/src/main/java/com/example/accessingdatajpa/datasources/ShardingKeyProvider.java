package com.example.accessingdatajpa.datasources;

import org.springframework.lang.Nullable;

import java.sql.SQLException;
import java.sql.ShardingKey;

public interface ShardingKeyProvider {
    @Nullable
    ShardingKey getShardingKey() throws SQLException;

    @Nullable
    ShardingKey getSuperShardingKey() throws SQLException;
}