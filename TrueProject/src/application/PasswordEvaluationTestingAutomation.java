package application;

/*******
 * <p> PasswordEvaluationTestingAutomation Class. </p>
 * 
 * @author Nathan and Carter; Code from HW and from Carter! 
 * 
 * @version 
 * 
 */

public class PasswordEvaluationTestingAutomation {
	
	static int numPassed = 0;
	static int numFailed = 0;
	
	/** Default constructor for the class */
	public PasswordEvaluationTestingAutomation () {
		
	}

	public static void main(String[] args) {

	}

	static String performTestCase(int testCase, String inputText, boolean expectedPass) {
	//	System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		//System.out.println("Input: \"" + inputText + "\"");
	//	System.out.println("______________");
	//	System.out.println("\nFinite state machine execution trace:");
		
		String resultText= PasswordEvaluator.evaluatePassword(inputText);
		
	/*	System.out.println();
		
		if (resultText != "") {
			if (expectedPass) {
				System.out.println("***Failure*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				numFailed++;
			}
			else {			
				System.out.println("***Success*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		}
		
		else {	
			if (expectedPass) {	
				System.out.println("***Success*** The password <" + inputText + 
						"> is valid, so this is a pass!");
				numPassed++;
			}
			else {
				System.out.println("***Failure*** The password <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
		}*/
		String word = displayEvaluation();
		return word;
	}
	
	static String displayEvaluation() {
		String pass = "";
		if (PasswordEvaluator.foundUpperCase)
			System.out.println("At least one upper case letter - Satisfied");
		else
		{
			System.out.println("At least one upper case letter - Not Satisfied");
			pass += "At least one upper case letter\n";
		}

		if (PasswordEvaluator.foundLowerCase)
			System.out.println("At least one lower case letter - Satisfied");
		else
		{
			System.out.println("At least one lower case letter - Not Satisfied");
			pass += "At least one lower case letter\n";
		}

		if (PasswordEvaluator.foundNumericDigit)
			System.out.println("At least one digit - Satisfied");
		else
		{
			System.out.println("At least one digit - Not Satisfied");
			pass += "At least one digit\n";
		}

		if (PasswordEvaluator.foundSpecialChar)
			System.out.println("At least one special character - Satisfied");
		else
		{
			System.out.println("At least one special character - Not Satisfied");
			pass += "At least one special character\n";
		}

		if (PasswordEvaluator.foundLongEnough)
			System.out.println("At least 8 characters - Satisfied");
		else
		{
			System.out.println("At least 8 characters - Not Satisfied");
			pass += "At least 8 characters long\n";
		}
		System.out.println(pass);
		return pass;
		
	}
}
