package com.gymops.service.impl;

import com.gymops.dto.membership.MembershipCreateRequest;
import com.gymops.dto.membership.MembershipResponse;
import com.gymops.dto.membership.MembershipUpdateRequest;
import com.gymops.entity.Member;
import com.gymops.entity.Membership;
import com.gymops.entity.MembershipPlan;
import com.gymops.entity.MembershipStatus;
import com.gymops.exception.BusinessRuleException;
import com.gymops.exception.DuplicateResourceException;
import com.gymops.exception.ResourceNotFoundException;
import com.gymops.repository.MemberRepository;
import com.gymops.repository.MembershipPlanRepository;
import com.gymops.repository.MembershipRepository;
import com.gymops.service.MembershipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;
    private final MembershipPlanRepository membershipPlanRepository;

    public MembershipServiceImpl(MembershipRepository membershipRepository,
                                MemberRepository memberRepository,
                                MembershipPlanRepository membershipPlanRepository) {
        this.membershipRepository = membershipRepository;
        this.memberRepository = memberRepository;
        this.membershipPlanRepository = membershipPlanRepository;
    }

    @Override
    @Transactional
    public MembershipResponse createMembership(MembershipCreateRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Membership request must not be null");
        }

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));

        MembershipPlan plan = membershipPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + request.getPlanId()));

        if (request.getStartDate() == null) {
            throw new BusinessRuleException("Membership start date is required");
        }

        if (membershipRepository.existsByMemberAndMembershipPlanAndStatus(member, plan, MembershipStatus.ACTIVE)) {
            throw new DuplicateResourceException("Member already has an active membership for this plan");
        }

        Membership membership = request.toEntity(member, plan);
        Membership savedMembership = membershipRepository.save(membership);
        return MembershipResponse.fromEntity(savedMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipResponse getMembershipById(Long id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + id));
        return MembershipResponse.fromEntity(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipResponse> getAllMemberships() {
        return membershipRepository.findAll().stream()
                .map(MembershipResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public MembershipResponse updateMembership(Long id, MembershipUpdateRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Membership update request must not be null");
        }

        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + id));

        if (request.getMemberId() != null) {
            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));
            membership.setMember(member);
        }

        if (request.getPlanId() != null) {
            MembershipPlan plan = membershipPlanRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + request.getPlanId()));
            membership.setMembershipPlan(plan);
        }

        if (request.getStartDate() != null) {
            membership.setStartDate(request.getStartDate());
        }

        if (request.getStatus() != null) {
            membership.setStatus(request.getStatus());
        }

        Membership updatedMembership = membershipRepository.save(membership);
        return MembershipResponse.fromEntity(updatedMembership);
    }

    @Override
    @Transactional
    public void deleteMembership(Long id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + id));

        if (membership.getStatus() == MembershipStatus.CANCELLED) {
            throw new BusinessRuleException("Membership is already cancelled and cannot be deleted again");
        }

        membership.setStatus(MembershipStatus.CANCELLED);
        membershipRepository.save(membership);
    }
}
