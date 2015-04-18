package mc.Mitchellbrine.mava.syntax;

import java.lang.reflect.Type;

public enum MavaNativeTypes {

	String("string", ""),
	Int("int", 0,Integer.class,"parseInt"),
	Float("float", 0.0f,Float.class,"parseFloat"),
	Double("double", 0.0,Double.class,"parseDouble"),
	Long("long", 0, Long.class,"parseLong"),
	Short("short", 0, Short.class,"parseShort");
	
	public String inCodeReference;
	public Object defaultValue;
	public Class typeClass;
	public String parseMethod;
	public boolean isParsable;
	
	MavaNativeTypes(String codeName, Object defaultValue,Class typeClass,String parseMethodName) {
		this.inCodeReference = codeName;
		this.defaultValue = defaultValue;
		this.typeClass = typeClass;
		this.parseMethod = parseMethodName;
	}

	MavaNativeTypes(String codeName, Object defaultValue) {
		this.inCodeReference = codeName;
		this.defaultValue = defaultValue;
		this.isParsable = false;
	}
	
	public Object parseValue(String oldObject) throws Exception {
		if (this.isParsable) {
			try {
				return typeClass.getMethod(parseMethod, oldObject.getClass()).invoke(this, oldObject);
			} catch (NumberFormatException ex) {
				return defaultValue;
			}
		}
		return oldObject;
	}
	
}
