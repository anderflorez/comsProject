package com.unlimitedcompanies.coms.data.query;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "conditionGroupL3")
public class ConditionGL3
{
	@Id
	@Column(name = "conditionGroupL3Id")
	private String group3Id;
	private String lOperator;
	
	@OneToOne
	@JoinColumn(name = "conditionGroupL2_FK")
	private ConditionGL1 parentGroup;

	public ConditionGL3()
	{
		this.group3Id = UUID.randomUUID().toString();
	}

	public ConditionGL3(String lOperator, ConditionGL1 parentGroup)
	{
		this.group3Id = UUID.randomUUID().toString();
		this.lOperator = lOperator;
		this.parentGroup = parentGroup;
	}

	public String getGroup3Id()
	{
		return group3Id;
	}

	public String getlOperator()
	{
		return lOperator;
	}

	public ConditionGL1 getParentGroup()
	{
		return parentGroup;
	}
	
}
