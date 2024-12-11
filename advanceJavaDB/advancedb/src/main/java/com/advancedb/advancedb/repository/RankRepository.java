package com.advancedb.advancedb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advancedb.advancedb.model.Ranks;

@Repository
public interface RankRepository extends JpaRepository<Ranks, Long> {
    List<Ranks> findByRankdesc(String rankdesc);

}
