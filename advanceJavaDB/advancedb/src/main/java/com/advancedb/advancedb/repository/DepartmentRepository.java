package com.advancedb.advancedb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advancedb.advancedb.model.Departments;


@Repository
public interface DepartmentRepository extends JpaRepository<Departments, Long> {
    List<Departments> findByDeptname(String deptname);
}
