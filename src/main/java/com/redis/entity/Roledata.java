package com.redis.entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sce_role")
public class Roledata implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "role_id")
	int role_id;
	@Column(name = "role_name")
	String roleName;
	String description;
//	@JsonBackReference
//	@OneToMany(mappedBy = "roledataobj", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	List<Rolegroup> rolegroupobj;

}
