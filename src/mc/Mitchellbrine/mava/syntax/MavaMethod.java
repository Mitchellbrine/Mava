package mc.Mitchellbrine.mava.syntax;

import mc.Mitchellbrine.mava.parse.Parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MavaMethod {
	
	public String identifier;
	private Set<String> body;
	private String modifiers;
	private MavaClass clazz;
	
	private List<MavaVariable> variables;
	
	public MavaMethod(String modifiers, String id, Set<String> methodBody) {
		this.modifiers = modifiers;
		this.identifier = id;
		this.body = methodBody;
	}

	public void addVariable(MavaVariable variable) {
		if (variables == null)
			variables = new ArrayList<MavaVariable>();
		variables.add(variable);
	}

	/**
	 * Gets the modifiers on the method
	 * @return a new instance of the string, so it is un-alterable
	 */
	public String getModifiers() {
		return new String(modifiers.getBytes());
	}

	public void setClass(MavaClass clazz) {
		if (this.clazz != null) {
			return;
		}
		this.clazz = clazz;
	}

	public void invoke() {
		try {
			Parser.parseMethodBody(this);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}

	public Set<String> getBody() {
		return new HashSet<String>(body);
	}

	public List<MavaVariable> getVariables() {
		return variables == null ? null : new ArrayList<MavaVariable>(variables) {
		};
	}

	public MavaClass getClazz() {
		return clazz;
	}

	public void addModifier(String modifier) {
		if (modifiers != null)
			return;
		modifiers = modifier;
	}
	
}
