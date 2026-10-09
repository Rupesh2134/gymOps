package com.gymops.dto.membershipplan;

import com.gymops.entity.MembershipPlan;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Membership plan details returned by the API.")
public class MembershipPlanResponse {

    private Long id;
    private String name;
    private Integer durationInMonths;
    private BigDecimal price;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MembershipPlanResponse() {
    }

    public static MembershipPlanResponse fromEntity(MembershipPlan plan) {
        if (plan == null) {
            return null;
        }

        MembershipPlanResponse response = new MembershipPlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setDurationInMonths(plan.getDurationInMonths());
        response.setPrice(plan.getPrice());
        response.setDescription(plan.getDescription());
        response.setActive(plan.getActive());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Integer durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
