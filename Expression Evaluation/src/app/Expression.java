package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is
	 * created and stored, even if it appears more than once in the expression. At
	 * this time, values for all variables and all array items are set to zero -
	 * they will be loaded from a file in the loadVariableValues method.
	 * 
	 * @param expr   The expression
	 * @param vars   The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 *               [-+/*\\\\(\\)\\]\\[]+ 4x+45/g+d*x-d
	 *               (var+45)/g+(d*var-dat[10+(23-x*a[1]+a[d-f])])
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		/**
		 * DO NOT create new vars and arrays - they are already created before being
		 * sent in to this method - you just need to fill them in.
		 **/

		if (expr == null || vars == null || arrays == null) {
			return;
		}

		// remove all whitespace
		expr = fixExpression(expr.replaceAll("\\s+", ""));

		String currentName = "";
		for (int i = 0; i < expr.length(); i++) {
			char c = expr.charAt(i);
			if (Character.isLetter(c)) {
				currentName += c;
			}

			if (c == '[') {
				addArray(currentName, arrays);
				currentName = "";
			}
			if (currentName != "" && ((!Character.isLetter(c)) || i == expr.length() - 1)) {
				addVariable(currentName, vars);
				currentName = "";
			}

		}

		// * PRINT OUT RESULTANT ARRAYLISTS, TEST SHIZ:
		for (Variable v : vars) {
			System.out.println(v.toString());
		}
		for (Array a : arrays) {
			System.out.println(a.toString());
		}

	}

	private static void addVariable(String name, ArrayList<Variable> vars) {

		for (Variable var : vars) {
			if (var.name.equals(name)) {
				return;
			}
		}
		vars.add(new Variable(name));
	}

	private static void addArray(String name, ArrayList<Array> arrays) {

		for (Array a : arrays) {
			if (a.name.equals(name)) {
				return;
			}
		}
		arrays.add(new Array(name));
	}

	/**
	 * Loads values for variables and arrays in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input
	 * @param vars   The variables array list, previously populated by
	 *               makeVariableLists
	 * @param arrays The arrays array list - previously populated by
	 *               makeVariableLists
	 */

	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	/**
	 * Evaluates the expression.
	 * 
	 * @param vars   The variables array list, with values for all variables in the
	 *               expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/

		expr=fixExpression(expr.replaceAll("\\s+",""));
		System.out.println("evaluate called with:   " + expr);
		float answer = 0;

		if (expr.contains("[")) {

			int startIndex = 0;
			int endIndex = 0;

			int braketCount = 0;
			for (int i = 0; i < expr.length(); i++) {

				if (expr.charAt(i) == '[') {
					if (braketCount == 0) {
						startIndex = i;
					}
					braketCount++;
				}
				if (expr.charAt(i) == ']') {
					endIndex = i;
					braketCount--;
					if (braketCount == 0) {

						System.out.println("[] subexpression:  " + expr.substring(startIndex + 1, endIndex));
						String arrayName = getName(expr, startIndex - 1);

						expr = fixExpression(expr.substring(0, startIndex - arrayName.length())
								+ getArrayValue(arrayName,
										evaluate(expr.substring(startIndex + 1, endIndex), vars, arrays), arrays)
								+ expr.substring(endIndex + 1, expr.length()));

						i = 0;
					}
				}
			}
		}

		if (expr.contains("(")) {
			int startIndex = 0;
			int endIndex = 0;

			int parenthesisCount = 0;
			
			for (int i = 0; i < expr.length(); i++) {

				if (expr.charAt(i) == '(') {
					if (parenthesisCount == 0) {
						startIndex = i;
					}
					parenthesisCount++;
				}
				if (expr.charAt(i) == ')') {
					endIndex = i;
					parenthesisCount--;
					if (parenthesisCount == 0) {

						System.out.println("parenthesis subexpression:   " + expr.substring(startIndex + 1, endIndex));
						expr = fixExpression(expr.substring(0, startIndex)
								+ evaluate(expr.substring(startIndex + 1, endIndex), vars, arrays)
								+ expr.substring(endIndex + 1, expr.length()));

						i = 0;
					}
				}
			}
		}

		if (containsLetter(expr)) {
			System.out.print("variable replacement:   " + expr);
			String currentName = "";
			for (int i = 0; i < expr.length(); i++) {
				char c = expr.charAt(i);
				if (Character.isLetter(c)) {
					currentName += c;
				}

				if (currentName != "" && (isCharAnOperator(c))) {
					// System.out.println(expr+" asdfsdfdsf");
					expr = expr.substring(0, i - currentName.length()) + getVariableValue(currentName, vars)
							+ expr.substring(i, expr.length());
					currentName = "";
				}

				if (currentName != "" && i + 1 == expr.length()) {
					// System.out.println(expr+" bsfvdsvc");
					expr = expr.substring(0, i - currentName.length() + 1) + getVariableValue(currentName, vars)
							+ expr.substring(i, i);
					currentName = "";
					break;
				}
			}
			System.out.println(" --->   " + expr);
		}

		// multiply
		if (expr.contains("*") || expr.contains("/")) {
			

			System.out.println("Starting multiplcations:   " + expr);
			for (int i = 0; i < expr.length(); i++) {
				char c = expr.charAt(i);
				String a = "";
				String b = "";
				if (c == '*' || c == '/') {

					int k = i - 1;
					char d = expr.charAt(k);
					boolean negative = false;

					int k2 = i - 1;
					int count = 0;
					while (k2 >= 0) {
						if (expr.charAt(k2) == '-' && count == 0 && k2 == 0) {
							negative = true;
							break;
						}

						if (isCharAnOperator(expr.charAt(k2))) {
							count++;
						}

						k2--;
					}

					while ((d != '/' && d != '*' && d != '+' && d != '-') && k >= 0) {

						if (negative) {
							a = d + a;

						} else {
							a = d + a;
						}
						k--;
						try {
							d = expr.charAt(k);

						} catch (StringIndexOutOfBoundsException error) {

						}
					}

					int j = i + 1;
					char e = expr.charAt(j);

					if (expr.charAt(j) == '-') {
						j++;
						b += '-';
						e = expr.charAt(j);
					}

					while ((e != '/' && e != '*' && e != '+' && e != '-') && j < expr.length()) {

						b += e;
						j++;
						try {
							e = expr.charAt(j);
						} catch (StringIndexOutOfBoundsException error) {

						}
					}

					float aF = Float.parseFloat(a);
					float aB = Float.parseFloat(b);
					float result = 0;

					if (c == '*') {
						result = aF * aB;
						
					}

					if (c == '/') {
						result = aF / aB;
					}
					expr = fixExpression(expr.substring(0, k + 1) +String.format("%.16f",result) + expr.substring(j, expr.length()));
					
					System.out.println("Multiplacion/division: " + expr + ":  " + aF + "  */ " + aB);
					a="";
					b="";
					i=0;
				}
			}
		}

		// add and subtract
		if (expr.contains("+") || expr.contains("-")) {
			int i = 0;
			String num = "";
			boolean subract = false;
			int count = 0;
			while (i < expr.length()) {
				char c = expr.charAt(i);
				i++;
				if (c != '+' && c != '-') {

					num += c;
				}
				
				//CONSTRUCTION
				try {
					if(expr.charAt(i-1)=='E') {
						i++;
						String power = "";
						while(i<expr.length()&&expr.charAt(i)!='+'&&expr.charAt(i)!='-') {
							power+=expr.charAt(i);
							i++;
						}
						
						float result=Float.parseFloat(num)*(float)Math.pow(10,Float.parseFloat(power));
					}
				}
				catch(StringIndexOutOfBoundsException error) {
					
				}
				
				//END CONSTRUCTION

				if (c == '+' || c == '-' || i == expr.length()) {
					if (expr.charAt(0) == '-') {

					}
					
					if (subract) {
						try {
							answer -= Float.parseFloat(num);
							num = "";
						} catch (NumberFormatException error) {

						}
						num = "";
					} else {
						try {
							answer += Float.parseFloat(num);
							num = "";
						} catch (NumberFormatException error) {

						}
					}
				}
				if (c == '+') {
					subract = false;
				}

				if (c == '-') {
					subract = true;
				}

			}
			System.out.println("Addtion/subtraction:  "+answer + "  = " + expr);
			return answer;

		} else
			return Float.parseFloat(expr);
		// String[] tokens = expr.split("[*/]+");
	}

	private static boolean isCharAnOperator(char c) {
		if (c == '*' || c == '/' || c == '+' || c == '-') {
			return true;
		}
		return false;
	}

	private static boolean containsLetter(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLetter(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	private static String getName(String expr, int startIndex) {
		int i = startIndex;
		String name = "";
		while (i >= 0 && Character.isLetter(expr.charAt(i))) {
			// System.out.println("adf");
			name = expr.charAt(i) + name;
			i--;
		}

		return name;
	}

	private static float getVariableValue(String name, ArrayList<Variable> vars) {
		for (Variable v : vars) {
			if (v.name.equals(name)) {
				return v.value;
			}
		}

		return 0;
	}

	private static float getArrayValue(String name, float index, ArrayList<Array> arrays) {
		for (Array a : arrays) {
			if (a.name.equals(name)) {
				return a.values[(int) index];
			}
		}

		return 0;
	}
	
	private static String fixExpression(String expr) {
		if(expr.contains("--")) {
			expr=expr.replace("--","+");
		}
		if(expr.contains("-+")) {
			expr=expr.replace("-+","-");
		}
		if(expr.contains("+-")) {
			expr=expr.replace("+-","-");
		}
		if(expr.contains("++")) {
			expr=expr.replace("++","+");
		}
		if(expr.contains("E0.0")) {
			expr=expr.replace("E0.0","");
		}
		return expr;
	}
}