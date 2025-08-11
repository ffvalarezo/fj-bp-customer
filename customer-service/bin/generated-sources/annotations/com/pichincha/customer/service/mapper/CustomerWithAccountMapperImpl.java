package com.pichincha.customer.service.mapper;

import com.pichincha.common.infrastructure.input.adapter.rest.models.CustomerWithAccount;
import com.pichincha.customer.domain.CustomerWithAccountEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class CustomerWithAccountMapperImpl implements CustomerWithAccountMapper {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public CustomerWithAccountEntity toEntity(CustomerWithAccount dto) {
        if ( dto == null ) {
            return null;
        }

        CustomerWithAccountEntity customerWithAccountEntity = new CustomerWithAccountEntity();

        customerWithAccountEntity.setCustomer( customerMapper.toEntity( dto.getCustomer() ) );

        return customerWithAccountEntity;
    }

    @Override
    public CustomerWithAccount toDto(CustomerWithAccountEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CustomerWithAccount customerWithAccount = new CustomerWithAccount();

        customerWithAccount.setCustomer( customerMapper.toDto( entity.getCustomer() ) );

        return customerWithAccount;
    }
}
