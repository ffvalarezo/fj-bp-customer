package com.pichincha.customerbp.service.mapper;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customerbp.domain.CustomerEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public CustomerEntity toEntity(Customer dto) {
        if ( dto == null ) {
            return null;
        }

        CustomerEntity customerEntity = new CustomerEntity();

        customerEntity.setName( dto.getFullName() );
        customerEntity.setPhone( dto.getCelular() );
        if ( dto.getActive() != null ) {
            customerEntity.setStatus( dto.getActive() );
        }
        customerEntity.setAddress( dto.getAddress() );
        if ( dto.getAge() != null ) {
            customerEntity.setAge( dto.getAge() );
        }
        customerEntity.setEmail( dto.getEmail() );
        customerEntity.setGender( dto.getGender() );
        customerEntity.setIdentification( dto.getIdentification() );
        customerEntity.setPassword( dto.getPassword() );

        return customerEntity;
    }

    @Override
    public Customer toDto(CustomerEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Customer customer = new Customer();

        customer.setFullName( entity.getName() );
        customer.setCelular( entity.getPhone() );
        customer.setActive( entity.isStatus() );
        customer.setAddress( entity.getAddress() );
        customer.setAge( entity.getAge() );
        if ( entity.getCustomerId() != null ) {
            customer.setCustomerId( entity.getCustomerId().intValue() );
        }
        customer.setEmail( entity.getEmail() );
        customer.setGender( entity.getGender() );
        customer.setIdentification( entity.getIdentification() );
        customer.setPassword( entity.getPassword() );

        return customer;
    }

    @Override
    public List<Customer> toDtoList(List<CustomerEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<Customer> list1 = new ArrayList<Customer>( list.size() );
        for ( CustomerEntity customerEntity : list ) {
            list1.add( toDto( customerEntity ) );
        }

        return list1;
    }

    @Override
    public void updateEntityFromRequest(Customer dto, CustomerEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getFullName() != null ) {
            entity.setName( dto.getFullName() );
        }
        if ( dto.getCelular() != null ) {
            entity.setPhone( dto.getCelular() );
        }
        if ( dto.getActive() != null ) {
            entity.setStatus( dto.getActive() );
        }
        if ( dto.getAddress() != null ) {
            entity.setAddress( dto.getAddress() );
        }
        if ( dto.getAge() != null ) {
            entity.setAge( dto.getAge() );
        }
        if ( dto.getEmail() != null ) {
            entity.setEmail( dto.getEmail() );
        }
        if ( dto.getGender() != null ) {
            entity.setGender( dto.getGender() );
        }
        if ( dto.getIdentification() != null ) {
            entity.setIdentification( dto.getIdentification() );
        }
        if ( dto.getPassword() != null ) {
            entity.setPassword( dto.getPassword() );
        }
    }
}
