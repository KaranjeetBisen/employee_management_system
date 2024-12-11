package com.advancedb.advancedb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.advancedb.advancedb.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByEmpid(String empid);

    @Query(value = "SELECT * FROM employee WHERE LOWER(fullname) LIKE LOWER(CONCAT('%', :filter, '%'))", nativeQuery = true)
    List<Employee> findEmployeesByFullname(@Param("filter") String filter);

}