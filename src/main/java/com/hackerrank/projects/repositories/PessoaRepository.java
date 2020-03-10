package com.hackerrank.projects.repositories;

import org.springframework.data.jpa.repository.*;

import com.hackerrank.projects.controllers.Test;


public interface PessoaRepository extends JpaRepository<Test, Long>, JpaSpecificationExecutor<Test> {

}
