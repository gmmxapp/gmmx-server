package com.gmmx.mvp.mapper;

import com.gmmx.mvp.dto.AuthDtos;
import com.gmmx.mvp.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    AuthDtos.UserResponse toResponse(UserAccount user);
}
