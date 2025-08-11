package com.pichincha.customer.domain;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWithAccountEntity {
	
	private CustomerEntity customer;
	private AccountRequest account;

}
