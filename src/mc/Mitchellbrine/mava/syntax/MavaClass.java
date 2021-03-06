package mc.Mitchellbrine.mava.syntax;

import mc.Mitchellbrine.mava.ex.ClassCreationException;
import mc.Mitchellbrine.mava.parse.Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MavaClass {
	
	public String space;
	public String name;
	private String modifiers;
	
	private Set<MavaMethod> methods;
	private Set<MavaVariable> variables;
	private Set<MavaClass> visibleClasses;

	private List<Method> javaMethods;
	public boolean isJavaClass;
	private Object[] params = null;

	public MavaClass() {
		variables = new HashSet<MavaVariable>();
		methods = new HashSet<MavaMethod>();
		this.addVariable(new MavaVariable(this.getClass().getName(),"class"));
	}

	public MavaClass(String space, String name) {
		this();
		this.space = space;
		this.name = name;
	}

	public MavaClass(Class clazz) {
		this(clazz.getPackage().getName(), clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1));
		this.javaMethods = Arrays.asList(clazz.getMethods());
		this.isJavaClass = true;
	}

	public static MavaClass createFromScan(File file) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String s;
		MavaClass clazz = new MavaClass();
		List<String> linesOfCode = new ArrayList<String>();
		while ((s = reader.readLine()) != null) {
			linesOfCode.add(s);
		}
		reader.close();
		for (String string : linesOfCode) {
			if (Parser.parseLine(clazz,string,linesOfCode))
				return clazz;
		}
		throw new ClassCreationException("Class definition was never terminated");
	}

	public static MavaClass createFromScan(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String s;
		MavaClass clazz = new MavaClass();
		List<String> linesOfCode = new ArrayList<String>();
		while ((s = reader.readLine()) != null) {
			linesOfCode.add(s);
		}
		reader.close();
		for (String string : linesOfCode) {
			if (Parser.parseLine(clazz,string,linesOfCode))
				return clazz;
		}
		throw new ClassCreationException("Class definition was never terminated");
	}

	public void addVariable(MavaVariable variable) {
		variables.add(variable);
	}


	public void setModifiers(String modifier) {
		if (this.modifiers != null)
			return;
		modifiers = modifier;
	}

	public void addMethod(MavaMethod method) {
		if (methods == null)
			methods = new HashSet<MavaMethod>();
		methods.add(method);
	}

	public Set<MavaMethod> getMethods() {
		return new HashSet<MavaMethod>(methods);
	}

	public Set<MavaVariable> getVariables() {
		return variables == null ? null : new HashSet<MavaVariable>(variables);
	}

	public Set<MavaClass> getVisibleClasses() {
		return visibleClasses == null ? null : new HashSet<MavaClass>(visibleClasses);
	}

	public void addVisibleClass(MavaClass namespace) {
		if (visibleClasses == null)
			visibleClasses = new HashSet<MavaClass>();
		visibleClasses.add(namespace);
	}

	public void invokeMethodWithName(String name, Object... params) throws Exception {
		if (isJavaClass) {
			for (Method method : javaMethods) {
				if (method.getName().equals(name)) {
					method.invoke(this, params);
				}
			}
		} else {
			throw new RuntimeException("Java-only method called within Mava. Shouldn't be possible.");
		}
	}

	public void invokeMethodWithName(String name, boolean useParams) throws Exception {
		if (isJavaClass) {
			for (Method method : javaMethods) {
				if (method.getName().equals(name)) {
					method.invoke(this, params);
				}
			}
		} else {
			throw new RuntimeException("Java-only method called within Mava. Shouldn't be possible.");
		}
	}

	public void parseNatives(String params) {
		this.params = new Object[params.split(", ").length];
		for (int i = 0; i < params.split(", ").length;i++) {
			String string = params.split(", ")[i];
			if (string.startsWith("\"") && string.endsWith("\"")) {
				this.params[i] = string.replaceAll("\"","");
				continue;
			}
			for (MavaNativeTypes types : MavaNativeTypes.values()) {
				if (types.isParsable(string)) {
					try {
						this.params[i] = types.parseValue(string);
					} catch (Exception ex) {
						throw new RuntimeException("Error parsing natives",ex);
					}
				}
			}
		}
	}

	public static void test() {
		System.out.println("Test complete!");
	}

	public MavaMethod getMethod(String methodName, Class<String>... arguments) {
		for (MavaMethod method : methods) {
			if (method.identifier.equals(methodName)) {
				boolean correctArguments = false;
				if (method.getVariables() == null) {
					if (arguments.length > 0)
						throw new IllegalArgumentException(String.format("Incorrect arguments for %s",methodName));
					else
						return method;
				}
				for (int i = 0; i < method.getVariables().size();i++) {
					if (i > 0 && !correctArguments)
						break;
					correctArguments = method.getVariables().get(i).className.equals(arguments[i]);
				}
				if (correctArguments)
					return method;
			}
		}
		return null;
	}

}
