package com.gymops.dto.membership;

import com.gymops.entity.MembershipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@Schema(description = "Optional membership fields to update; omitted or null fields remain unchanged.")
public class MembershipUpdateRequest {

    private Long memberId;

    private Long planId;

    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    private MembershipStatus status;

    public MembershipUpdateRequest() {
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
}
