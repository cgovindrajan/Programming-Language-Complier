package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
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
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		binaryChain.getE0().visit(this, arg);
		TypeName chainType = binaryChain.getE0().typeName;
		
		binaryChain.getE1().visit(this, arg);
		ChainElem chainElement = binaryChain.getE1();
		TypeName chainElementType = binaryChain.getE1().typeName;
		
		Token operator = binaryChain.getArrow();

		
		if(chainType == URL){ 
			if(operator.kind == ARROW && chainElementType == IMAGE)
				binaryChain.typeName = IMAGE;		
		}
		else if(chainType == FILE){ 
			if(operator.kind == ARROW && chainElementType == IMAGE)
				binaryChain.typeName = IMAGE;		
		}
		else if(chainType == FRAME){ 
			if(operator.kind == ARROW && chainElement instanceof FrameOpChain){
				Kind kind = chainElement.firstToken.kind;
				if(kind == KW_XLOC || kind == KW_YLOC)
					binaryChain.typeName = INTEGER;
				else if (kind == KW_SHOW || kind == KW_HIDE || kind == KW_MOVE)
					binaryChain.typeName = FRAME;
			}
		}
		else if(chainType == IMAGE){ 
			if((operator.kind == BARARROW || operator.kind == ARROW) && chainElement instanceof FilterOpChain){
				Kind kind = chainElement.firstToken.kind;
				
				if(kind == OP_GRAY || kind == OP_BLUR || kind == OP_CONVOLVE)
					binaryChain.typeName = IMAGE;	
			}
			else if(operator.kind == ARROW ){
				Kind kind = chainElement.firstToken.kind;
				
				 if (chainElementType == FRAME)
						binaryChain.typeName = FRAME;
				else if(chainElementType == FILE)
				binaryChain.typeName = NONE;

				else if(chainElement instanceof ImageOpChain){
					
					if ( kind == OP_WIDTH || kind == OP_HEIGHT)
						binaryChain.typeName = INTEGER;
					else if(kind == KW_SCALE)
						binaryChain.typeName = IMAGE;
					
				}
				else if ((chainElement instanceof IdentChain) && chainElement.typeName == IMAGE) 
					binaryChain.typeName = IMAGE;
			}
		}	
		else if(chainType == INTEGER && operator.kind == ARROW && chainElement instanceof IdentChain && chainElement.typeName == INTEGER)
			binaryChain.typeName = INTEGER;

		if (binaryChain.typeName != null) 
			return binaryChain;
		else
			throw new TypeCheckException("Error :: Can not parse though visitor of binary chain : " + binaryChain.toString());

	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		Token operator = binaryExpression.getOp();
		TypeName type0 = null,type1 = null;
		
		binaryExpression.getE0().visit(this, arg);
		type0 = binaryExpression.getE0().typeName;
		
		binaryExpression.getE1().visit(this, arg);
		type1 = binaryExpression.getE1().typeName;
		

		if(operator.kind == PLUS || operator.kind == MINUS){
			if(type0 == INTEGER && type1 == INTEGER)
				binaryExpression.typeName = INTEGER;
			else if (type0 == IMAGE && type1 == IMAGE)
			 binaryExpression.typeName = IMAGE;
		}
