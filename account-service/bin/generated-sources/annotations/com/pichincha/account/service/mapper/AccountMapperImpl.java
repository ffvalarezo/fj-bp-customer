package com.pichincha.account.service.mapper;

import com.pichincha.account.domain.AccountEntity;
import com.pichincha.account.domain.model.AccountType;
import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountEntity toEntity(AccountRequest dto) {
        if ( dto == null ) {
            return null;
        }

        AccountEntity accountEntity = new AccountEntity();

        accountEntity.setAccountNumber( dto.getAccountNumber() );
        accountEntity.setAccountType( accountTypeEnumToAccountType( dto.getAccountType() ) );
        if ( dto.getCustomerId() != null ) {
            accountEntity.setCustomerId( dto.getCustomerId().longValue() );
        }
        accountEntity.setInitialBalance( dto.getInitialBalance() );
        accountEntity.setStatus( dto.getStatus() );

        return accountEntity;
    }

    @Override
    public AccountRequest toDto(AccountEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AccountRequest accountRequest = new AccountRequest();

        accountRequest.setAccountNumber( entity.getAccountNumber() );
        accountRequest.setAccountType( accountTypeToAccountTypeEnum( entity.getAccountType() ) );
        accountRequest.setInitialBalance( entity.getInitialBalance() );
        accountRequest.setStatus( entity.getStatus() );
        if ( entity.getCustomerId() != null ) {
            accountRequest.setCustomerId( entity.getCustomerId().intValue() );
        }

        return accountRequest;
    }

    @Override
    public List<AccountRequest> toDtoList(List<AccountEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<AccountRequest> list1 = new ArrayList<AccountRequest>( list.size() );
        for ( AccountEntity accountEntity : list ) {
            list1.add( toDto( accountEntity ) );
        }

        return list1;
    }

    @Override
    public void updateEntityFromRequest(AccountRequest dto, AccountEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getAccountNumber() != null ) {
            entity.setAccountNumber( dto.getAccountNumber() );
        }
        if ( dto.getAccountType() != null ) {
            entity.setAccountType( accountTypeEnumToAccountType( dto.getAccountType() ) );
        }
        if ( dto.getCustomerId() != null ) {
            entity.setCustomerId( dto.getCustomerId().longValue() );
        }
        if ( dto.getInitialBalance() != null ) {
            entity.setInitialBalance( dto.getInitialBalance() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
    }

    protected AccountType accountTypeEnumToAccountType(AccountRequest.AccountTypeEnum accountTypeEnum) {
        if ( accountTypeEnum == null ) {
            return null;
        }

        AccountType accountType;

        switch ( accountTypeEnum ) {
            case SAVINGS: accountType = AccountType.SAVINGS;
            break;
            case CURRENT: accountType = AccountType.CURRENT;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + accountTypeEnum );
        }

        return accountType;
    }

    protected AccountRequest.AccountTypeEnum accountTypeToAccountTypeEnum(AccountType accountType) {
        if ( accountType == null ) {
            return null;
        }

        AccountRequest.AccountTypeEnum accountTypeEnum;

        switch ( accountType ) {
            case SAVINGS: accountTypeEnum = AccountRequest.AccountTypeEnum.SAVINGS;
            break;
            case CURRENT: accountTypeEnum = AccountRequest.AccountTypeEnum.CURRENT;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + accountType );
        }

        return accountTypeEnum;
    }
}
