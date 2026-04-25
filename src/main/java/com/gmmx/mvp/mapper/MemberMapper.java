package com.gmmx.mvp.mapper;

import com.gmmx.mvp.dto.MemberDtos;
import com.gmmx.mvp.entity.MemberProfile;
import com.gmmx.mvp.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.mobile", target = "mobile")
    MemberDtos.MemberResponse toResponse(MemberProfile profile);
}
