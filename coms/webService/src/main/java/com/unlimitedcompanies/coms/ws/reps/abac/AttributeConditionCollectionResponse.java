package com.unlimitedcompanies.coms.ws.reps.abac;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.LazyInitializationException;
import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.AttributeCondition;

@XmlRootElement(name = "attributeConditions")
public class AttributeConditionCollectionResponse extends ResourceSupport
{
	List<AttributeConditionDTO> attributeConditions;
	
	protected AttributeConditionCollectionResponse() 
	{
		this.attributeConditions = new ArrayList<>();
	}
	
	public AttributeConditionCollectionResponse(Set<AttributeCondition> attributeConditions)
	{
		try
		{
			if (attributeConditions != null)
			{
				this.attributeConditions = new ArrayList<>();
				for(AttributeCondition condition : attributeConditions)
				{
					this.attributeConditions.add(new AttributeConditionDTO(condition));
				}
			}
		}
		catch (LazyInitializationException e)
		{
			// The resource is not available
			this.attributeConditions = null;
		}
	}

	@XmlElement(name = "attributeCondition")
	public List<AttributeConditionDTO> getAttributeConditions()
	{
		return attributeConditions;
	}

	public void setAttributeConditions(List<AttributeConditionDTO> attributeConditions)
	{
		this.attributeConditions = attributeConditions;
	}
}
