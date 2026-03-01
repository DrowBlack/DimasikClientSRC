package org.openjdk.nashorn.internal.codegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;
import org.openjdk.nashorn.internal.codegen.Compiler;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.codegen.FoldConstants;
import org.openjdk.nashorn.internal.ir.AccessNode;
import org.openjdk.nashorn.internal.ir.BaseNode;
import org.openjdk.nashorn.internal.ir.BinaryNode;
import org.openjdk.nashorn.internal.ir.Block;
import org.openjdk.nashorn.internal.ir.BlockLexicalContext;
import org.openjdk.nashorn.internal.ir.BlockStatement;
import org.openjdk.nashorn.internal.ir.BreakNode;
import org.openjdk.nashorn.internal.ir.CallNode;
import org.openjdk.nashorn.internal.ir.CaseNode;
import org.openjdk.nashorn.internal.ir.CatchNode;
import org.openjdk.nashorn.internal.ir.ClassNode;
import org.openjdk.nashorn.internal.ir.ContinueNode;
import org.openjdk.nashorn.internal.ir.DebuggerNode;
import org.openjdk.nashorn.internal.ir.EmptyNode;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.ExpressionStatement;
import org.openjdk.nashorn.internal.ir.ForNode;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.IdentNode;
import org.openjdk.nashorn.internal.ir.IfNode;
import org.openjdk.nashorn.internal.ir.IndexNode;
import org.openjdk.nashorn.internal.ir.JoinPredecessorExpression;
import org.openjdk.nashorn.internal.ir.JumpStatement;
import org.openjdk.nashorn.internal.ir.JumpToInlinedFinally;
import org.openjdk.nashorn.internal.ir.LabelNode;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LiteralNode;
import org.openjdk.nashorn.internal.ir.LoopNode;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.ObjectNode;
import org.openjdk.nashorn.internal.ir.ReturnNode;
import org.openjdk.nashorn.internal.ir.RuntimeNode;
import org.openjdk.nashorn.internal.ir.Statement;
import org.openjdk.nashorn.internal.ir.SwitchNode;
import org.openjdk.nashorn.internal.ir.Symbol;
import org.openjdk.nashorn.internal.ir.ThrowNode;
import org.openjdk.nashorn.internal.ir.TryNode;
import org.openjdk.nashorn.internal.ir.UnaryNode;
import org.openjdk.nashorn.internal.ir.VarNode;
import org.openjdk.nashorn.internal.ir.WhileNode;
import org.openjdk.nashorn.internal.ir.WithNode;
import org.openjdk.nashorn.internal.ir.visitor.NodeOperatorVisitor;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;
import org.openjdk.nashorn.internal.ir.visitor.SimpleNodeVisitor;
import org.openjdk.nashorn.internal.parser.Token;
import org.openjdk.nashorn.internal.parser.TokenType;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.ErrorManager;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.Source;
import org.openjdk.nashorn.internal.runtime.logging.DebugLogger;
import org.openjdk.nashorn.internal.runtime.logging.Loggable;
import org.openjdk.nashorn.internal.runtime.logging.Logger;

