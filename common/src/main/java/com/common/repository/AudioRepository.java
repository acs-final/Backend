package com.common.repository;

import com.common.entity.Audio;
import com.common.entity.Fairytale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AudioRepository extends JpaRepository<Audio, Long> {

    List<Audio> findAllByFairytale(Fairytale fairytale);

}
