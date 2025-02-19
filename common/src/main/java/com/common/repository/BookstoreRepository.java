package com.common.repository;

import com.common.entity.Bookstore;
import com.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookstoreRepository extends JpaRepository<Bookstore, Long> {

    List<Bookstore> findAllByMember(Member member);

}
