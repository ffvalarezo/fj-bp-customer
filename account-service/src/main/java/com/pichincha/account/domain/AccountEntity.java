package com.pichincha.account.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.pichincha.account.domain.model.AccountType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

	@Id
	@Column("account_id")
	private Long id;

	@Column("account_number")
	private String accountNumber;

	@Enumerated(EnumType.STRING)
	@Column("account_type")
	private AccountType accountType;

	@Column("initial_balance")
	private BigDecimal initialBalance;

	@Column("status")
	private Boolean status;

	@Column("customer_id")
	private Long customerId;

	@Column("created_at")
	private LocalDateTime createdAt;

	@Column("updated_at")
	private LocalDateTime updatedAt;

}