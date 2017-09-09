package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
public class Scanner {
	/**
	 * Kind enum
	 */
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}

	HashMap<String,Kind > kindMap = new HashMap<>();
	public void setkindMap(){
		kindMap.put("true", Kind.KW_TRUE);
		kindMap.put("false", Kind.KW_FALSE);
		kindMap.put("integer", Kind.KW_INTEGER);
		kindMap.put("boolean", Kind.KW_BOOLEAN);
		kindMap.put("image", Kind.KW_IMAGE);
		kindMap.put("url", Kind.KW_URL);
		kindMap.put("file", Kind.KW_FILE);
		kindMap.put("frame", Kind.KW_FRAME);
		kindMap.put("while", Kind.KW_WHILE);
		kindMap.put("if", Kind.KW_IF);
		kindMap.put("blur", Kind.OP_BLUR);
		kindMap.put("gray", Kind.OP_GRAY);
		kindMap.put("convolve", Kind.OP_CONVOLVE);
		kindMap.put("screenheight", Kind.KW_SCREENHEIGHT);
		kindMap.put("height", Kind.OP_HEIGHT);
		kindMap.put("width", Kind.OP_WIDTH);
		kindMap.put("screenwidth", Kind.KW_SCREENWIDTH);
		kindMap.put("xloc", Kind.KW_XLOC);
		kindMap.put("yloc", Kind.KW_YLOC);
		kindMap.put("hide", Kind.KW_HIDE);
		kindMap.put("show", Kind.KW_SHOW);
		kindMap.put("move", Kind.KW_MOVE);
		kindMap.put("sleep", Kind.OP_SLEEP);
		kindMap.put("scale", Kind.KW_SCALE);
	}
	
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		
	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			
			if(this.kind == Kind.INT_LIT)
				return chars.substring(pos, pos + length);
			else if(this.kind == Kind.IDENT)
				return chars.substring(pos, pos + length);
				else
			return this.kind.getText();
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			int lineNo = Collections.binarySearch(lineNumber,pos);
			lineNo = lineNo < 0?( - lineNo ) - 2:lineNo;
			int columnPosition = pos - lineNumber.get(lineNo);
			return new LinePos(lineNo,columnPosition);
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			//TODO IMPLEMENT THIS
			//return 0;
			return Integer.parseInt(this.getText());
		}
		  @Override
		  public int hashCode() {
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		  }

		  @Override
		  public boolean equals(Object obj) {
		   if (this == obj) {
		    return true;
		   }
		   if (obj == null) {
		    return false;
		   }
		   if (!(obj instanceof Token)) {
		    return false;
		   }
		   Token other = (Token) obj;
		   if (!getOuterType().equals(other.getOuterType())) {
		    return false;
		   }
		   if (kind != other.kind) {
		    return false;
		   }
		   if (length != other.length) {
		    return false;
		   }
		   if (pos != other.pos) {
		    return false;
		   }
		   return true;
		  }

		  private Scanner getOuterType() {
		   return Scanner.this;
		  }
		
	}

	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		setkindMap();
	}
	
	public int commentHandler(int position,int length){
		boolean commentClosure=false;
		//Check for length 
		while(position < length){
			// if input is having /* as the character set then check for the closing of the commend with */ until keep incrementing position 
			if((position < (length -1)) && chars.charAt(position) == '/' && chars.charAt(position + 1) == '*'){
				position = position + 2;
				commentClosure = false;
				
				while( (!commentClosure) && (position < (length -1)) ){
					if(chars.charAt(position) == '*' && chars.charAt(position + 1) == '/'){
						commentClosure = true;
						position = position + 2;
					}
					else if(chars.charAt(position) == '\n'){
						lineNumber.add(position+1);
						position++;
					}
					else 
						position++;
				};
				
				if(!commentClosure)
					return -1;
				
		}
			else if(chars.charAt(position) == '\n') {
				// Handling /n received from file
				if(chars.charAt(position) == '\\' && chars.charAt(position + 1) == 'n')
					position++;
				position++;
				lineNumber.add(position);
			}

			else if(Character.isWhitespace(chars.charAt(position)))
			position++;
			else
				break;
		}
		return position;
	}

	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0,charLength=chars.length()==0?-1:chars.length(),posCopy=0;
		String tokenState = "START";
		Character currentTokenChar;
		lineNumber.add(0);
		
		while(pos <= charLength ){
			currentTokenChar = pos < charLength ? chars.charAt(pos) : '"';
			
			switch(tokenState){
			case "START":{
				pos = commentHandler(pos,charLength);
				if(pos == -1)
					throw new IllegalCharException("ERROR: Comments are not properly closed before eof");
				posCopy = pos;
				currentTokenChar = pos < charLength ? chars.charAt(pos) : '"';
				if(currentTokenChar == '"')
					pos++;
				else if (currentTokenChar == '('){
					tokens.add(new Token(Kind.LPAREN, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == ')'){
					tokens.add(new Token(Kind.RPAREN, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == '{'){
					tokens.add(new Token(Kind.LBRACE, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == '}'){
					tokens.add(new Token(Kind.RBRACE, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == ','){
					tokens.add(new Token(Kind.COMMA, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == ';'){
					tokens.add(new Token(Kind.SEMI, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == '&'){
					tokens.add(new Token(Kind.AND, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == '+'){
					tokens.add(new Token(Kind.PLUS, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == '*'){
					tokens.add(new Token(Kind.TIMES, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == '/'){
					tokens.add(new Token(Kind.DIV, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == '%'){
					tokens.add(new Token(Kind.MOD, posCopy, 1));
					pos++;
				}
				else if (currentTokenChar == '-'){
					pos++;
					if((pos < charLength) && (chars.charAt(pos) == '>')){
						pos++;
						tokens.add(new Token(Kind.ARROW, posCopy, pos-posCopy));
					}
					else 
						tokens.add(new Token(Kind.MINUS, posCopy, 1));
					tokenState = "START";
				}
				else if (currentTokenChar == '|'){
					pos++;
					if((pos < charLength) && (chars.charAt(pos) == '-')){
						pos++;
						if((pos < charLength) && (chars.charAt(pos) == '>')){
							pos++;
							tokens.add(new Token(Kind.BARARROW, posCopy, pos-posCopy));
							tokenState = "START";
						}
						//else
							//throw new IllegalCharException("ERROR: Invalid Token !!. Valid token expected is : '|->'.");
					}
					else {
						tokens.add(new Token(Kind.OR, posCopy, pos - posCopy));
						tokenState = "START";
					}
				}
				else if (currentTokenChar == '='){
					pos++;
					if((pos<charLength) && (chars.charAt(pos) == '=')){
						pos++;
						tokens.add(new Token(Kind.EQUAL, posCopy, pos-posCopy));
						tokenState = "START";
					}else
						throw new IllegalCharException("ERROR: Valid token is : '==' but got only '='.");

				}
				else if (currentTokenChar == '!'){
					pos++;
					if ((pos<charLength) && (chars.charAt(pos) == '=')) {
						pos++;
						tokens.add(new Token(Kind.NOTEQUAL, posCopy, pos-posCopy));
					}else 
						tokens.add(new Token(Kind.NOT, posCopy, 1));
					tokenState = "START";		
				}
				else if (currentTokenChar == '<'){
					pos++;
					if ((pos<charLength) && (chars.charAt(pos) == '=')) {
						pos++;
						tokens.add(new Token(Kind.LE, posCopy, pos-posCopy));
					}else if ((pos<charLength) && (chars.charAt(pos) == '-')) {
						pos++;
						tokens.add(new Token(Kind.ASSIGN, posCopy, pos-posCopy));
					}else
						tokens.add(new Token(Kind.LT, posCopy, 1));
					tokenState = "START";	
				}
				else if (currentTokenChar == '>'){
					pos++;
					if ((pos<charLength) && (chars.charAt(pos) == '=')) {
						pos++;
						tokens.add(new Token(Kind.GE, posCopy, pos-posCopy));
					}else 
						tokens.add(new Token(Kind.GT, posCopy, 1));
					tokenState = "START";
				}
				else if (Character.isDigit(currentTokenChar)){
					if(currentTokenChar == '0')
						tokens.add(new Token(Kind.INT_LIT, posCopy, 1));
					else
						tokenState = "NUMERIC_STATE";
					pos++;
				}
				else if (Character.isJavaIdentifierStart(currentTokenChar)){
					pos++;
					tokenState = "IDENTIFIER_STATE";
				}
				else
					throw new IllegalCharException("ERROR: INPUT TOKEN IS NOT AS PER REQUIRED LANGUAGE");
			}
			break;
			case "NUMERIC_STATE" : {
				if( (pos < charLength) && Character.isDigit(chars.charAt(pos)))
					pos++;
				else {
					try {
						String digit = chars.substring(posCopy, pos);
						if(kindMap.containsKey(digit))
							tokens.add(new Token(kindMap.get(digit), posCopy, pos - posCopy));
						else{
							Integer.parseInt(digit);
							tokens.add(new Token(Kind.INT_LIT, posCopy, pos-posCopy));
						}
					} catch (NumberFormatException e) {
						// TODO: handle exception
						throw new IllegalNumberException("ERROR: INPUT NUMBER IS OUT OF RANGE.");
					}
					tokenState = "START";
				}
			}
			break;
			case "IDENTIFIER_STATE" : {
				if( (pos < charLength) && Character.isJavaIdentifierPart(chars.charAt(pos)))
					pos++;
				else {
						String string = chars.substring(posCopy, pos);
						if(kindMap.containsKey(string))
							tokens.add(new Token(kindMap.get(string), posCopy, pos - posCopy));
						else{
							tokens.add(new Token(Kind.IDENT, posCopy, pos-posCopy));
						}
						tokenState = "START";
					} 
			}
			break;
			default: assert false;
		}
	}
	//TODO IMPLEMENT THIS!!!!
		pos=pos>charLength?charLength:pos;
		
		tokens.add(new Token(Kind.EOF,pos,0));
		return this;  
	}

	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;
	ArrayList<Integer> lineNumber = new ArrayList<Integer>();
	
	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);		
	}

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		return t.getLinePos();
	}


}
