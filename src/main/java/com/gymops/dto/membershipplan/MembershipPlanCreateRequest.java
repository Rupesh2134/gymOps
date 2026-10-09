package com.gymops.dto.membershipplan;

import com.gymops.entity.MembershipPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Plan name, duration, price, and optional description used to create a membership plan.")
public class MembershipPlanCreateRequest {

    @Schema(description = "Unique plan name.", example = "Standard", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name must not exceed 100 characters")
    private String name;

    @Schema(description = "Plan duration in months.", example = "3", minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Duration in months is required")
    @Min(value = 1, message = "Duration must be at least 1 month")
    private Integer durationInMonths;

    @Schema(description = "Plan price.", example = "79.99", minimum = "0.01",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Boolean active = true;

    public MembershipPlanCreateRequest() {
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

    public MembershipPlan toEntity() {
        MembershipPlan plan = new MembershipPlan();
        plan.setName(this.name);
        plan.setDurationInMonths(this.durationInMonths);
        plan.setPrice(this.price);
        plan.setDescription(this.description);
        plan.setActive(this.active != null ? this.active : true);
        return plan;
    }
}
