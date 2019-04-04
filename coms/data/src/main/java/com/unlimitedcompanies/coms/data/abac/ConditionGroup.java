package com.unlimitedcompanies.coms.data.abac;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "conditionGroup")
public class ConditionGroup
{
	@Id
	private Integer conditionGroupId;
	
	@Column(unique=false, nullable=false)
	private LogicOperator logicOperator;
	
	@ManyToOne
	@JoinColumn(name="abacPolicyId_FK")
	@Column(unique=false, nullable=true)
	private ABACPolicy abacPolicy;
	
	@ManyToOne
	@JoinColumn(name="parentConditionGroupId_FK")
	@Column(unique=false, nullable=true)
	private ConditionGroup parentConditionGroup;
	
	@OneToMany(mappedBy="parentConditionGroup")
	private List<ConditionGroup> conditionGroups;
	
	@OneToMany(mappedBy="parentConditionGroup")
	private List<EntityCondition> entityConditions;
	
	@OneToMany(mappedBy="parentConditionGroup")
	private List<RecordCondition> recordConditions;
	
	protected ConditionGroup() 
	{
		this.conditionGroups = new ArrayList<>();
		this.entityConditions = new ArrayList<>();
		this.recordConditions = new ArrayList<>();
	}
	
	protected ConditionGroup(ABACPolicy policy)
	{
		this.abacPolicy = policy;
		this.parentConditionGroup = null;
		this.logicOperator = LogicOperator.AND;
		this.conditionGroups = new ArrayList<>();
		this.entityConditions = new ArrayList<>();
		this.recordConditions = new ArrayList<>();
	}
	
	protected ConditionGroup(ABACPolicy policy, LogicOperator operator)
	{
		this.abacPolicy = policy;
		this.parentConditionGroup = null;
		this.logicOperator = operator;
		this.conditionGroups = new ArrayList<>();
		this.entityConditions = new ArrayList<>();
		this.recordConditions = new ArrayList<>();
	}
	
	private ConditionGroup(ConditionGroup parentConditionGroup)
	{
		this.abacPolicy = null;
		this.parentConditionGroup = parentConditionGroup;
		this.logicOperator = LogicOperator.AND;
		this.conditionGroups = new ArrayList<>();
		this.entityConditions = new ArrayList<>();
		this.recordConditions = new ArrayList<>();
	}
	
	private ConditionGroup(ConditionGroup parentConditionGroup, LogicOperator operator)
	{
		this.abacPolicy = null;
		this.parentConditionGroup = parentConditionGroup;
		this.logicOperator = operator;
		this.conditionGroups = new ArrayList<>();
		this.entityConditions = new ArrayList<>();
		this.recordConditions = new ArrayList<>();
	}

	public Integer getConditionGroupId()
	{
		return conditionGroupId;
	}

	protected void setConditionGroupId(Integer conditionGroupId)
	{
		this.conditionGroupId = conditionGroupId;
	}

	protected String getLogicOperator()
	{
		return logicOperator.toString();
	}
	
	public LogicOperator getOperator()
	{
		return logicOperator;
	}

	protected void setLogicOperator(String logicOperator)
	{
		this.logicOperator = LogicOperator.valueOf(logicOperator.toUpperCase());
	}
	
	public void setLogicOperator(LogicOperator logicOperator)
	{
		this.logicOperator = logicOperator;
	}

	public ABACPolicy getAbacPolicy()
	{
		return abacPolicy;
	}

	protected void setAbacPolicy(ABACPolicy abacPolicy)
	{
		this.abacPolicy = abacPolicy;
	}

	public ConditionGroup getParentConditionGroup()
	{
		return parentConditionGroup;
	}

	protected void setParentConditionGroup(ConditionGroup parentConditionGroup)
	{
		this.parentConditionGroup = parentConditionGroup;
		if (!parentConditionGroup.conditionGroups.contains(this))
		{
			parentConditionGroup.conditionGroups.add(this);
		}
	}

	protected List<ConditionGroup> getConditionGroups()
	{
		return conditionGroups;
	}

	protected void setConditionGroups(List<ConditionGroup> conditionGroups)
	{
		this.conditionGroups = conditionGroups;
	}

	public ConditionGroup addConditionGroup()
	{
		ConditionGroup conditionGroup = new ConditionGroup(this);
		this.conditionGroups.add(conditionGroup);
		return conditionGroup;
	}
	
	public ConditionGroup addConditionGroup(LogicOperator logicOperator)
	{
		ConditionGroup conditionGroup = new ConditionGroup(this, logicOperator);
		this.conditionGroups.add(conditionGroup);
		return conditionGroup;
	}
	
	public List<EntityCondition> getEntityConditions()
	{
		return entityConditions;
	}
	
	protected void setEntityConditions(List<EntityCondition> entityConditions)
	{
		this.entityConditions = entityConditions;
	}
	
	protected void addEntityCondition(EntityCondition entityCondition)
	{
		this.entityConditions.add(entityCondition);
		if (!entityCondition.getParentConditionGroup().equals(this))
		{
			entityCondition.setParentConditionGroup(this);
		}
	}
	
	public void addEntityCondition(UserAttribute userAttribute, ComparisonOperator comparison, String value)
	{
		EntityCondition entityCondition = new EntityCondition(this, userAttribute, comparison, value);
		if (!this.entityConditions.contains(entityCondition))
		{
			this.entityConditions.add(entityCondition);			
		}
	}

	public List<RecordCondition> getRecordConditions()
	{
		return recordConditions;
	}

	private void setRecordConditions(List<RecordCondition> recordConditions)
	{
		this.recordConditions = recordConditions;
	}
	
	protected void addRecordCondition(RecordCondition recordCondition)
	{
		this.recordConditions.add(recordCondition);
		if (!recordCondition.getParentConditionGroup().equals(this))
		{
			recordCondition.setParentConditionGroup(this);
		}
	}
	
	public void addRecordCondition(UserAttribute userAttribute, ComparisonOperator comparison, ResourceAttribute resourceAttribute)
	{
		RecordCondition recordCondition = new RecordCondition(this, userAttribute, comparison, resourceAttribute);
		if (!this.recordConditions.contains(recordCondition))
		{
			this.recordConditions.add(recordCondition);
		}
	}
}
