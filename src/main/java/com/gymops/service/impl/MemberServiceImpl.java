package com.gymops.service.impl;

import com.gymops.dto.member.MemberCreateRequest;
import com.gymops.dto.member.MemberResponse;
import com.gymops.dto.member.MemberUpdateRequest;
import com.gymops.entity.Member;
import com.gymops.entity.MemberStatus;
import com.gymops.exception.BusinessRuleException;
import com.gymops.exception.DuplicateResourceException;
import com.gymops.exception.ResourceNotFoundException;
import com.gymops.repository.MemberRepository;
import com.gymops.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public MemberResponse createMember(MemberCreateRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Member request must not be null");
        }

        validateDuplicateEmail(request.getEmail());
        validateDuplicatePhone(request.getPhone());

        Member member = request.toEntity();
        Member savedMember = memberRepository.save(member);
        return MemberResponse.fromEntity(savedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        return MemberResponse.fromEntity(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public MemberResponse updateMember(Long id, MemberUpdateRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Member update request must not be null");
        }

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(member.getEmail())) {
            validateDuplicateEmail(request.getEmail());
            member.setEmail(request.getEmail());
        }
        if (request.getPhone() != null && !request.getPhone().equals(member.getPhone())) {
            validateDuplicatePhone(request.getPhone());
            member.setPhone(request.getPhone());
        }
        if (request.getFirstName() != null) {
            member.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            member.setLastName(request.getLastName());
        }
        if (request.getDateOfBirth() != null) {
            member.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            member.setGender(request.getGender());
        }
        if (request.getJoinDate() != null) {
            member.setJoinDate(request.getJoinDate());
        }
        if (request.getStatus() != null) {
            member.setStatus(request.getStatus());
        }

        Member updatedMember = memberRepository.save(member);
        return MemberResponse.fromEntity(updatedMember);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        if (member.getStatus() == MemberStatus.INACTIVE) {
            throw new BusinessRuleException("Member is already inactive and cannot be deleted again");
        }

        member.setStatus(MemberStatus.INACTIVE);
        memberRepository.save(member);
    }

    private void validateDuplicateEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        if (memberRepository.existsByEmail(email.trim())) {
            throw new DuplicateResourceException("Email already exists: " + email.trim());
        }
    }

    private void validateDuplicatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return;
        }

        if (memberRepository.existsByPhone(phone.trim())) {
            throw new DuplicateResourceException("Phone already exists: " + phone.trim());
        }
    }
}
