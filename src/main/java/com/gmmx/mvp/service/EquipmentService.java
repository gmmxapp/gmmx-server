package com.gmmx.mvp.service;

import com.gmmx.mvp.dto.EquipmentDtos;
import com.gmmx.mvp.entity.Equipment;
import com.gmmx.mvp.exception.ResourceNotFoundException;
import com.gmmx.mvp.mapper.EquipmentMapper;
import com.gmmx.mvp.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    @Transactional
    public EquipmentDtos.EquipmentResponse createEquipment(EquipmentDtos.EquipmentRequest request) {
        Equipment equipment = equipmentMapper.toEntity(request);
        return equipmentMapper.toResponse(equipmentRepository.save(equipment));
    }

    public List<EquipmentDtos.EquipmentResponse> getAllEquipment() {
        return equipmentRepository.findAllByTenantId(com.gmmx.mvp.core.tenant.TenantContext.getTenantId()).stream()
                .map(equipmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    public EquipmentDtos.EquipmentResponse getEquipmentById(UUID id) {
        return equipmentRepository.findByIdAndTenantId(id, com.gmmx.mvp.core.tenant.TenantContext.getTenantId())
                .map(equipmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
    }

    @Transactional
    public EquipmentDtos.EquipmentResponse updateEquipment(UUID id, EquipmentDtos.EquipmentRequest request) {
        Equipment equipment = equipmentRepository.findByIdAndTenantId(id, com.gmmx.mvp.core.tenant.TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
        equipmentMapper.updateEntity(request, equipment);
        return equipmentMapper.toResponse(equipmentRepository.save(equipment));
    }

    @Transactional
    public void deleteEquipment(UUID id) {
        Equipment equipment = equipmentRepository.findByIdAndTenantId(id, com.gmmx.mvp.core.tenant.TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
        equipmentRepository.delete(equipment);
    }
}
