package com.trackstar.usermanagementservice.mapper;

import com.trackstar.usermanagementservice.dto.UserDto;
import com.trackstar.usermanagementservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for the entity User and its DTO UserDto.
 */
@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);
}
