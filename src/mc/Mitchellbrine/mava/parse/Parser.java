package mc.Mitchellbrine.mava.parse;

import mc.Mitchellbrine.mava.Run;
import mc.Mitchellbrine.mava.syntax.MavaClass;
import mc.Mitchellbrine.mava.syntax.MavaMethod;
import mc.Mitchellbrine.mava.syntax.MavaVariable;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Mitchellbrine on 2015.
 */
public class Parser {

	public static boolean parseLine(MavaClass clazz, String string,List<String> linesOfCode) {
		if (string.startsWith("space")) {
			clazz.space = string.substring(6);
		}
		if (string.startsWith("class")) {
			//clazz.name =
			if (string.indexOf(" ",string.indexOf(" ") + 1) < 0) {
				clazz.name = string.substring(string.indexOf(" ") + 1);
			} else {
				clazz.name = string.substring(string.indexOf(" ") + 1, string.indexOf(" ", string.indexOf(" ") + 1));
			}
		}
		if (string.startsWith("import")) {
			MavaClass newClazz = null;
			try {
				Class javaClazz = Class.forName(string.substring(string.indexOf(" ") + 1));
				if (javaClazz != null)
					newClazz = new MavaClass(javaClazz);
			} catch (ClassNotFoundException ex) {
				// Shh. Don't throw the exception...
				newClazz = Run.getClass(clazz,string.substring(string.indexOf(" ") + 1));
			}
			/*if (newClazz == null) {
				System.err.println(String.format("Class %s does not exist", string.substring(string.indexOf(" ") + 1)));
			} else*/
			if (newClazz != null){
				clazz.addVisibleClass(newClazz);
			}
		}
		if (string.replaceAll("\t","").startsWith("method")) {
			clazz.addMethod(readMethod(clazz, string, linesOfCode));
		}
		return string.startsWith("terminate");
	}

	private static MavaMethod readMethod(MavaClass clazz, String string, List<String> lines) {
		int tabs = string.length() - string.replaceAll("\t", "").length();
		Matcher matcher = PatternUtils.brackets.matcher(string.replaceAll("\t", "").substring(7));
			String modifier = "";

		while(matcher.find()) {
			System.out.println(matcher.group(0));
			modifier = matcher.group(0);
		}

			String[] withoutModifier = string.replaceAll("\t", "").substring(7,string.indexOf("(") - 1).split(" ");

			int index = lines.indexOf(string) + 1;

		HashSet<String> methodLines = new HashSet<String>();

			do {
				methodLines.add(lines.get(index));
				index++;
			} while (lines.get(index).length() - lines.get(index).replaceAll("\t", "").length() > tabs && !lines.get(index).replaceAll("\t", "").equalsIgnoreCase("terminate"));

			MavaMethod method = new MavaMethod(modifier, withoutModifier[1], methodLines);
			if (string.substring(string.indexOf("(")).replaceAll("\\(", "").replaceAll("\\)", "").split(", ").length > 0) {
				for (String variables : string.substring(string.indexOf("(")).replaceAll("\\(", "").replaceAll("\\)", "").split(", ")) {
					//System.out.println(variables);
					String[] variableMeta = variables.split(" ");
					String className = "";
					String variableName = "";
					try {
						className = variableMeta[0];
						variableName = variableMeta[1];
					} catch (ArrayIndexOutOfBoundsException ex) {
						// TROLOLOLOLOLOLOLOLOLOL
					}
					//System.out.println(className + " " + variableName);
					method.addVariable(new MavaVariable(className, variableName));
				}
			}
			method.setClass(clazz);
			return method;
	}

