package com.common.repository;

import com.common.entity.Fairytale;
import com.common.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {


    Image findFirstByFairytale(Fairytale fairytale);

    List<Image> findAllByFairytale(Fairytale fairytaleId);
}
