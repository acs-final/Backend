package com.common.repository;

import com.common.entity.Body;
import com.common.entity.Fairytale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyRepository extends JpaRepository<Body, Long> {

    List<Body> findAllByFairytale(Fairytale fairytale);

}
