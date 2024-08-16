package team3.cashvault.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import team3.cashvault.domain.dto.UpdateUserAdminDto;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.mappers.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UpdateUserAdminMapperImpl implements Mapper<UserEntity, UpdateUserAdminDto> {

    private final ModelMapper modelMapper;

    public UpdateUserAdminMapperImpl(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UpdateUserAdminDto mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, UpdateUserAdminDto.class);
    }

    @Override
    public UserEntity mapFrom(UpdateUserAdminDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }

    @Override
    public List<UpdateUserAdminDto> mapList(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(this::mapTo)
                .collect(Collectors.toList());
    }
}
