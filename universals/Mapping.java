package universals;

import java.util.HashSet;
import java.util.Set;

import universals.Classifier.TYPE;

public class Mapping
{
	private String raw = null;
	private Set<TYPE> types = new HashSet<TYPE>();
	
	public Mapping(String raw)
	{
		this.raw = raw;
	}
	
	public void addType(TYPE t)
	{
		if (!types.contains(t))
			this.types.add(t);
	}
	
	public void addTypes(Set<TYPE> ts)
	{
		for (TYPE t : ts)
			addType(t);
	}
	
	public String getRaw()
	{
		return this.raw;
	}
	
	public Set<TYPE> getTypes()
	{
		return this.types;
	}
	
	public int numTypes()
	{
		return this.types.size();
	}
}