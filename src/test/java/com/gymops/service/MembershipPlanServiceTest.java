package com.gymops.service;

import com.gymops.dto.membershipplan.MembershipPlanCreateRequest;
import com.gymops.dto.membershipplan.MembershipPlanResponse;
import com.gymops.dto.membershipplan.MembershipPlanUpdateRequest;
import com.gymops.entity.MembershipPlan;
import com.gymops.exception.DuplicateResourceException;
import com.gymops.exception.ResourceNotFoundException;
import com.gymops.repository.MembershipPlanRepository;
import com.gymops.service.impl.MembershipPlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipPlanServiceTest {

    @Mock
    private MembershipPlanRepository membershipPlanRepository;

    @InjectMocks
    private MembershipPlanServiceImpl membershipPlanService;

    @BeforeEach
    void setUp() {
        membershipPlanService = new MembershipPlanServiceImpl(membershipPlanRepository);
    }

    @Test
    void createPlan_shouldSaveAndReturnResponse() {
        MembershipPlanCreateRequest request = new MembershipPlanCreateRequest();
        request.setName("Gold");
        request.setDurationInMonths(6);
        request.setPrice(new BigDecimal("2500.00"));
        request.setDescription("Premium access");

        when(membershipPlanRepository.existsByName("Gold")).thenReturn(false);

        MembershipPlan plan = new MembershipPlan();
        setId(plan, 1L);
        plan.setName("Gold");
        plan.setDurationInMonths(6);
        plan.setPrice(new BigDecimal("2500.00"));
        plan.setDescription("Premium access");
        plan.setActive(true);

        when(membershipPlanRepository.save(any(MembershipPlan.class))).thenReturn(plan);

        MembershipPlanResponse response = membershipPlanService.createPlan(request);

        assertNotNull(response);
        assertEquals("Gold", response.getName());
        assertEquals(new BigDecimal("2500.00"), response.getPrice());
    }

    @Test
    void createPlan_shouldThrowWhenDuplicateName() {
        MembershipPlanCreateRequest request = new MembershipPlanCreateRequest();
        request.setName("Gold");
        request.setDurationInMonths(6);
        request.setPrice(new BigDecimal("2500.00"));

        when(membershipPlanRepository.existsByName("Gold")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> membershipPlanService.createPlan(request));
    }

    @Test
    void getPlanById_shouldReturnPlan() {
        MembershipPlan plan = new MembershipPlan();
        setId(plan, 2L);
        plan.setName("Silver");
        plan.setDurationInMonths(3);
        plan.setPrice(new BigDecimal("1200.50"));
        plan.setActive(true);

        when(membershipPlanRepository.findById(2L)).thenReturn(Optional.of(plan));

        MembershipPlanResponse response = membershipPlanService.getPlanById(2L);

        assertEquals(2L, response.getId());
        assertEquals("Silver", response.getName());
    }

    @Test
    void updatePlan_shouldUpdateProvidedFields() {
        MembershipPlan plan = new MembershipPlan();
        setId(plan, 3L);
        plan.setName("Old Plan");
        plan.setDurationInMonths(4);
        plan.setPrice(new BigDecimal("400.00"));
        plan.setActive(true);

        MembershipPlanUpdateRequest request = new MembershipPlanUpdateRequest();
        request.setName("Updated Plan");
        request.setPrice(new BigDecimal("600.00"));

        when(membershipPlanRepository.findById(3L)).thenReturn(Optional.of(plan));
        when(membershipPlanRepository.existsByName("Updated Plan")).thenReturn(false);
        when(membershipPlanRepository.save(any(MembershipPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MembershipPlanResponse response = membershipPlanService.updatePlan(3L, request);

        assertEquals("Updated Plan", response.getName());
        assertEquals(new BigDecimal("600.00"), response.getPrice());
    }

    @Test
    void deletePlan_shouldDeactivatePlan() {
        MembershipPlan plan = new MembershipPlan();
        setId(plan, 4L);
        plan.setName("Platinum");
        plan.setDurationInMonths(12);
        plan.setPrice(new BigDecimal("5000.00"));
        plan.setActive(true);

        when(membershipPlanRepository.findById(4L)).thenReturn(Optional.of(plan));
        when(membershipPlanRepository.save(any(MembershipPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        membershipPlanService.deletePlan(4L);

        assertFalse(plan.getActive());
        verify(membershipPlanRepository).save(plan);
    }

    @Test
    void getAllPlans_shouldReturnList() {
        MembershipPlan plan = new MembershipPlan();
        setId(plan, 5L);
        plan.setName("Basic");
        plan.setDurationInMonths(1);
        plan.setPrice(new BigDecimal("500.00"));
        plan.setActive(true);

        when(membershipPlanRepository.findAll()).thenReturn(List.of(plan));

        List<MembershipPlanResponse> result = membershipPlanService.getAllPlans();

        assertEquals(1, result.size());
        assertEquals("Basic", result.get(0).getName());
    }

    @Test
    void getPlanById_shouldThrowWhenMissing() {
        when(membershipPlanRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> membershipPlanService.getPlanById(404L));
    }

    private void setId(MembershipPlan plan, long id) {
        try {
            Field field = MembershipPlan.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(plan, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
