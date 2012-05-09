package com.optimo.quakertown.formvalidation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberValidator {

	  private Pattern pattern;
	  private Matcher matcher;

	  private static final String NUMBER_PATTERN = 
		  "[0-9]*";

	  public NumberValidator(){
		  pattern = Pattern.compile(NUMBER_PATTERN);
	  }

	  /**
	   * Validate hex with regular expression
	   * @param hex hex for validation
	   * @return true valid hex, false invalid hex
	   */
	  public boolean validate(final String hex){

		  matcher = pattern.matcher(hex);
		  return matcher.matches();

	  }
	
}
