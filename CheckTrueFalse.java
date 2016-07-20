import java.io.*;
import java.util.*;

/**
 * @author james spargo
 *
 */
public class CheckTrueFalse {

	/**
	 * @param args
	 */
	public static HashMap<String,Integer> symbolstable = new HashMap<String,Integer>();
	public static String[][] hash = new String[64][2];
	public static LogicalExpression knowledge_base = new LogicalExpression();
	public static LogicalExpression statement = new LogicalExpression();
	public static Integer entailscount = 0;
	public static Integer notentailscount = 0;
	public static boolean check = false;
	public static void main(String[] args) {
		
		String outputFile = "result.txt";
		if( args.length != 3){
			//takes three arguments
			System.out.println("Usage: " + args[0] +  " [wumpus-rules-file] [additional-knowledge-file] [input_file]\n");
			exit_function(0);
		}
		
		//create some buffered IO streams
		String buffer;
		BufferedReader inputStream;
		BufferedWriter outputStream;
		
		//create the knowledge base and the statement
		boolean entails;

		//open the wumpus_rules.txt
		try {
			inputStream = new BufferedReader( new FileReader( args[0] ) );
			
			//load the wumpus rules
			System.out.println("loading the wumpus rules...");
			knowledge_base.setConnective("and");
		
			while(  ( buffer = inputStream.readLine() ) != null ) 
                        {
				if( !(buffer.startsWith("#") || (buffer.equals( "" )) )) 
                                {
					//the line is not a comment
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
				} 
                                else 
                                {
					//the line is a comment. do nothing and read the next line
				}
			}		
			
			//close the input file
			inputStream.close();

		} catch(Exception e) 
                {
			System.out.println("failed to open " + args[0] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading wumpus rules
		
		//read the additional knowledge file
		try {
			inputStream = new BufferedReader( new FileReader( args[1] ) );
			
			//load the additional knowledge
			System.out.println("loading the additional knowledge...");
			
			// the connective for knowledge_base is already set.  no need to set it again.
			// i might want the LogicalExpression.setConnective() method to check for that
			//knowledge_base.setConnective("and");
			
			while(  ( buffer = inputStream.readLine() ) != null) 
                 {
                    if( !(buffer.startsWith("#") || (buffer.equals("") ))) 
                    {
				    	LogicalExpression subExpression = readExpression( buffer );
					    String s = ExtractSymbol(subExpression,true);
					    knowledge_base.setSubexpression( subExpression );
                    } 
                    else 
                    {
				//the line is a comment. do nothing and read the next line
                    }
                }
			
			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[1] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading additional knowledge
		
		
		// check for a valid knowledge_base
		if( !valid_expression( knowledge_base ) ) {
			System.out.println("invalid knowledge base");
			exit_function(0);
		}
		
		// print the knowledge_base
		//knowledge_base.print_expression("\n");
		
		
		// read the statement file
		try {
			inputStream = new BufferedReader( new FileReader( args[2] ) );
			
			System.out.println("\n\nLoading the statement file...");
			//buffer = inputStream.readLine();
			
			// actually read the statement file
			// assuming that the statement file is only one line long
			while( ( buffer = inputStream.readLine() ) != null ) {
				if( !buffer.startsWith("#") ) {
					    //the line is not a comment
						statement = readExpression( buffer );
                                                break;
				} else {
					//the line is a commend. no nothing and read the next line
				}
			}
			
			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[2] );
			e.printStackTrace();
			exit_function(0);
		}
		// end reading the statement file
		
		// check for a valid statement
		if( !valid_expression( statement ) ) {
			System.out.println("invalid statement");
			exit_function(0);
		}
		
		//print the statement
		//statement.print_expression( "" );
		//print a new line
		System.out.println("\n");
						
		//testing
		//System.out.println("I don't know if the statement is definitely true or definitely false.");
		
		//System.out.println(symbolstable);
		//System.out.println(symbolstable.size());
		//for (int i=0;i<64;i++)
		//{
		//	System.out.println(hash[i][0]+":"+hash[i][1]);
		//}
		//entails = ttEntails(knowledge_base,statement);
		ttCheckall(0,1);
		ttCheckall(0,0);
		try {
		    BufferedWriter output = new BufferedWriter(
							       new FileWriter( outputFile ) );
		    
		    if(entailscount == 0 && notentailscount == 0)
		    {
		    	output.write("Both True and False");
		    	System.out.println("Both True and False");
		    }
		    else if (entailscount > 0 && notentailscount > 0)
		    {
		    	output.write("possibly true, possibly false");
		    	System.out.println("possibly true, possibly false");
		    }
		    else if (entailscount > 0 && notentailscount == 0)
		    {
		    	output.write("definitely true");
		    	System.out.println("definitely true");
		    }
		    else if (entailscount == 0 && notentailscount > 0)
		    {
		    	output.write("definitely false");
		    	System.out.println("definitely false");
		    }
		    output.close();
		    
		} catch( IOException e ) {
		    System.out.println("\nProblem writing to the output file!\n" +
				       "Try again.");
		    e.printStackTrace();
		}
	} //end of main
	
	public static void ttCheckall(int i, int value)
	{	
		int j;
		//System.out.println("i:"+i);
		boolean value2 = false;
		
		if (i == 64 && value == 1) 
		{
			//System.out.println(symbolstable);
			//exit_function(0);
			/*int q,z;
			String w = "B_3_4";
			String p = "P_4_4";
			q = symbolstable.get(w);
			z = symbolstable.get(p);
			if ((q == 1) && (z==1))
			{
				check = true;
				System.out.println(symbolstable);
			}*/
			boolean KB_truthvalue = PL_true(knowledge_base);
			//System.out.println("**************************Harsha************");
			boolean alpha_truthvalue = PL_true(statement);
			if(KB_truthvalue)
			{
				if(alpha_truthvalue)
				{
					entailscount++;
				}
				else
				{
					notentailscount++;
				}
			}
			//System.out.println(symbolstable);
			return;
		}
		
		if (i<= 63)
		{
			for(j=i;j<=63;j++)
			{
				//System.out.println(hash[j][0]+":"+symbolstable.get(hash[j][0]));
				if (symbolstable.get(hash[j][0]) == 2)
				{
					//System.out.println("found 2:"+j);
					value2 = true;
					break;
				}
				//j++;
			}
			if (value2 == true)
			{	
				//System.out.println("value of j:"+j);
				symbolstable.put(hash[j][0], value);
				ttCheckall(j+1,1);
				ttCheckall(j+1,0);
							
				symbolstable.put(hash[j][0], 2);
			}
		}
	}
	
	public static boolean PL_true(LogicalExpression KB)
	{
		//System.out.println("check"+check);
		//if (check)
		//{
			//System.out.println("test");
		
		if(!(KB.getUniqueSymbol() == null))
		{
			Integer val;
			val = symbolstable.get(KB.getUniqueSymbol());
			//System.out.println(""+KB.getUniqueSymbol()+":"+val);
			if(val == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		Enumeration e = KB.getSubexpressions().elements();
		//Integer ex = KB.getSubexpressions().capacity();
		//System.out.println("-----------:"+ex);
		//System.out.println("enu assigned");
		if (KB.getConnective().equals("not"))
		{
			boolean x = PL_true((LogicalExpression)e.nextElement());
			//System.out.println(""+!x);
			return !x;
		}
		
		if (KB.getConnective().equals("and"))
		{
			boolean xprev = true;
			Integer expcount = KB.getSubexpressions().size();
			while (expcount > 0)
			{
				boolean x = PL_true((LogicalExpression)e.nextElement());
				xprev = xprev && x;
				expcount--;
			}
			//System.out.println(""+xprev);
			return xprev;
		}
		
		if (KB.getConnective().equals("or"))
		{
			boolean xprev = false;
			Integer expcount = KB.getSubexpressions().size();
			//System.out.println(""+expcount);
			while (expcount > 0)
			{
				boolean x = PL_true((LogicalExpression)e.nextElement());
				xprev = xprev || x;
				expcount--;
			}
			//System.out.println(""+xprev);
			return xprev;
		}
		
		if (KB.getConnective().equals("iff"))
		{
			boolean x = PL_true((LogicalExpression)e.nextElement());
			boolean y = PL_true((LogicalExpression)e.nextElement());
			
			//System.out.println("iff:"+x+":"+y);
			if(x==y)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		if (KB.getConnective().equals("if"))
		{
			boolean x = PL_true((LogicalExpression)e.nextElement());
			boolean y = PL_true((LogicalExpression)e.nextElement());
			
			//System.out.println("if:"+x+":"+y);
			if(x && !y)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		//}
		return true;	
	}
	
	public static String ExtractSymbol(LogicalExpression AK, boolean b)
	{
		String us;
		String notus = "";
		String connection;
		Integer value;
		if (!((us = AK.getUniqueSymbol()) == null))
		{
			value = symbolstable.get(us);
			//System.out.println("val:"+value);
			if (value == 2 && b== true)
			{
				//System.out.println("Symbol::::::::::"+AK.getUniqueSymbol());
				symbolstable.put(us, 1);
				//System.out.println(""+symbolstable.get(us));
			}
			return us;
		}
		connection = AK.getConnective();
		if(connection.equals("not"))
		{
			LogicalExpression notexp;	// = AK.getNextSubexpression();
			Enumeration e = AK.getSubexpressions().elements();
			notexp = ( LogicalExpression )e.nextElement();
			notus = ExtractSymbol(notexp, false);
			value = symbolstable.get(notus);
			if ((value == 2))
			{
				symbolstable.put(notus, 0);
			}
		}
		return notus;
	}
	//{
		
		//return true;
	//}
	/* this method reads logical expressions
	 * if the next string is a:
	 * - '(' => then the next 'symbol' is a subexpression
	 * - else => it must be a unique_symbol
	 * 
	 * it returns a logical expression
	 * 
	 * notes: i'm not sure that I need the counter
	 * 
	 */
	public static LogicalExpression readExpression( String input_string ) 
        {
          LogicalExpression result = new LogicalExpression();
          
          //testing
          //System.out.println("readExpression() beginning -"+ input_string +"-");
          //testing
          //System.out.println("\nread_exp");
          
          //trim the whitespace off
          input_string = input_string.trim();
          
          if( input_string.startsWith("(") ) 
          {
            //its a subexpression
          
            String symbolString = "";
            
            // remove the '(' from the input string
            symbolString = input_string.substring( 1 );
            //symbolString.trim();
            
            //testing
            //System.out.println("readExpression() without opening paren -"+ symbolString + "-");
				  
            if( !symbolString.endsWith(")" ) ) 
            {
              // missing the closing paren - invalid expression
              System.out.println("missing ')' !!! - invalid expression! - readExpression():-" + symbolString );
              exit_function(0);
              
            }
            else 
            {
              //remove the last ')'
              //it should be at the end
              symbolString = symbolString.substring( 0 , ( symbolString.length() - 1 ) );
              symbolString.trim();
              
              //testing
              //System.out.println("readExpression() without closing paren -"+ symbolString + "-");
              
              // read the connective into the result LogicalExpression object					  
              symbolString = result.setConnective( symbolString );
              
              //testing
              //System.out.println("added connective:-" + result.getConnective() + "-: here is the string that is left -" + symbolString + "-:");
              //System.out.println("added connective:->" + result.getConnective() + "<-");
            }
            
            //read the subexpressions into a vector and call setSubExpressions( Vector );
            result.setSubexpressions( read_subexpressions( symbolString ) );
            
          } 
          else 
          {   	
            // the next symbol must be a unique symbol
            // if the unique symbol is not valid, the setUniqueSymbol will tell us.
            result.setUniqueSymbol( input_string );
          
            //testing
            //System.out.println(" added:-" + input_string + "-:as a unique symbol: readExpression()" );
          }
          
          return result;
        }

	/* this method reads in all of the unique symbols of a subexpression
	 * the only place it is called is by read_expression(String, long)(( the only read_expression that actually does something ));
	 * 
	 * each string is EITHER:
	 * - a unique Symbol
	 * - a subexpression
	 * - Delineated by spaces, and paren pairs
	 * 
	 * it returns a vector of logicalExpressions
	 * 
	 * 
	 */
	
	public static Vector<LogicalExpression> read_subexpressions( String input_string ) {

	Vector<LogicalExpression> symbolList = new Vector<LogicalExpression>();
	LogicalExpression newExpression;// = new LogicalExpression();
	String newSymbol = new String();
	
	//testing
	//System.out.println("reading subexpressions! beginning-" + input_string +"-:");
	//System.out.println("\nread_sub");

	input_string.trim();

	while( input_string.length() > 0 ) {
		
		newExpression = new LogicalExpression();
		
		//testing
		//System.out.println("read subexpression() entered while with input_string.length ->" + input_string.length() +"<-");

		if( input_string.startsWith( "(" ) ) {
			//its a subexpression.
			// have readExpression parse it into a LogicalExpression object

			//testing
			//System.out.println("read_subexpression() entered if with: ->" + input_string + "<-");
			
			// find the matching ')'
			int parenCounter = 1;
			int matchingIndex = 1;
			while( ( parenCounter > 0 ) && ( matchingIndex < input_string.length() ) ) {
					if( input_string.charAt( matchingIndex ) == '(') {
						parenCounter++;
					} else if( input_string.charAt( matchingIndex ) == ')') {
						parenCounter--;
					}
				matchingIndex++;
			}
			
			// read untill the matching ')' into a new string
			newSymbol = input_string.substring( 0, matchingIndex );
			
			//testing
			//System.out.println( "-----read_subExpression() - calling readExpression with: ->" + newSymbol + "<- matchingIndex is ->" + matchingIndex );

			// pass that string to readExpression,
			newExpression = readExpression( newSymbol );

			// add the LogicalExpression that it returns to the vector symbolList
			symbolList.add( newExpression );

			// trim the logicalExpression from the input_string for further processing
			input_string = input_string.substring( newSymbol.length(), input_string.length() );

		} else {
			//its a unique symbol ( if its not, setUniqueSymbol() will tell us )

			// I only want the first symbol, so, create a LogicalExpression object and
			// add the object to the vector
			
			if( input_string.contains( " " ) ) {
				//remove the first string from the string
				newSymbol = input_string.substring( 0, input_string.indexOf( " " ) );
				input_string = input_string.substring( (newSymbol.length() + 1), input_string.length() );
				
				//testing
				//System.out.println( "read_subExpression: i just read ->" + newSymbol + "<- and i have left ->" + input_string +"<-" );
			} else {
				newSymbol = input_string;
				input_string = "";
			}
			
			//testing
			//System.out.println( "readSubExpressions() - trying to add -" + newSymbol + "- as a unique symbol with ->" + input_string + "<- left" );
			
			newExpression.setUniqueSymbol( newSymbol );
			
	    	//testing
	    	//System.out.println("readSubexpression(): added:-" + newSymbol + "-:as a unique symbol. adding it to the vector" );

			symbolList.add( newExpression );
			
			//testing
			//System.out.println("read_subexpression() - after adding: ->" + newSymbol + "<- i have left ->"+ input_string + "<-");
			
		}
		
		//testing
		//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-beforeTrim end of while");
		
		input_string.trim();
		
		if( input_string.startsWith( " " )) {
			//remove the leading whitespace
			input_string = input_string.substring(1);
		}
		
		//testing
		//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-afterTrim with string length-" + input_string.length() + "<- end of while");
	}
	return symbolList;
}


	/* this method checks to see if a logical expression is valid or not 
	 * a valid expression either:
	 * ( this is an XOR )
	 * - is a unique_symbol
	 * - has:
	 *  -- a connective
	 *  -- a vector of logical expressions
	 *  
	 * */
	public static boolean valid_expression(LogicalExpression expression)
	{
		
		// checks for an empty symbol
		// if symbol is not empty, check the symbol and
		// return the truthiness of the validity of that symbol

		if ( !(expression.getUniqueSymbol() == null) && ( expression.getConnective() == null ) ) {
			// we have a unique symbol, check to see if its valid
			return valid_symbol( expression.getUniqueSymbol() );

			//testing
			//System.out.println("valid_expression method: symbol is not empty!\n");
			}

		// symbol is empty, so
		// check to make sure the connective is valid
	  
		// check for 'if / iff'
		if ( ( expression.getConnective().equalsIgnoreCase("if") )  ||
		      ( expression.getConnective().equalsIgnoreCase("iff") ) ) {
			
			// the connective is either 'if' or 'iff' - so check the number of connectives
			if (expression.getSubexpressions().size() != 2) {
				System.out.println("error: connective \"" + expression.getConnective() +
						"\" with " + expression.getSubexpressions().size() + " arguments\n" );
				return false;
				}
			}
		// end 'if / iff' check
	  
		// check for 'not'
		else   if ( expression.getConnective().equalsIgnoreCase("not") ) {
			// the connective is NOT - there can be only one symbol / subexpression
			if ( expression.getSubexpressions().size() != 1)
			{
				System.out.println("error: connective \""+ expression.getConnective() + "\" with "+ expression.getSubexpressions().size() +" arguments\n" ); 
				return false;
				}
			}
		// end check for 'not'
		
		// check for 'and / or / xor'
		else if ( ( !expression.getConnective().equalsIgnoreCase("and") )  &&
				( !expression.getConnective().equalsIgnoreCase( "or" ) )  &&
				( !expression.getConnective().equalsIgnoreCase("xor" ) ) ) {
			System.out.println("error: unknown connective " + expression.getConnective() + "\n" );
			return false;
			}
		// end check for 'and / or / not'
		// end connective check

	  
		// checks for validity of the logical_expression 'symbols' that go with the connective
		for( Enumeration e = expression.getSubexpressions().elements(); e.hasMoreElements(); ) {
			LogicalExpression testExpression = (LogicalExpression)e.nextElement();
			
			// for each subExpression in expression,
			//check to see if the subexpression is valid
			if( !valid_expression( testExpression ) ) {
				return false;
			}
		}

		//testing
		//System.out.println("The expression is valid");
		
		// if the method made it here, the expression must be valid
		return true;
	}
	



	/** this function checks to see if a unique symbol is valid */
	//////////////////// this function should be done and complete
	// originally returned a data type of long.
	// I think this needs to return true /false
	//public long valid_symbol( String symbol ) {
	public static boolean valid_symbol( String symbol ) {
		if (  symbol == null || ( symbol.length() == 0 )) {
			
			//testing
			//System.out.println("String: " + symbol + " is invalid! Symbol is either Null or the length is zero!\n");
			
			return false;
		}

		for ( int counter = 0; counter < symbol.length(); counter++ ) {
			if ( (symbol.charAt( counter ) != '_') &&
					( !Character.isLetterOrDigit( symbol.charAt( counter ) ) ) ) {
				
				System.out.println("String: " + symbol + " is invalid! Offending character:---" + symbol.charAt( counter ) + "---\n");
				
				return false;
			}
		}
		
		// the characters of the symbol string are either a letter or a digit or an underscore,
		//return true
		return true;
	}

        private static void exit_function(int value) {
                System.out.println("exiting from checkTrueFalse");
                  System.exit(value);
                }	
}


