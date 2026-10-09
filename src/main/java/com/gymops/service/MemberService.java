package com.gymops.service;

import com.gymops.dto.member.MemberCreateRequest;
import com.gymops.dto.member.MemberResponse;
import com.gymops.dto.member.MemberUpdateRequest;

import java.util.List;

public interface MemberService {

    MemberResponse createMember(MemberCreateRequest request);

    MemberResponse getMemberById(Long id);

    List<MemberResponse> getAllMembers();

    MemberResponse updateMember(Long id, MemberUpdateRequest request);

    void deleteMember(Long id);
}
