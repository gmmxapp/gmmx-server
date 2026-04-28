package com.gmmx.mvp.mapper;

import com.gmmx.mvp.dto.EquipmentDtos;
import com.gmmx.mvp.entity.Equipment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

    Equipment toEntity(EquipmentDtos.EquipmentRequest request);

    EquipmentDtos.EquipmentResponse toResponse(Equipment entity);

    void updateEntity(EquipmentDtos.EquipmentRequest request, @MappingTarget Equipment entity);
}
