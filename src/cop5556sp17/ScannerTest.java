package cop5556sp17;

import static cop5556sp17.Scanner.Kind.SEMI;
import static cop5556sp17.Scanner.Kind;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


import org.junit.rules.ExternalResource;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}
	
	@Test
	public void testIntegerValue() throws IllegalCharException, IllegalNumberException {
		String input = "2011";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token.kind);
		assertEquals(0, token.pos);
		assertEquals(input.length(), token.length);
		assertEquals(input, token.getText());
		assertEquals(Integer.parseInt(input), token.intVal());
		
	}
	@Test
	public void testString() throws IllegalCharException, IllegalNumberException {
		String input = "temp";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(0, token.pos);
		assertEquals(input.length(), token.length);
		assertEquals(input, token.getText());
		assertEquals(input, token.getText());
	}
	@Test
	public void testkeywordSet1() throws IllegalCharException, IllegalNumberException {
		String input = "integer boolean image url file frame while if sleep screenheight screenwidth";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//Test First Token
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(0, token.pos);
		assertEquals("integer".length(), token.length);
		assertEquals("integer", token.getText());
		//Test Second Token
		token = scanner.nextToken();
		assertEquals(Kind.KW_BOOLEAN, token.kind);
		assertEquals(8, token.pos);
		assertEquals("boolean".length(), token.length);
		assertEquals("boolean", token.getText());
		//Test Third Token
		token = scanner.nextToken();
		assertEquals(Kind.KW_IMAGE, token.kind);
		assertEquals(16, token.pos);
		assertEquals("image".length(), token.length);
		assertEquals("image", token.getText());
		//Test Fourth Token
		token = scanner.nextToken();
		assertEquals(Kind.KW_URL, token.kind);
		assertEquals(22, token.pos);
		assertEquals("url".length(), token.length);
		assertEquals("url", token.getText());
		//Test Fifth Token
		token = scanner.nextToken();
		assertEquals(Kind.KW_FILE, token.kind);
		assertEquals(26, token.pos);
		assertEquals("file".length(), token.length);
		assertEquals("file", token.getText());
		//Test 6th Token
		token = scanner.nextToken();
		assertEquals(Kind.KW_FRAME, token.kind);
		assertEquals(31, token.pos);
		assertEquals("frame".length(), token.length);
		assertEquals("frame", token.getText());
		//Test 7th Token
		token = scanner.nextToken();
		assertEquals(Kind.KW_WHILE, token.kind);
		assertEquals(37, token.pos);
		assertEquals("while".length(), token.length);
		assertEquals("while", token.getText());
		//Test 8th Token
		token = scanner.nextToken();
		assertEquals(Kind.KW_IF, token.kind);
		assertEquals(43, token.pos);
		assertEquals("if".length(), token.length);
		assertEquals("if", token.getText());
		//Test 6th Token
		token = scanner.nextToken();
		assertEquals(Kind.OP_SLEEP, token.kind);
		assertEquals(46, token.pos);
		assertEquals("sleep".length(), token.length);
		assertEquals("sleep", token.getText());
		//Test 7th Token
		token = scanner.nextToken();
		assertEquals(Kind.KW_SCREENHEIGHT, token.kind);
		assertEquals(52, token.pos);
		assertEquals("screenheight".length(), token.length);
		assertEquals("screenheight", token.getText());
		//Test 8th Token
		token = scanner.nextToken();
		assertEquals(Kind.KW_SCREENWIDTH, token.kind);
		assertEquals(65, token.pos);
		assertEquals("screenwidth".length(), token.length);
		assertEquals("screenwidth", token.getText());
	}
	@Test
	public void testkeywordSet2() throws IllegalCharException, IllegalNumberException {
		String input = "gray convolve blur scale width height";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//Test First Token
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.OP_GRAY, token.kind);
		assertEquals(0, token.pos);
		assertEquals("gray".length(), token.length);
		assertEquals("gray", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.OP_CONVOLVE, token.kind);
		assertEquals(5, token.pos);
		assertEquals("convolve".length(), token.length);
		assertEquals("convolve", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.OP_BLUR, token.kind);
		assertEquals(14, token.pos);
		assertEquals("blur".length(), token.length);
		assertEquals("blur", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.KW_SCALE, token.kind);
		assertEquals(19, token.pos);
		assertEquals("scale".length(), token.length);
		assertEquals("scale", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.OP_WIDTH, token.kind);
		assertEquals(25, token.pos);
		assertEquals("width".length(), token.length);
		assertEquals("width", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.OP_HEIGHT, token.kind);
		assertEquals(31, token.pos);
		assertEquals("height".length(), token.length);
		assertEquals("height", token.getText());
	}

	@Test
	public void testkeywordSet3() throws IllegalCharException, IllegalNumberException {
		String input = "xloc yloc hide show move true false";
		
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//Test First Token
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.KW_XLOC, token.kind);
		assertEquals(0, token.pos);
		assertEquals("xloc".length(), token.length);
		assertEquals("xloc", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.KW_YLOC, token.kind);
		assertEquals(5, token.pos);
		assertEquals("yloc".length(), token.length);
		assertEquals("yloc", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.KW_HIDE, token.kind);
		assertEquals(10, token.pos);
		assertEquals("hide".length(), token.length);
		assertEquals("hide", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.KW_SHOW, token.kind);
		assertEquals(15, token.pos);
		assertEquals("show".length(), token.length);
		assertEquals("show", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.KW_MOVE, token.kind);
		assertEquals(20, token.pos);
		assertEquals("move".length(), token.length);
		assertEquals("move", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.KW_TRUE, token.kind);
		assertEquals(25, token.pos);
		assertEquals("true".length(), token.length);
		assertEquals("true", token.getText());
		token = scanner.nextToken();
		assertEquals(Kind.KW_FALSE, token.kind);
		assertEquals(30, token.pos);
		assertEquals("false".length(), token.length);
		assertEquals("false", token.getText());
	}
	
	@Test
	public void testMultiLineComment() throws IllegalCharException, IllegalNumberException {
		Scanner scanner = new Scanner("/*This is first /n * assignment / for PLP /n */");
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(token.kind, Kind.EOF);
	}
	

	@Test
	public void testInvalidMultiLineComment() throws IllegalCharException, IllegalNumberException {
		Scanner scanner = new Scanner("/*This is first /n * assignment / for PLP /n * blah blah /");
		Scanner.Token token = scanner.nextToken();
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}
	
	@Test
	public void testInBetweenComment() throws IllegalCharException, IllegalNumberException {
		Scanner scanner = new Scanner("integer a /*test*/;");
		scanner.scan();
		//Test First Token
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(0, token.pos);
		assertEquals("integer".length(), token.length);
		assertEquals("integer", token.getText());
		
		token = scanner.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(8, token.pos);
		assertEquals("a".length(), token.length);
		assertEquals("a", token.getText());
		

		token = scanner.nextToken();
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(18, token.pos);
		assertEquals(";".length(), token.length);
		assertEquals(";", token.getText());
	}
	
	@Test
	public void testValidOperator() throws IllegalCharException, IllegalNumberException {
		Scanner scanner = new Scanner("<- > >= < <= ! != |-> ->");
		scanner.scan();

		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.ASSIGN, token.kind);
		assertEquals(0, token.pos);
		
		token = scanner.nextToken();
		assertEquals(Kind.GT, token.kind);
		assertEquals(3, token.pos);
		
		token = scanner.nextToken();
		assertEquals(Kind.GE, token.kind);
		assertEquals(5, token.pos);
		
		token = scanner.nextToken();
		assertEquals(Kind.LT, token.kind);
		assertEquals(8, token.pos);
		
		token = scanner.nextToken();
		assertEquals(Kind.LE, token.kind);
		assertEquals(10, token.pos);
		
		token = scanner.nextToken();
		assertEquals(Kind.NOT, token.kind);
		assertEquals(13, token.pos);
		
		token = scanner.nextToken();
		assertEquals(Kind.NOTEQUAL, token.kind);
		assertEquals(15, token.pos);
		
		token = scanner.nextToken();
		assertEquals(Kind.BARARROW, token.kind);
		assertEquals(18, token.pos);
		
		token = scanner.nextToken();
		assertEquals(Kind.ARROW, token.kind);
		assertEquals(22, token.pos);
		
	}
	


	@Test
	public void testIdent() throws IllegalCharException, IllegalNumberException {
	String input = "_number Quit $num  tEst 123abc";
	Scanner scanner = new Scanner(input);
	scanner.scan();
	Scanner.Token token = scanner.nextToken();
	
	assertEquals(Kind.IDENT, token.kind);
	assertEquals(0, token.pos);
	assertEquals("_number".length(), token.length);
	assertEquals("_number", token.getText());
	
	token = scanner.nextToken();
	assertEquals(Kind.IDENT, token.kind);
	assertEquals(8, token.pos);
	assertEquals("Quit".length(), token.length);
	assertEquals("Quit", token.getText());
	
	token = scanner.nextToken();
	assertEquals(Kind.IDENT, token.kind);
	assertEquals(13, token.pos);
	assertEquals("$num".length(), token.length);
	assertEquals("$num", token.getText());
	
	token = scanner.nextToken();
	assertEquals(Kind.IDENT, token.kind);
	assertEquals(19, token.pos);
	assertEquals("tEst".length(), token.length);
	assertEquals("tEst", token.getText());
	
	token = scanner.nextToken();
	assertEquals(Kind.INT_LIT, token.kind);
	assertEquals(24, token.pos);
	assertEquals("123".length(), token.length);
	assertEquals(123, token.intVal());
	
	token = scanner.nextToken();
	assertEquals(Kind.IDENT, token.kind);
	assertEquals(27, token.pos);
	assertEquals("abc".length(), token.length);
	assertEquals("abc", token.getText());
	
	}

	
	@Test
	public void testLeadingZero() throws IllegalCharException, IllegalNumberException {
	String input = "123 0012345";
	Scanner scanner = new Scanner(input);
	scanner.scan();
	Scanner.Token token = scanner.nextToken();
	assertEquals(Kind.INT_LIT, token.kind);
	assertEquals(0, token.pos);
	assertEquals("123".length(), token.length);
	assertEquals(123, token.intVal());
	
	token = scanner.nextToken();
	assertEquals(Kind.INT_LIT, token.kind);
	assertEquals(4, token.pos);
	assertEquals("0".length(), token.length);
	assertEquals(0, token.intVal());
	
	token = scanner.nextToken();
	assertEquals(Kind.INT_LIT, token.kind);
	assertEquals(5, token.pos);
	assertEquals("0".length(), token.length);
	assertEquals(0, token.intVal());
	
	token = scanner.nextToken();
	assertEquals(Kind.INT_LIT, token.kind);
	assertEquals(6, token.pos);
	assertEquals("12345".length(), token.length);
	assertEquals(12345, token.intVal());
	
	
	}

	
	
	@Test
	public void testOpertor1() throws IllegalCharException, IllegalNumberException {
	String input = "===";
	Scanner scanner = new Scanner(input);
	thrown.expect(IllegalCharException.class);
	scanner.scan();
	
	}
	/**
	* 
	* @throws IllegalCharException
	* @throws IllegalNumberException
	*/
	@Test
	public void testSeparatorOperator2() throws IllegalCharException, IllegalNumberException {
	String input = "==!==";
	Scanner scanner = new Scanner(input);
	thrown.expect(IllegalCharException.class);
	scanner.scan();
	 
	}
	
	@Test
	public void testAssignOperator() throws IllegalCharException, IllegalNumberException {
	String input = "/* Sum */ a <- 1 + 2;\n";
	Scanner scanner = new Scanner(input);
	scanner.scan();
	Scanner.Token token = scanner.nextToken();
	assertEquals(Kind.IDENT, token.kind);
	assertEquals(10, token.pos);
	assertEquals("a".length(), token.length);
	assertEquals("a", token.getText());
	 
	token = scanner.nextToken();
	assertEquals(Kind.ASSIGN, token.kind);
	assertEquals(12, token.pos);
	assertEquals("<-".length(), token.length);
	assertEquals("<-", token.getText());
	 
	token = scanner.nextToken();
	assertEquals(Kind.INT_LIT, token.kind);
	assertEquals(15, token.pos);
	assertEquals("1".length(), token.length);
	assertEquals(1, token.intVal());
	 
	
	token = scanner.nextToken();
	assertEquals(Kind.PLUS, token.kind);
	assertEquals(17, token.pos);
	assertEquals("+".length(), token.length);
	assertEquals("+", token.getText());
	
	token = scanner.nextToken();
	assertEquals(Kind.INT_LIT, token.kind);
	assertEquals(19, token.pos);
	assertEquals("2".length(), token.length);
	assertEquals(2, token.intVal());
	
	}
	
	
	@Test

