package com.gymops.repository;

import com.gymops.entity.Member;
import com.gymops.entity.Membership;
import com.gymops.entity.MembershipPlan;
import com.gymops.entity.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByMember(Member member);

    List<Membership> findByMembershipPlan(MembershipPlan membershipPlan);

    List<Membership> findByStatus(MembershipStatus status);

    long countByStatus(MembershipStatus status);

    long countByStatusAndEndDateBetween(MembershipStatus status, LocalDate startDate, LocalDate endDate);

    boolean existsByMemberAndMembershipPlanAndStatus(Member member, MembershipPlan membershipPlan, MembershipStatus status);
}
