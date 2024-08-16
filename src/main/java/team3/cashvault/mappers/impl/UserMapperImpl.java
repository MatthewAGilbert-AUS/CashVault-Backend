package team3.cashvault.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import team3.cashvault.domain.dto.UserDto;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.mappers.Mapper;

import java.util.List;
import java.util.stream.Collectors;
@Component
public class UserMapperImpl implements Mapper<UserEntity, UserDto> {

    private final ModelMapper modelMapper;

    public UserMapperImpl(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }
    

    @Override
    public List<UserDto> mapList(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(this::mapTo)
                .collect(Collectors.toList());
    }
}
