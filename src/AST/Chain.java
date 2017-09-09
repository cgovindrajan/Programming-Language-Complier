package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;


public abstract class Chain extends Statement {
	public TypeName typeName = null;
	public Chain(Token firstToken) {
		super(firstToken);
	}

}
