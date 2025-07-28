package com.redis.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="sce_role_group")
public class Rolegroup {
    @Id
    @Column(name = "role_group_id")
    int groupid;
    private boolean isActive;
    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    Roledata roledataobj;	
    
   

}