@Logger(name="lower")
final class Lower
extends NodeOperatorVisitor<BlockLexicalContext>
implements Loggable {
    private final DebugLogger log;
    private final boolean es6;
    private final Source source;
    private static final Pattern SAFE_PROPERTY_NAME = Pattern.compile("[a-zA-Z_$][\\w$]*");

    Lower(Compiler compiler) {
        super(new BlockLexicalContext(){

            @Override
            public List<Statement> popStatements() {
                ArrayList<Statement> newStatements = new ArrayList<Statement>();
                boolean terminated = false;
                List<Statement> statements = super.popStatements();
                for (Statement statement : statements) {
                    if (!terminated) {
                        newStatements.add(statement);
                        if (!statement.isTerminal() && !(statement instanceof JumpStatement)) continue;
                        terminated = true;
                        continue;
                    }
                    FoldConstants.extractVarNodesFromDeadCode(statement, newStatements);
                }
                return newStatements;
            }

            @Override
            protected Block afterSetStatements(Block block) {
                List<Statement> stmts = block.getStatements();
                ListIterator<Statement> li = stmts.listIterator(stmts.size());
                while (li.hasPrevious()) {
                    Statement stmt = li.previous();
                    if (stmt instanceof VarNode && ((VarNode)stmt).getInit() == null) continue;
                    return block.setIsTerminal(this, stmt.isTerminal());
                }
                return block.setIsTerminal(this, false);
            }
        });
        this.log = this.initLogger(compiler.getContext());
        this.es6 = compiler.getScriptEnvironment()._es6;
        this.source = compiler.getSource();
    }

    @Override
    public DebugLogger getLogger() {
        return this.log;
    }

    @Override
    public DebugLogger initLogger(Context context) {
        return context.getLogger(this.getClass());
    }

    @Override
    public boolean enterBreakNode(BreakNode breakNode) {
        this.addStatement(breakNode);
        return false;
    }

    @Override
    public Node leaveCallNode(CallNode callNode) {
        return this.checkEval(callNode.setFunction(Lower.markerFunction(callNode.getFunction())));
    }

    @Override
    public boolean enterCatchNode(CatchNode catchNode) {
        Expression exception = catchNode.getException();
        if (exception != null && !(exception instanceof IdentNode)) {
            this.throwNotImplementedYet("es6.destructuring", exception);
        }
        return true;
    }

    @Override
    public Node leaveCatchNode(CatchNode catchNode) {
        return this.addStatement(catchNode);
    }

    @Override
    public boolean enterContinueNode(ContinueNode continueNode) {
        this.addStatement(continueNode);
        return false;
    }

    @Override
    public boolean enterDebuggerNode(DebuggerNode debuggerNode) {
        int line = debuggerNode.getLineNumber();
        long token = debuggerNode.getToken();
        int finish = debuggerNode.getFinish();
        this.addStatement(new ExpressionStatement(line, token, finish, new RuntimeNode(token, finish, RuntimeNode.Request.DEBUGGER, new ArrayList<Expression>())));
        return false;
    }

    @Override
    public boolean enterJumpToInlinedFinally(JumpToInlinedFinally jumpToInlinedFinally) {
        this.addStatement(jumpToInlinedFinally);
        return false;
    }

    @Override
    public boolean enterEmptyNode(EmptyNode emptyNode) {
        return false;
    }

    @Override
    public Node leaveIndexNode(IndexNode indexNode) {
        String name = Lower.getConstantPropertyName(indexNode.getIndex());
        if (name != null) {
            assert (indexNode.isIndex());
            return new AccessNode(indexNode.getToken(), indexNode.getFinish(), indexNode.getBase(), name);
        }
        return super.leaveIndexNode(indexNode);
    }

    @Override
    public Node leaveDELETE(UnaryNode delete) {
        Expression expression = delete.getExpression();
        if (expression instanceof IdentNode || expression instanceof BaseNode) {
            return delete;
        }
        return new BinaryNode(Token.recast(delete.getToken(), TokenType.COMMARIGHT), expression, LiteralNode.newInstance(delete.getToken(), delete.getFinish(), true));
    }

    private static String getConstantPropertyName(Expression expression) {
        Object value;
        if (expression instanceof LiteralNode.PrimitiveLiteralNode && (value = ((LiteralNode)expression).getValue()) instanceof String && SAFE_PROPERTY_NAME.matcher((String)value).matches()) {
            return (String)value;
        }
        return null;
    }

    @Override
    public Node leaveExpressionStatement(ExpressionStatement expressionStatement) {
        Expression expr = expressionStatement.getExpression();
        ExpressionStatement node = expressionStatement;
        FunctionNode currentFunction = ((BlockLexicalContext)this.lc).getCurrentFunction();
        if (currentFunction.isProgram() && !Lower.isInternalExpression(expr) && !Lower.isEvalResultAssignment(expr)) {
            node = expressionStatement.setExpression(new BinaryNode(Token.recast(expressionStatement.getToken(), TokenType.ASSIGN), this.compilerConstant(CompilerConstants.RETURN), expr));
        }
        if (this.es6 && expressionStatement.destructuringDeclarationType() != null) {
            this.throwNotImplementedYet("es6.destructuring", expressionStatement);
        }
        return this.addStatement(node);
    }

    @Override
    public Node leaveBlockStatement(BlockStatement blockStatement) {
        return this.addStatement(blockStatement);
    }

    @Override
    public boolean enterForNode(ForNode forNode) {
        if (this.es6 && (forNode.getInit() instanceof ObjectNode || forNode.getInit() instanceof LiteralNode.ArrayLiteralNode)) {
            this.throwNotImplementedYet("es6.destructuring", forNode);
        }
        return super.enterForNode(forNode);
    }

    @Override
    public Node leaveForNode(ForNode forNode) {
        ForNode newForNode = forNode;
        JoinPredecessorExpression test = forNode.getTest();
        if (!forNode.isForInOrOf() && Expression.isAlwaysTrue(test)) {
            newForNode = forNode.setTest(this.lc, null);
        }
        newForNode = this.checkEscape(newForNode);
        if (!this.es6 && newForNode.isForInOrOf()) {
            this.addStatementEnclosedInBlock(newForNode);
        } else {
            this.addStatement(newForNode);
        }
        return newForNode;
    }

    @Override
    public boolean enterFunctionNode(FunctionNode functionNode) {
        if (this.es6) {
            IdentNode lastParam;
            int numParams;
            if (functionNode.getKind() == FunctionNode.Kind.MODULE) {
                this.throwNotImplementedYet("es6.module", functionNode);
            }
            if (functionNode.getKind() == FunctionNode.Kind.GENERATOR) {
                this.throwNotImplementedYet("es6.generator", functionNode);
            }
            if (functionNode.usesSuper()) {
                this.throwNotImplementedYet("es6.super", functionNode);
            }
            if ((numParams = functionNode.getNumOfParams()) > 0 && (lastParam = functionNode.getParameter(numParams - 1)).isRestParameter()) {
                this.throwNotImplementedYet("es6.rest.param", lastParam);
            }
            for (IdentNode param : functionNode.getParameters()) {
                if (!param.isDestructuredParameter()) continue;
                this.throwNotImplementedYet("es6.destructuring", functionNode);
            }
        }
        return super.enterFunctionNode(functionNode);
    }

    @Override
    public Node leaveFunctionNode(FunctionNode functionNode) {
        this.log.info("END FunctionNode: ", functionNode.getName());
        return functionNode;
    }

    @Override
    public Node leaveIfNode(IfNode ifNode) {
        return this.addStatement(ifNode);
    }

    @Override
    public Node leaveIN(BinaryNode binaryNode) {
        return new RuntimeNode(binaryNode);
    }

    @Override
    public Node leaveINSTANCEOF(BinaryNode binaryNode) {
        return new RuntimeNode(binaryNode);
    }

    @Override
    public Node leaveLabelNode(LabelNode labelNode) {
        return this.addStatement(labelNode);
    }

    @Override
    public Node leaveReturnNode(ReturnNode returnNode) {
        this.addStatement(returnNode);
        return returnNode;
    }

    @Override
    public Node leaveCaseNode(CaseNode caseNode) {
        LiteralNode lit;
        Expression test = caseNode.getTest();
        if (test instanceof LiteralNode && (lit = (LiteralNode)test).isNumeric() && !(lit.getValue() instanceof Integer) && JSType.isRepresentableAsInt(lit.getNumber())) {
            return caseNode.setTest((Expression)LiteralNode.newInstance((Node)lit, (Number)lit.getInt32()).accept(this));
        }
        return caseNode;
    }

    @Override
    public Node leaveSwitchNode(SwitchNode switchNode) {
        if (!switchNode.isUniqueInteger()) {
            this.addStatementEnclosedInBlock(switchNode);
        } else {
            this.addStatement(switchNode);
        }
        return switchNode;
    }

    @Override
    public Node leaveThrowNode(ThrowNode throwNode) {
        return this.addStatement(throwNode);
    }

    private static <T extends Node> T ensureUniqueNamesIn(T node) {
        return (T)node.accept(new SimpleNodeVisitor(){

            @Override
            public Node leaveFunctionNode(FunctionNode functionNode) {
                String name = functionNode.getName();
                return functionNode.setName(this.lc, this.lc.getCurrentFunction().uniqueName(name));
            }

            @Override
            public Node leaveDefault(Node labelledNode) {
                return labelledNode.ensureUniqueLabels(this.lc);
            }
        });
    }

    private static Block createFinallyBlock(Block finallyBody) {
        ArrayList<Statement> newStatements = new ArrayList<Statement>();
        for (Statement statement : finallyBody.getStatements()) {
            newStatements.add(statement);
            if (!statement.hasTerminalFlags()) continue;
            break;
        }
        return finallyBody.setStatements(null, newStatements);
    }

    private Block catchAllBlock(TryNode tryNode) {
        int lineNumber = tryNode.getLineNumber();
        long token = tryNode.getToken();
        int finish = tryNode.getFinish();
        IdentNode exception = new IdentNode(token, finish, ((BlockLexicalContext)this.lc).getCurrentFunction().uniqueName(CompilerConstants.EXCEPTION_PREFIX.symbolName()));
        Block catchBody = new Block(token, finish, new ThrowNode(lineNumber, token, finish, new IdentNode(exception), true));
        assert (catchBody.isTerminal());
        CatchNode catchAllNode = new CatchNode(lineNumber, token, finish, new IdentNode(exception), null, catchBody, true);
        Block catchAllBlock = new Block(token, finish, catchAllNode);
        return (Block)catchAllBlock.accept(this);
    }

    private IdentNode compilerConstant(CompilerConstants cc) {
        FunctionNode functionNode = ((BlockLexicalContext)this.lc).getCurrentFunction();
        return new IdentNode(functionNode.getToken(), functionNode.getFinish(), cc.symbolName());
    }

    private static boolean isTerminalFinally(Block finallyBlock) {
        return finallyBlock.getLastStatement().hasTerminalFlags();
    }

    private TryNode spliceFinally(TryNode tryNode, final ThrowNode rethrow, Block finallyBody) {
        assert (tryNode.getFinallyBody() == null);
        final Block finallyBlock = Lower.createFinallyBlock(finallyBody);
        final ArrayList<Block> inlinedFinallies = new ArrayList<Block>();
        final FunctionNode fn = ((BlockLexicalContext)this.lc).getCurrentFunction();
        TryNode newTryNode = (TryNode)tryNode.accept((NodeVisitor)new SimpleNodeVisitor(){

            @Override
            public boolean enterFunctionNode(FunctionNode functionNode) {
                return false;
            }

            @Override
            public Node leaveThrowNode(ThrowNode throwNode) {
                if (rethrow == throwNode) {
                    return new BlockStatement(Lower.prependFinally(finallyBlock, throwNode));
                }
                return throwNode;
            }

            @Override
            public Node leaveBreakNode(BreakNode breakNode) {
                return this.leaveJumpStatement(breakNode);
            }

            @Override
            public Node leaveContinueNode(ContinueNode continueNode) {
                return this.leaveJumpStatement(continueNode);
            }

            private Node leaveJumpStatement(JumpStatement jump) {
                if (jump.getTarget(this.lc) == null) {
                    return Lower.createJumpToInlinedFinally(fn, inlinedFinallies, Lower.prependFinally(finallyBlock, jump));
                }
                return jump;
            }

            @Override
            public Node leaveReturnNode(ReturnNode returnNode) {
                Expression expr = returnNode.getExpression();
                if (Lower.isTerminalFinally(finallyBlock)) {
                    if (expr == null) {
                        return Lower.createJumpToInlinedFinally(fn, inlinedFinallies, Lower.ensureUniqueNamesIn(finallyBlock));
                    }
                    ArrayList<Statement> newStatements = new ArrayList<Statement>(2);
                    int retLineNumber = returnNode.getLineNumber();
                    long retToken = returnNode.getToken();
                    newStatements.add(new ExpressionStatement(retLineNumber, retToken, returnNode.getFinish(), expr));
                    newStatements.add(Lower.createJumpToInlinedFinally(fn, inlinedFinallies, Lower.ensureUniqueNamesIn(finallyBlock)));
                    return new BlockStatement(retLineNumber, new Block(retToken, finallyBlock.getFinish(), newStatements));
                }
                if (expr == null || expr instanceof LiteralNode.PrimitiveLiteralNode || expr instanceof IdentNode && CompilerConstants.RETURN.symbolName().equals(((IdentNode)expr).getName())) {
                    return Lower.createJumpToInlinedFinally(fn, inlinedFinallies, Lower.prependFinally(finallyBlock, returnNode));
                }
                ArrayList<Statement> newStatements = new ArrayList<Statement>();
                int retLineNumber = returnNode.getLineNumber();
                long retToken = returnNode.getToken();
                int retFinish = returnNode.getFinish();
                IdentNode resultNode = new IdentNode(expr.getToken(), expr.getFinish(), CompilerConstants.RETURN.symbolName());
                newStatements.add(new ExpressionStatement(retLineNumber, retToken, retFinish, new BinaryNode(Token.recast(returnNode.getToken(), TokenType.ASSIGN), resultNode, expr)));
                newStatements.add(Lower.createJumpToInlinedFinally(fn, inlinedFinallies, Lower.prependFinally(finallyBlock, returnNode.setExpression(resultNode))));
                return new BlockStatement(retLineNumber, new Block(retToken, retFinish, newStatements));
            }
        });
        this.addStatement(inlinedFinallies.isEmpty() ? newTryNode : newTryNode.setInlinedFinallies(this.lc, inlinedFinallies));
        this.addStatement(new BlockStatement(finallyBlock));
        return newTryNode;
    }

    private static JumpToInlinedFinally createJumpToInlinedFinally(FunctionNode fn, List<Block> inlinedFinallies, Block finallyBlock) {
        String labelName = fn.uniqueName(":finally");
        long token = finallyBlock.getToken();
        int finish = finallyBlock.getFinish();
        inlinedFinallies.add(new Block(token, finish, new LabelNode(finallyBlock.getFirstStatementLineNumber(), token, finish, labelName, finallyBlock)));
        return new JumpToInlinedFinally(labelName);
    }

    private static Block prependFinally(Block finallyBlock, Statement statement) {
        Block inlinedFinally = Lower.ensureUniqueNamesIn(finallyBlock);
        if (Lower.isTerminalFinally(finallyBlock)) {
            return inlinedFinally;
        }
        List<Statement> stmts = inlinedFinally.getStatements();
        ArrayList<Statement> newStmts = new ArrayList<Statement>(stmts.size() + 1);
        newStmts.addAll(stmts);
        newStmts.add(statement);
        return new Block(inlinedFinally.getToken(), statement.getFinish(), newStmts);
    }

    @Override
    public Node leaveTryNode(TryNode tryNode) {
        Block finallyBody = tryNode.getFinallyBody();
        TryNode newTryNode = tryNode.setFinallyBody(this.lc, null);
        if (finallyBody == null || finallyBody.getStatementCount() == 0) {
            List<CatchNode> catches = newTryNode.getCatches();
            if (catches == null || catches.isEmpty()) {
                return this.addStatement(new BlockStatement(tryNode.getBody()));
            }
            return this.addStatement(this.ensureUnconditionalCatch(newTryNode));
        }
        Block catchAll = this.catchAllBlock(tryNode);
        final ArrayList rethrows = new ArrayList(1);
        catchAll.accept(new SimpleNodeVisitor(){

            @Override
            public boolean enterThrowNode(ThrowNode throwNode) {
                rethrows.add(throwNode);
                return true;
            }
        });
        assert (rethrows.size() == 1);
        if (!tryNode.getCatchBlocks().isEmpty()) {
            Block outerBody = new Block(newTryNode.getToken(), newTryNode.getFinish(), this.ensureUnconditionalCatch(newTryNode));
            newTryNode = newTryNode.setBody(this.lc, outerBody).setCatchBlocks(this.lc, null);
        }
        newTryNode = newTryNode.setCatchBlocks(this.lc, Arrays.asList(catchAll));
        return (TryNode)((BlockLexicalContext)this.lc).replace(tryNode, this.spliceFinally(newTryNode, (ThrowNode)rethrows.get(0), finallyBody));
    }

    private TryNode ensureUnconditionalCatch(TryNode tryNode) {
        List<CatchNode> catches = tryNode.getCatches();
        if (catches == null || catches.isEmpty() || catches.get(catches.size() - 1).getExceptionCondition() == null) {
            return tryNode;
        }
        ArrayList<Block> newCatchBlocks = new ArrayList<Block>(tryNode.getCatchBlocks());
        newCatchBlocks.add(this.catchAllBlock(tryNode));
        return tryNode.setCatchBlocks(this.lc, newCatchBlocks);
    }

    @Override
    public boolean enterUnaryNode(UnaryNode unaryNode) {
        if (this.es6) {
            if (unaryNode.isTokenType(TokenType.YIELD) || unaryNode.isTokenType(TokenType.YIELD_STAR)) {
                this.throwNotImplementedYet("es6.yield", unaryNode);
            } else if (unaryNode.isTokenType(TokenType.SPREAD_ARGUMENT) || unaryNode.isTokenType(TokenType.SPREAD_ARRAY)) {
                this.throwNotImplementedYet("es6.spread", unaryNode);
            }
        }
        return super.enterUnaryNode(unaryNode);
    }

    @Override
    public boolean enterASSIGN(BinaryNode binaryNode) {
        if (this.es6 && (binaryNode.lhs() instanceof ObjectNode || binaryNode.lhs() instanceof LiteralNode.ArrayLiteralNode)) {
            this.throwNotImplementedYet("es6.destructuring", binaryNode);
        }
        return super.enterASSIGN(binaryNode);
    }

    @Override
    public Node leaveVarNode(VarNode varNode) {
        this.addStatement(varNode);
        if (varNode.getFlag(4) && ((BlockLexicalContext)this.lc).getCurrentFunction().isProgram() && ((FunctionNode)varNode.getInit()).isAnonymous()) {
            new ExpressionStatement(varNode.getLineNumber(), varNode.getToken(), varNode.getFinish(), new IdentNode(varNode.getName())).accept(this);
        }
        return varNode;
    }

    @Override
    public Node leaveWhileNode(WhileNode whileNode) {
        JoinPredecessorExpression test = whileNode.getTest();
        Block body = whileNode.getBody();
        if (Expression.isAlwaysTrue(test)) {
            ForNode forNode = (ForNode)new ForNode(whileNode.getLineNumber(), whileNode.getToken(), whileNode.getFinish(), body, 0).accept((NodeVisitor)this);
            ((BlockLexicalContext)this.lc).replace(whileNode, forNode);
            return forNode;
        }
        return this.addStatement(this.checkEscape(whileNode));
    }

    @Override
    public Node leaveWithNode(WithNode withNode) {
        return this.addStatement(withNode);
    }

    @Override
    public boolean enterClassNode(ClassNode classNode) {
        this.throwNotImplementedYet("es6.class", classNode);
        return super.enterClassNode(classNode);
    }

    private static Expression markerFunction(Expression function) {
        if (function instanceof IdentNode) {
            return ((IdentNode)function).setIsFunction();
        }
        if (function instanceof BaseNode) {
            return ((BaseNode)function).setIsFunction();
        }
        return function;
    }

    private String evalLocation(IdentNode node) {
        Source source = ((BlockLexicalContext)this.lc).getCurrentFunction().getSource();
        int pos = node.position();
        return source.getName() + '#' + source.getLine(pos) + ':' + source.getColumn(pos) + "<eval>";
    }

    private CallNode checkEval(CallNode callNode) {
        if (callNode.getFunction() instanceof IdentNode) {
            List<Expression> args = callNode.getArgs();
            IdentNode callee = (IdentNode)callNode.getFunction();
            if (args.size() >= 1 && CompilerConstants.EVAL.symbolName().equals(callee.getName())) {
                ArrayList<Expression> evalArgs = new ArrayList<Expression>(args.size());
                for (Expression arg : args) {
                    evalArgs.add((Expression)Lower.ensureUniqueNamesIn(arg).accept(this));
                }
                return callNode.setEvalArgs(new CallNode.EvalArgs(evalArgs, this.evalLocation(callee)));
            }
        }
        return callNode;
    }

    private static boolean controlFlowEscapes(final LexicalContext lex, Block loopBody) {
        final ArrayList escapes = new ArrayList();
        loopBody.accept(new SimpleNodeVisitor(){

            @Override
            public Node leaveBreakNode(BreakNode node) {
                escapes.add(node);
                return node;
            }

            @Override
            public Node leaveContinueNode(ContinueNode node) {
                if (lex.contains(node.getTarget(lex))) {
                    escapes.add(node);
                }
                return node;
            }
        });
        return !escapes.isEmpty();
    }

    private <T extends LoopNode> T checkEscape(T loopNode) {
        boolean escapes = Lower.controlFlowEscapes(this.lc, loopNode.getBody());
        if (escapes) {
            return (T)loopNode.setBody(this.lc, loopNode.getBody().setIsTerminal(this.lc, false)).setControlFlowEscapes(this.lc, escapes);
        }
        return loopNode;
    }

    private Node addStatement(Statement statement) {
        ((BlockLexicalContext)this.lc).appendStatement(statement);
        return statement;
    }

    private void addStatementEnclosedInBlock(Statement stmt) {
        BlockStatement b = BlockStatement.createReplacement(stmt, Collections.singletonList(stmt));
        if (stmt.isTerminal()) {
            b = b.setBlock(b.getBlock().setIsTerminal(null, true));
        }
        this.addStatement(b);
    }

    private static boolean isInternalExpression(Expression expression) {
        if (!(expression instanceof IdentNode)) {
            return false;
        }
        Symbol symbol = ((IdentNode)expression).getSymbol();
        return symbol != null && symbol.isInternal();
    }

    private static boolean isEvalResultAssignment(Node expression) {
        Expression lhs;
        Node e = expression;
        if (e instanceof BinaryNode && (lhs = ((BinaryNode)e).lhs()) instanceof IdentNode) {
            return ((IdentNode)lhs).getName().equals(CompilerConstants.RETURN.symbolName());
        }
        return false;
    }

    private void throwNotImplementedYet(String msgId, Node node) {
        long token = node.getToken();
        int line = this.source.getLine(node.getStart());
        int column = this.source.getColumn(node.getStart());
        String message = ECMAErrors.getMessage("unimplemented." + msgId, new String[0]);
        String formatted = ErrorManager.format(message, this.source, line, column, token);
        throw new RuntimeException(formatted);
    }
}
