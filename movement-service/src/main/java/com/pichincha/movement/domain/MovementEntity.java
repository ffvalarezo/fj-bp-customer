package com.pichincha.movement.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.pichincha.movement.domain.model.MovementType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "movement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovementEntity {

	@Id
	@Column("movement_id")
	private Long id;

	@Column("movement_date")
	private LocalDateTime date;

	@Enumerated(EnumType.STRING)
	@Column("movement_type")
	private MovementType movementType;

	@Column("movement_value")
	private BigDecimal value;

	@Column("balance")
	private BigDecimal balance;

	@Column("account_number")
	private String accountNumber;

	@Column("created_at")
	private LocalDateTime createdAt;

	@Column("updated_at")
	private LocalDateTime updatedAt;

}