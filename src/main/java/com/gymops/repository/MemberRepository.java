package com.gymops.repository;

import com.gymops.entity.Member;
import com.gymops.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    long countByStatus(MemberStatus status);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<Member> findByStatus(MemberStatus status);
}
