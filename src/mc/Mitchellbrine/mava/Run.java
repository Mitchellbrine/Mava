package mc.Mitchellbrine.mava;

import mc.Mitchellbrine.mava.ex.ClassCreationException;
import mc.Mitchellbrine.mava.syntax.MavaClass;
import mc.Mitchellbrine.mava.syntax.MavaMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Run {

	public static Set<MavaClass> classes;
	private static List<String> scheduledNamespaces;
	private static List<MavaClass> nameSpaceClasses;
	private static int loadProcess = -1;

	public static void main(String args[]) {
		boolean log = false;
		String logLocation = "";
		if (args.length <= 0) {
			System.out.println("No script or program provided.");
			return;
		}
		
		String program = args[0];

		List<String> argList = Arrays.asList(args);

		if (argList.equals("-log")) {
			log = true;
			logLocation = "mava/log";
		}

		File file = new File(program);

		classes = new HashSet<MavaClass>();
		scheduledNamespaces = new ArrayList<String>();
		nameSpaceClasses = new ArrayList<MavaClass>();

		loadProcess = 0;

		if (file.getName().endsWith(".zip")) {
			try {
				ZipFile zip = new ZipFile(file);
				while (zip.entries().hasMoreElements()) {
					ZipEntry entry = zip.entries().nextElement();
					if (entry.getName().endsWith(".mava")) {
						classes.add(MavaClass.createFromScan(zip.getInputStream(entry)));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (file.getName().endsWith(".mava")) {
			try {
				classes.add(MavaClass.createFromScan(file));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		loadProcess = 1;

		for (int i = 0; i < scheduledNamespaces.size();i++) {
			String namespace = scheduledNamespaces.get(i);
			MavaClass clazz = nameSpaceClasses.get(i);
			MavaClass newClazz = null;
			try {
				Class javaClazz = Class.forName(namespace);
				if (javaClazz != null)
					newClazz = new MavaClass(javaClazz);
			} catch (ClassNotFoundException ex) {
				// Shh. Don't throw the exception...
				newClazz = Run.getClass(clazz,namespace);
			}
			if (newClazz == null)
				throw new ClassCreationException(String.format("Class %s does not exist", namespace));
			clazz.addVisibleClass(newClazz);
		}

		scheduledNamespaces.clear();
		nameSpaceClasses.clear();

		loadProcess = 2;

		for (MavaClass clazz : classes) {
			for (MavaMethod method : clazz.getMethods()) {
				if (method.identifier.equals("main")) {
					for (String arg : argList) {
						if (arg.startsWith("-arg:")) {
							if (method.getVariables() == null) {
								System.out.println("Odd, no variable list is unusual");
								continue;
							}
							if (method.getVariables().get(0) == null) {
								System.out.println("Null variable is also odd.");
								continue;
							}
							if (method.getVariables().get(0).value == null) {
								method.getVariables().get(0).set(arg.substring(arg.indexOf(":") + 1));
								System.out.println(String.format("Received argument %s from the program arguments, injecting...",arg.substring(arg.indexOf(":")+1)));
							}
						}
					}
					method.invoke();
				}
			}
		}
		
	}

	public static MavaClass getClass(MavaClass clasz, String namespace) {
		for (MavaClass clazz : classes) {
			if ((clazz.space + "." + clazz.name).equals(namespace))
				return clazz;
		}
		if (loadProcess == 0) {
			//System.out.println("Mava class " + namespace + " was not found. Will search after class addition");
			scheduledNamespaces.add(namespace);
			nameSpaceClasses.add(clasz);
		}
		return null;
	}

	public static MavaClass getClass(String namespace) {
		for (MavaClass clazz : classes) {
			if ((clazz.space + "." + clazz.name).equals(namespace))
				return clazz;
		}
		return null;
	}

	public static void hello() {
		System.out.println("Hello World!");
	}
	
}
