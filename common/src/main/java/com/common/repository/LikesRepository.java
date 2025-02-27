package com.common.repository;

import com.common.entity.Body;
import com.common.entity.Fairytale;
import com.common.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Likes findByFairytaleAndMemberId(Fairytale fairytale, String memberId);

    Boolean existsByFairytaleAndMemberId(Fairytale fairytale, String memberId);

}