//		else if(operator.kind == DIV || operator.kind == MOD){
//			if(type0 == INTEGER && type1 == INTEGER)
//				binaryExpression.typeName = INTEGER;
//		}
		else if(operator.kind == TIMES || operator.kind == MOD || operator.kind == DIV){
			if(type0 == INTEGER && type1 == INTEGER)
				binaryExpression.typeName = INTEGER;
			else if(type0 == INTEGER && type1 == IMAGE)
				binaryExpression.typeName = IMAGE;
			else if(type0 == IMAGE && type1 == INTEGER)
				binaryExpression.typeName = IMAGE;
		}
		else if(operator.kind == LT || operator.kind == GT || operator.kind == LE || operator.kind == GE ){
			if(type0 == INTEGER && type1 == INTEGER)
				binaryExpression.typeName = BOOLEAN;
			else if(type0 == BOOLEAN && type1 == BOOLEAN)
				binaryExpression.typeName = BOOLEAN;
		}
		else if(operator.kind == EQUAL || operator.kind == NOTEQUAL || operator.kind == OR || operator.kind == AND){
			if(type0 == type1)
				binaryExpression.typeName = BOOLEAN;
		}
		
		if (binaryExpression.typeName != null) {
			return binaryExpression;
		}
		else 
			throw new TypeCheckException("Error :: Error found at visitor of binary expression :" + binaryExpression.getE0().toString());
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {

		symtab.enterScope();
		
		for (Dec declaration: block.getDecs()) 
			declaration.visit(this, arg);
		
		for (Statement statement: block.getStatements())
			statement.visit(this, arg);
		
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		booleanLitExpression.typeName = BOOLEAN;
		return booleanLitExpression;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple tuple = filterOpChain.getArg();
		tuple.visit(this, arg);
		int tupleSize = tuple.getExprList().size();
		
		if (tupleSize == 0){
			filterOpChain.typeName = IMAGE;
			return filterOpChain;
		}
		else
			throw new TypeCheckException("Error :: Tuple Size > 0 In Filter Op Chain :" + filterOpChain.toString());
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		Boolean valid = false;
		frameOpChain.kind = frameOpChain.firstToken.kind;
		Kind kind = frameOpChain.firstToken.kind;
		
		Tuple tuple = frameOpChain.getArg();
		tuple.visit(this, arg);
		int tupleSize = tuple.getExprList().size();
		
		
		if((kind == KW_SHOW || kind == KW_HIDE) && tupleSize == 0) {
			frameOpChain.typeName = NONE;
			valid = true;
			}
		else if((kind == KW_XLOC || kind == KW_YLOC) && tupleSize == 0) {
				frameOpChain.typeName = INTEGER;
				valid = true;
			}

		else if((kind == KW_MOVE) && tupleSize == 2) {
				frameOpChain.typeName = NONE;
				valid = true;
		}
		if(valid)
			return frameOpChain;
		else
			throw new TypeCheckException("Error :: No Matching type in Filter op");
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		
		if(symtab.lookup(identChain.getFirstToken().getText())!=null){
			Dec dec = symtab.lookup(identChain.getFirstToken().getText());
			identChain.typeName = dec.typeName;
			identChain.dec = dec;
			
		}
		else {
			throw new TypeCheckException("Error :: Declaration not found in Dec");
		}
		return identChain;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		if (symtab.lookup(identExpression.firstToken.getText()) != null) {
			
			identExpression.typeName = symtab.lookup(identExpression.firstToken.getText()).typeName;
			identExpression.dec = symtab.lookup(identExpression.firstToken.getText());
			return identExpression;
		}
		else
			throw new TypeCheckException("Error : Ident not declared in current Scope :: " + identExpression.toString() );
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		ifStatement.getE().visit(this, arg);
		if (ifStatement.getE().typeName == BOOLEAN){
			ifStatement.getB().visit(this, arg);
			return ifStatement;
		}
		else 
		throw new TypeCheckException("Error :: Expression Type is not of Boolean Type : " + ifStatement.toString() );
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		intLitExpression.typeName = INTEGER;
		return intLitExpression;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		sleepStatement.getE().visit(this, arg);
		if ((sleepStatement.getE()).typeName == INTEGER) 
			return sleepStatement;
		else 
		throw new TypeCheckException("Error :: Expression in Sleep is not of Type : Integer @ :" + sleepStatement.firstToken.toString());
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		whileStatement.getE().visit(this, arg);
		if (whileStatement.getE().typeName == BOOLEAN){
			whileStatement.getB().visit(this, arg);
			return whileStatement;
		}
			
		else
			throw new TypeCheckException("Error :: Expression Type is not of Boolean Type : " + whileStatement.toString());
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		declaration.typeName = Type.getTypeName(declaration.firstToken);
		boolean flag = symtab.insert(declaration.getIdent().getText(), declaration);
		if(!flag)
			throw new TypeCheckException("Error :: Variable  : " + declaration.getIdent().getText() + " already declared in current scope");
		return declaration;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		symtab.enterScope();
		
		for (ParamDec paramDec: program.getParams()) 
			paramDec.visit(this, arg);
		
		program.getB().visit(this, arg);
		return program;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getVar().visit(this, arg);
		assignStatement.getE().visit(this, arg);
		if (assignStatement.getVar().dec.typeName == assignStatement.getE().typeName) 
			return assignStatement;
		else 
			throw new TypeCheckException("Error :: Mismatch at Left side and Right side of assignment : " + assignStatement.toString()) ;

	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		if (symtab.lookup(identX.firstToken.getText()) != null) {
			identX.dec = symtab.lookup(identX.firstToken.getText());
			return identX;
		}
		else 
			throw new TypeCheckException(" Error :: Ident not declared in current Scope : " + identX );
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		paramDec.typeName = Type.getTypeName(paramDec.getType());
		boolean flag = symtab.insert(paramDec.getIdent().getText(), paramDec);
		if(!flag)
			throw new TypeCheckException("Error :: Variable  : " + paramDec.getIdent().getText() + " already declared in current scope");
		
		return paramDec;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		constantExpression.typeName = INTEGER;
		return constantExpression;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		imageOpChain.kind = imageOpChain.firstToken.kind;
		Kind kind = imageOpChain.firstToken.kind;
		Tuple tuple = imageOpChain.getArg();
		tuple.visit(this, arg);
		int tupleSize = tuple.getExprList().size();
		if((kind == OP_WIDTH || kind == OP_HEIGHT ) && tupleSize == 0)
			imageOpChain.typeName = INTEGER;
		
		else if((kind == KW_SCALE) && tupleSize == 1)
			imageOpChain.typeName = IMAGE;

		else 
			 throw new TypeCheckException("Error : Mismatch found in Image Chain @ " + imageOpChain.toString());
		
		return imageOpChain;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		for (Expression expression : tuple.getExprList()){
			expression.visit(this, arg);
			if (expression.typeName != INTEGER)
				throw new TypeCheckException("Error :: One or more expression in the token are not of type : Integer @ : " + expression.firstToken.toString());
		}	
		return tuple;
	}


}

