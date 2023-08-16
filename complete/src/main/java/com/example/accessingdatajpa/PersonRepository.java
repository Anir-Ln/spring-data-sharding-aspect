package com.example.accessingdatajpa;

import com.example.accessingdatajpa.annotation.DirectShardRouting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends CrudRepository<Person, Long> {
	@DirectShardRouting(hints = {"hint1", "hint2"})
	@Override
	<S extends Person> S save(@Param("entity") S entity);
}
