package team3.cashvault.mappers.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import team3.cashvault.domain.dto.TransactionDto;
import team3.cashvault.domain.entities.TransactionEntity;
import team3.cashvault.mappers.Mapper;

@Component
public class TransactionResponseMapperImpl implements Mapper<TransactionEntity, TransactionDto> {

    private final ModelMapper modelMapper;

    public TransactionResponseMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public TransactionDto mapTo(TransactionEntity transactionEntity) {
        return modelMapper.map(transactionEntity, TransactionDto.class);
    }

    @Override
    public TransactionEntity mapFrom(TransactionDto transactionDto) {
        return modelMapper.map(transactionDto, TransactionEntity.class);
    }


    @Override
    public List<TransactionDto> mapList(List<TransactionEntity> transactionEntities) {
        return transactionEntities.stream()
                .map(this::mapTo)
                .collect(Collectors.toList());
    }
}