public void testLinePos() throws IllegalCharException, IllegalNumberException {
	String input = "temp\n  flip\ninteger\n      ;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		String linePos = "LinePos [line=0, posInLine=0]";
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(linePos, token.getLinePos().toString());
		assertEquals(0, token.pos);
		assertEquals("temp".length(), token.length);
		assertEquals("temp", token.getText());
		
		token = scanner.nextToken();
		linePos = "LinePos [line=1, posInLine=2]";
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(linePos, token.getLinePos().toString());
		assertEquals(7, token.pos);
		assertEquals("flip".length(), token.length);
		assertEquals("flip", token.getText());
		
		token = scanner.nextToken();
		linePos = "LinePos [line=2, posInLine=0]";
		assertEquals(Kind.KW_INTEGER, token.kind);
		assertEquals(linePos, token.getLinePos().toString());
		assertEquals(12, token.pos);
		assertEquals("integer".length(), token.length);
		assertEquals("integer", token.getText());
		
		token = scanner.nextToken();
		linePos = "LinePos [line=3, posInLine=6]";
		assertEquals(Kind.SEMI, token.kind);
		assertEquals(linePos, token.getLinePos().toString());
		assertEquals(26, token.pos);
		assertEquals(";".length(), token.length);
		assertEquals(";", token.getText());
		
	}
	
	@Test
	public void testMiniProgram() throws IllegalCharException, IllegalNumberException {
	String input = "integer num1 <- 2;\ninteger sum <- num1 + 100\n;";
	Scanner scanner = new Scanner(input);
	scanner.scan();
	Scanner.Token token = scanner.nextToken();
	String linePos = "LinePos [line=0, posInLine=0]";
	assertEquals(Kind.KW_INTEGER, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(0, token.pos);
	assertEquals("integer".length(), token.length);
	assertEquals("integer", token.getText());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=0, posInLine=8]";
	assertEquals(Kind.IDENT, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(8, token.pos);
	assertEquals("num1".length(), token.length);
	assertEquals("num1", token.getText());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=0, posInLine=13]";
	assertEquals(Kind.ASSIGN, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(13, token.pos);
	assertEquals("<-".length(), token.length);
	assertEquals("<-", token.getText());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=0, posInLine=16]";
	assertEquals(Kind.INT_LIT, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(16, token.pos);
	assertEquals("2".length(), token.length);
	assertEquals(2, token.intVal());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=0, posInLine=17]";
	assertEquals(Kind.SEMI, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(17, token.pos);
	assertEquals(";".length(), token.length);
	assertEquals(";", token.getText());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=1, posInLine=0]";
	assertEquals(Kind.KW_INTEGER, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(19, token.pos);
	assertEquals("integer".length(), token.length);
	assertEquals("integer", token.getText());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=1, posInLine=8]";
	assertEquals(Kind.IDENT, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(27, token.pos);
	assertEquals("sum".length(), token.length);
	assertEquals("sum", token.getText());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=1, posInLine=12]";
	assertEquals(Kind.ASSIGN, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(31, token.pos);
	assertEquals("<-".length(), token.length);
	assertEquals("<-", token.getText());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=1, posInLine=15]";
	assertEquals(Kind.IDENT, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(34, token.pos);
	assertEquals("num1".length(), token.length);
	assertEquals("num1", token.getText());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=1, posInLine=20]";
	assertEquals(Kind.PLUS, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(39, token.pos);
	assertEquals("+".length(), token.length);
	assertEquals("+", token.getText());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=1, posInLine=22]";
	assertEquals(Kind.INT_LIT, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(41, token.pos);
	assertEquals("100".length(), token.length);
	assertEquals(100, token.intVal());
	
	token = scanner.nextToken();
	linePos = "LinePos [line=2, posInLine=0]";
	assertEquals(Kind.SEMI, token.kind);
	assertEquals(linePos, token.getLinePos().toString());
	assertEquals(45, token.pos);
	assertEquals(";".length(), token.length);
	assertEquals(";", token.getText());


	}
	
	@Test
	public void testboolean() throws IllegalCharException, IllegalNumberException {
	String input = "boolean bool <- true;";
	Scanner scanner = new Scanner(input);
	scanner.scan();
	Scanner.Token token = scanner.nextToken();
	assertEquals(Kind.KW_BOOLEAN, token.kind);
	assertEquals(0, token.pos);
	assertEquals("boolean".length(), token.length);
	assertEquals("boolean", token.getText());
	
	token = scanner.nextToken();
	assertEquals(Kind.IDENT, token.kind);
	assertEquals(8, token.pos);
	assertEquals("bool".length(), token.length);
	assertEquals("bool", token.getText());
	
	token = scanner.nextToken();
	assertEquals(Kind.ASSIGN, token.kind);
	assertEquals(13, token.pos);
	assertEquals("<-".length(), token.length);
	assertEquals("<-", token.getText());
	
	token = scanner.nextToken();
	assertEquals(Kind.KW_TRUE, token.kind);
	assertEquals(16, token.pos);
	assertEquals("true".length(), token.length);
	assertEquals("true", token.getText());
	
	token = scanner.nextToken();
	assertEquals(Kind.SEMI, token.kind);
	assertEquals(20, token.pos);
	assertEquals(";".length(), token.length);
	assertEquals(";", token.getText());
	
	
	}
	@Test
	public void TestInvalidComment() throws IllegalCharException, IllegalNumberException {
		Scanner scanner = new Scanner("/* sasjhfa 12653 */ djifew */");
		//thrown.expect(IllegalCharException.class);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(20, token.pos);
		assertEquals("djifew".length(), token.length);
		assertEquals("djifew", token.getText());
		
		token = scanner.nextToken();
		assertEquals(Kind.TIMES, token.kind);
		assertEquals(27, token.pos);
		assertEquals("*".length(), token.length);
		assertEquals("*", token.getText());
		
		token = scanner.nextToken();
		assertEquals(Kind.DIV, token.kind);
		assertEquals(28, token.pos);
		assertEquals("/".length(), token.length);
		assertEquals("/", token.getText());
		
	}
	
	@Test
	public void testTab() throws IllegalCharException, IllegalNumberException {
		Scanner scanner = new Scanner("abc \tdef");
		//thrown.expect(IllegalCharException.class);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(0, token.pos);
		assertEquals("abc".length(), token.length);
		assertEquals("abc", token.getText());
		
		token = scanner.nextToken();
		assertEquals(Kind.IDENT, token.kind);
		assertEquals(5, token.pos);
		assertEquals("def".length(), token.length);
		assertEquals("def", token.getText());
		
	}
	
	

}
