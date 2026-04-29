package com.gmmx.mvp.service;

import com.gmmx.mvp.core.tenant.TenantContext;
import com.gmmx.mvp.dto.LeadDtos;
import com.gmmx.mvp.entity.Lead;
import com.gmmx.mvp.repository.LeadRepository;
import com.gmmx.mvp.repository.TrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final TrainerProfileRepository trainerProfileRepository;

    @Transactional
    public LeadDtos.LeadResponse createLead(LeadDtos.LeadCreateRequest request) {
        Lead lead = new Lead();
        lead.setFullName(request.getFullName());
        lead.setMobile(request.getMobile());
        lead.setEmail(request.getEmail());
        lead.setNotes(request.getNotes());
        lead.setSource(request.getSource());
        lead.setInterestLevel(request.getInterestLevel());
        
        if (request.getAssignedTrainerId() != null) {
            lead.setAssignedTrainer(trainerProfileRepository.findById(request.getAssignedTrainerId()).orElse(null));
        }
        
        lead.setTrialDate(request.getTrialDate());

        lead = leadRepository.save(lead);
        return mapToResponse(lead);
    }

    @Transactional(readOnly = true)
    public Page<LeadDtos.LeadResponse> getAllLeads(Pageable pageable) {
        return leadRepository.findAllByTenantId(TenantContext.getTenantId(), pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public LeadDtos.LeadResponse updateStatus(UUID id, LeadDtos.LeadStatusUpdateRequest request) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        lead.setStatus(request.getStatus());
        lead = leadRepository.save(lead);
        return mapToResponse(lead);
    }

    private LeadDtos.LeadResponse mapToResponse(Lead lead) {
        LeadDtos.LeadResponse response = new LeadDtos.LeadResponse();
        response.setId(lead.getId());
        response.setFullName(lead.getFullName());
        response.setMobile(lead.getMobile());
        response.setEmail(lead.getEmail());
        response.setNotes(lead.getNotes());
        response.setStatus(lead.getStatus());
        response.setSource(lead.getSource());
        response.setInterestLevel(lead.getInterestLevel());
        response.setTrialDate(lead.getTrialDate());
        response.setTrialCompleted(lead.isTrialCompleted());
        response.setCreatedAt(lead.getCreatedAt());
        
        if (lead.getAssignedTrainer() != null) {
            response.setAssignedTrainerId(lead.getAssignedTrainer().getId());
            response.setAssignedTrainerName(lead.getAssignedTrainer().getUser().getFullName());
        }
        
        return response;
    }
}
