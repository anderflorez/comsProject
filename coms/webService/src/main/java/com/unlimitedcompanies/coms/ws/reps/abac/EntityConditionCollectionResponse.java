package com.unlimitedcompanies.coms.ws.reps.abac;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.LazyInitializationException;
import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.EntityCondition;

@XmlRootElement(name = "entityConditions")
public class EntityConditionCollectionResponse extends ResourceSupport
{
	List<EntityConditionDTO> entityConditions;
	
	protected EntityConditionCollectionResponse() 
	{
		this.entityConditions = new ArrayList<>();
	}
	
	public EntityConditionCollectionResponse(Set<EntityCondition> entityConditions)
	{
		try
		{
			if (entityConditions != null)
			{
				this.entityConditions = new ArrayList<>();
				for (EntityCondition condition : entityConditions)
				{
					this.entityConditions.add(new EntityConditionDTO(condition));
				}				
			}
		}
		catch (LazyInitializationException e)
		{
			// The resource is not available
			this.entityConditions = null;
		}
	}
	
	@XmlElement(name = "entityCondition")
	public List<EntityConditionDTO> getEntityConditions()
	{
		return entityConditions;
	}

	public void setEntityConditions(List<EntityConditionDTO> entityConditions)
	{
		this.entityConditions = entityConditions;
	}
}
