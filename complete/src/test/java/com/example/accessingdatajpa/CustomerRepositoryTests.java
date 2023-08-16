/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.accessingdatajpa;

import com.example.accessingdatajpa.aspects.DirectShardRoutingAspect;
import com.example.accessingdatajpa.datasources.OracleShardingDataSource;
import com.example.accessingdatajpa.datasources.ShardingKeyProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ConnectionBuilder;
import java.sql.SQLException;
import java.sql.ShardingKey;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CustomerRepositoryTests {
	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private DataSource dataSource;
	@Autowired
	DirectShardRoutingAspect directShardRoutingAspect;

	@Test
	public void testFindByLastName() {
		Person person = new Person("anir", "lahyane");
		personRepository.save(person);
	}

	@Test
	public void test() throws SQLException {
		Assertions.assertInstanceOf(OracleShardingDataSource.class, dataSource);

		DataSource proxyDataSource = mock();
		DataSource directRoutingDataSource = mock(RETURNS_DEEP_STUBS);
		Connection standardConnection = mock();
		Connection shardConnection = mock();
		ShardingKeyProvider provider = mock();
		ShardingKey key = mock();
		ShardingKey superKey = mock();
		ConnectionBuilder connectionBuilder = mock();
		ConnectionBuilder shardConnectionBuilder = mock();

		given(provider.getShardingKey()).willReturn(key);
		given(provider.getSuperShardingKey()).willReturn(superKey);

		given(proxyDataSource.createConnectionBuilder()).willReturn(connectionBuilder);
		given(connectionBuilder.build()).willReturn(standardConnection);

		when(directRoutingDataSource.createConnectionBuilder().shardingKey(key).superShardingKey(superKey)).thenReturn(shardConnectionBuilder);
		given(shardConnectionBuilder.build()).willReturn(shardConnection);


		((OracleShardingDataSource) dataSource).setDirectRoutingDataSource(directRoutingDataSource);
		((OracleShardingDataSource) dataSource).setProxyDataSource(proxyDataSource);


		directShardRoutingAspect.setShardingKeyProvider(provider);

		Person person = new Person("test", "test");
		personRepository.save(person);
	}
}
