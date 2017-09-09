package cop5556sp17;

import java.lang.reflect.Array;
import java.util.ArrayList;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
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
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction
	FieldVisitor fv;

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	
	//command line arguments index, Slot number counter and local variable array
	int paramDecArgs = 0;
	int slotNum = 1;
	ArrayList<VariableAttrs> localVariables = new ArrayList<>();

	static class VariableAttrs{
		Label start;
		Label end;
		int slotNum;
		Dec dec;
	}
	
	static class BinaryChainLocal {
		Kind kind;
		int direction;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
		//add the visitlocalvariable method for all variable in localvariable array
		for (VariableAttrs varAttrs: localVariables){
			mv.visitLocalVariable(varAttrs.dec.getIdent().getText(), varAttrs.dec.typeName.getJVMTypeDesc(), null, varAttrs.start, varAttrs.end, varAttrs.slotNum);
		}
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		BinaryChainLocal binarychainlocal = new BinaryChainLocal();
		binarychainlocal.kind = binaryChain.getArrow().kind;
		Chain chain = binaryChain.getE0();
		binarychainlocal.direction = 1;
		
		chain.visit(this, binarychainlocal);
		if (chain.typeName == TypeName.FILE)
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
		else if (chain.typeName == TypeName.URL) 
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false); 
		
		ChainElem chainElem = binaryChain.getE1();
		binarychainlocal.direction = 2;
		
		chainElem.visit(this, binarychainlocal);	
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		if (binaryExpression.typeName == TypeName.INTEGER){
			binaryExpression.getE0().visit(this, arg);
			binaryExpression.getE1().visit(this, arg);
			Kind kind = binaryExpression.getOp().kind;
			if(kind == Kind.PLUS)
				mv.visitInsn(IADD);
			else if(kind == Kind.MINUS)
				mv.visitInsn(ISUB);
			else if(kind == Kind.TIMES)
				mv.visitInsn(IMUL);
			else if(kind == Kind.DIV)
				mv.visitInsn(IDIV);
			else if(kind == Kind.MOD)
				mv.visitInsn(IREM);
			} 
		else if (binaryExpression.typeName == TypeName.BOOLEAN){
			Label start = new Label();
			Label end = new Label();
			Kind kind = binaryExpression.getOp().kind;
			if(kind == Kind.AND){
				binaryExpression.getE0().visit(this, arg);
				mv.visitJumpInsn(IFEQ, start);
				binaryExpression.getE1().visit(this, arg);
				mv.visitJumpInsn(IFEQ, start);
				mv.visitInsn(ICONST_1);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(end);
			}
			else if(kind == Kind.OR){
				binaryExpression.getE0().visit(this, arg);
				mv.visitJumpInsn(IFNE, start);
				binaryExpression.getE1().visit(this, arg);
				mv.visitJumpInsn(IFNE, start);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(end);
			}
			else if(kind == Kind.EQUAL || kind == Kind.NOTEQUAL || kind == Kind.GT || kind == Kind.GE 
					|| kind == Kind.LT || kind == Kind.LE){
					binaryExpression.getE0().visit(this, arg);
					binaryExpression.getE1().visit(this, arg);
					int temp = 0;
					if(binaryExpression.getOp().kind == Kind.EQUAL)
						temp = IF_ICMPEQ;
					else if(binaryExpression.getOp().kind == Kind.NOTEQUAL)
						temp = IF_ICMPNE;
					else if(binaryExpression.getOp().kind == Kind.GT)
						temp = IF_ICMPGT;
					else if(binaryExpression.getOp().kind == Kind.GE)
						temp = IF_ICMPGE;
					else if(binaryExpression.getOp().kind == Kind.LE)
						temp = IF_ICMPLE;
					else if(binaryExpression.getOp().kind == Kind.LT)
						temp = IF_ICMPLT;

					mv.visitJumpInsn(temp, start);
					mv.visitInsn(ICONST_0);
					mv.visitJumpInsn(GOTO, end);
					mv.visitLabel(start);
					mv.visitInsn(ICONST_1);
					mv.visitLabel(end);
			}
		} else if (binaryExpression.typeName == TypeName.IMAGE){
			binaryExpression.getE0().visit(this, arg);
			binaryExpression.getE1().visit(this, arg);
			String operatorName = null;
			String operatorDesc = null;
			if(binaryExpression.getOp().kind == Kind.PLUS){
				operatorName = "add";
				operatorDesc = PLPRuntimeImageOps.addSig;
			}
			else if(binaryExpression.getOp().kind == Kind.MINUS){
				operatorName = "sub";
				operatorDesc = PLPRuntimeImageOps.subSig;
			}
			else if(binaryExpression.getOp().kind == Kind.TIMES){
				operatorName = "mul";
				operatorDesc = PLPRuntimeImageOps.mulSig;
			}
			else if(binaryExpression.getOp().kind == Kind.DIV){
				operatorName = "div";
				operatorDesc = PLPRuntimeImageOps.divSig;
			}
			else if(binaryExpression.getOp().kind == Kind.MOD){
				operatorName = "mod";
				operatorDesc = PLPRuntimeImageOps.modSig;
			}
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, operatorName, operatorDesc, false);
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		
		Label start = new Label();
		Label end = new Label();
		mv.visitLabel(start);
		
		for (Dec dec : block.getDecs()) {
			VariableAttrs var = new VariableAttrs();
			var.dec = dec;
			var.start = start;
			var.end = end;
			var.slotNum = slotNum;
			dec.visit(this, arg);
			localVariables.add(var);
		}
		
		for (Statement statement : block.getStatements()) {
			statement.visit(this, arg);
			if (statement instanceof Chain)
				mv.visitInsn(POP);
		}
		mv.visitLabel(end);
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(booleanLitExpression.getValue());
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		Kind kind = constantExpression.getFirstToken().kind;
		if (kind == Kind.KW_SCREENWIDTH)
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
		 else if (kind == Kind.KW_SCREENHEIGHT) 
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
		
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		
		declaration.slotNum = slotNum;
		slotNum = slotNum + 1; 
		if(declaration.typeName == TypeName.FRAME){
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, declaration.slotNum);	
		}
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		Tuple tuple = filterOpChain.getArg();
		tuple.visit(this, arg);
		String operator = null;
		Kind kind = filterOpChain.firstToken.kind ;
		if(kind == Kind.OP_BLUR)
			operator = "blurOp"; 
		else if(kind == Kind.OP_CONVOLVE)
			operator = "convolveOp";
		else if(kind == Kind.OP_GRAY)
			operator = "grayOp";
		
		BinaryChainLocal bChainInfo = (BinaryChainLocal) arg;
		if (bChainInfo.kind == Kind.ARROW) {
			mv.visitInsn(ACONST_NULL);
		} else {
			mv.visitInsn(DUP);
			mv.visitInsn(SWAP);
		}
		mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, operator, PLPRuntimeFilterOps.opSig, false);
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		Tuple tuple = frameOpChain.getArg();
		tuple.visit(this, arg);
		String operatorName = null;
		String operatorDesc = null;
		Kind kind = frameOpChain.getFirstToken().kind;
		if(kind == Kind.KW_SHOW){
			operatorName = "showImage";
			operatorDesc = PLPRuntimeFrame.showImageDesc;
		}
		else if(kind == Kind.KW_HIDE){
			operatorName = "hideImage";
			operatorDesc = PLPRuntimeFrame.hideImageDesc;
		}
		else if(kind == Kind.KW_XLOC){
			operatorName = "getXVal";
			operatorDesc = PLPRuntimeFrame.getXValDesc;
		}	
		else if(kind == Kind.KW_YLOC){
			operatorName = "getYVal";
			operatorDesc = PLPRuntimeFrame.getYValDesc;
		}	
		else if(kind == Kind.KW_MOVE){
			operatorName = "moveFrame";
			operatorDesc = PLPRuntimeFrame.moveFrameDesc;
		}			
		mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, operatorName, operatorDesc, false);

		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		BinaryChainLocal bChainInfo = (BinaryChainLocal) arg;
		Dec dec = identChain.dec;
		
		if (bChainInfo.direction == 1){
			TypeName decType = dec.typeName;
			if(decType == TypeName.INTEGER || decType == TypeName.BOOLEAN){
				if (dec instanceof ParamDec) {
					mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(), identChain.dec.typeName.getJVMTypeDesc());
				} else {
					mv.visitVarInsn(ILOAD, dec.slotNum);
				}
			}
			else if(decType == TypeName.IMAGE || decType == TypeName.FRAME){
				mv.visitVarInsn(ALOAD, dec.slotNum);
			}
			else if(decType == TypeName.FILE || decType == TypeName.URL)
				mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(), dec.typeName.getJVMTypeDesc());
		} 
		else {
			TypeName decType = dec.typeName;
			if(decType == TypeName.INTEGER ){
				mv.visitInsn(DUP);
				if(dec instanceof ParamDec)
					mv.visitFieldInsn(PUTSTATIC, className, identChain.getFirstToken().getText(), dec.typeName.getJVMTypeDesc());
				else 
					mv.visitVarInsn(ISTORE, dec.slotNum);
			}
			else if (decType == TypeName.IMAGE ){
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, dec.slotNum);
			}
			else if (decType == TypeName.FILE ){
				mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(), dec.typeName.getJVMTypeDesc());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
			    //mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(), dec.typeName.getJVMTypeDesc());
			}
			else if (decType == TypeName.FRAME ){
				//mv.visitInsn(ACONST_NULL);
				mv.visitVarInsn(ALOAD, dec.slotNum);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, dec.slotNum);
			}
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		if (identExpression.dec instanceof ParamDec) 
			mv.visitFieldInsn(GETSTATIC, className, identExpression.getFirstToken().getText(), identExpression.dec.typeName.getJVMTypeDesc());
	    else if (identExpression.typeName == TypeName.INTEGER || identExpression.typeName == TypeName.BOOLEAN) 
			mv.visitVarInsn(ILOAD, identExpression.dec.slotNum);
	    else 
			mv.visitVarInsn(ALOAD, identExpression.dec.slotNum);
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		if (identX.dec instanceof ParamDec)
			mv.visitFieldInsn(PUTSTATIC, className, identX.getFirstToken().getText(), identX.dec.typeName.getJVMTypeDesc());
		 else if (identX.dec.typeName == TypeName.INTEGER || identX.dec.typeName == TypeName.BOOLEAN)
			mv.visitVarInsn(ISTORE, identX.dec.slotNum);
		 else if (identX.dec.typeName == TypeName.IMAGE){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
			mv.visitVarInsn(ASTORE, identX.dec.slotNum);
		} else 
			mv.visitVarInsn(ASTORE, identX.dec.slotNum);
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		Label label = new Label();
		ifStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFEQ, label);
		ifStatement.getB().visit(this, arg);
		mv.visitLabel(label);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		Tuple tuple = imageOpChain.getArg();
		tuple.visit(this, arg);
		Kind kind = imageOpChain.getFirstToken().kind;
		if(kind == Kind.KW_SCALE)
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
		else if(kind == Kind.OP_WIDTH)
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getWidth", PLPRuntimeImageOps.getWidthSig, false);
		else if(kind == Kind.OP_HEIGHT)
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getHeight", PLPRuntimeImageOps.getHeightSig, false);
	
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(intLitExpression.value);
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//create a field
		fv = cw.visitField(ACC_STATIC, paramDec.getIdent().getText(), paramDec.typeName.getJVMTypeDesc(), null, null);
		fv.visitEnd();
		if(paramDec.typeName == TypeName.INTEGER){
			mv.visitVarInsn(ALOAD, 1);
			mv.visitIntInsn(BIPUSH, paramDecArgs++);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			CodeGenUtils.genPrint(DEVEL, mv, "\n paramdec: "+paramDec.getIdent().getText()+"=");
			CodeGenUtils.genPrintTOS(DEVEL, mv, TypeName.INTEGER);
		}
		else if(paramDec.typeName == TypeName.BOOLEAN){
			mv.visitVarInsn(ALOAD, 1);
			mv.visitIntInsn(BIPUSH, paramDecArgs++);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			CodeGenUtils.genPrint(DEVEL, mv, "\n paramdec: "+paramDec.getIdent().getText()+"=");
			CodeGenUtils.genPrintTOS(DEVEL, mv, TypeName.BOOLEAN);
		}
		else if (paramDec.typeName == TypeName.URL){
			mv.visitVarInsn(ALOAD, 1);
			mv.visitIntInsn(BIPUSH, paramDecArgs++);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
		}
		else if (paramDec.typeName == TypeName.FILE){
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitIntInsn(BIPUSH, paramDecArgs++);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
		}
		mv.visitFieldInsn(PUTSTATIC, className, paramDec.getIdent().getText(), paramDec.typeName.getJVMTypeDesc());
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		sleepStatement.getE().visit(this, arg);
		if (sleepStatement.getE().typeName == TypeName.INTEGER) {
			mv.visitInsn(I2L);
		}
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		for (Expression exp: tuple.getExprList()){
			exp.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		Label start = new Label();
		Label end = new Label();
		mv.visitLabel(start);
		whileStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFEQ, end);
		whileStatement.getB().visit(this, arg);
		mv.visitJumpInsn(GOTO, start);
		mv.visitLabel(end);
		return null;
	}

}