	public static void parseMethodBody(MavaMethod method) {
		for (String tabLine : method.getBody()) {
			String line = tabLine.replaceAll("\t","");
			if (line.startsWith("//") || tabLine.startsWith("//"))
				continue;
			if (line.indexOf(".") < line.indexOf("=")) {
				if (line.startsWith("if")) {
					// Work in if- statement stuff
				} else {
					// THIS is creation of a variable
					String varName = line.substring(line.indexOf(" "), line.indexOf(" ", line.indexOf(" ")));
					String varClazz = line.substring(0,line.indexOf(" "));
					method.addVariable(new MavaVariable(varClazz,varName));
				}
			} else {
				MavaMethod method1 = null;
				for (MavaMethod mavaMethod : method.getClazz().getMethods()) {
					if (mavaMethod.identifier.equals(line.substring(0,line.indexOf("(")))) {
						method1 = mavaMethod;
					}
				}
				if (method1 != null) {
					if (line.indexOf("(") + 1 != line.indexOf(")")) {

					}
					method1.invoke();
					continue;
				}
				MavaVariable variable = null;
				if (method.getVariables() != null) {
					for (MavaVariable mVariable : method.getVariables()) {
						if (mVariable.variableName.equals(line.substring(0, line.indexOf(".")))) {
							variable = mVariable;
						}
					}
				}
				if (variable == null) {
					if (method.getClazz().getVariables() != null) {
						for (MavaVariable v1 : method.getClazz().getVariables()) {
							if (v1.variableName.equals(line.substring(0, line.indexOf(".")))) {
								variable = v1;
							}
						}
						if (variable == null) {
							if (method.getClazz().getVisibleClasses() == null)
								throw new RuntimeException(String.format("Class %s does not exist", line.substring(0, line.indexOf("."))));
							MavaClass mavaClass = null;
							for (MavaClass methodClazz : method.getClazz().getVisibleClasses()) {
								if (methodClazz.name.equals(line.substring(0, line.indexOf(".")))) {
									mavaClass = methodClazz;
								}
							}
							if (mavaClass == null)
								throw new RuntimeException(String.format("Class %s does not exist", line.substring(0, line.indexOf("."))));
							try {
								if (!line.substring(line.indexOf("(") + 1, line.indexOf(")")).isEmpty()) {
									mavaClass.parseNatives(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
									mavaClass.invokeMethodWithName(line.substring(line.indexOf(".") + 1, line.indexOf("(")),true);
								} else {
									mavaClass.invokeMethodWithName(line.substring(line.indexOf(".") + 1, line.indexOf("(")));
								}
							} catch (Exception exc) {
								throw new RuntimeException("Exception caught when executing method " + line.substring(line.indexOf(".") + 1, line.indexOf("(")) + " from " + mavaClass.name, exc);
							}
							continue;
						}
						MavaClass clazz = null;
						try {
							Class javaClazz = Class.forName(variable.className);
							if (javaClazz != null) {
								clazz = new MavaClass(javaClazz);
							}
						} catch (ClassNotFoundException ex) {
							for (MavaClass clazz1 : Run.classes) {
								int index = variable.className.lastIndexOf(".") < 0 ? 0 : variable.className.lastIndexOf(".") + 1;
								if (clazz1.name.equals(variable.className.substring(index))) {
									clazz = clazz1;
								}
							}
						}
						if (clazz == null)
							throw new RuntimeException(String.format("Class %s does not exist", variable.className));
						try {
							if (!line.substring(line.indexOf("(") + 1, line.indexOf(")")).isEmpty()) {
								clazz.invokeMethodWithName(line.substring(line.indexOf(".") + 1, line.indexOf("(")), line.substring(line.indexOf("(") + 1, line.indexOf(")")).split(", "));
							} else {
								clazz.invokeMethodWithName(line.substring(line.indexOf(".") + 1, line.indexOf("(")));
							}
						} catch (Exception exc) {
							throw new RuntimeException("Exception caught when executing method " + line.substring(line.indexOf(".") + 1, line.indexOf("(")) + " from " + clazz.name, exc);
						}
					} else {
						if (method.getClazz().getVisibleClasses() == null)
							throw new RuntimeException(String.format("Class %s does not exist", line.substring(0, line.indexOf("."))));
						MavaClass mavaClass = null;
						for (MavaClass methodClazz : method.getClazz().getVisibleClasses()) {
							System.out.println(line.substring(0, line.indexOf(".")) + " | " + methodClazz.name);
							if (methodClazz.name.equals(line.substring(0, line.indexOf(".")))) {
								mavaClass = methodClazz;
							}
						}
						if (mavaClass == null)
							throw new RuntimeException(String.format("Class %s does not exist", line.substring(0, line.indexOf("."))));
						try {
							if (!line.substring(line.indexOf("(") + 1, line.indexOf(")")).isEmpty()) {
								mavaClass.invokeMethodWithName(line.substring(line.indexOf(".") + 1, line.indexOf("(")), line.substring(line.indexOf("(") + 1, line.indexOf(")")).split(", "));
							} else {
								mavaClass.invokeMethodWithName(line.substring(line.indexOf(".") + 1, line.indexOf("(")));
							}
						} catch (Exception exc) {
							throw new RuntimeException("Exception caught when executing method " + line.substring(line.indexOf(".") + 1, line.indexOf("(")) + " from " + mavaClass.name, exc);
						}
					}
				}
			}

		}
	}

}
