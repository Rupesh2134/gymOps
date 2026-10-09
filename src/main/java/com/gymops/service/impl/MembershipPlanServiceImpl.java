package com.gymops.service.impl;

import com.gymops.dto.membershipplan.MembershipPlanCreateRequest;
import com.gymops.dto.membershipplan.MembershipPlanResponse;
import com.gymops.dto.membershipplan.MembershipPlanUpdateRequest;
import com.gymops.entity.MembershipPlan;
import com.gymops.exception.BusinessRuleException;
import com.gymops.exception.DuplicateResourceException;
import com.gymops.exception.ResourceNotFoundException;
import com.gymops.repository.MembershipPlanRepository;
import com.gymops.service.MembershipPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;

    public MembershipPlanServiceImpl(MembershipPlanRepository membershipPlanRepository) {
        this.membershipPlanRepository = membershipPlanRepository;
    }

    @Override
    @Transactional
    public MembershipPlanResponse createPlan(MembershipPlanCreateRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Membership plan request must not be null");
        }

        if (request.getName() != null && membershipPlanRepository.existsByName(request.getName().trim())) {
            throw new DuplicateResourceException("Membership plan already exists with name: " + request.getName().trim());
        }

        if (request.getDurationInMonths() == null || request.getDurationInMonths() < 1) {
            throw new BusinessRuleException("Membership duration must be at least 1 month");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Membership plan price must be greater than zero");
        }

        MembershipPlan plan = request.toEntity();
        MembershipPlan savedPlan = membershipPlanRepository.save(plan);
        return MembershipPlanResponse.fromEntity(savedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipPlanResponse getPlanById(Long id) {
        MembershipPlan plan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + id));
        return MembershipPlanResponse.fromEntity(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlanResponse> getAllPlans() {
        return membershipPlanRepository.findAll().stream()
                .map(MembershipPlanResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public MembershipPlanResponse updatePlan(Long id, MembershipPlanUpdateRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Membership plan update request must not be null");
        }

        MembershipPlan plan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + id));

        if (request.getName() != null && !request.getName().equalsIgnoreCase(plan.getName())) {
            if (membershipPlanRepository.existsByName(request.getName().trim())) {
                throw new DuplicateResourceException("Membership plan already exists with name: " + request.getName().trim());
            }
            plan.setName(request.getName());
        }

        if (request.getDurationInMonths() != null) {
            if (request.getDurationInMonths() < 1) {
                throw new BusinessRuleException("Membership duration must be at least 1 month");
            }
            plan.setDurationInMonths(request.getDurationInMonths());
        }

        if (request.getPrice() != null) {
            if (request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BusinessRuleException("Membership plan price must be greater than zero");
            }
            plan.setPrice(request.getPrice());
        }

        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }

        if (request.getActive() != null) {
            plan.setActive(request.getActive());
        }

        MembershipPlan updatedPlan = membershipPlanRepository.save(plan);
        return MembershipPlanResponse.fromEntity(updatedPlan);
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        MembershipPlan plan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + id));

        if (Boolean.FALSE.equals(plan.getActive())) {
            throw new BusinessRuleException("Membership plan is already inactive and cannot be deleted again");
        }

        plan.setActive(false);
        membershipPlanRepository.save(plan);
    }
}
