package com.redis.entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "sce_user_role")
public class UserRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "user_role_id")
	private Long userRoleId;
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	private Long roleGroupId;
	private boolean isActive;
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	List<Rolegroup> rolegrpobj;
}
