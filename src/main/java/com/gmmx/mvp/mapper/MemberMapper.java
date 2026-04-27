package com.gmmx.mvp.mapper;

import com.gmmx.mvp.dto.MemberDtos;
import com.gmmx.mvp.entity.MemberProfile;
import com.gmmx.mvp.entity.TrainerProfile;
import com.gmmx.mvp.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.mobile", target = "mobile")
    @Mapping(source = "assignedTrainer.id", target = "assignedTrainerId")
    @Mapping(source = "assignedTrainer.user.fullName", target = "assignedTrainerName")
    MemberDtos.MemberResponse toResponse(MemberProfile profile);
}
