package com.gymops.service;

import com.gymops.dto.membershipplan.MembershipPlanCreateRequest;
import com.gymops.dto.membershipplan.MembershipPlanResponse;
import com.gymops.dto.membershipplan.MembershipPlanUpdateRequest;

import java.util.List;

public interface MembershipPlanService {

    MembershipPlanResponse createPlan(MembershipPlanCreateRequest request);

    MembershipPlanResponse getPlanById(Long id);

    List<MembershipPlanResponse> getAllPlans();

    MembershipPlanResponse updatePlan(Long id, MembershipPlanUpdateRequest request);

    void deletePlan(Long id);
}
