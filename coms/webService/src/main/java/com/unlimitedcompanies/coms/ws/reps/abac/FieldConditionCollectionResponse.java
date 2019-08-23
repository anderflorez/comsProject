package com.unlimitedcompanies.coms.ws.reps.abac;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.LazyInitializationException;
import org.springframework.hateoas.ResourceSupport;

import com.unlimitedcompanies.coms.domain.abac.FieldCondition;

@XmlRootElement(name = "fieldConditions")
public class FieldConditionCollectionResponse extends ResourceSupport
{
	List<FieldConditionDTO> fieldConditions;
	
	protected FieldConditionCollectionResponse() 
	{
		this.fieldConditions = new ArrayList<>();
	}
	
	public FieldConditionCollectionResponse(Set<FieldCondition> fieldConditions)
	{
		try
		{
			if(fieldConditions != null)
			{
				this.fieldConditions = new ArrayList<>();
				for(FieldCondition condition : fieldConditions)
				{
					this.fieldConditions.add(new FieldConditionDTO(condition));
				}
			}
		}
		catch (LazyInitializationException e)
		{
			// The resource is not available
			this.fieldConditions = null;
		}
	}

	@XmlElement(name = "fieldCondition")
	public List<FieldConditionDTO> getFieldConditions()
	{
		return fieldConditions;
	}

	public void setFieldConditions(List<FieldConditionDTO> fieldConditions)
	{
		this.fieldConditions = fieldConditions;
	}
}
