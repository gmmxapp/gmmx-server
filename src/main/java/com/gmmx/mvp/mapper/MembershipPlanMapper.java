package com.gmmx.mvp.mapper;

import com.gmmx.mvp.dto.MembershipPlanDtos;
import com.gmmx.mvp.entity.MembershipPlan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MembershipPlanMapper {

    MembershipPlan toEntity(MembershipPlanDtos.MembershipPlanRequest request);

    MembershipPlanDtos.MembershipPlanResponse toResponse(MembershipPlan entity);

    void updateEntity(MembershipPlanDtos.MembershipPlanRequest request, @MappingTarget MembershipPlan entity);
}
