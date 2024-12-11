package com.advancedb.advancedb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advancedb.advancedb.model.ShadowEmployee;

@Repository
public interface ShadowEmployeeRepository extends JpaRepository<ShadowEmployee, Long> {

}