package com.bavis.budgetapp.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author bavis
 * 
 *  	Entity To Hold High Level Categories That Are Pre-Determined (Wants, Needs, Investments)
 *
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ParentCategory extends Category{
	private double budgetAllocation; //percentage of monthly income 
}
