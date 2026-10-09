package com.gymops.dto.membership;

import com.gymops.entity.Member;
import com.gymops.entity.Membership;
import com.gymops.entity.MembershipPlan;
import com.gymops.entity.MembershipStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Membership details, including the associated member and plan.")
public class MembershipResponse {

    private Long id;
    private Long memberId;
    private String memberName;
    private Long planId;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private MembershipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MembershipResponse() {
    }

    public static MembershipResponse fromEntity(Membership membership) {
        if (membership == null) {
            return null;
        }

        MembershipResponse response = new MembershipResponse();
        response.setId(membership.getId());

        Member member = membership.getMember();
        if (member != null) {
            response.setMemberId(member.getId());
            response.setMemberName(member.getFirstName() + (member.getLastName() != null ? " " + member.getLastName() : ""));
        }

        MembershipPlan plan = membership.getMembershipPlan();
        if (plan != null) {
            response.setPlanId(plan.getId());
            response.setPlanName(plan.getName());
        }

        response.setStartDate(membership.getStartDate());
        response.setEndDate(membership.getEndDate());
        response.setStatus(membership.getStatus());
        response.setCreatedAt(membership.getCreatedAt());
        response.setUpdatedAt(membership.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
