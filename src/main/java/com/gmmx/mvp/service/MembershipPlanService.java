package com.gmmx.mvp.service;

import com.gmmx.mvp.dto.MembershipPlanDtos;
import com.gmmx.mvp.entity.MembershipPlan;
import com.gmmx.mvp.exception.ResourceNotFoundException;
import com.gmmx.mvp.mapper.MembershipPlanMapper;
import com.gmmx.mvp.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;
    private final MembershipPlanMapper planMapper;

    @Transactional
    public MembershipPlanDtos.MembershipPlanResponse createPlan(MembershipPlanDtos.MembershipPlanRequest request) {
        MembershipPlan plan = planMapper.toEntity(request);
        return planMapper.toResponse(planRepository.save(plan));
    }

    public List<MembershipPlanDtos.MembershipPlanResponse> getAllPlans() {
        return planRepository.findAllByTenantId(com.gmmx.mvp.core.tenant.TenantContext.getTenantId()).stream()
                .map(planMapper::toResponse)
                .collect(Collectors.toList());
    }

    public MembershipPlanDtos.MembershipPlanResponse getPlanById(UUID id) {
        return planRepository.findByIdAndTenantId(id, com.gmmx.mvp.core.tenant.TenantContext.getTenantId())
                .map(planMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Membership Plan not found"));
    }

    @Transactional
    public MembershipPlanDtos.MembershipPlanResponse updatePlan(UUID id, MembershipPlanDtos.MembershipPlanRequest request) {
        MembershipPlan plan = planRepository.findByIdAndTenantId(id, com.gmmx.mvp.core.tenant.TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership Plan not found"));
        planMapper.updateEntity(request, plan);
        return planMapper.toResponse(planRepository.save(plan));
    }

    @Transactional
    public void deletePlan(UUID id) {
        MembershipPlan plan = planRepository.findByIdAndTenantId(id, com.gmmx.mvp.core.tenant.TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership Plan not found"));
        planRepository.delete(plan);
    }
}
