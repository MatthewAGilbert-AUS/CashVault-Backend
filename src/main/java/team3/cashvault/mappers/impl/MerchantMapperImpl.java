package team3.cashvault.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import team3.cashvault.domain.dto.MerchantDto;
import team3.cashvault.domain.entities.MerchantEntity;
import team3.cashvault.mappers.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MerchantMapperImpl implements Mapper<MerchantEntity, MerchantDto> {
    private final ModelMapper modelMapper;

    public MerchantMapperImpl(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public MerchantDto mapTo(MerchantEntity userEntity) {
        return modelMapper.map(userEntity, MerchantDto.class);
    }

    @Override
    public MerchantEntity mapFrom(MerchantDto userDto) {
        return modelMapper.map(userDto, MerchantEntity.class);
    }
    
    @Override
    public List<MerchantDto> mapList(List<MerchantEntity> userEntities) {
        return userEntities.stream()
                .map(this::mapTo)
                .collect(Collectors.toList());
    }
}
