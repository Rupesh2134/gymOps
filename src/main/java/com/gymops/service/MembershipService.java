package com.gymops.service;

import com.gymops.dto.membership.MembershipCreateRequest;
import com.gymops.dto.membership.MembershipResponse;
import com.gymops.dto.membership.MembershipUpdateRequest;

import java.util.List;

public interface MembershipService {

    MembershipResponse createMembership(MembershipCreateRequest request);

    MembershipResponse getMembershipById(Long id);

    List<MembershipResponse> getAllMemberships();

    MembershipResponse updateMembership(Long id, MembershipUpdateRequest request);

    void deleteMembership(Long id);
}
