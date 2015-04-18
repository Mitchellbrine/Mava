package mc.Mitchellbrine.mava.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchellbrine on 2015.
 */
public class PseudoClass {

	public Class javaClass;

	public List<Method> methods;
	public List<Field> variables;

	public PseudoClass(Class javaClass, List<Field> variables) {
		this.javaClass = javaClass;
		this.methods = new ArrayList<Method>();
		for (Method method : javaClass.getDeclaredMethods()) {
			if (method.isAccessible()) {
				methods.add(method);
			}
		}
		this.variables = variables;
	}

	public void invokeMethodWithName(String name, Object... params) throws Exception {
			for (Method method : methods) {
				if (method.getName().equals(name)) {
					method.invoke(this,params);
				}
			}
	}

}
