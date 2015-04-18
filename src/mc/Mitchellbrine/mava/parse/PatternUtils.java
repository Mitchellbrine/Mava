package mc.Mitchellbrine.mava.parse;

import java.util.regex.Pattern;

/**
 * Created by Mitchellbrine on 2015.
 */
public class PatternUtils {

	public static Pattern brackets;
	public static Pattern method;

	static {
		brackets = Pattern.compile("\\\\[(.*?)\\\\]");
	}

}
