package cop5556sp17;

import static cop5556sp17.Scanner.Kind.ASSIGN;
import static cop5556sp17.Scanner.Kind.IDENT;
import static cop5556sp17.Scanner.Kind.LBRACE;
import static cop5556sp17.Scanner.Kind.LPAREN;
import static cop5556sp17.Scanner.Kind.RPAREN;
import static cop5556sp17.Scanner.Kind.SEMI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.WhileStatement;


public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	//Public Declaration of variables
	Scanner scanner;
	Token t;
	HashMap<Kind,String > operantKind = new HashMap<>();
	
	// routine for filling up HashMap
	
	public void setoperantKind(){
		//arrowOp ∷= ARROW   |   BARARROW
		operantKind.put(Kind.ARROW,"arrowOperant");
		operantKind.put(Kind.BARARROW,"arrowOperant");
		
		//relOp ∷=  LT | LE | GT | GE | EQUAL | NOTEQUAL
		operantKind.put(Kind.LT,"relationOperant");
		operantKind.put(Kind.LE,"relationOperant");
		operantKind.put(Kind.GT,"relationOperant");
		operantKind.put(Kind.GE,"relationOperant");
		operantKind.put(Kind.EQUAL,"relationOperant");
		operantKind.put(Kind.NOTEQUAL,"relationOperant");
		
		//weakOp  ∷= PLUS | MINUS | OR   
		operantKind.put(Kind.PLUS,"arithmeticOperantWeak");
		operantKind.put(Kind.MINUS,"arithmeticOperantWeak");
		operantKind.put(Kind.OR,"arithmeticOperantWeak");
		
		//strongOp ∷= TIMES | DIV | AND | MOD
		operantKind.put(Kind.TIMES,"arithmeticOperantStrong");
		operantKind.put(Kind.DIV,"arithmeticOperantStrong");
		operantKind.put(Kind.AND,"arithmeticOperantStrong");
		operantKind.put(Kind.MOD,"arithmeticOperantStrong");
		
		//dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT
		operantKind.put(Kind.KW_INTEGER,"declareOperant");
		operantKind.put(Kind.KW_BOOLEAN,"declareOperant");
		operantKind.put(Kind.KW_IMAGE,"declareOperant");
		operantKind.put(Kind.KW_FRAME,"declareOperant");
		
		//statement ::=   OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
		//factor ∷= IDENT | INT_LIT | KW_TRUE | KW_FALSE | KW_SCREENWIDTH | KW_SCREENHEIGHT 
		operantKind.put(Kind.OP_SLEEP,"statementOperant");
		operantKind.put(Kind.KW_WHILE,"statementOperant");
		operantKind.put(Kind.KW_IF,"statementOperant");
		operantKind.put(Kind.IDENT,"statementOperant");
		operantKind.put(Kind.OP_BLUR,"statementOperant");
		operantKind.put(Kind.OP_GRAY,"statementOperant");
		operantKind.put(Kind.OP_CONVOLVE,"statementOperant");
		operantKind.put(Kind.KW_SHOW,"statementOperant");
		operantKind.put(Kind.KW_HIDE,"statementOperant");
		operantKind.put(Kind.KW_MOVE,"statementOperant");
		operantKind.put(Kind.KW_XLOC,"statementOperant");
		operantKind.put(Kind.KW_YLOC,"statementOperant");
		operantKind.put(Kind.OP_WIDTH,"statementOperant");
		operantKind.put(Kind.OP_HEIGHT,"statementOperant");
		operantKind.put(Kind.KW_SCALE,"statementOperant");	
	}
	
	
	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
		setoperantKind();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	Program parse() throws SyntaxException {
		Program program = program();
		matchEOF();
		return program;
	}

	Expression expression() throws SyntaxException {
		//expression ∷= term ( relOp term)*
		//1. Call term
		//2. do call consume(relation Operand) and then term until token has relation Operand
		Expression expression0 = term();
		while(operantKind.containsKey(t.kind) && operantKind.get(t.kind) == "relationOperant"){
			Token previousToken = t;
			consume();
			Expression expression1 = term();
			expression0 = new BinaryExpression(expression0.getFirstToken(), expression0, previousToken, expression1);
		}
			return expression0;
	}
	
	Expression term() throws SyntaxException {
		//term ∷= elem ( weakOp  elem)*
		//1. call element()
		//2. repeat untill weak operant :: consume(weakOperant) and call elem()
		Expression expression0 = elem();
		while(operantKind.containsKey(t.kind) && operantKind.get(t.kind) == "arithmeticOperantWeak"){
			Token previousToken = t;
			consume();
			Expression expression1 = elem();
			expression0 = new BinaryExpression(expression0.getFirstToken(), expression0, previousToken, expression1);
		}
		return expression0;
	}

	Expression elem() throws SyntaxException {
		//elem ∷= factor ( strongOp factor)*
		Expression expression0 = factor();
		while(operantKind.containsKey(t.kind) && operantKind.get(t.kind) == "arithmeticOperantStrong"){
			Token previousToken = t;
			consume();
			Expression expression1 = factor();
			expression0 = new BinaryExpression(expression0.getFirstToken(), expression0, previousToken, expression1);
		}
		return expression0;
	}

	Expression factor() throws SyntaxException {
		Kind kind = t.kind;
		Expression expression;
		switch (kind) {
		case IDENT: {
			expression = new IdentExpression(t);
			consume();
		}
			break;
		case INT_LIT: {
			expression = new IntLitExpression(t);
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			expression = new BooleanLitExpression(t);
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			expression = new ConstantExpression(t);
			consume();
		}
			break;
		case LPAREN: {
			consume();
			expression = expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("Error :: Provided factor is invalid @ "+ kind + ":" + kind.getText());
		}
		return expression;
	}

	Block block() throws SyntaxException {
		ArrayList<Dec> declareList = new ArrayList<Dec>();
		ArrayList<Statement> statementList = new ArrayList<Statement>();
		Token previousToken = t;
		match(LBRACE);
		while(operantKind.containsKey(t.kind)){
			if (operantKind.get(t.kind) == "declareOperant" )
				declareList.add(dec());
			 else if(operantKind.get(t.kind) == "statementOperant")
				 statementList.add(statement());
			else 
				break;
		}
		match(Kind.RBRACE);
		return new Block(previousToken, declareList, statementList);

	}

	Program program() throws SyntaxException {
		Token previousToken = t;
		Block programBlock = null;
		match(IDENT);
		ArrayList<ParamDec> paramDeclareList = new ArrayList<ParamDec>();
		if (t.kind == LBRACE)
			programBlock = block();
		 else {
			 paramDeclareList.add(paramDec());
			//while (t.isKind(COMMA)){
			while (t.kind == Kind.COMMA){
				consume();
				paramDeclareList.add(paramDec());
			}
			programBlock = block();
		}
		matchEOF();
		return new Program(previousToken, paramDeclareList, programBlock);

	}

	ParamDec paramDec() throws SyntaxException {
		Kind kind = t.kind;
		if(kind == Kind.KW_URL || kind == Kind.KW_FILE || kind == Kind.KW_INTEGER 
				|| kind == Kind.KW_BOOLEAN){
			Token previousToken= t;
			consume();
			Token currentToken = t;
			match(IDENT);
			return new ParamDec(previousToken, currentToken);
		}
		else 
			throw new SyntaxException("Error :: Illegal Parameter Declaration @ " + kind + ":" + kind.getText());
	}

	Dec dec() throws SyntaxException {
		Kind kind = t.kind;
		if(kind == Kind.KW_INTEGER || kind == Kind.KW_BOOLEAN || kind == Kind.KW_IMAGE || kind == Kind.KW_FRAME){
			Token previousToken = t;
			consume();
			Token currentToken = t;
			match(IDENT);
			return new Dec(previousToken, currentToken);
		}
		else
			throw new SyntaxException("Error :: Illegal Declaration(dec) @ " + kind + ":" + kind.getText());

	}

	Statement statement() throws SyntaxException {
		Kind kind = t.kind;
		if(kind == Kind.KW_WHILE){
			Token previousToken = t;
			consume();
			match(LPAREN);
			Expression expression = expression();
			match(RPAREN);
			Block programBlock = block();
			return new WhileStatement(previousToken, expression, programBlock);
		}
		else if(kind == Kind.KW_IF){
			Token previousToken = t;
			consume();
			match(LPAREN);
			Expression expression = expression();
			match(RPAREN);
			Block programBlock = block();
			return new IfStatement(previousToken, expression, programBlock);
		}
		else if (kind == Kind.OP_SLEEP){
			Token previousToken = t;
			consume();
			Expression expression = expression();
			match(SEMI);
			return new SleepStatement(previousToken, expression);
		}
		else if (kind == Kind.IDENT){
			Token nextToken = scanner.peek();
			//if(nextToken.iskind(ASSIGN)){
			if(nextToken.kind == Kind.ASSIGN){
				IdentLValue identVal = new IdentLValue(t);
				Token previousToken = t;
				consume();
				match(ASSIGN);
				Expression expression = expression();
				match(SEMI);
				return new AssignmentStatement(previousToken, identVal, expression);
			}
			else {
				Chain chain = chain();
				match(SEMI);
				 return chain;
			}
		}
		else if ( kind == Kind.OP_BLUR || kind == Kind.OP_GRAY || kind == Kind.OP_CONVOLVE ||
				 kind == Kind.KW_SHOW || kind == Kind.KW_HIDE || kind == Kind.KW_MOVE ||
				 kind == Kind.KW_XLOC || kind == Kind.KW_XLOC || kind == Kind.KW_YLOC ||
				 kind == Kind.OP_WIDTH || kind == Kind.OP_HEIGHT || kind == Kind.KW_SCALE ){
			Chain chain = chain();
			match(SEMI);	
			return chain;
		}
		else {
			throw new SyntaxException("Error :: Illegal Statement with " + kind  + ":" + kind.getText() );
		}

	}

	Chain chain() throws SyntaxException {
		Token arrowToken,previousToken = t;
		Chain chain = chainElem();
		if (operantKind.containsKey(t.kind) && operantKind.get(t.kind) == "arrowOperant"){
			arrowToken = t;
			consume();
			ChainElem chainElement = chainElem();
			chain = new BinaryChain(previousToken, chain, arrowToken, chainElement);
			while (operantKind.containsKey(t.kind) && operantKind.get(t.kind) == "arrowOperant"){
				arrowToken = t;
				consume();
				chainElement = chainElem();
				chain = new BinaryChain(previousToken, chain, arrowToken, chainElement);
			}
		} else 
			throw new SyntaxException("Error :: Illegal chain caused by :: " + t.getText() + " of kind :: " + t.kind);
		return chain;
	}

	ChainElem chainElem() throws SyntaxException {
		Kind kind = t.kind;
		Token previousToken = t;
		ChainElem chainElement = null;
		if(kind == Kind.IDENT){
			chainElement = new IdentChain(t);
			consume();
		}
		else if(kind == Kind.OP_BLUR || kind == Kind.OP_GRAY ||kind == Kind.OP_CONVOLVE){
			previousToken = t;
			consume();
			Tuple argument = arg();
			chainElement = new FilterOpChain(previousToken, argument);
		}
		else if(kind == Kind.KW_SHOW || kind == Kind.KW_HIDE ||kind == Kind.KW_MOVE
				|| kind == Kind.KW_XLOC || kind == Kind.KW_YLOC){
			previousToken = t;
			consume();
			Tuple argument = arg();
			chainElement = new FrameOpChain(previousToken, argument);
		}
		else if(kind == Kind.OP_WIDTH || kind == Kind.OP_HEIGHT || kind == Kind.KW_SCALE){
			previousToken = t;
			consume();
			Tuple argument = arg();
			chainElement = new ImageOpChain(previousToken, argument);
		}
		else
			throw new SyntaxException("Error :: Illegal Statement in chainElem. Please Check. Error found @ " + kind.getText());
		
		return chainElement;
	}

	Tuple arg() throws SyntaxException {
		ArrayList<Expression> expressionList = new ArrayList<Expression>();
		Token previousToken = t;
		if (t.kind == Kind.LPAREN) {
			consume();
			expressionList.add(expression());
			//while (t.isKind(COMMA)){
			while (t.kind == Kind.COMMA){
				consume();
				expressionList.add(expression());
			}
			match(RPAREN);
		}
		return new Tuple(previousToken, expressionList);
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		//if (t.isKind(EOF)) {
		if (t.kind == Kind.EOF) {
				
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		//if (t.isKind(kind)) {

		
		if (t.kind == kind) {
			return consume();
		}
		throw new SyntaxException("Error :: input contains " + t.kind + " but expected is :: " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		for (Kind locKind : kinds){
			if (t.kind == locKind){
				Token matchToken = t;
				consume();
				return matchToken;
			}
		}
		throw new SyntaxException("Error :: Provided token of kind :: " + t.kind + " does not match any valid token type");
		
		//return null; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
