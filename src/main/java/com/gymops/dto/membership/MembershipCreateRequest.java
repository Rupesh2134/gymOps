package com.gymops.dto.membership;

import com.gymops.entity.Member;
import com.gymops.entity.Membership;
import com.gymops.entity.MembershipPlan;
import com.gymops.entity.MembershipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@Schema(description = "Member, plan, and start date for a new membership.")
public class MembershipCreateRequest {

    @Schema(description = "ID of the member enrolling.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Member id is required")
    private Long memberId;

    @Schema(description = "ID of the membership plan.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Plan id is required")
    private Long planId;

    @Schema(description = "Membership start date.", example = "2026-10-09",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    private MembershipStatus status = MembershipStatus.ACTIVE;

    public MembershipCreateRequest() {
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public Membership toEntity(Member member, MembershipPlan membershipPlan) {
        Membership membership = new Membership();
        membership.setMember(member);
        membership.setMembershipPlan(membershipPlan);
        membership.setStartDate(this.startDate);
        membership.setStatus(this.status != null ? this.status : MembershipStatus.ACTIVE);
        return membership;
    }
}
