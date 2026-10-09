package com.gymops.service;

import com.gymops.dto.member.MemberCreateRequest;
import com.gymops.dto.member.MemberResponse;
import com.gymops.dto.member.MemberUpdateRequest;
import com.gymops.entity.Gender;
import com.gymops.entity.Member;
import com.gymops.entity.MemberStatus;
import com.gymops.exception.DuplicateResourceException;
import com.gymops.exception.ResourceNotFoundException;
import com.gymops.repository.MemberRepository;
import com.gymops.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberServiceImpl(memberRepository);
    }

    @Test
    void createMember_shouldSaveAndReturnResponse() {
        MemberCreateRequest request = new MemberCreateRequest();
        request.setFirstName("Asha");
        request.setPhone("+15551234567");
        request.setJoinDate(LocalDate.now());
        request.setEmail("asha@example.com");
        request.setGender(Gender.FEMALE);

        when(memberRepository.existsByEmail("asha@example.com")).thenReturn(false);
        when(memberRepository.existsByPhone("+15551234567")).thenReturn(false);

        Member member = new Member();
        setId(member, 1L);
        member.setFirstName("Asha");
        member.setPhone("+15551234567");
        member.setJoinDate(LocalDate.now());
        member.setEmail("asha@example.com");
        member.setGender(Gender.FEMALE);
        member.setStatus(MemberStatus.ACTIVE);

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberResponse response = memberService.createMember(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Asha", response.getFirstName());
        assertEquals("asha@example.com", response.getEmail());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void createMember_shouldThrowWhenEmailAlreadyExists() {
        MemberCreateRequest request = new MemberCreateRequest();
        request.setFirstName("Asha");
        request.setPhone("+15551234567");
        request.setJoinDate(LocalDate.now());
        request.setEmail("asha@example.com");

        when(memberRepository.existsByEmail("asha@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> memberService.createMember(request));
    }

    @Test
    void getMemberById_shouldReturnMember() {
        Member member = new Member();
        setId(member, 2L);
        member.setFirstName("Nisha");
        member.setPhone("+15551234568");
        member.setJoinDate(LocalDate.now());
        member.setStatus(MemberStatus.ACTIVE);

        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));

        MemberResponse response = memberService.getMemberById(2L);

        assertEquals(2L, response.getId());
        assertEquals("Nisha", response.getFirstName());
    }

    @Test
    void getMemberById_shouldThrowWhenMissing() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.getMemberById(999L));
    }

    @Test
    void updateMember_shouldUpdateOnlyProvidedFields() {
        Member member = new Member();
        setId(member, 3L);
        member.setFirstName("Old");
        member.setLastName("Name");
        member.setEmail("old@example.com");
        member.setPhone("+15550000000");
        member.setJoinDate(LocalDate.now().minusDays(20));
        member.setStatus(MemberStatus.ACTIVE);

        MemberUpdateRequest request = new MemberUpdateRequest();
        request.setFirstName("New");
        request.setPhone("+15550000001");

        when(memberRepository.findById(3L)).thenReturn(Optional.of(member));
        when(memberRepository.existsByPhone("+15550000001")).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberResponse response = memberService.updateMember(3L, request);

        assertEquals("New", response.getFirstName());
        assertEquals("+15550000001", response.getPhone());
    }

    @Test
    void deleteMember_shouldSoftDelete() {
        Member member = new Member();
        setId(member, 4L);
        member.setStatus(MemberStatus.ACTIVE);

        when(memberRepository.findById(4L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        memberService.deleteMember(4L);

        assertEquals(MemberStatus.INACTIVE, member.getStatus());
        verify(memberRepository).save(member);
    }

    @Test
    void getAllMembers_shouldReturnList() {
        Member member = new Member();
        setId(member, 5L);
        member.setFirstName("Ravi");
        member.setPhone("+15551234569");
        member.setJoinDate(LocalDate.now());
        member.setStatus(MemberStatus.ACTIVE);

        when(memberRepository.findAll()).thenReturn(List.of(member));

        List<MemberResponse> result = memberService.getAllMembers();

        assertEquals(1, result.size());
        assertEquals("Ravi", result.get(0).getFirstName());
    }

    private void setId(Member member, long id) {
        try {
            Field field = Member.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(member, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
