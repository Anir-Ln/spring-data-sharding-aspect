package com.example.accessingdatajpa.aspects;

import com.example.accessingdatajpa.annotation.DirectShardRouting;
import com.example.accessingdatajpa.datasources.OracleShardingDataSource;
import com.example.accessingdatajpa.datasources.ShardingKeyProvider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;

@Aspect
@Component
public class DirectShardRoutingAspect {
    @Autowired
    DataSource dataSource;
    ShardingKeyProvider shardingKeyProvider;

    @Around("@annotation(com.example.accessingdatajpa.annotation.DirectShardRouting)")
    public Object invoke(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        DirectShardRouting annotation = methodSignature.getMethod().getAnnotation(DirectShardRouting.class);
        String[] hints = annotation.hints();

        System.out.println("hints = " + Arrays.toString(hints));

        try {
            ((OracleShardingDataSource) dataSource).setShardingKeyForCurrentThread(shardingKeyProvider.getShardingKey());
            ((OracleShardingDataSource) dataSource).setSuperShardingKeyForCurrentThread(shardingKeyProvider.getSuperShardingKey());
            return pjp.proceed();
        } finally {
            ((OracleShardingDataSource) dataSource).clearShardingKeysFromCurrentThread();
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setShardingKeyProvider(ShardingKeyProvider shardingKeyProvider) {
        this.shardingKeyProvider = shardingKeyProvider;
    }
}
