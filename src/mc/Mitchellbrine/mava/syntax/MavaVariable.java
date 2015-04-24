package mc.Mitchellbrine.mava.syntax;

public class MavaVariable {

	public String className;
	public String variableName;
	public Object value;
	private boolean isNative;
	private MavaNativeTypes nativeType;

	public MavaVariable(String className, String variableName) {
		this.className = className;
		this.variableName = variableName;
		MavaNativeTypes type = null;
		for (MavaNativeTypes types : MavaNativeTypes.values()) {
			if (types.inCodeReference.equals(className)) {
				type = types;
				break;
			}
		}
		this.isNative = type != null;
		this.nativeType = type;
	}

	public boolean isNative() {
		return this.isNative;
	}

	public Object getValue() {
		return value;
	}

	public void set(Object value) {
		if (isNative) {
			try {
				this.value = nativeType.parseValue((String)value);
			} catch (Exception ex) {
				throw new RuntimeException("A native was assigned an incorrect parser. This should be impossible and is EXTREMELY BAD.",ex);
			}
		} else {
			// TODO: Handle other types and figure out how to parse with them. For now, let's just set it without caution.
			try {
				if (value.getClass() == Class.forName(className)) {
					this.value = value;
				}
			} catch (Exception ex) {
				throw new RuntimeException(String.format("The classes throw an exception"),ex);
			}
			/*
			if (Run.getClass(className) != null) {
				// This is a Mava class, not a java one.
				MavaClass clazz = Run.getClass(className);
				if ()
			} else {
				// This is a Java type, we can handle this, right?
			}
			*/
		}
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
