package com.gymops.service;

import com.gymops.dto.membership.MembershipCreateRequest;
import com.gymops.dto.membership.MembershipResponse;
import com.gymops.dto.membership.MembershipUpdateRequest;
import com.gymops.entity.Member;
import com.gymops.entity.MemberStatus;
import com.gymops.entity.Membership;
import com.gymops.entity.MembershipPlan;
import com.gymops.entity.MembershipStatus;
import com.gymops.exception.BusinessRuleException;
import com.gymops.exception.ResourceNotFoundException;
import com.gymops.repository.MemberRepository;
import com.gymops.repository.MembershipPlanRepository;
import com.gymops.repository.MembershipRepository;
import com.gymops.service.impl.MembershipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MembershipPlanRepository membershipPlanRepository;

    @InjectMocks
    private MembershipServiceImpl membershipService;

    @BeforeEach
    void setUp() {
        membershipService = new MembershipServiceImpl(membershipRepository, memberRepository, membershipPlanRepository);
    }

    @Test
    void createMembership_shouldSaveAndReturnResponse() {
        Member member = new Member();
        setMemberId(member, 1L);
        member.setStatus(MemberStatus.ACTIVE);

        MembershipPlan plan = new MembershipPlan();
        setPlanId(plan, 10L);
        plan.setName("Gold");
        plan.setDurationInMonths(6);
        plan.setPrice(new BigDecimal("2500.00"));
        plan.setActive(true);

        MembershipCreateRequest request = new MembershipCreateRequest();
        request.setMemberId(1L);
        request.setPlanId(10L);
        request.setStartDate(LocalDate.now());

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(membershipPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(membershipRepository.existsByMemberAndMembershipPlanAndStatus(member, plan, MembershipStatus.ACTIVE)).thenReturn(false);
        when(membershipRepository.save(any(Membership.class))).thenAnswer(invocation -> {
            Membership saved = invocation.getArgument(0);
            setMembershipId(saved, 99L);
            return saved;
        });

        MembershipResponse response = membershipService.createMembership(request);

        assertNotNull(response);
        assertEquals(99L, response.getId());
        assertEquals(1L, response.getMemberId());
        assertEquals(10L, response.getPlanId());
    }

    @Test
    void createMembership_shouldThrowWhenDuplicateActiveMembershipForSamePlan() {
        Member member = new Member();
        setMemberId(member, 1L);
        member.setStatus(MemberStatus.ACTIVE);

        MembershipPlan plan = new MembershipPlan();
        setPlanId(plan, 10L);
        plan.setActive(true);

        MembershipCreateRequest request = new MembershipCreateRequest();
        request.setMemberId(1L);
        request.setPlanId(10L);
        request.setStartDate(LocalDate.now());

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(membershipPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(membershipRepository.existsByMemberAndMembershipPlanAndStatus(member, plan, MembershipStatus.ACTIVE)).thenReturn(true);

        assertThrows(com.gymops.exception.DuplicateResourceException.class, () -> membershipService.createMembership(request));
    }

    @Test
    void getMembershipById_shouldReturnMembership() {
        Membership membership = new Membership();
        setMembershipId(membership, 11L);
        membership.setMember(new Member());
        setMemberId(membership.getMember(), 1L);
        membership.setMembershipPlan(plan());
        membership.setStartDate(LocalDate.now());
        membership.setStatus(MembershipStatus.ACTIVE);
        setMembershipEndDate(membership, LocalDate.now().plusMonths(3));

        when(membershipRepository.findById(11L)).thenReturn(Optional.of(membership));

        MembershipResponse response = membershipService.getMembershipById(11L);

        assertEquals(11L, response.getId());
        assertEquals(1L, response.getMemberId());
    }

    @Test
    void updateMembership_shouldApplyNewDatesAndStatus() {
        Membership membership = new Membership();
        setMembershipId(membership, 12L);
        membership.setMember(new Member());
        setMemberId(membership.getMember(), 2L);
        membership.setMembershipPlan(plan());
        membership.setStartDate(LocalDate.of(2024, 1, 1));
        membership.setStatus(MembershipStatus.ACTIVE);

        MembershipUpdateRequest request = new MembershipUpdateRequest();
        request.setStatus(MembershipStatus.CANCELLED);

        when(membershipRepository.findById(12L)).thenReturn(Optional.of(membership));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MembershipResponse response = membershipService.updateMembership(12L, request);

        assertEquals(MembershipStatus.CANCELLED, response.getStatus());
    }

    @Test
    void deleteMembership_shouldCancelMembership() {
        Membership membership = new Membership();
        setMembershipId(membership, 13L);
        membership.setMember(new Member());
        setMemberId(membership.getMember(), 3L);
        membership.setMembershipPlan(plan());
        membership.setStartDate(LocalDate.now());
        membership.setStatus(MembershipStatus.ACTIVE);

        when(membershipRepository.findById(13L)).thenReturn(Optional.of(membership));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(invocation -> invocation.getArgument(0));

        membershipService.deleteMembership(13L);

        assertEquals(MembershipStatus.CANCELLED, membership.getStatus());
        verify(membershipRepository).save(membership);
    }

    @Test
    void getAllMemberships_shouldReturnList() {
        Membership membership = new Membership();
        setMembershipId(membership, 14L);
        membership.setMember(new Member());
        setMemberId(membership.getMember(), 4L);
        membership.setMembershipPlan(plan());
        membership.setStartDate(LocalDate.now());
        membership.setStatus(MembershipStatus.ACTIVE);

        when(membershipRepository.findAll()).thenReturn(List.of(membership));

        List<MembershipResponse> response = membershipService.getAllMemberships();

        assertEquals(1, response.size());
        assertEquals(14L, response.get(0).getId());
    }

    @Test
    void getMembershipById_shouldThrowWhenMissing() {
        when(membershipRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> membershipService.getMembershipById(404L));
    }

    private MembershipPlan plan() {
        MembershipPlan plan = new MembershipPlan();
        setPlanId(plan, 20L);
        plan.setName("Silver");
        plan.setDurationInMonths(3);
        plan.setPrice(new BigDecimal("900.00"));
        plan.setActive(true);
        return plan;
    }

    private void setMemberId(Member member, long id) {
        try {
            Field field = Member.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(member, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setPlanId(MembershipPlan plan, long id) {
        try {
            Field field = MembershipPlan.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(plan, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMembershipId(Membership membership, long id) {
        try {
            Field field = Membership.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(membership, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMembershipEndDate(Membership membership, LocalDate endDate) {
        try {
            Field field = Membership.class.getDeclaredField("endDate");
            field.setAccessible(true);
            field.set(membership, endDate);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
