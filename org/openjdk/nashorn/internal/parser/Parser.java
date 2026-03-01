package org.openjdk.nashorn.internal.parser;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.codegen.Namespace;
import org.openjdk.nashorn.internal.ir.AccessNode;
import org.openjdk.nashorn.internal.ir.BaseNode;
import org.openjdk.nashorn.internal.ir.BinaryNode;
import org.openjdk.nashorn.internal.ir.Block;
import org.openjdk.nashorn.internal.ir.BlockStatement;
import org.openjdk.nashorn.internal.ir.BreakNode;
import org.openjdk.nashorn.internal.ir.CallNode;
import org.openjdk.nashorn.internal.ir.CaseNode;
import org.openjdk.nashorn.internal.ir.CatchNode;
import org.openjdk.nashorn.internal.ir.ClassNode;
import org.openjdk.nashorn.internal.ir.ContinueNode;
import org.openjdk.nashorn.internal.ir.DebuggerNode;
import org.openjdk.nashorn.internal.ir.EmptyNode;
import org.openjdk.nashorn.internal.ir.ErrorNode;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.ExpressionList;
import org.openjdk.nashorn.internal.ir.ExpressionStatement;
import org.openjdk.nashorn.internal.ir.ForNode;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.IdentNode;
import org.openjdk.nashorn.internal.ir.IfNode;
import org.openjdk.nashorn.internal.ir.IndexNode;
import org.openjdk.nashorn.internal.ir.JoinPredecessorExpression;
import org.openjdk.nashorn.internal.ir.LabelNode;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LiteralNode;
import org.openjdk.nashorn.internal.ir.Module;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.ObjectNode;
import org.openjdk.nashorn.internal.ir.PropertyKey;
import org.openjdk.nashorn.internal.ir.PropertyNode;
import org.openjdk.nashorn.internal.ir.ReturnNode;
import org.openjdk.nashorn.internal.ir.RuntimeNode;
import org.openjdk.nashorn.internal.ir.Statement;
import org.openjdk.nashorn.internal.ir.SwitchNode;
import org.openjdk.nashorn.internal.ir.TemplateLiteral;
import org.openjdk.nashorn.internal.ir.TernaryNode;
import org.openjdk.nashorn.internal.ir.ThrowNode;
import org.openjdk.nashorn.internal.ir.TryNode;
import org.openjdk.nashorn.internal.ir.UnaryNode;
import org.openjdk.nashorn.internal.ir.VarNode;
import org.openjdk.nashorn.internal.ir.WhileNode;
import org.openjdk.nashorn.internal.ir.WithNode;
import org.openjdk.nashorn.internal.ir.debug.ASTWriter;
import org.openjdk.nashorn.internal.ir.debug.PrintVisitor;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;
import org.openjdk.nashorn.internal.parser.AbstractParser;
import org.openjdk.nashorn.internal.parser.Lexer;
import org.openjdk.nashorn.internal.parser.ParserContext;
import org.openjdk.nashorn.internal.parser.ParserContextBlockNode;
import org.openjdk.nashorn.internal.parser.ParserContextBreakableNode;
import org.openjdk.nashorn.internal.parser.ParserContextFunctionNode;
import org.openjdk.nashorn.internal.parser.ParserContextLabelNode;
import org.openjdk.nashorn.internal.parser.ParserContextLoopNode;
import org.openjdk.nashorn.internal.parser.ParserContextModuleNode;
import org.openjdk.nashorn.internal.parser.ParserContextSwitchNode;
import org.openjdk.nashorn.internal.parser.Token;
import org.openjdk.nashorn.internal.parser.TokenKind;
import org.openjdk.nashorn.internal.parser.TokenLookup;
import org.openjdk.nashorn.internal.parser.TokenStream;
import org.openjdk.nashorn.internal.parser.TokenType;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ErrorManager;
import org.openjdk.nashorn.internal.runtime.JSErrorType;
import org.openjdk.nashorn.internal.runtime.ParserException;
import org.openjdk.nashorn.internal.runtime.RecompilableScriptFunctionData;
import org.openjdk.nashorn.internal.runtime.ScriptEnvironment;
import org.openjdk.nashorn.internal.runtime.Source;
import org.openjdk.nashorn.internal.runtime.Timing;
import org.openjdk.nashorn.internal.runtime.linker.NameCodec;
import org.openjdk.nashorn.internal.runtime.logging.DebugLogger;
import org.openjdk.nashorn.internal.runtime.logging.Loggable;
import org.openjdk.nashorn.internal.runtime.logging.Logger;

@Logger(name="parser")
public class Parser
extends AbstractParser
implements Loggable {
    private static final String ARGUMENTS_NAME = CompilerConstants.ARGUMENTS_VAR.symbolName();
    private static final String CONSTRUCTOR_NAME = "constructor";
    private static final String GET_NAME = "get";
    private static final String SET_NAME = "set";
    private final ScriptEnvironment env;
    private final boolean scripting;
    private List<Statement> functionDeclarations;
    private final ParserContext lc = new ParserContext();
    private final Deque<Object> defaultNames = new ArrayDeque<Object>();
    private final Namespace namespace;
    private final DebugLogger log;
    protected final Lexer.LineInfoReceiver lineInfoReceiver;
    private RecompilableScriptFunctionData reparsedFunction;

    public Parser(ScriptEnvironment env, Source source, ErrorManager errors) {
        this(env, source, errors, env._strict, null);
    }

    public Parser(ScriptEnvironment env, Source source, ErrorManager errors, boolean strict, DebugLogger log) {
        this(env, source, errors, strict, 0, log);
    }

    public Parser(ScriptEnvironment env, Source source, ErrorManager errors, boolean strict, int lineOffset, DebugLogger log) {
        super(source, errors, strict, lineOffset);
        this.env = env;
        this.namespace = new Namespace(env.getNamespace());
        this.scripting = env._scripting;
        this.lineInfoReceiver = this.scripting ? (receiverLine, receiverLinePosition) -> {
            this.line = receiverLine;
            this.linePosition = receiverLinePosition;
        } : null;
        this.log = log == null ? DebugLogger.DISABLED_LOGGER : log;
    }

    @Override
    public DebugLogger getLogger() {
        return this.log;
    }

    @Override
    public DebugLogger initLogger(Context context) {
        return context.getLogger(this.getClass());
    }

    public void setFunctionName(String name) {
        this.defaultNames.push(this.createIdentNode(0L, 0, name));
    }

    public void setReparsedFunction(RecompilableScriptFunctionData reparsedFunction) {
        this.reparsedFunction = reparsedFunction;
    }

    public FunctionNode parse() {
        return this.parse(CompilerConstants.PROGRAM.symbolName(), 0, this.source.getLength(), 0);
    }

    private void scanFirstToken() {
        this.k = -1;
        this.next();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public FunctionNode parse(String scriptName, int startPos, int len, int reparseFlags) {
        FunctionNode functionNode;
        boolean isTimingEnabled = this.env.isTimingEnabled();
        long t0 = isTimingEnabled ? System.nanoTime() : 0L;
        this.log.info(this, " begin for '", scriptName, "'");
        try {
            this.stream = new TokenStream();
            this.lexer = new Lexer(this.source, startPos, len, this.stream, this.scripting && !this.env._no_syntax_extensions, this.isES6(), this.reparsedFunction != null);
            this.lexer.line = this.lexer.pendingLine = this.lineOffset + 1;
            this.line = this.lineOffset;
            this.scanFirstToken();
            functionNode = this.program(scriptName, reparseFlags);
        }
        catch (Exception e) {
            FunctionNode functionNode2;
            try {
                this.handleParseException(e);
                functionNode2 = null;
            }
            catch (Throwable throwable) {
                String end = this + " end '" + scriptName + "'";
                if (isTimingEnabled) {
                    this.env._timing.accumulateTime(this.toString(), System.nanoTime() - t0);
                    this.log.info(end, "' in ", Timing.toMillisPrint(System.nanoTime() - t0), " ms");
                } else {
                    this.log.info(end);
                }
                throw throwable;
            }
            String end = this + " end '" + scriptName + "'";
            if (isTimingEnabled) {
                this.env._timing.accumulateTime(this.toString(), System.nanoTime() - t0);
                this.log.info(end, "' in ", Timing.toMillisPrint(System.nanoTime() - t0), " ms");
            } else {
                this.log.info(end);
            }
            return functionNode2;
        }
        String end = this + " end '" + scriptName + "'";
        if (isTimingEnabled) {
            this.env._timing.accumulateTime(this.toString(), System.nanoTime() - t0);
            this.log.info(end, "' in ", Timing.toMillisPrint(System.nanoTime() - t0), " ms");
        } else {
            this.log.info(end);
        }
        return functionNode;
    }

    public FunctionNode parseModule(String moduleName, int startPos, int len) {
        try {
            this.stream = new TokenStream();
            this.lexer = new Lexer(this.source, startPos, len, this.stream, this.scripting && !this.env._no_syntax_extensions, this.isES6(), this.reparsedFunction != null);
            this.lexer.line = this.lexer.pendingLine = this.lineOffset + 1;
            this.line = this.lineOffset;
            this.scanFirstToken();
            return this.module(moduleName);
        }
        catch (Exception e) {
            this.handleParseException(e);
            return null;
        }
    }

    public FunctionNode parseModule(String moduleName) {
        return this.parseModule(moduleName, 0, this.source.getLength());
    }

    public void parseFormalParameterList() {
        try {
            this.stream = new TokenStream();
            this.lexer = new Lexer(this.source, this.stream, this.scripting && !this.env._no_syntax_extensions, this.isES6());
            this.scanFirstToken();
            this.formalParameterList(TokenType.EOF, false);
        }
        catch (Exception e) {
            this.handleParseException(e);
        }
    }

    public void parseFunctionBody() {
        try {
            this.stream = new TokenStream();
            this.lexer = new Lexer(this.source, this.stream, this.scripting && !this.env._no_syntax_extensions, this.isES6());
            int functionLine = this.line;
            this.scanFirstToken();
            long functionToken = Token.toDesc(TokenType.FUNCTION, 0, this.source.getLength());
            IdentNode ident = new IdentNode(functionToken, Token.descPosition(functionToken), CompilerConstants.PROGRAM.symbolName());
            ParserContextFunctionNode function = this.createParserContextFunctionNode(ident, functionToken, FunctionNode.Kind.NORMAL, functionLine, Collections.emptyList());
            this.lc.push(function);
            ParserContextBlockNode body = this.newBlock();
            this.functionDeclarations = new ArrayList<Statement>();
            this.sourceElements(0);
            this.addFunctionDeclarations(function);
            this.functionDeclarations = null;
            this.restoreBlock(body);
            body.setFlag(1);
            Block functionBody = new Block(functionToken, this.source.getLength() - 1, body.getFlags() | 0x10, body.getStatements());
            this.lc.pop(function);
            this.expect(TokenType.EOF);
            FunctionNode functionNode = this.createFunctionNode(function, functionToken, ident, Collections.emptyList(), FunctionNode.Kind.NORMAL, functionLine, functionBody);
            this.printAST(functionNode);
        }
        catch (Exception e) {
            this.handleParseException(e);
        }
    }

    private void handleParseException(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            message = e.toString();
        }
        if (e instanceof ParserException) {
            this.errors.error((ParserException)e);
        } else {
            this.errors.error(message);
        }
        if (this.env._dump_on_error) {
            e.printStackTrace(this.env.getErr());
        }
    }

    private void recover(Exception e) {
        if (e != null) {
            String message = e.getMessage();
            if (message == null) {
                message = e.toString();
            }
            if (e instanceof ParserException) {
                this.errors.error((ParserException)e);
            } else {
                this.errors.error(message);
            }
            if (this.env._dump_on_error) {
                e.printStackTrace(this.env.getErr());
            }
        }
        block4: while (true) {
            switch (this.type) {
                case EOF: {
                    break block4;
                }
                case EOL: 
                case SEMICOLON: 
                case RBRACE: {
                    this.next();
                    break block4;
                }
                default: {
                    this.nextOrEOL();
                    continue block4;
                }
            }
            break;
        }
    }

    private ParserContextBlockNode newBlock() {
        return this.lc.push(new ParserContextBlockNode(this.token));
    }

    private ParserContextFunctionNode createParserContextFunctionNode(IdentNode ident, long functionToken, FunctionNode.Kind kind, int functionLine, List<IdentNode> parameters) {
        StringBuilder sb = new StringBuilder();
        ParserContextFunctionNode parentFunction = this.lc.getCurrentFunction();
        if (parentFunction != null && !parentFunction.isProgram()) {
            sb.append(parentFunction.getName()).append(CompilerConstants.NESTED_FUNCTION_SEPARATOR.symbolName());
        }
        assert (ident.getName() != null);
        sb.append(ident.getName());
        String name = this.namespace.uniqueName(sb.toString());
        assert (parentFunction != null || kind == FunctionNode.Kind.MODULE || name.equals(CompilerConstants.PROGRAM.symbolName())) : "name = " + name;
        int flags = 0;
        if (this.isStrictMode) {
            flags |= 4;
        }
        if (parentFunction == null) {
            flags |= 0x2000;
        }
        ParserContextFunctionNode functionNode = new ParserContextFunctionNode(functionToken, ident, name, this.namespace, functionLine, kind, parameters);
        functionNode.setFlag(flags);
        return functionNode;
    }

    private FunctionNode createFunctionNode(ParserContextFunctionNode function, long startToken, IdentNode ident, List<IdentNode> parameters, FunctionNode.Kind kind, int functionLine, Block body) {
        FunctionNode functionNode = new FunctionNode(this.source, functionLine, body.getToken(), Token.descPosition(body.getToken()), startToken, function.getLastToken(), this.namespace, ident, function.getName(), parameters, function.getParameterExpressions(), kind, function.getFlags(), body, function.getEndParserState(), function.getModule(), function.getDebugFlags());
        this.printAST(functionNode);
        return functionNode;
    }

    private void restoreBlock(ParserContextBlockNode block) {
        this.lc.pop(block);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Block getBlock(boolean needsBraces) {
        long blockToken = this.token;
        ParserContextBlockNode newBlock = this.newBlock();
        try {
            if (needsBraces) {
                this.expect(TokenType.LBRACE);
            }
            this.statementList();
        }
        finally {
            this.restoreBlock(newBlock);
        }
        if (needsBraces) {
            this.expect(TokenType.RBRACE);
        }
        int flags = newBlock.getFlags() | (needsBraces ? 0 : 16);
        return new Block(blockToken, this.finish, flags, newBlock.getStatements());
    }

    private Block getStatement() {
        return this.getStatement(false);
    }

    private Block getStatement(boolean labelledStatement) {
        if (this.type == TokenType.LBRACE) {
            return this.getBlock(true);
        }
        ParserContextBlockNode newBlock = this.newBlock();
        try {
            this.statement(false, 0, true, labelledStatement);
        }
        finally {
            this.restoreBlock(newBlock);
        }
        return new Block(newBlock.getToken(), this.finish, newBlock.getFlags() | 0x10, newBlock.getStatements());
    }

    private void detectSpecialFunction(IdentNode ident) {
        String name = ident.getName();
        if (CompilerConstants.EVAL.symbolName().equals(name)) {
            Parser.markEval(this.lc);
        } else if (TokenType.SUPER.getName().equals(name)) {
            assert (ident.isDirectSuper());
            Parser.markSuperCall(this.lc);
        }
    }

    private void detectSpecialProperty(IdentNode ident) {
        if (Parser.isArguments(ident)) {
            this.getCurrentNonArrowFunction().setFlag(8);
        }
    }

    private boolean useBlockScope() {
        return this.isES6();
    }

    private boolean isES6() {
        return this.env._es6;
    }

    private static boolean isArguments(String name) {
        return ARGUMENTS_NAME.equals(name);
    }

    static boolean isArguments(IdentNode ident) {
        return Parser.isArguments(ident.getName());
    }

    private static boolean checkIdentLValue(IdentNode ident) {
        return ident.tokenType().getKind() != TokenKind.KEYWORD;
    }

    private Expression verifyAssignment(long op, Expression lhs, Expression rhs) {
        TokenType opType = Token.descType(op);
        switch (opType) {
            case ASSIGN: 
            case ASSIGN_ADD: 
            case ASSIGN_BIT_AND: 
            case ASSIGN_BIT_OR: 
            case ASSIGN_BIT_XOR: 
            case ASSIGN_DIV: 
            case ASSIGN_MOD: 
            case ASSIGN_MUL: 
            case ASSIGN_SAR: 
            case ASSIGN_SHL: 
            case ASSIGN_SHR: 
            case ASSIGN_SUB: {
                if (lhs instanceof IdentNode) {
                    if (!Parser.checkIdentLValue((IdentNode)lhs)) {
                        return this.referenceError(lhs, rhs, false);
                    }
                    this.verifyIdent((IdentNode)lhs, "assignment");
                    break;
                }
                if (lhs instanceof AccessNode || lhs instanceof IndexNode) break;
                if (opType == TokenType.ASSIGN && this.isDestructuringLhs(lhs)) {
                    this.verifyDestructuringAssignmentPattern(lhs, "assignment");
                    break;
                }
                return this.referenceError(lhs, rhs, this.env._early_lvalue_error);
            }
        }
        if (BinaryNode.isLogical(opType)) {
            return new BinaryNode(op, new JoinPredecessorExpression(lhs), new JoinPredecessorExpression(rhs));
        }
        return new BinaryNode(op, lhs, rhs);
    }

    private boolean isDestructuringLhs(Expression lhs) {
        if (lhs instanceof ObjectNode || lhs instanceof LiteralNode.ArrayLiteralNode) {
            return this.isES6();
        }
        return false;
    }

    private void verifyDestructuringAssignmentPattern(Expression pattern, final String contextString) {
        assert (pattern instanceof ObjectNode || pattern instanceof LiteralNode.ArrayLiteralNode);
        pattern.accept(new VerifyDestructuringPatternNodeVisitor(new LexicalContext()){

            @Override
            protected void verifySpreadElement(Expression lvalue) {
                if (!Parser.this.checkValidLValue(lvalue, contextString)) {
                    throw Parser.this.error(AbstractParser.message("invalid.lvalue", new String[0]), lvalue.getToken());
                }
            }

            @Override
            public boolean enterIdentNode(IdentNode identNode) {
                Parser.this.verifyIdent(identNode, contextString);
                if (!Parser.checkIdentLValue(identNode)) {
                    Parser.this.referenceError(identNode, null, true);
                    return false;
                }
                return false;
            }

            @Override
            public boolean enterAccessNode(AccessNode accessNode) {
                return false;
            }

            @Override
            public boolean enterIndexNode(IndexNode indexNode) {
                return false;
            }

            @Override
            protected boolean enterDefault(Node node) {
                throw Parser.this.error(String.format("unexpected node in AssignmentPattern: %s", node));
            }
        });
    }

    private static UnaryNode incDecExpression(long firstToken, TokenType tokenType, Expression expression, boolean isPostfix) {
        if (isPostfix) {
            return new UnaryNode(Token.recast(firstToken, tokenType == TokenType.DECPREFIX ? TokenType.DECPOSTFIX : TokenType.INCPOSTFIX), expression.getStart(), Token.descPosition(firstToken) + Token.descLength(firstToken), expression);
        }
        return new UnaryNode(firstToken, expression);
    }

    private FunctionNode program(String scriptName, int reparseFlags) {
        long functionToken = Token.toDesc(TokenType.FUNCTION, Token.descPosition(Token.withDelimiter(this.token)), this.source.getLength());
        int functionLine = this.line;
        IdentNode ident = new IdentNode(functionToken, Token.descPosition(functionToken), scriptName);
        ParserContextFunctionNode script = this.createParserContextFunctionNode(ident, functionToken, FunctionNode.Kind.SCRIPT, functionLine, Collections.emptyList());
        this.lc.push(script);
        ParserContextBlockNode body = this.newBlock();
        this.functionDeclarations = new ArrayList<Statement>();
        this.sourceElements(reparseFlags);
        this.addFunctionDeclarations(script);
        this.functionDeclarations = null;
        this.restoreBlock(body);
        body.setFlag(1);
        Block programBody = new Block(functionToken, this.finish, body.getFlags() | 0x10 | 0x20, body.getStatements());
        this.lc.pop(script);
        script.setLastToken(this.token);
        this.expect(TokenType.EOF);
        return this.createFunctionNode(script, functionToken, ident, Collections.emptyList(), FunctionNode.Kind.SCRIPT, functionLine, programBody);
    }

    private String getDirective(Node stmt) {
        LiteralNode lit;
        long litToken;
        TokenType tt;
        Expression expr;
        if (stmt instanceof ExpressionStatement && (expr = ((ExpressionStatement)stmt).getExpression()) instanceof LiteralNode && ((tt = Token.descType(litToken = (lit = (LiteralNode)expr).getToken())) == TokenType.STRING || tt == TokenType.ESCSTRING)) {
            return this.source.getString(lit.getStart(), Token.descLength(litToken));
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sourceElements(int reparseFlags) {
        ArrayList<Statement> directiveStmts = null;
        boolean checkDirective = true;
        int functionFlags = reparseFlags;
        boolean oldStrictMode = this.isStrictMode;
        try {
            while (this.type != TokenType.EOF) {
                if (this.type == TokenType.RBRACE) {
                    break;
                }
                try {
                    this.statement(true, functionFlags, false, false);
                    functionFlags = 0;
                    if (checkDirective) {
                        Statement lastStatement = this.lc.getLastStatement();
                        String directive = this.getDirective(lastStatement);
                        boolean bl = checkDirective = directive != null;
                        if (checkDirective) {
                            int debugFlag;
                            if (!oldStrictMode) {
                                if (directiveStmts == null) {
                                    directiveStmts = new ArrayList<Statement>();
                                }
                                directiveStmts.add(lastStatement);
                            }
                            if ("use strict".equals(directive)) {
                                this.isStrictMode = true;
                                ParserContextFunctionNode function = this.lc.getCurrentFunction();
                                function.setFlag(4);
                                if (!oldStrictMode) {
                                    for (Node node : directiveStmts) {
                                        this.getValue(node.getToken());
                                    }
                                    this.verifyIdent(function.getIdent(), "function name");
                                    for (IdentNode identNode : function.getParameters()) {
                                        this.verifyIdent(identNode, "function parameter");
                                    }
                                }
                            } else if (Context.DEBUG && (debugFlag = FunctionNode.getDirectiveFlag(directive)) != 0) {
                                ParserContextFunctionNode function = this.lc.getCurrentFunction();
                                function.setDebugFlag(debugFlag);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    int errorLine = this.line;
                    long errorToken = this.token;
                    this.recover(e);
                    ErrorNode errorNode = new ErrorNode(errorToken, this.finish);
                    ExpressionStatement expressionStatement = new ExpressionStatement(errorLine, errorToken, this.finish, errorNode);
                    this.appendStatement(expressionStatement);
                }
                this.stream.commit(this.k);
            }
        }
        finally {
            this.isStrictMode = oldStrictMode;
        }
    }

    private void statement() {
        this.statement(false, 0, false, false);
    }

    private void statement(boolean topLevel, int reparseFlags, boolean singleStatement, boolean labelledStatement) {
        switch (this.type) {
            case LBRACE: {
                this.block();
                break;
            }
            case VAR: {
                this.variableStatement(this.type);
                break;
            }
            case SEMICOLON: {
                this.emptyStatement();
                break;
            }
            case IF: {
                this.ifStatement();
                break;
            }
            case FOR: {
                this.forStatement();
                break;
            }
            case WHILE: {
                this.whileStatement();
                break;
            }
            case DO: {
                this.doStatement();
                break;
            }
            case CONTINUE: {
                this.continueStatement();
                break;
            }
            case BREAK: {
                this.breakStatement();
                break;
            }
            case RETURN: {
                this.returnStatement();
                break;
            }
            case WITH: {
                this.withStatement();
                break;
            }
            case SWITCH: {
                this.switchStatement();
                break;
            }
            case THROW: {
                this.throwStatement();
                break;
            }
            case TRY: {
                this.tryStatement();
                break;
            }
            case DEBUGGER: {
                this.debuggerStatement();
                break;
            }
            case EOF: 
            case RPAREN: 
            case RBRACKET: {
                this.expect(TokenType.SEMICOLON);
                break;
            }
            case FUNCTION: {
                if (singleStatement && (!labelledStatement || this.isStrictMode)) {
                    throw this.error(AbstractParser.message("expected.stmt", "function declaration"), this.token);
                }
                this.functionExpression(true, topLevel || labelledStatement);
                return;
            }
            default: {
                int propertyLine;
                long propertyToken;
                String ident;
                if (this.useBlockScope() && (this.type == TokenType.LET && this.lookaheadIsLetDeclaration(false) || this.type == TokenType.CONST)) {
                    if (singleStatement) {
                        throw this.error(AbstractParser.message("expected.stmt", this.type.getName() + " declaration"), this.token);
                    }
                    this.variableStatement(this.type);
                    break;
                }
                if (this.type == TokenType.CLASS && this.isES6()) {
                    if (singleStatement) {
                        throw this.error(AbstractParser.message("expected.stmt", "class declaration"), this.token);
                    }
                    this.classDeclaration(false);
                    break;
                }
                if (this.env._const_as_var && this.type == TokenType.CONST) {
                    this.variableStatement(TokenType.VAR);
                    break;
                }
                if (this.type == TokenType.IDENT || this.isNonStrictModeIdent()) {
                    if (this.T(this.k + 1) == TokenType.COLON) {
                        this.labelStatement();
                        return;
                    }
                    if ((reparseFlags & 0x40) != 0) {
                        ident = (String)this.getValue();
                        propertyToken = this.token;
                        propertyLine = this.line;
                        if (GET_NAME.equals(ident)) {
                            this.next();
                            this.addPropertyFunctionStatement(this.propertyGetterFunction(propertyToken, propertyLine));
                            return;
                        }
                        if (SET_NAME.equals(ident)) {
                            this.next();
                            this.addPropertyFunctionStatement(this.propertySetterFunction(propertyToken, propertyLine));
                            return;
                        }
                    }
                }
                if ((reparseFlags & 0x80) != 0 && (this.type == TokenType.IDENT || this.type == TokenType.LBRACKET || this.isNonStrictModeIdent())) {
                    ident = (String)this.getValue();
                    propertyToken = this.token;
                    propertyLine = this.line;
                    Expression propertyKey = this.propertyName();
                    int flags = CONSTRUCTOR_NAME.equals(ident) ? 0x400000 : 0x200000;
                    this.addPropertyFunctionStatement(this.propertyMethodFunction(propertyKey, propertyToken, propertyLine, false, flags, false));
                    return;
                }
                this.expressionStatement();
            }
        }
    }

    private void addPropertyFunctionStatement(PropertyFunction propertyFunction) {
        FunctionNode fn = propertyFunction.functionNode;
        this.functionDeclarations.add(new ExpressionStatement(fn.getLineNumber(), fn.getToken(), this.finish, fn));
    }

    private ClassNode classDeclaration(boolean isDefault) {
        int classLineNumber = this.line;
        ClassNode classExpression = this.classExpression(!isDefault);
        if (!isDefault) {
            VarNode classVar = new VarNode(classLineNumber, classExpression.getToken(), classExpression.getIdent().getFinish(), classExpression.getIdent(), classExpression, 2);
            this.appendStatement(classVar);
        }
        return classExpression;
    }

    private ClassNode classExpression(boolean isStatement) {
        assert (this.type == TokenType.CLASS);
        int classLineNumber = this.line;
        long classToken = this.token;
        this.next();
        IdentNode className = null;
        if (isStatement || this.type == TokenType.IDENT) {
            className = this.getIdent();
        }
        return this.classTail(classLineNumber, classToken, className, isStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ClassNode classTail(int classLineNumber, long classToken, IdentNode className, boolean isStatement) {
        boolean oldStrictMode = this.isStrictMode;
        this.isStrictMode = true;
        try {
            Expression classHeritage = null;
            if (this.type == TokenType.EXTENDS) {
                this.next();
                classHeritage = this.leftHandSideExpression();
            }
            this.expect(TokenType.LBRACE);
            PropertyNode constructor = null;
            ArrayList<PropertyNode> classElements = new ArrayList<PropertyNode>();
            HashMap<ClassElementKey, Integer> keyToIndexMap = new HashMap<ClassElementKey, Integer>();
            while (true) {
                PropertyNode classElement;
                if (this.type == TokenType.SEMICOLON) {
                    this.next();
                    continue;
                }
                if (this.type == TokenType.RBRACE) break;
                long classElementToken = this.token;
                boolean isStatic = false;
                if (this.type == TokenType.STATIC) {
                    isStatic = true;
                    this.next();
                }
                boolean generator = false;
                if (this.isES6() && this.type == TokenType.MUL) {
                    generator = true;
                    this.next();
                }
                if ((classElement = this.methodDefinition(isStatic, classHeritage != null, generator)).isComputed()) {
                    classElements.add(classElement);
                    continue;
                }
                if (!classElement.isStatic() && CONSTRUCTOR_NAME.equals(classElement.getKeyName())) {
                    if (constructor == null) {
                        constructor = classElement;
                        continue;
                    }
                    throw this.error(AbstractParser.message("multiple.constructors", new String[0]), classElementToken);
                }
                ClassElementKey key = new ClassElementKey(classElement.isStatic(), classElement.getKeyName());
                Integer existing = (Integer)keyToIndexMap.get(key);
                if (existing == null) {
                    keyToIndexMap.put(key, classElements.size());
                    classElements.add(classElement);
                    continue;
                }
                PropertyNode existingProperty = classElements.get(existing);
                Expression value = classElement.getValue();
                FunctionNode getter = classElement.getGetter();
                FunctionNode setter = classElement.getSetter();
                if (value != null || existingProperty.getValue() != null) {
                    keyToIndexMap.put(key, classElements.size());
                    classElements.add(classElement);
                    continue;
                }
                if (getter != null) {
                    assert (existingProperty.getGetter() != null || existingProperty.getSetter() != null);
                    classElements.set(existing, existingProperty.setGetter(getter));
                    continue;
                }
                if (setter == null) continue;
                assert (existingProperty.getGetter() != null || existingProperty.getSetter() != null);
                classElements.set(existing, existingProperty.setSetter(setter));
            }
            long lastToken = this.token;
            this.expect(TokenType.RBRACE);
            if (constructor == null) {
                constructor = this.createDefaultClassConstructor(classLineNumber, classToken, lastToken, className, classHeritage != null);
            }
            classElements.trimToSize();
            ClassNode classNode = new ClassNode(classLineNumber, classToken, this.finish, className, classHeritage, constructor, classElements, isStatement);
            return classNode;
        }
        finally {
            this.isStrictMode = oldStrictMode;
        }
    }

    private PropertyNode createDefaultClassConstructor(int classLineNumber, long classToken, long lastToken, IdentNode className, boolean subclass) {
        List<IdentNode> parameters;
        List<Statement> statements;
        int ctorFinish = this.finish;
        long identToken = Token.recast(classToken, TokenType.IDENT);
        if (subclass) {
            IdentNode superIdent = this.createIdentNode(identToken, ctorFinish, TokenType.SUPER.getName()).setIsDirectSuper();
            IdentNode argsIdent = this.createIdentNode(identToken, ctorFinish, "args").setIsRestParameter();
            UnaryNode spreadArgs = new UnaryNode(Token.recast(classToken, TokenType.SPREAD_ARGUMENT), argsIdent);
            CallNode superCall = new CallNode(classLineNumber, classToken, ctorFinish, superIdent, Collections.singletonList(spreadArgs), false);
            statements = Collections.singletonList(new ExpressionStatement(classLineNumber, classToken, ctorFinish, superCall));
            parameters = Collections.singletonList(argsIdent);
        } else {
            statements = Collections.emptyList();
            parameters = Collections.emptyList();
        }
        Block body = new Block(classToken, ctorFinish, 32, statements);
        IdentNode ctorName = className != null ? className : this.createIdentNode(identToken, ctorFinish, CONSTRUCTOR_NAME);
        ParserContextFunctionNode function = this.createParserContextFunctionNode(ctorName, classToken, FunctionNode.Kind.NORMAL, classLineNumber, parameters);
        function.setLastToken(lastToken);
        function.setFlag(0x200000);
        function.setFlag(0x400000);
        if (subclass) {
            function.setFlag(0x800000);
            function.setFlag(524288);
        }
        if (className == null) {
            function.setFlag(1);
        }
        return new PropertyNode(classToken, ctorFinish, ctorName, this.createFunctionNode(function, classToken, ctorName, parameters, FunctionNode.Kind.NORMAL, classLineNumber, body), null, null, false, false);
    }

    private PropertyNode methodDefinition(boolean isStatic, boolean subclass, boolean generator) {
        long methodToken = this.token;
        int methodLine = this.line;
        boolean computed = this.type == TokenType.LBRACKET;
        boolean isIdent = this.type == TokenType.IDENT;
        Expression propertyName = this.propertyName();
        int flags = 0x200000;
        if (!computed) {
            String name = ((PropertyKey)((Object)propertyName)).getPropertyName();
            if (!generator && isIdent && this.type != TokenType.LPAREN && name.equals(GET_NAME)) {
                PropertyFunction methodDefinition = this.propertyGetterFunction(methodToken, methodLine, flags);
                this.verifyAllowedMethodName(methodDefinition.key, isStatic, methodDefinition.computed, false, true);
                return new PropertyNode(methodToken, this.finish, methodDefinition.key, null, methodDefinition.functionNode, null, isStatic, methodDefinition.computed);
            }
            if (!generator && isIdent && this.type != TokenType.LPAREN && name.equals(SET_NAME)) {
                PropertyFunction methodDefinition = this.propertySetterFunction(methodToken, methodLine, flags);
                this.verifyAllowedMethodName(methodDefinition.key, isStatic, methodDefinition.computed, false, true);
                return new PropertyNode(methodToken, this.finish, methodDefinition.key, null, null, methodDefinition.functionNode, isStatic, methodDefinition.computed);
            }
            if (!isStatic && !generator && name.equals(CONSTRUCTOR_NAME)) {
                flags |= 0x400000;
                if (subclass) {
                    flags |= 0x800000;
                }
            }
            this.verifyAllowedMethodName(propertyName, isStatic, false, generator, false);
        }
        PropertyFunction methodDefinition = this.propertyMethodFunction(propertyName, methodToken, methodLine, generator, flags, computed);
        return new PropertyNode(methodToken, this.finish, methodDefinition.key, methodDefinition.functionNode, null, null, isStatic, computed);
    }

    private void verifyAllowedMethodName(Expression key, boolean isStatic, boolean computed, boolean generator, boolean accessor) {
        if (!computed) {
            if (!isStatic && generator && ((PropertyKey)((Object)key)).getPropertyName().equals(CONSTRUCTOR_NAME)) {
                throw this.error(AbstractParser.message("generator.constructor", new String[0]), key.getToken());
            }
            if (!isStatic && accessor && ((PropertyKey)((Object)key)).getPropertyName().equals(CONSTRUCTOR_NAME)) {
                throw this.error(AbstractParser.message("accessor.constructor", new String[0]), key.getToken());
            }
            if (isStatic && ((PropertyKey)((Object)key)).getPropertyName().equals("prototype")) {
                throw this.error(AbstractParser.message("static.prototype.method", new String[0]), key.getToken());
            }
        }
    }

    private void block() {
        this.appendStatement(new BlockStatement(this.line, this.getBlock(true)));
    }

    private void statementList() {
        block3: while (this.type != TokenType.EOF) {
            switch (this.type) {
                case RBRACE: 
                case CASE: 
                case DEFAULT: {
                    break block3;
                }
                default: {
                    this.statement();
                    continue block3;
                }
            }
        }
    }

    private void verifyIdent(IdentNode ident, String contextString) {
        this.verifyStrictIdent(ident, contextString);
        this.checkEscapedKeyword(ident);
    }

    private void verifyStrictIdent(IdentNode ident, String contextString) {
        if (this.isStrictMode) {
            switch (ident.getName()) {
                case "eval": 
                case "arguments": {
                    throw this.error(AbstractParser.message("strict.name", ident.getName(), contextString), ident.getToken());
                }
            }
            if (ident.isFutureStrictName()) {
                throw this.error(AbstractParser.message("strict.name", ident.getName(), contextString), ident.getToken());
            }
        }
    }

    private void checkEscapedKeyword(IdentNode ident) {
        TokenType tokenType;
        if (this.isES6() && ident.containsEscapes() && (tokenType = TokenLookup.lookupKeyword(ident.getName().toCharArray(), 0, ident.getName().length())) != TokenType.IDENT && (tokenType.getKind() != TokenKind.FUTURESTRICT || this.isStrictMode)) {
            throw this.error(AbstractParser.message("keyword.escaped.character", new String[0]), ident.getToken());
        }
    }

    private void variableStatement(TokenType varType) {
        this.variableDeclarationList(varType, true, -1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ForVariableDeclarationListResult variableDeclarationList(TokenType varType, boolean isStatement, int sourceOrder) {
        ForVariableDeclarationListResult forResult;
        assert (varType == TokenType.VAR || varType == TokenType.LET || varType == TokenType.CONST);
        int varLine = this.line;
        long varToken = this.token;
        this.next();
        int varFlags = 0;
        if (varType == TokenType.LET) {
            varFlags |= 1;
        } else if (varType == TokenType.CONST) {
            varFlags |= 2;
        }
        ForVariableDeclarationListResult forVariableDeclarationListResult = forResult = isStatement ? null : new ForVariableDeclarationListResult();
        while (true) {
            boolean isDestructuring;
            if (this.type == TokenType.YIELD && this.inGeneratorFunction()) {
                this.expect(TokenType.IDENT);
            }
            String contextString = "variable name";
            Expression binding = this.bindingIdentifierOrPattern("variable name");
            boolean bl = isDestructuring = !(binding instanceof IdentNode);
            if (isDestructuring) {
                int finalVarFlags = varFlags;
                this.verifyDestructuringBindingPattern(binding, identNode -> {
                    this.verifyIdent((IdentNode)identNode, "variable name");
                    if (!this.env._parse_only) {
                        VarNode var = new VarNode(varLine, varToken, sourceOrder, identNode.getFinish(), identNode.setIsDeclaredHere(), null, finalVarFlags);
                        this.appendStatement(var);
                    }
                });
            }
            Expression init = null;
            if (this.type == TokenType.ASSIGN) {
                if (!isStatement) {
                    forResult.recordDeclarationWithInitializer(varToken);
                }
                this.next();
                if (!isDestructuring) {
                    this.defaultNames.push(binding);
                }
                try {
                    init = this.assignmentExpression(!isStatement);
                }
                finally {
                    if (!isDestructuring) {
                        this.defaultNames.pop();
                    }
                }
            } else if (isStatement) {
                if (isDestructuring) {
                    throw this.error(AbstractParser.message("missing.destructuring.assignment", new String[0]), this.token);
                }
                if (varType == TokenType.CONST) {
                    throw this.error(AbstractParser.message("missing.const.assignment", ((IdentNode)binding).getName()));
                }
            }
            if (!isDestructuring) {
                IdentNode name;
                assert (init != null || varType != TokenType.CONST || !isStatement);
                IdentNode ident = (IdentNode)binding;
                if (!isStatement && ident.getName().equals("let")) {
                    throw this.error(AbstractParser.message("let.binding.for", new String[0]));
                }
                IdentNode identNode2 = name = varType == TokenType.LET || varType == TokenType.CONST ? ident.setIsDeclaredHere() : ident;
                if (!isStatement) {
                    if (init == null && varType == TokenType.CONST) {
                        forResult.recordMissingAssignment(name);
                    }
                    forResult.addBinding(new IdentNode(name));
                }
                VarNode var = new VarNode(varLine, varToken, sourceOrder, this.finish, name, init, varFlags);
                this.appendStatement(var);
            } else {
                assert (init != null || !isStatement);
                if (init != null) {
                    Expression assignment = this.verifyAssignment(Token.recast(varToken, TokenType.ASSIGN), binding, init);
                    if (isStatement) {
                        this.appendStatement(new ExpressionStatement(varLine, assignment.getToken(), this.finish, assignment, varType));
                    } else {
                        forResult.addAssignment(assignment);
                        forResult.addBinding(assignment);
                    }
                } else if (!isStatement) {
                    forResult.recordMissingAssignment(binding);
                    forResult.addBinding(binding);
                }
            }
            if (this.type != TokenType.COMMARIGHT) break;
            this.next();
        }
        if (isStatement) {
            this.endOfLine();
        }
        return forResult;
    }

    private boolean isBindingIdentifier() {
        return this.type == TokenType.IDENT || this.isNonStrictModeIdent();
    }

    private IdentNode bindingIdentifier(String contextString) {
        IdentNode name = this.getIdent();
        this.verifyIdent(name, contextString);
        return name;
    }

    private Expression bindingPattern() {
        if (this.type == TokenType.LBRACKET) {
            return this.arrayLiteral();
        }
        if (this.type == TokenType.LBRACE) {
            return this.objectLiteral();
        }
        throw this.error(AbstractParser.message("expected.binding", new String[0]));
    }

    private Expression bindingIdentifierOrPattern(String contextString) {
        if (this.isBindingIdentifier() || !this.isES6()) {
            return this.bindingIdentifier(contextString);
        }
        return this.bindingPattern();
    }

    private void verifyDestructuringBindingPattern(Expression pattern, final Consumer<IdentNode> identifierCallback) {
        assert (pattern instanceof BinaryNode && pattern.isTokenType(TokenType.ASSIGN) || pattern instanceof ObjectNode || pattern instanceof LiteralNode.ArrayLiteralNode);
        pattern.accept(new VerifyDestructuringPatternNodeVisitor(new LexicalContext()){

            @Override
            protected void verifySpreadElement(Expression lvalue) {
                if (!(lvalue instanceof IdentNode)) {
                    if (Parser.this.isDestructuringLhs(lvalue)) {
                        Parser.this.verifyDestructuringBindingPattern(lvalue, identifierCallback);
                    } else {
                        throw Parser.this.error("Expected a valid binding identifier", lvalue.getToken());
                    }
                }
            }

            @Override
            public boolean enterIdentNode(IdentNode identNode) {
                identifierCallback.accept(identNode);
                return false;
            }

            @Override
            protected boolean enterDefault(Node node) {
                throw Parser.this.error(String.format("unexpected node in BindingPattern: %s", node));
            }
        });
    }

    private void emptyStatement() {
        if (this.env._empty_statements) {
            this.appendStatement(new EmptyNode(this.line, this.token, Token.descPosition(this.token) + Token.descLength(this.token)));
        }
        this.next();
    }

    private void expressionStatement() {
        int expressionLine = this.line;
        long expressionToken = this.token;
        Expression expression = this.expression();
        if (expression != null) {
            ExpressionStatement expressionStatement = new ExpressionStatement(expressionLine, expressionToken, this.finish, expression);
            this.appendStatement(expressionStatement);
        } else {
            this.expect(null);
        }
        this.endOfLine();
    }

    private void ifStatement() {
        int ifLine = this.line;
        long ifToken = this.token;
        this.next();
        this.expect(TokenType.LPAREN);
        Expression test = this.expression();
        this.expect(TokenType.RPAREN);
        Block pass = this.getStatement();
        Block fail = null;
        if (this.type == TokenType.ELSE) {
            this.next();
            fail = this.getStatement();
        }
        this.appendStatement(new IfNode(ifLine, ifToken, fail != null ? fail.getFinish() : pass.getFinish(), test, pass, fail));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void forStatement() {
        long forToken = this.token;
        int forLine = this.line;
        int forStart = Token.descPosition(forToken);
        ParserContextBlockNode outer = this.useBlockScope() ? this.newBlock() : null;
        ParserContextLoopNode forNode = new ParserContextLoopNode();
        this.lc.push(forNode);
        Node body = null;
        Expression init = null;
        JoinPredecessorExpression test = null;
        JoinPredecessorExpression modify = null;
        ForVariableDeclarationListResult varDeclList = null;
        int flags = 0;
        boolean isForOf = false;
        try {
            this.next();
            if (!this.env._no_syntax_extensions && this.type == TokenType.IDENT && "each".equals(this.getValue())) {
                flags |= 2;
                this.next();
            }
            this.expect(TokenType.LPAREN);
            Object varType = null;
            switch (this.type) {
                case VAR: {
                    varType = this.type;
                    varDeclList = this.variableDeclarationList((TokenType)((Object)varType), false, forStart);
                    break;
                }
                case SEMICOLON: {
                    break;
                }
                default: {
                    if (this.useBlockScope() && (this.type == TokenType.LET && this.lookaheadIsLetDeclaration(true) || this.type == TokenType.CONST)) {
                        flags |= 8;
                        varType = this.type;
                        varDeclList = this.variableDeclarationList((TokenType)((Object)varType), false, forStart);
                        break;
                    }
                    if (this.env._const_as_var && this.type == TokenType.CONST) {
                        varType = TokenType.VAR;
                        varDeclList = this.variableDeclarationList((TokenType)((Object)varType), false, forStart);
                        break;
                    }
                    init = this.expression(this.unaryExpression(), TokenType.COMMARIGHT.getPrecedence(), true);
                }
            }
            switch (this.type) {
                case SEMICOLON: {
                    if (varDeclList != null) {
                        assert (init == null);
                        init = varDeclList.init;
                        if (varDeclList.missingAssignment != null) {
                            if (varDeclList.missingAssignment instanceof IdentNode) {
                                throw this.error(AbstractParser.message("missing.const.assignment", ((IdentNode)varDeclList.missingAssignment).getName()));
                            }
                            throw this.error(AbstractParser.message("missing.destructuring.assignment", new String[0]), varDeclList.missingAssignment.getToken());
                        }
                    }
                    if ((flags & 2) != 0) {
                        throw this.error(AbstractParser.message("for.each.without.in", new String[0]), this.token);
                    }
                    this.expect(TokenType.SEMICOLON);
                    if (this.type != TokenType.SEMICOLON) {
                        test = this.joinPredecessorExpression();
                    }
                    this.expect(TokenType.SEMICOLON);
                    if (this.type == TokenType.RPAREN) break;
                    modify = this.joinPredecessorExpression();
                    break;
                }
                case IDENT: {
                    if (this.isES6() && "of".equals(this.getValue())) {
                        isForOf = true;
                    } else {
                        this.expect(TokenType.SEMICOLON);
                        break;
                    }
                }
                case IN: {
                    flags |= isForOf ? 4 : 1;
                    test = new JoinPredecessorExpression();
                    if (varDeclList != null) {
                        if (varDeclList.secondBinding != null) {
                            throw this.error(AbstractParser.message("many.vars.in.for.in.loop", isForOf ? "of" : "in"), varDeclList.secondBinding.getToken());
                        }
                        if (varDeclList.declarationWithInitializerToken != 0L && (this.isStrictMode || this.type != TokenType.IN || varType != TokenType.VAR || varDeclList.init != null)) {
                            throw this.error(AbstractParser.message("for.in.loop.initializer", isForOf ? "of" : "in"), varDeclList.declarationWithInitializerToken);
                        }
                        init = varDeclList.firstBinding;
                        assert (init instanceof IdentNode || this.isDestructuringLhs(init));
                    } else {
                        assert (init != null) : "for..in/of init expression can not be null here";
                        if (!this.checkValidLValue(init, isForOf ? "for-of iterator" : "for-in iterator")) {
                            throw this.error(AbstractParser.message("not.lvalue.for.in.loop", isForOf ? "of" : "in"), init.getToken());
                        }
                    }
                    this.next();
                    modify = isForOf ? new JoinPredecessorExpression(this.assignmentExpression(false)) : this.joinPredecessorExpression();
                    break;
                }
                default: {
                    this.expect(TokenType.SEMICOLON);
                }
            }
            this.expect(TokenType.RPAREN);
            body = this.getStatement();
            this.lc.pop(forNode);
        }
        catch (Throwable throwable) {
            this.lc.pop(forNode);
            for (Statement var : forNode.getStatements()) {
                assert (var instanceof VarNode);
                this.appendStatement(var);
            }
            if (body != null) {
                this.appendStatement(new ForNode(forLine, forToken, body.getFinish(), (Block)body, forNode.getFlags() | flags, init, test, modify));
            }
            if (outer != null) {
                this.restoreBlock(outer);
                if (body != null) {
                    ArrayList<Statement> statements = new ArrayList<Statement>();
                    for (Statement var : outer.getStatements()) {
                        if (var instanceof VarNode && !((VarNode)var).isBlockScoped()) {
                            this.appendStatement(var);
                            continue;
                        }
                        statements.add(var);
                    }
                    this.appendStatement(new BlockStatement(forLine, new Block(outer.getToken(), body.getFinish(), statements)));
                }
            }
            throw throwable;
        }
        for (Statement var : forNode.getStatements()) {
            assert (var instanceof VarNode);
            this.appendStatement(var);
        }
        if (body != null) {
            this.appendStatement(new ForNode(forLine, forToken, body.getFinish(), (Block)body, forNode.getFlags() | flags, init, test, modify));
        }
        if (outer != null) {
            this.restoreBlock(outer);
            if (body != null) {
                ArrayList<Statement> statements = new ArrayList<Statement>();
                for (Statement var : outer.getStatements()) {
                    if (var instanceof VarNode && !((VarNode)var).isBlockScoped()) {
                        this.appendStatement(var);
                        continue;
                    }
                    statements.add(var);
                }
                this.appendStatement(new BlockStatement(forLine, new Block(outer.getToken(), body.getFinish(), statements)));
            }
        }
    }

    private boolean checkValidLValue(Expression init, String contextString) {
        if (init instanceof IdentNode) {
            if (!Parser.checkIdentLValue((IdentNode)init)) {
                return false;
            }
            this.verifyIdent((IdentNode)init, contextString);
            return true;
        }
        if (init instanceof AccessNode || init instanceof IndexNode) {
            return true;
        }
        if (this.isDestructuringLhs(init)) {
            this.verifyDestructuringAssignmentPattern(init, contextString);
            return true;
        }
        return false;
    }

    private boolean lookaheadIsLetDeclaration(boolean ofContextualKeyword) {
        assert (this.type == TokenType.LET);
        int i = 1;
        while (true) {
            TokenType t = this.T(this.k + i);
            switch (t) {
                case EOL: 
                case COMMENT: {
                    break;
                }
                case IDENT: {
                    if (ofContextualKeyword && this.isES6() && "of".equals(this.getValue(this.getToken(this.k + i)))) {
                        return false;
                    }
                }
                case LBRACE: 
                case LBRACKET: {
                    return true;
                }
                default: {
                    return !this.isStrictMode && t.getKind() == TokenKind.FUTURESTRICT;
                }
            }
            ++i;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void whileStatement() {
        Block body;
        JoinPredecessorExpression test;
        long whileToken = this.token;
        int whileLine = this.line;
        this.next();
        ParserContextLoopNode whileNode = new ParserContextLoopNode();
        this.lc.push(whileNode);
        try {
            this.expect(TokenType.LPAREN);
            test = this.joinPredecessorExpression();
            this.expect(TokenType.RPAREN);
            body = this.getStatement();
        }
        finally {
            this.lc.pop(whileNode);
        }
        this.appendStatement(new WhileNode(whileLine, whileToken, body.getFinish(), false, test, body));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doStatement() {
        JoinPredecessorExpression test;
        int doLine;
        Block body;
        long doToken = this.token;
        this.next();
        ParserContextLoopNode doWhileNode = new ParserContextLoopNode();
        this.lc.push(doWhileNode);
        try {
            body = this.getStatement();
            this.expect(TokenType.WHILE);
            this.expect(TokenType.LPAREN);
            doLine = this.line;
            test = this.joinPredecessorExpression();
            this.expect(TokenType.RPAREN);
            if (this.type == TokenType.SEMICOLON) {
                this.endOfLine();
            }
        }
        finally {
            this.lc.pop(doWhileNode);
        }
        this.appendStatement(new WhileNode(doLine, doToken, this.finish, true, test, body));
    }

    private void continueStatement() {
        int continueLine = this.line;
        long continueToken = this.token;
        this.nextOrEOL();
        ParserContextLabelNode labelNode = null;
        switch (this.type) {
            case EOF: 
            case EOL: 
            case SEMICOLON: 
            case RBRACE: {
                break;
            }
            default: {
                IdentNode ident = this.getIdent();
                labelNode = this.lc.findLabel(ident.getName());
                if (labelNode != null) break;
                throw this.error(AbstractParser.message("undefined.label", ident.getName()), ident.getToken());
            }
        }
        String labelName = labelNode == null ? null : labelNode.getLabelName();
        ParserContextLoopNode targetNode = this.lc.getContinueTo(labelName);
        if (targetNode == null) {
            throw this.error(AbstractParser.message("illegal.continue.stmt", new String[0]), continueToken);
        }
        this.endOfLine();
        this.appendStatement(new ContinueNode(continueLine, continueToken, this.finish, labelName));
    }

    private void breakStatement() {
        int breakLine = this.line;
        long breakToken = this.token;
        this.nextOrEOL();
        ParserContextLabelNode labelNode = null;
        switch (this.type) {
            case EOF: 
            case EOL: 
            case SEMICOLON: 
            case RBRACE: {
                break;
            }
            default: {
                IdentNode ident = this.getIdent();
                labelNode = this.lc.findLabel(ident.getName());
                if (labelNode != null) break;
                throw this.error(AbstractParser.message("undefined.label", ident.getName()), ident.getToken());
            }
        }
        String labelName = labelNode == null ? null : labelNode.getLabelName();
        ParserContextBreakableNode targetNode = this.lc.getBreakable(labelName);
        if (targetNode instanceof ParserContextBlockNode) {
            targetNode.setFlag(256);
        }
        if (targetNode == null) {
            throw this.error(AbstractParser.message("illegal.break.stmt", new String[0]), breakToken);
        }
        this.endOfLine();
        this.appendStatement(new BreakNode(breakLine, breakToken, this.finish, labelName));
    }

    private void returnStatement() {
        if (this.lc.getCurrentFunction().getKind() == FunctionNode.Kind.SCRIPT || this.lc.getCurrentFunction().getKind() == FunctionNode.Kind.MODULE) {
            throw this.error(AbstractParser.message("invalid.return", new String[0]));
        }
        int returnLine = this.line;
        long returnToken = this.token;
        this.nextOrEOL();
        Expression expression = null;
        switch (this.type) {
            case EOF: 
            case EOL: 
            case SEMICOLON: 
            case RBRACE: {
                break;
            }
            default: {
                expression = this.expression();
            }
        }
        this.endOfLine();
        this.appendStatement(new ReturnNode(returnLine, returnToken, this.finish, expression));
    }

    private Expression yieldExpression(boolean noIn) {
        Expression expression;
        assert (this.inGeneratorFunction());
        long yieldToken = this.token;
        assert (this.type == TokenType.YIELD);
        this.nextOrEOL();
        boolean yieldAsterisk = false;
        if (this.type == TokenType.MUL) {
            yieldAsterisk = true;
            yieldToken = Token.recast(yieldToken, TokenType.YIELD_STAR);
            this.next();
        }
        switch (this.type) {
            case EOF: 
            case EOL: 
            case SEMICOLON: 
            case RBRACE: 
            case RPAREN: 
            case RBRACKET: 
            case COMMARIGHT: 
            case COLON: {
                if (!yieldAsterisk) {
                    expression = Parser.newUndefinedLiteral(yieldToken, this.finish);
                    if (this.type != TokenType.EOL) break;
                    this.next();
                    break;
                }
            }
            default: {
                expression = this.assignmentExpression(noIn);
            }
        }
        return new UnaryNode(yieldToken, expression);
    }

    private static UnaryNode newUndefinedLiteral(long token, int finish) {
        return new UnaryNode(Token.recast(token, TokenType.VOID), LiteralNode.newInstance(token, finish, 0));
    }

    private void withStatement() {
        int withLine = this.line;
        long withToken = this.token;
        this.next();
        if (this.isStrictMode) {
            throw this.error(AbstractParser.message("strict.no.with", new String[0]), withToken);
        }
        this.expect(TokenType.LPAREN);
        Expression expression = this.expression();
        this.expect(TokenType.RPAREN);
        Block body = this.getStatement();
        this.appendStatement(new WithNode(withLine, withToken, this.finish, expression, body));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void switchStatement() {
        Expression expression;
        int switchLine = this.line;
        long switchToken = this.token;
        ParserContextBlockNode switchBlock = this.newBlock();
        this.next();
        ParserContextSwitchNode switchNode = new ParserContextSwitchNode();
        this.lc.push(switchNode);
        CaseNode defaultCase = null;
        ArrayList<CaseNode> cases = new ArrayList<CaseNode>();
        try {
            this.expect(TokenType.LPAREN);
            expression = this.expression();
            this.expect(TokenType.RPAREN);
            this.expect(TokenType.LBRACE);
            while (this.type != TokenType.RBRACE) {
                Expression caseExpression = null;
                long caseToken = this.token;
                switch (this.type) {
                    case CASE: {
                        this.next();
                        caseExpression = this.expression();
                        break;
                    }
                    case DEFAULT: {
                        if (defaultCase != null) {
                            throw this.error(AbstractParser.message("duplicate.default.in.switch", new String[0]));
                        }
                        this.next();
                        break;
                    }
                    default: {
                        this.expect(TokenType.CASE);
                    }
                }
                this.expect(TokenType.COLON);
                Block statements = this.getBlock(false);
                CaseNode caseNode = new CaseNode(caseToken, this.finish, caseExpression, statements);
                if (caseExpression == null) {
                    defaultCase = caseNode;
                }
                cases.add(caseNode);
            }
            this.next();
        }
        finally {
            this.lc.pop(switchNode);
            this.restoreBlock(switchBlock);
        }
        SwitchNode switchStatement = new SwitchNode(switchLine, switchToken, this.finish, expression, cases, defaultCase);
        this.appendStatement(new BlockStatement(switchLine, new Block(switchToken, this.finish, switchBlock.getFlags() | 0x10 | 0x80, switchStatement)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void labelStatement() {
        Block body;
        long labelToken = this.token;
        IdentNode ident = this.getIdent();
        this.expect(TokenType.COLON);
        if (this.lc.findLabel(ident.getName()) != null) {
            throw this.error(AbstractParser.message("duplicate.label", ident.getName()), labelToken);
        }
        ParserContextLabelNode labelNode = new ParserContextLabelNode(ident.getName());
        try {
            this.lc.push(labelNode);
            body = this.getStatement(true);
        }
        finally {
            assert (this.lc.peek() instanceof ParserContextLabelNode);
            this.lc.pop(labelNode);
        }
        this.appendStatement(new LabelNode(this.line, labelToken, this.finish, ident.getName(), body));
    }

    private void throwStatement() {
        int throwLine = this.line;
        long throwToken = this.token;
        this.nextOrEOL();
        Expression expression = null;
        switch (this.type) {
            case EOL: 
            case SEMICOLON: 
            case RBRACE: {
                break;
            }
            default: {
                expression = this.expression();
            }
        }
        if (expression == null) {
            throw this.error(AbstractParser.message("expected.operand", this.type.getNameOrType()));
        }
        this.endOfLine();
        this.appendStatement(new ThrowNode(throwLine, throwToken, this.finish, expression, false));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tryStatement() {
        int tryLine = this.line;
        long tryToken = this.token;
        this.next();
        int startLine = this.line;
        ParserContextBlockNode outer = this.newBlock();
        try {
            Block tryBody = this.getBlock(true);
            ArrayList<Block> catchBlocks = new ArrayList<Block>();
            while (this.type == TokenType.CATCH) {
                Expression ifExpression;
                boolean isDestructuring;
                int catchLine = this.line;
                long catchToken = this.token;
                this.next();
                this.expect(TokenType.LPAREN);
                String contextString = "catch argument";
                Expression exception = this.bindingIdentifierOrPattern("catch argument");
                boolean bl = isDestructuring = !(exception instanceof IdentNode);
                if (isDestructuring) {
                    this.verifyDestructuringBindingPattern(exception, identNode -> this.verifyIdent((IdentNode)identNode, "catch argument"));
                } else {
                    this.verifyIdent((IdentNode)exception, "catch argument");
                }
                if (!this.env._no_syntax_extensions && this.type == TokenType.IF) {
                    this.next();
                    ifExpression = this.expression();
                } else {
                    ifExpression = null;
                }
                this.expect(TokenType.RPAREN);
                ParserContextBlockNode catchBlock = this.newBlock();
                try {
                    Block catchBody = this.getBlock(true);
                    CatchNode catchNode = new CatchNode(catchLine, catchToken, this.finish, exception, ifExpression, catchBody, false);
                    this.appendStatement(catchNode);
                }
                finally {
                    this.restoreBlock(catchBlock);
                    catchBlocks.add(new Block(catchBlock.getToken(), this.finish, catchBlock.getFlags() | 0x10, catchBlock.getStatements()));
                }
                if (ifExpression != null) continue;
                break;
            }
            Block finallyStatements = null;
            if (this.type == TokenType.FINALLY) {
                this.next();
                finallyStatements = this.getBlock(true);
            }
            if (catchBlocks.isEmpty() && finallyStatements == null) {
                throw this.error(AbstractParser.message("missing.catch.or.finally", new String[0]), tryToken);
            }
            TryNode tryNode = new TryNode(tryLine, tryToken, this.finish, tryBody, catchBlocks, finallyStatements);
            assert (this.lc.peek() == outer);
            this.appendStatement(tryNode);
        }
        finally {
            this.restoreBlock(outer);
        }
        this.appendStatement(new BlockStatement(startLine, new Block(tryToken, this.finish, outer.getFlags() | 0x10, outer.getStatements())));
    }

    private void debuggerStatement() {
        int debuggerLine = this.line;
        long debuggerToken = this.token;
        this.next();
        this.endOfLine();
        this.appendStatement(new DebuggerNode(debuggerLine, debuggerToken, this.finish));
    }

    private Expression primaryExpression() {
        int primaryLine = this.line;
        long primaryToken = this.token;
        switch (this.type) {
            case THIS: {
                String name = this.type.getName();
                this.next();
                Parser.markThis(this.lc);
                return new IdentNode(primaryToken, this.finish, name);
            }
            case IDENT: {
                IdentNode ident = this.getIdent();
                if (ident == null) break;
                this.detectSpecialProperty(ident);
                this.checkEscapedKeyword(ident);
                return ident;
            }
            case OCTAL_LEGACY: {
                if (this.isStrictMode) {
                    throw this.error(AbstractParser.message("strict.no.octal", new String[0]), this.token);
                }
            }
            case STRING: 
            case ESCSTRING: 
            case DECIMAL: 
            case HEXADECIMAL: 
            case OCTAL: 
            case BINARY_NUMBER: 
            case FLOATING: 
            case REGEX: 
            case XML: {
                return this.getLiteral();
            }
            case EXECSTRING: {
                return this.execString(primaryLine, primaryToken);
            }
            case FALSE: {
                this.next();
                return LiteralNode.newInstance(primaryToken, this.finish, false);
            }
            case TRUE: {
                this.next();
                return LiteralNode.newInstance(primaryToken, this.finish, true);
            }
            case NULL: {
                this.next();
                return LiteralNode.newInstance(primaryToken, this.finish);
            }
            case LBRACKET: {
                return this.arrayLiteral();
            }
            case LBRACE: {
                return this.objectLiteral();
            }
            case LPAREN: {
                this.next();
                if (this.isES6()) {
                    if (this.type == TokenType.RPAREN) {
                        this.nextOrEOL();
                        this.expectDontAdvance(TokenType.ARROW);
                        return new ExpressionList(primaryToken, this.finish, Collections.emptyList());
                    }
                    if (this.type == TokenType.ELLIPSIS) {
                        IdentNode restParam = this.formalParameterList(false).get(0);
                        this.expectDontAdvance(TokenType.RPAREN);
                        this.nextOrEOL();
                        this.expectDontAdvance(TokenType.ARROW);
                        return new ExpressionList(primaryToken, this.finish, Collections.singletonList(restParam));
                    }
                }
                Expression expression = this.expression();
                this.expect(TokenType.RPAREN);
                return expression;
            }
            case TEMPLATE: 
            case TEMPLATE_HEAD: {
                return this.templateLiteral();
            }
            default: {
                if (this.lexer.scanLiteral(primaryToken, this.type, this.lineInfoReceiver)) {
                    this.next();
                    return this.getLiteral();
                }
                if (!this.isNonStrictModeIdent()) break;
                return this.getIdent();
            }
        }
        return null;
    }

    CallNode execString(int primaryLine, long primaryToken) {
        IdentNode execIdent = new IdentNode(primaryToken, this.finish, "$EXEC");
        this.next();
        this.expect(TokenType.LBRACE);
        List<Expression> arguments = Collections.singletonList(this.expression());
        this.expect(TokenType.RBRACE);
        return new CallNode(primaryLine, primaryToken, this.finish, execIdent, arguments, false);
    }

    private LiteralNode<Expression[]> arrayLiteral() {
        long arrayToken = this.token;
        this.next();
        ArrayList<Expression> elements = new ArrayList<Expression>();
        boolean elision = true;
        boolean hasSpread = false;
        block5: while (true) {
            long spreadToken = 0L;
            switch (this.type) {
                case RBRACKET: {
                    this.next();
                    break block5;
                }
                case COMMARIGHT: {
                    this.next();
                    if (elision) {
                        elements.add(null);
                    }
                    elision = true;
                    continue block5;
                }
                case ELLIPSIS: {
                    if (this.isES6()) {
                        hasSpread = true;
                        spreadToken = this.token;
                        this.next();
                    }
                }
                default: {
                    if (!elision) {
                        throw this.error(AbstractParser.message("expected.comma", this.type.getNameOrType()));
                    }
                    Expression expression = this.assignmentExpression(false);
                    if (expression != null) {
                        if (spreadToken != 0L) {
                            expression = new UnaryNode(Token.recast(spreadToken, TokenType.SPREAD_ARRAY), expression);
                        }
                        elements.add(expression);
                    } else {
                        this.expect(TokenType.RBRACKET);
                    }
                    elision = false;
                    continue block5;
                }
            }
            break;
        }
        return LiteralNode.newInstance(arrayToken, this.finish, elements, hasSpread, elision);
    }

    private ObjectNode objectLiteral() {
        long objectToken = this.token;
        this.next();
        ArrayList<PropertyNode> elements = new ArrayList<PropertyNode>();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        boolean commaSeen = true;
        block4: while (true) {
            switch (this.type) {
                case RBRACE: {
                    this.next();
                    break block4;
                }
                case COMMARIGHT: {
                    if (commaSeen) {
                        throw this.error(AbstractParser.message("expected.property.id", this.type.getNameOrType()));
                    }
                    this.next();
                    commaSeen = true;
                    continue block4;
                }
                default: {
                    if (!commaSeen) {
                        throw this.error(AbstractParser.message("expected.comma", this.type.getNameOrType()));
                    }
                    commaSeen = false;
                    PropertyNode property = this.propertyAssignment();
                    if (property.isComputed()) {
                        elements.add(property);
                        continue block4;
                    }
                    String key = property.getKeyName();
                    Integer existing = (Integer)map.get(key);
                    if (existing == null) {
                        map.put(key, elements.size());
                        elements.add(property);
                        continue block4;
                    }
                    PropertyNode existingProperty = (PropertyNode)elements.get(existing);
                    Expression value = property.getValue();
                    FunctionNode getter = property.getGetter();
                    FunctionNode setter = property.getSetter();
                    Expression prevValue = existingProperty.getValue();
                    FunctionNode prevGetter = existingProperty.getGetter();
                    FunctionNode prevSetter = existingProperty.getSetter();
                    if (!this.isES6()) {
                        this.checkPropertyRedefinition(property, value, getter, setter, prevValue, prevGetter, prevSetter);
                    } else if (property.getKey() instanceof IdentNode && ((IdentNode)property.getKey()).isProtoPropertyName() && existingProperty.getKey() instanceof IdentNode && ((IdentNode)existingProperty.getKey()).isProtoPropertyName()) {
                        throw this.error(AbstractParser.message("multiple.proto.key", new String[0]), property.getToken());
                    }
                    if (value != null || prevValue != null) {
                        map.put(key, elements.size());
                        elements.add(property);
                        continue block4;
                    }
                    if (getter != null) {
                        assert (prevGetter != null || prevSetter != null);
                        elements.set(existing, existingProperty.setGetter(getter));
                        continue block4;
                    }
                    if (setter == null) continue block4;
                    assert (prevGetter != null || prevSetter != null);
                    elements.set(existing, existingProperty.setSetter(setter));
                    continue block4;
                }
            }
            break;
        }
        return new ObjectNode(objectToken, this.finish, elements);
    }

    private void checkPropertyRedefinition(PropertyNode property, Expression value, FunctionNode getter, FunctionNode setter, Expression prevValue, FunctionNode prevGetter, FunctionNode prevSetter) {
        boolean isAccessor;
        if (this.isStrictMode && value != null && prevValue != null) {
            throw this.error(AbstractParser.message("property.redefinition", property.getKeyName()), property.getToken());
        }
        boolean isPrevAccessor = prevGetter != null || prevSetter != null;
        boolean bl = isAccessor = getter != null || setter != null;
        if (prevValue != null && isAccessor) {
            throw this.error(AbstractParser.message("property.redefinition", property.getKeyName()), property.getToken());
        }
        if (isPrevAccessor && value != null) {
            throw this.error(AbstractParser.message("property.redefinition", property.getKeyName()), property.getToken());
        }
        if (isAccessor && isPrevAccessor && (getter != null && prevGetter != null || setter != null && prevSetter != null)) {
            throw this.error(AbstractParser.message("property.redefinition", property.getKeyName()), property.getToken());
        }
    }

    private PropertyKey literalPropertyName() {
        switch (this.type) {
            case IDENT: {
                return this.getIdent().setIsPropertyName();
            }
            case OCTAL_LEGACY: {
                if (this.isStrictMode) {
                    throw this.error(AbstractParser.message("strict.no.octal", new String[0]), this.token);
                }
            }
            case STRING: 
            case ESCSTRING: 
            case DECIMAL: 
            case HEXADECIMAL: 
            case OCTAL: 
            case BINARY_NUMBER: 
            case FLOATING: {
                return this.getLiteral();
            }
        }
        return this.getIdentifierName().setIsPropertyName();
    }

    private Expression computedPropertyName() {
        this.expect(TokenType.LBRACKET);
        Expression expression = this.assignmentExpression(false);
        this.expect(TokenType.RBRACKET);
        return expression;
    }

    private Expression propertyName() {
        if (this.type == TokenType.LBRACKET && this.isES6()) {
            return this.computedPropertyName();
        }
        return (Expression)((Object)this.literalPropertyName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PropertyNode propertyAssignment() {
        Expression propertyValue;
        Expression propertyName;
        boolean isIdentifier;
        boolean computed;
        long propertyToken = this.token;
        int functionLine = this.line;
        boolean generator = false;
        if (this.type == TokenType.MUL && this.isES6()) {
            generator = true;
            this.next();
        }
        boolean bl = computed = this.type == TokenType.LBRACKET;
        if (this.type == TokenType.IDENT) {
            String ident = (String)this.expectValue(TokenType.IDENT);
            if (!(this.type == TokenType.COLON || this.type == TokenType.LPAREN && this.isES6())) {
                switch (ident) {
                    case "get": {
                        PropertyFunction getter = this.propertyGetterFunction(propertyToken, functionLine);
                        return new PropertyNode(propertyToken, this.finish, getter.key, null, getter.functionNode, null, false, getter.computed);
                    }
                    case "set": {
                        PropertyFunction setter = this.propertySetterFunction(propertyToken, functionLine);
                        return new PropertyNode(propertyToken, this.finish, setter.key, null, null, setter.functionNode, false, setter.computed);
                    }
                }
            }
            isIdentifier = true;
            IdentNode identNode = this.createIdentNode(propertyToken, this.finish, ident).setIsPropertyName();
            if (this.type == TokenType.COLON && ident.equals("__proto__")) {
                identNode = identNode.setIsProtoPropertyName();
            }
            propertyName = identNode;
        } else {
            isIdentifier = this.isNonStrictModeIdent();
            propertyName = this.propertyName();
        }
        if (generator) {
            this.expectDontAdvance(TokenType.LPAREN);
        }
        if (this.type == TokenType.LPAREN && this.isES6()) {
            propertyValue = this.propertyMethodFunction((Expression)propertyName, (long)propertyToken, (int)functionLine, (boolean)generator, (int)0x200000, (boolean)computed).functionNode;
        } else if (isIdentifier && (this.type == TokenType.COMMARIGHT || this.type == TokenType.RBRACE || this.type == TokenType.ASSIGN) && this.isES6()) {
            propertyValue = this.createIdentNode(propertyToken, this.finish, ((IdentNode)propertyName).getPropertyName());
            if (this.type == TokenType.ASSIGN) {
                long assignToken = this.token;
                this.next();
                Expression rhs = this.assignmentExpression(false);
                propertyValue = this.verifyAssignment(assignToken, propertyValue, rhs);
            }
        } else {
            this.expect(TokenType.COLON);
            this.defaultNames.push(propertyName);
            try {
                propertyValue = this.assignmentExpression(false);
            }
            finally {
                this.defaultNames.pop();
            }
        }
        return new PropertyNode(propertyToken, this.finish, propertyName, propertyValue, null, null, false, computed);
    }

    private PropertyFunction propertyGetterFunction(long getSetToken, int functionLine) {
        return this.propertyGetterFunction(getSetToken, functionLine, 0x200000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PropertyFunction propertyGetterFunction(long getSetToken, int functionLine, int flags) {
        Block functionBody;
        boolean computed = this.type == TokenType.LBRACKET;
        Expression propertyName = this.propertyName();
        String getterName = propertyName instanceof PropertyKey ? ((PropertyKey)((Object)propertyName)).getPropertyName() : this.getDefaultValidFunctionName(functionLine, false);
        IdentNode getNameNode = this.createIdentNode(propertyName.getToken(), this.finish, NameCodec.encode("get " + getterName));
        this.expect(TokenType.LPAREN);
        this.expect(TokenType.RPAREN);
        ParserContextFunctionNode functionNode = this.createParserContextFunctionNode(getNameNode, getSetToken, FunctionNode.Kind.GETTER, functionLine, Collections.emptyList());
        functionNode.setFlag(flags);
        if (computed) {
            functionNode.setFlag(1);
        }
        this.lc.push(functionNode);
        try {
            functionBody = this.functionBody(functionNode);
        }
        finally {
            this.lc.pop(functionNode);
        }
        FunctionNode function = this.createFunctionNode(functionNode, getSetToken, getNameNode, Collections.emptyList(), FunctionNode.Kind.GETTER, functionLine, functionBody);
        return new PropertyFunction(propertyName, function, computed);
    }

    private PropertyFunction propertySetterFunction(long getSetToken, int functionLine) {
        return this.propertySetterFunction(getSetToken, functionLine, 0x200000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PropertyFunction propertySetterFunction(long getSetToken, int functionLine, int flags) {
        Block functionBody;
        IdentNode argIdent;
        boolean computed = this.type == TokenType.LBRACKET;
        Expression propertyName = this.propertyName();
        String setterName = propertyName instanceof PropertyKey ? ((PropertyKey)((Object)propertyName)).getPropertyName() : this.getDefaultValidFunctionName(functionLine, false);
        IdentNode setNameNode = this.createIdentNode(propertyName.getToken(), this.finish, NameCodec.encode("set " + setterName));
        this.expect(TokenType.LPAREN);
        if (this.isBindingIdentifier()) {
            argIdent = this.getIdent();
            this.verifyIdent(argIdent, "setter argument");
        } else {
            argIdent = null;
        }
        this.expect(TokenType.RPAREN);
        ArrayList<IdentNode> parameters = new ArrayList<IdentNode>();
        if (argIdent != null) {
            parameters.add(argIdent);
        }
        ParserContextFunctionNode functionNode = this.createParserContextFunctionNode(setNameNode, getSetToken, FunctionNode.Kind.SETTER, functionLine, parameters);
        functionNode.setFlag(flags);
        if (computed) {
            functionNode.setFlag(1);
        }
        this.lc.push(functionNode);
        try {
            functionBody = this.functionBody(functionNode);
        }
        finally {
            this.lc.pop(functionNode);
        }
        FunctionNode function = this.createFunctionNode(functionNode, getSetToken, setNameNode, parameters, FunctionNode.Kind.SETTER, functionLine, functionBody);
        return new PropertyFunction(propertyName, function, computed);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PropertyFunction propertyMethodFunction(Expression key, long methodToken, int methodLine, boolean generator, int flags, boolean computed) {
        String methodName = key instanceof PropertyKey ? ((PropertyKey)((Object)key)).getPropertyName() : this.getDefaultValidFunctionName(methodLine, false);
        IdentNode methodNameNode = this.createIdentNode(key.getToken(), this.finish, methodName);
        FunctionNode.Kind functionKind = generator ? FunctionNode.Kind.GENERATOR : FunctionNode.Kind.NORMAL;
        ParserContextFunctionNode functionNode = this.createParserContextFunctionNode(methodNameNode, methodToken, functionKind, methodLine, null);
        functionNode.setFlag(flags);
        if (computed) {
            functionNode.setFlag(1);
        }
        this.lc.push(functionNode);
        try {
            List<IdentNode> parameters;
            ParserContextBlockNode parameterBlock = this.newBlock();
            try {
                this.expect(TokenType.LPAREN);
                parameters = this.formalParameterList(generator);
                functionNode.setParameters(parameters);
                this.expect(TokenType.RPAREN);
            }
            finally {
                this.restoreBlock(parameterBlock);
            }
            Block functionBody = this.functionBody(functionNode);
            functionBody = Parser.maybeWrapBodyInParameterBlock(functionBody, parameterBlock);
            FunctionNode function = this.createFunctionNode(functionNode, methodToken, methodNameNode, parameters, functionKind, methodLine, functionBody);
            PropertyFunction propertyFunction = new PropertyFunction(key, function, computed);
            return propertyFunction;
        }
        finally {
            this.lc.pop(functionNode);
        }
    }

    private Expression leftHandSideExpression() {
        List<Expression> arguments;
        int callLine = this.line;
        long callToken = this.token;
        Expression lhs = this.memberExpression();
        if (this.type == TokenType.LPAREN) {
            arguments = Parser.optimizeList(this.argumentList());
            if (lhs instanceof IdentNode) {
                this.detectSpecialFunction((IdentNode)lhs);
                this.checkEscapedKeyword((IdentNode)lhs);
            }
            lhs = new CallNode(callLine, callToken, this.finish, lhs, arguments, false);
        }
        block6: while (true) {
            callLine = this.line;
            callToken = this.token;
            switch (this.type) {
                case LPAREN: {
                    arguments = Parser.optimizeList(this.argumentList());
                    lhs = new CallNode(callLine, callToken, this.finish, lhs, arguments, false);
                    continue block6;
                }
                case LBRACKET: {
                    this.next();
                    Expression rhs = this.expression();
                    this.expect(TokenType.RBRACKET);
                    lhs = new IndexNode(callToken, this.finish, lhs, rhs);
                    continue block6;
                }
                case PERIOD: {
                    this.next();
                    IdentNode property = this.getIdentifierName();
                    lhs = new AccessNode(callToken, this.finish, lhs, property.getName());
                    continue block6;
                }
                case TEMPLATE: 
                case TEMPLATE_HEAD: {
                    arguments = this.templateLiteralArgumentList();
                    lhs = new CallNode(callLine, callToken, this.finish, lhs, arguments, false);
                    continue block6;
                }
            }
            break;
        }
        return lhs;
    }

    private Expression newExpression() {
        long newToken = this.token;
        this.next();
        if (this.type == TokenType.PERIOD && this.isES6()) {
            this.next();
            if (this.type == TokenType.IDENT && "target".equals(this.getValue())) {
                if (this.lc.getCurrentFunction().isProgram()) {
                    throw this.error(AbstractParser.message("new.target.in.function", new String[0]), this.token);
                }
                this.next();
                Parser.markNewTarget(this.lc);
                return new IdentNode(newToken, this.finish, "new.target");
            }
            throw this.error(AbstractParser.message("expected.target", new String[0]), this.token);
        }
        int callLine = this.line;
        Expression constructor = this.memberExpression();
        if (constructor == null) {
            return null;
        }
        ArrayList<Expression> arguments = this.type == TokenType.LPAREN ? this.argumentList() : new ArrayList();
        if (!this.env._no_syntax_extensions && this.type == TokenType.LBRACE) {
            arguments.add(this.objectLiteral());
        }
        CallNode callNode = new CallNode(callLine, constructor.getToken(), this.finish, constructor, Parser.optimizeList(arguments), true);
        return new UnaryNode(newToken, callNode);
    }

    private Expression memberExpression() {
        Expression lhs;
        boolean isSuper = false;
        block0 : switch (this.type) {
            case NEW: {
                lhs = this.newExpression();
                break;
            }
            case FUNCTION: {
                lhs = this.functionExpression(false, false);
                break;
            }
            case CLASS: {
                if (this.isES6()) {
                    lhs = this.classExpression(false);
                    break;
                }
            }
            case SUPER: {
                ParserContextFunctionNode currentFunction;
                if (this.isES6() && (currentFunction = this.getCurrentNonArrowFunction()).isMethod()) {
                    long identToken = Token.recast(this.token, TokenType.IDENT);
                    this.next();
                    lhs = this.createIdentNode(identToken, this.finish, TokenType.SUPER.getName());
                    switch (this.type) {
                        case LBRACKET: 
                        case PERIOD: {
                            this.getCurrentNonArrowFunction().setFlag(0x100000);
                            isSuper = true;
                            break block0;
                        }
                        case LPAREN: {
                            if (!currentFunction.isSubclassConstructor()) break;
                            lhs = ((IdentNode)lhs).setIsDirectSuper();
                            break block0;
                        }
                    }
                    throw this.error(AbstractParser.message("invalid.super", new String[0]), identToken);
                }
            }
            default: {
                lhs = this.primaryExpression();
            }
        }
        block15: while (true) {
            long callToken = this.token;
            switch (this.type) {
                case LBRACKET: {
                    this.next();
                    Expression index = this.expression();
                    this.expect(TokenType.RBRACKET);
                    lhs = new IndexNode(callToken, this.finish, lhs, index);
                    if (!isSuper) continue block15;
                    isSuper = false;
                    lhs = ((BaseNode)lhs).setIsSuper();
                    continue block15;
                }
                case PERIOD: {
                    if (lhs == null) {
                        throw this.error(AbstractParser.message("expected.operand", this.type.getNameOrType()));
                    }
                    this.next();
                    IdentNode property = this.getIdentifierName();
                    lhs = new AccessNode(callToken, this.finish, lhs, property.getName());
                    if (!isSuper) continue block15;
                    isSuper = false;
                    lhs = ((BaseNode)lhs).setIsSuper();
                    continue block15;
                }
                case TEMPLATE: 
                case TEMPLATE_HEAD: {
                    int callLine = this.line;
                    List<Expression> arguments = this.templateLiteralArgumentList();
                    lhs = new CallNode(callLine, callToken, this.finish, lhs, arguments, false);
                    continue block15;
                }
            }
            break;
        }
        return lhs;
    }

    private ArrayList<Expression> argumentList() {
        ArrayList<Expression> nodeList = new ArrayList<Expression>();
        this.next();
        boolean first = true;
        while (this.type != TokenType.RPAREN) {
            if (!first) {
                this.expect(TokenType.COMMARIGHT);
            } else {
                first = false;
            }
            long spreadToken = 0L;
            if (this.type == TokenType.ELLIPSIS && this.isES6()) {
                spreadToken = this.token;
                this.next();
            }
            Expression expression = this.assignmentExpression(false);
            if (spreadToken != 0L) {
                expression = new UnaryNode(Token.recast(spreadToken, TokenType.SPREAD_ARGUMENT), expression);
            }
            nodeList.add(expression);
        }
        this.expect(TokenType.RPAREN);
        return nodeList;
    }

    private static <T> List<T> optimizeList(ArrayList<T> list) {
        return List.copyOf(list);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Expression functionExpression(boolean isStatement, boolean topLevel) {
        Block functionBody;
        long functionToken = this.token;
        int functionLine = this.line;
        assert (this.type == TokenType.FUNCTION);
        this.next();
        boolean generator = false;
        if (this.type == TokenType.MUL && this.isES6()) {
            generator = true;
            this.next();
        }
        IdentNode name = null;
        if (this.isBindingIdentifier()) {
            if (this.type == TokenType.YIELD && (!isStatement && generator || isStatement && this.inGeneratorFunction())) {
                this.expect(TokenType.IDENT);
            }
            name = this.getIdent();
            this.verifyIdent(name, "function name");
        } else if (isStatement && this.env._no_syntax_extensions && this.reparsedFunction == null) {
            this.expect(TokenType.IDENT);
        }
        boolean isAnonymous = false;
        if (name == null) {
            String tmpName = this.getDefaultValidFunctionName(functionLine, isStatement);
            name = new IdentNode(functionToken, Token.descPosition(functionToken), tmpName);
            isAnonymous = true;
        }
        FunctionNode.Kind functionKind = generator ? FunctionNode.Kind.GENERATOR : FunctionNode.Kind.NORMAL;
        List<IdentNode> parameters = Collections.emptyList();
        ParserContextFunctionNode functionNode = this.createParserContextFunctionNode(name, functionToken, functionKind, functionLine, parameters);
        this.lc.push(functionNode);
        this.hideDefaultName();
        try {
            ParserContextBlockNode parameterBlock = this.newBlock();
            try {
                this.expect(TokenType.LPAREN);
                parameters = this.formalParameterList(generator);
                functionNode.setParameters(parameters);
                this.expect(TokenType.RPAREN);
            }
            finally {
                this.restoreBlock(parameterBlock);
            }
            functionBody = Parser.maybeWrapBodyInParameterBlock(this.functionBody(functionNode), parameterBlock);
        }
        finally {
            this.defaultNames.pop();
            this.lc.pop(functionNode);
        }
        if (isStatement) {
            if (topLevel || this.useBlockScope() || !this.isStrictMode && this.env._function_statement == ScriptEnvironment.FunctionStatementBehavior.ACCEPT) {
                functionNode.setFlag(2);
            } else {
                if (this.isStrictMode) {
                    throw this.error(JSErrorType.SYNTAX_ERROR, AbstractParser.message("strict.no.func.decl.here", new String[0]), functionToken);
                }
                if (this.env._function_statement == ScriptEnvironment.FunctionStatementBehavior.ERROR) {
                    throw this.error(JSErrorType.SYNTAX_ERROR, AbstractParser.message("no.func.decl.here", new String[0]), functionToken);
                }
                if (this.env._function_statement == ScriptEnvironment.FunctionStatementBehavior.WARNING) {
                    this.warning(JSErrorType.SYNTAX_ERROR, AbstractParser.message("no.func.decl.here.warn", new String[0]), functionToken);
                }
            }
            if (Parser.isArguments(name)) {
                this.lc.getCurrentFunction().setFlag(256);
            }
        }
        if (isAnonymous) {
            functionNode.setFlag(1);
        }
        this.verifyParameterList(parameters, functionNode);
        FunctionNode function = this.createFunctionNode(functionNode, functionToken, name, parameters, functionKind, functionLine, functionBody);
        if (isStatement) {
            if (isAnonymous) {
                this.appendStatement(new ExpressionStatement(functionLine, functionToken, this.finish, function));
                return function;
            }
            int varFlags = topLevel || !this.useBlockScope() ? 0 : 1;
            VarNode varNode = new VarNode(functionLine, functionToken, this.finish, name, function, varFlags);
            if (topLevel) {
                this.functionDeclarations.add(varNode);
            } else if (this.useBlockScope()) {
                this.prependStatement(varNode);
            } else {
                this.appendStatement(varNode);
            }
        }
        return function;
    }

    private void verifyParameterList(List<IdentNode> parameters, ParserContextFunctionNode functionNode) {
        IdentNode duplicateParameter = functionNode.getDuplicateParameterBinding();
        if (duplicateParameter != null) {
            if (functionNode.isStrict() || functionNode.getKind() == FunctionNode.Kind.ARROW || !functionNode.isSimpleParameterList()) {
                throw this.error(AbstractParser.message("strict.param.redefinition", duplicateParameter.getName()), duplicateParameter.getToken());
            }
            int arity = parameters.size();
            HashSet<String> parametersSet = new HashSet<String>(arity);
            for (int i = arity - 1; i >= 0; --i) {
                IdentNode parameter = parameters.get(i);
                String parameterName = parameter.getName();
                if (parametersSet.contains(parameterName)) {
                    parameterName = functionNode.uniqueName(parameterName);
                    long parameterToken = parameter.getToken();
                    parameters.set(i, new IdentNode(parameterToken, Token.descPosition(parameterToken), functionNode.uniqueName(parameterName)));
                }
                parametersSet.add(parameterName);
            }
        }
    }

    private static Block maybeWrapBodyInParameterBlock(Block functionBody, ParserContextBlockNode parameterBlock) {
        assert (functionBody.isFunctionBody());
        if (!parameterBlock.getStatements().isEmpty()) {
            parameterBlock.appendStatement(new BlockStatement(functionBody));
            return new Block(parameterBlock.getToken(), functionBody.getFinish(), (functionBody.getFlags() | 0x40) & 0xFFFFFFDF, parameterBlock.getStatements());
        }
        return functionBody;
    }

    private String getDefaultValidFunctionName(int functionLine, boolean isStatement) {
        String defaultFunctionName = this.getDefaultFunctionName();
        if (Parser.isValidIdentifier(defaultFunctionName)) {
            if (isStatement) {
                return CompilerConstants.ANON_FUNCTION_PREFIX.symbolName() + defaultFunctionName;
            }
            return defaultFunctionName;
        }
        return CompilerConstants.ANON_FUNCTION_PREFIX.symbolName() + functionLine;
    }

    private static boolean isValidIdentifier(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }
        for (int i = 1; i < name.length(); ++i) {
            if (Character.isJavaIdentifierPart(name.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private String getDefaultFunctionName() {
        if (!this.defaultNames.isEmpty()) {
            Object nameExpr = this.defaultNames.peek();
            if (nameExpr instanceof PropertyKey) {
                this.markDefaultNameUsed();
                return ((PropertyKey)nameExpr).getPropertyName();
            }
            if (nameExpr instanceof AccessNode) {
                this.markDefaultNameUsed();
                return ((AccessNode)nameExpr).getProperty();
            }
        }
        return null;
    }

    private void markDefaultNameUsed() {
        this.defaultNames.pop();
        this.hideDefaultName();
    }

    private void hideDefaultName() {
        this.defaultNames.push("");
    }

    private List<IdentNode> formalParameterList(boolean yield) {
        return this.formalParameterList(TokenType.RPAREN, yield);
    }

    private List<IdentNode> formalParameterList(TokenType endType, boolean yield) {
        ArrayList<IdentNode> parameters = new ArrayList<IdentNode>();
        boolean first = true;
        while (this.type != endType) {
            IdentNode ident;
            if (!first) {
                this.expect(TokenType.COMMARIGHT);
            } else {
                first = false;
            }
            boolean restParameter = false;
            if (this.type == TokenType.ELLIPSIS && this.isES6()) {
                this.next();
                restParameter = true;
            }
            if (this.type == TokenType.YIELD && yield) {
                this.expect(TokenType.IDENT);
            }
            long paramToken = this.token;
            int paramLine = this.line;
            String contextString = "function parameter";
            if (this.isBindingIdentifier() || restParameter || !this.isES6()) {
                ParserContextFunctionNode currentFunction;
                ident = this.bindingIdentifier("function parameter");
                if (restParameter) {
                    ident = ident.setIsRestParameter();
                    this.expectDontAdvance(endType);
                    parameters.add(ident);
                    break;
                }
                if (this.type == TokenType.ASSIGN && this.isES6()) {
                    this.next();
                    ident = ident.setIsDefaultParameter();
                    if (this.type == TokenType.YIELD && yield) {
                        this.expect(TokenType.IDENT);
                    }
                    Expression initializer = this.assignmentExpression(false);
                    ParserContextFunctionNode currentFunction2 = this.lc.getCurrentFunction();
                    if (currentFunction2 != null) {
                        if (this.env._parse_only) {
                            BinaryNode assignment = new BinaryNode(Token.recast(paramToken, TokenType.ASSIGN), ident, initializer);
                            currentFunction2.addParameterExpression(ident, assignment);
                        } else {
                            BinaryNode test = new BinaryNode(Token.recast(paramToken, TokenType.EQ_STRICT), ident, Parser.newUndefinedLiteral(paramToken, this.finish));
                            TernaryNode value = new TernaryNode(Token.recast(paramToken, TokenType.TERNARY), (Expression)test, new JoinPredecessorExpression(initializer), new JoinPredecessorExpression(ident));
                            BinaryNode assignment = new BinaryNode(Token.recast(paramToken, TokenType.ASSIGN), ident, value);
                            this.lc.getFunctionBody(currentFunction2).appendStatement(new ExpressionStatement(paramLine, assignment.getToken(), assignment.getFinish(), assignment));
                        }
                    }
                }
                if ((currentFunction = this.lc.getCurrentFunction()) != null) {
                    currentFunction.addParameterBinding(ident);
                    if (ident.isRestParameter() || ident.isDefaultParameter()) {
                        currentFunction.setSimpleParameterList(false);
                    }
                }
            } else {
                ParserContextFunctionNode currentFunction;
                Expression pattern = this.bindingPattern();
                ident = this.createIdentNode(paramToken, pattern.getFinish(), String.format("arguments[%d]", parameters.size())).setIsDestructuredParameter();
                this.verifyDestructuringParameterBindingPattern(pattern, paramToken, paramLine, "function parameter");
                Expression value = ident;
                if (this.type == TokenType.ASSIGN) {
                    this.next();
                    ident = ident.setIsDefaultParameter();
                    Expression initializer = this.assignmentExpression(false);
                    if (this.env._parse_only) {
                        value = initializer;
                    } else {
                        BinaryNode test = new BinaryNode(Token.recast(paramToken, TokenType.EQ_STRICT), ident, Parser.newUndefinedLiteral(paramToken, this.finish));
                        value = new TernaryNode(Token.recast(paramToken, TokenType.TERNARY), (Expression)test, new JoinPredecessorExpression(initializer), new JoinPredecessorExpression(ident));
                    }
                }
                if ((currentFunction = this.lc.getCurrentFunction()) != null) {
                    BinaryNode assignment = new BinaryNode(Token.recast(paramToken, TokenType.ASSIGN), pattern, value);
                    if (this.env._parse_only) {
                        if (ident.isDefaultParameter()) {
                            currentFunction.addParameterExpression(ident, assignment);
                        } else {
                            currentFunction.addParameterExpression(ident, pattern);
                        }
                    } else {
                        this.lc.getFunctionBody(currentFunction).appendStatement(new ExpressionStatement(paramLine, assignment.getToken(), assignment.getFinish(), assignment));
                    }
                }
            }
            parameters.add(ident);
        }
        parameters.trimToSize();
        return parameters;
    }

    private void verifyDestructuringParameterBindingPattern(Expression pattern, long paramToken, int paramLine, String contextString) {
        this.verifyDestructuringBindingPattern(pattern, identNode -> {
            this.verifyIdent((IdentNode)identNode, contextString);
            ParserContextFunctionNode currentFunction = this.lc.getCurrentFunction();
            if (currentFunction != null) {
                if (!this.env._parse_only) {
                    this.lc.getFunctionBody(currentFunction).appendStatement(new VarNode(paramLine, Token.recast(paramToken, TokenType.VAR), pattern.getFinish(), (IdentNode)identNode, null));
                }
                currentFunction.addParameterBinding((IdentNode)identNode);
                currentFunction.setSimpleParameterList(false);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Block functionBody(ParserContextFunctionNode functionNode) {
        RecompilableScriptFunctionData data;
        boolean parseBody;
        ParserState endParserState;
        int bodyFinish;
        long bodyToken;
        ParserContextBlockNode body;
        block19: {
            body = null;
            bodyToken = this.token;
            bodyFinish = 0;
            endParserState = null;
            try {
                body = this.newBlock();
                if (this.env._debug_scopes) {
                    Parser.markEval(this.lc);
                }
                assert (functionNode != null);
                int functionId = functionNode.getId();
                boolean bl = parseBody = this.reparsedFunction == null || functionId <= this.reparsedFunction.getFunctionNodeId();
                if (!(this.env._no_syntax_extensions && functionNode.getKind() != FunctionNode.Kind.ARROW || this.type == TokenType.LBRACE)) {
                    Expression expr = this.assignmentExpression(false);
                    long lastToken = this.previousToken;
                    functionNode.setLastToken(this.previousToken);
                    assert (this.lc.getCurrentBlock() == this.lc.getFunctionBody(functionNode));
                    int lastFinish = Token.descPosition(lastToken) + (Token.descType(lastToken) == TokenType.EOL ? 0 : Token.descLength(lastToken));
                    if (parseBody) {
                        functionNode.setFlag(0x4000000);
                        ReturnNode returnNode = new ReturnNode(functionNode.getLineNumber(), expr.getToken(), lastFinish, expr);
                        this.appendStatement(returnNode);
                    }
                    break block19;
                }
                this.expectDontAdvance(TokenType.LBRACE);
                if (parseBody || !this.skipFunctionBody(functionNode)) {
                    this.next();
                    List<Statement> prevFunctionDecls = this.functionDeclarations;
                    this.functionDeclarations = new ArrayList<Statement>();
                    try {
                        this.sourceElements(0);
                        this.addFunctionDeclarations(functionNode);
                    }
                    finally {
                        this.functionDeclarations = prevFunctionDecls;
                    }
                    if (parseBody) {
                        endParserState = new ParserState(Token.descPosition(this.token), this.line, this.linePosition);
                    }
                }
                bodyFinish = this.finish;
                functionNode.setLastToken(this.token);
                this.expect(TokenType.RBRACE);
            }
            finally {
                this.restoreBlock(body);
            }
        }
        if (parseBody) {
            functionNode.setEndParserState(endParserState);
        } else if (!body.getStatements().isEmpty()) {
            body.setStatements(Collections.emptyList());
        }
        if (this.reparsedFunction != null && (data = this.reparsedFunction.getScriptFunctionData(functionNode.getId())) != null) {
            functionNode.setFlag(data.getFunctionFlags());
            if (functionNode.hasNestedEval()) {
                assert (functionNode.hasScopeBlock());
                body.setFlag(1);
            }
        }
        Block functionBody = new Block(bodyToken, bodyFinish, body.getFlags() | 0x20, body.getStatements());
        return functionBody;
    }

    private boolean skipFunctionBody(ParserContextFunctionNode functionNode) {
        if (this.reparsedFunction == null) {
            return false;
        }
        RecompilableScriptFunctionData data = this.reparsedFunction.getScriptFunctionData(functionNode.getId());
        if (data == null) {
            return false;
        }
        ParserState parserState = (ParserState)data.getEndParserState();
        assert (parserState != null);
        if (this.k < this.stream.last() && this.start < parserState.position && parserState.position <= Token.descPosition(this.stream.get(this.stream.last()))) {
            while (this.k < this.stream.last()) {
                long nextToken = this.stream.get(this.k + 1);
                if (Token.descPosition(nextToken) == parserState.position && Token.descType(nextToken) == TokenType.RBRACE) {
                    this.token = this.stream.get(this.k);
                    this.type = Token.descType(this.token);
                    this.next();
                    assert (this.type == TokenType.RBRACE && this.start == parserState.position);
                    return true;
                }
                ++this.k;
            }
        }
        this.stream.reset();
        this.lexer = parserState.createLexer(this.source, this.lexer, this.stream, this.scripting && !this.env._no_syntax_extensions, this.isES6());
        this.line = parserState.line;
        this.linePosition = parserState.linePosition;
        this.type = TokenType.SEMICOLON;
        this.scanFirstToken();
        return true;
    }

    private void printAST(FunctionNode functionNode) {
        if (functionNode.getDebugFlag(4)) {
            this.env.getErr().println(new ASTWriter(functionNode));
        }
        if (functionNode.getDebugFlag(1)) {
            this.env.getErr().println(new PrintVisitor(functionNode, true, false));
        }
    }

    private void addFunctionDeclarations(ParserContextFunctionNode functionNode) {
        VarNode lastDecl = null;
        for (int i = this.functionDeclarations.size() - 1; i >= 0; --i) {
            Statement decl = this.functionDeclarations.get(i);
            if (lastDecl == null && decl instanceof VarNode) {
                lastDecl = ((VarNode)decl).setFlag(4);
                decl = lastDecl;
                functionNode.setFlag(1024);
            }
            this.prependStatement(decl);
        }
    }

    private RuntimeNode referenceError(Expression lhs, Expression rhs, boolean earlyError) {
        if (this.env._parse_only || earlyError) {
            throw this.error(JSErrorType.REFERENCE_ERROR, AbstractParser.message("invalid.lvalue", new String[0]), lhs.getToken());
        }
        ArrayList<Expression> args = new ArrayList<Expression>();
        args.add(lhs);
        if (rhs == null) {
            args.add(LiteralNode.newInstance(lhs.getToken(), lhs.getFinish()));
        } else {
            args.add(rhs);
        }
        args.add(LiteralNode.newInstance(lhs.getToken(), lhs.getFinish(), lhs.toString()));
        return new RuntimeNode(lhs.getToken(), lhs.getFinish(), RuntimeNode.Request.REFERENCE_ERROR, args);
    }

    private Expression unaryExpression() {
        long unaryToken = this.token;
        switch (this.type) {
            case ADD: 
            case SUB: {
                TokenType opType = this.type;
                this.next();
                Expression expr = this.unaryExpression();
                return new UnaryNode(Token.recast(unaryToken, opType == TokenType.ADD ? TokenType.POS : TokenType.NEG), expr);
            }
            case DELETE: 
            case VOID: 
            case TYPEOF: 
            case BIT_NOT: 
            case NOT: {
                this.next();
                Expression expr = this.unaryExpression();
                return new UnaryNode(unaryToken, expr);
            }
            case INCPREFIX: 
            case DECPREFIX: {
                TokenType opType = this.type;
                this.next();
                Expression lhs = this.leftHandSideExpression();
                if (lhs == null) {
                    throw this.error(AbstractParser.message("expected.lvalue", this.type.getNameOrType()));
                }
                return this.verifyIncDecExpression(unaryToken, opType, lhs, false);
            }
        }
        Expression expression = this.leftHandSideExpression();
        if (this.last != TokenType.EOL) {
            switch (this.type) {
                case INCPREFIX: 
                case DECPREFIX: {
                    long opToken = this.token;
                    TokenType opType = this.type;
                    if (expression == null) {
                        throw this.error(AbstractParser.message("expected.lvalue", this.type.getNameOrType()));
                    }
                    this.next();
                    return this.verifyIncDecExpression(opToken, opType, expression, true);
                }
            }
        }
        if (expression == null) {
            throw this.error(AbstractParser.message("expected.operand", this.type.getNameOrType()));
        }
        return expression;
    }

    private Expression verifyIncDecExpression(long unaryToken, TokenType opType, Expression lhs, boolean isPostfix) {
        assert (lhs != null);
        if (!(lhs instanceof AccessNode || lhs instanceof IndexNode || lhs instanceof IdentNode)) {
            return this.referenceError(lhs, null, this.env._early_lvalue_error);
        }
        if (lhs instanceof IdentNode) {
            if (!Parser.checkIdentLValue((IdentNode)lhs)) {
                return this.referenceError(lhs, null, false);
            }
            this.verifyIdent((IdentNode)lhs, "operand for " + opType.getName() + " operator");
        }
        return Parser.incDecExpression(unaryToken, opType, lhs, isPostfix);
    }

    protected Expression expression() {
        Expression assignmentExpression = this.assignmentExpression(false);
        while (this.type == TokenType.COMMARIGHT) {
            long commaToken = this.token;
            this.next();
            boolean rhsRestParameter = false;
            if (this.type == TokenType.ELLIPSIS && this.isES6() && this.isRestParameterEndOfArrowFunctionParameterList()) {
                this.next();
                rhsRestParameter = true;
            }
            Expression rhs = this.assignmentExpression(false);
            if (rhsRestParameter) {
                rhs = ((IdentNode)rhs).setIsRestParameter();
                assert (this.type == TokenType.RPAREN);
            }
            assignmentExpression = new BinaryNode(commaToken, assignmentExpression, rhs);
        }
        return assignmentExpression;
    }

    private Expression expression(int minPrecedence, boolean noIn) {
        return this.expression(this.unaryExpression(), minPrecedence, noIn);
    }

    private JoinPredecessorExpression joinPredecessorExpression() {
        return new JoinPredecessorExpression(this.expression());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Expression expression(Expression exprLhs, int minPrecedence, boolean noIn) {
        int precedence = this.type.getPrecedence();
        Expression lhs = exprLhs;
        while (this.type.isOperator(noIn) && precedence >= minPrecedence) {
            long op = this.token;
            if (this.type == TokenType.TERNARY) {
                this.next();
                Expression trueExpr = this.expression(this.unaryExpression(), TokenType.ASSIGN.getPrecedence(), false);
                this.expect(TokenType.COLON);
                Expression falseExpr = this.expression(this.unaryExpression(), TokenType.ASSIGN.getPrecedence(), noIn);
                lhs = new TernaryNode(op, lhs, new JoinPredecessorExpression(trueExpr), new JoinPredecessorExpression(falseExpr));
            } else {
                Expression rhs;
                boolean isAssign;
                this.next();
                boolean bl = isAssign = Token.descType(op) == TokenType.ASSIGN;
                if (isAssign) {
                    this.defaultNames.push(lhs);
                }
                try {
                    rhs = this.unaryExpression();
                    int nextPrecedence = this.type.getPrecedence();
                    while (this.type.isOperator(noIn) && (nextPrecedence > precedence || nextPrecedence == precedence && !this.type.isLeftAssociative())) {
                        rhs = this.expression(rhs, nextPrecedence, noIn);
                        nextPrecedence = this.type.getPrecedence();
                    }
                }
                finally {
                    if (isAssign) {
                        this.defaultNames.pop();
                    }
                }
                lhs = this.verifyAssignment(op, lhs, rhs);
            }
            precedence = this.type.getPrecedence();
        }
        return lhs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Expression assignmentExpression(boolean noIn) {
        if (this.type == TokenType.YIELD && this.inGeneratorFunction() && this.isES6()) {
            return this.yieldExpression(noIn);
        }
        long startToken = this.token;
        int startLine = this.line;
        Expression exprLhs = this.conditionalExpression(noIn);
        if (this.type == TokenType.ARROW && this.isES6() && this.checkNoLineTerminator()) {
            Expression paramListExpr = exprLhs instanceof ExpressionList ? (((ExpressionList)exprLhs).getExpressions().isEmpty() ? null : ((ExpressionList)exprLhs).getExpressions().get(0)) : exprLhs;
            return this.arrowFunction(startToken, startLine, paramListExpr);
        }
        assert (!(exprLhs instanceof ExpressionList));
        if (Parser.isAssignmentOperator(this.type)) {
            boolean isAssign;
            boolean bl = isAssign = this.type == TokenType.ASSIGN;
            if (isAssign) {
                this.defaultNames.push(exprLhs);
            }
            try {
                long assignToken = this.token;
                this.next();
                Expression exprRhs = this.assignmentExpression(noIn);
                Expression expression = this.verifyAssignment(assignToken, exprLhs, exprRhs);
                return expression;
            }
            finally {
                if (isAssign) {
                    this.defaultNames.pop();
                }
            }
        }
        return exprLhs;
    }

    private static boolean isAssignmentOperator(TokenType type) {
        switch (type) {
            case ASSIGN: 
            case ASSIGN_ADD: 
            case ASSIGN_BIT_AND: 
            case ASSIGN_BIT_OR: 
            case ASSIGN_BIT_XOR: 
            case ASSIGN_DIV: 
            case ASSIGN_MOD: 
            case ASSIGN_MUL: 
            case ASSIGN_SAR: 
            case ASSIGN_SHL: 
            case ASSIGN_SHR: 
            case ASSIGN_SUB: {
                return true;
            }
        }
        return false;
    }

    private Expression conditionalExpression(boolean noIn) {
        return this.expression(TokenType.TERNARY.getPrecedence(), noIn);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Expression arrowFunction(long startToken, int functionLine, Expression paramListExpr) {
        assert (this.type != TokenType.ARROW || this.checkNoLineTerminator());
        this.expect(TokenType.ARROW);
        long functionToken = Token.recast(startToken, TokenType.ARROW);
        IdentNode name = new IdentNode(functionToken, Token.descPosition(functionToken), NameCodec.encode("=>:") + functionLine);
        ParserContextFunctionNode functionNode = this.createParserContextFunctionNode(name, functionToken, FunctionNode.Kind.ARROW, functionLine, null);
        functionNode.setFlag(1);
        this.lc.push(functionNode);
        try {
            List<IdentNode> parameters;
            ParserContextBlockNode parameterBlock = this.newBlock();
            try {
                parameters = this.convertArrowFunctionParameterList(paramListExpr, functionLine);
                functionNode.setParameters(parameters);
                if (!functionNode.isSimpleParameterList()) {
                    this.markEvalInArrowParameterList(parameterBlock);
                }
            }
            finally {
                this.restoreBlock(parameterBlock);
            }
            Block functionBody = this.functionBody(functionNode);
            functionBody = Parser.maybeWrapBodyInParameterBlock(functionBody, parameterBlock);
            this.verifyParameterList(parameters, functionNode);
            FunctionNode functionNode2 = this.createFunctionNode(functionNode, functionToken, name, parameters, FunctionNode.Kind.ARROW, functionLine, functionBody);
            return functionNode2;
        }
        finally {
            this.lc.pop(functionNode);
        }
    }

    private void markEvalInArrowParameterList(ParserContextBlockNode parameterBlock) {
        Iterator<ParserContextFunctionNode> iter = this.lc.getFunctions();
        final ParserContextFunctionNode current = iter.next();
        ParserContextFunctionNode parent = iter.next();
        if (parent.getFlag(32) != 0) {
            for (Statement st : parameterBlock.getStatements()) {
                st.accept((NodeVisitor<? extends LexicalContext>)new NodeVisitor<LexicalContext>(new LexicalContext()){

                    @Override
                    public boolean enterCallNode(CallNode callNode) {
                        if (callNode.getFunction() instanceof IdentNode && ((IdentNode)callNode.getFunction()).getName().equals("eval")) {
                            current.setFlag(32);
                        }
                        return true;
                    }
                });
            }
        }
    }

    private List<IdentNode> convertArrowFunctionParameterList(Expression paramListExpr, int functionLine) {
        List<IdentNode> parameters;
        if (paramListExpr == null) {
            parameters = Collections.emptyList();
        } else if (paramListExpr instanceof IdentNode || paramListExpr.isTokenType(TokenType.ASSIGN) || this.isDestructuringLhs(paramListExpr)) {
            parameters = Collections.singletonList(this.verifyArrowParameter(paramListExpr, 0, functionLine));
        } else if (paramListExpr instanceof BinaryNode && Token.descType(paramListExpr.getToken()) == TokenType.COMMARIGHT) {
            parameters = new ArrayList<IdentNode>();
            Expression car = paramListExpr;
            do {
                Expression cdr = ((BinaryNode)car).rhs();
                parameters.add(0, this.verifyArrowParameter(cdr, parameters.size(), functionLine));
            } while ((car = ((BinaryNode)car).lhs()) instanceof BinaryNode && Token.descType(car.getToken()) == TokenType.COMMARIGHT);
            parameters.add(0, this.verifyArrowParameter(car, parameters.size(), functionLine));
        } else {
            throw this.error(AbstractParser.message("expected.arrow.parameter", new String[0]), paramListExpr.getToken());
        }
        return parameters;
    }

    private IdentNode verifyArrowParameter(Expression param, int index, int paramLine) {
        String contextString = "function parameter";
        if (param instanceof IdentNode) {
            IdentNode ident = (IdentNode)param;
            this.verifyIdent(ident, "function parameter");
            ParserContextFunctionNode currentFunction = this.lc.getCurrentFunction();
            if (currentFunction != null) {
                currentFunction.addParameterBinding(ident);
            }
            return ident;
        }
        if (param.isTokenType(TokenType.ASSIGN)) {
            Expression lhs = ((BinaryNode)param).lhs();
            long paramToken = lhs.getToken();
            Expression initializer = ((BinaryNode)param).rhs();
            if (lhs instanceof IdentNode) {
                IdentNode ident = (IdentNode)lhs;
                ParserContextFunctionNode currentFunction = this.lc.getCurrentFunction();
                if (currentFunction != null) {
                    if (this.env._parse_only) {
                        currentFunction.addParameterExpression(ident, param);
                    } else {
                        BinaryNode test = new BinaryNode(Token.recast(paramToken, TokenType.EQ_STRICT), ident, Parser.newUndefinedLiteral(paramToken, this.finish));
                        TernaryNode value = new TernaryNode(Token.recast(paramToken, TokenType.TERNARY), (Expression)test, new JoinPredecessorExpression(initializer), new JoinPredecessorExpression(ident));
                        BinaryNode assignment = new BinaryNode(Token.recast(paramToken, TokenType.ASSIGN), ident, value);
                        this.lc.getFunctionBody(currentFunction).appendStatement(new ExpressionStatement(paramLine, assignment.getToken(), assignment.getFinish(), assignment));
                    }
                    currentFunction.addParameterBinding(ident);
                    currentFunction.setSimpleParameterList(false);
                }
                return ident;
            }
            if (this.isDestructuringLhs(lhs)) {
                IdentNode ident = this.createIdentNode(paramToken, param.getFinish(), String.format("arguments[%d]", index)).setIsDestructuredParameter().setIsDefaultParameter();
                this.verifyDestructuringParameterBindingPattern(param, paramToken, paramLine, "function parameter");
                ParserContextFunctionNode currentFunction = this.lc.getCurrentFunction();
                if (currentFunction != null) {
                    if (this.env._parse_only) {
                        currentFunction.addParameterExpression(ident, param);
                    } else {
                        BinaryNode test = new BinaryNode(Token.recast(paramToken, TokenType.EQ_STRICT), ident, Parser.newUndefinedLiteral(paramToken, this.finish));
                        TernaryNode value = new TernaryNode(Token.recast(paramToken, TokenType.TERNARY), (Expression)test, new JoinPredecessorExpression(initializer), new JoinPredecessorExpression(ident));
                        BinaryNode assignment = new BinaryNode(Token.recast(paramToken, TokenType.ASSIGN), param, value);
                        this.lc.getFunctionBody(currentFunction).appendStatement(new ExpressionStatement(paramLine, assignment.getToken(), assignment.getFinish(), assignment));
                    }
                }
                return ident;
            }
        } else if (this.isDestructuringLhs(param)) {
            long paramToken = param.getToken();
            IdentNode ident = this.createIdentNode(paramToken, param.getFinish(), String.format("arguments[%d]", index)).setIsDestructuredParameter();
            this.verifyDestructuringParameterBindingPattern(param, paramToken, paramLine, "function parameter");
            ParserContextFunctionNode currentFunction = this.lc.getCurrentFunction();
            if (currentFunction != null) {
                if (this.env._parse_only) {
                    currentFunction.addParameterExpression(ident, param);
                } else {
                    BinaryNode assignment = new BinaryNode(Token.recast(paramToken, TokenType.ASSIGN), param, ident);
                    this.lc.getFunctionBody(currentFunction).appendStatement(new ExpressionStatement(paramLine, assignment.getToken(), assignment.getFinish(), assignment));
                }
            }
            return ident;
        }
        throw this.error(AbstractParser.message("invalid.arrow.parameter", new String[0]), param.getToken());
    }

    private boolean checkNoLineTerminator() {
        assert (this.type == TokenType.ARROW);
        if (this.last == TokenType.RPAREN) {
            return true;
        }
        if (this.last == TokenType.IDENT) {
            return true;
        }
        block5: for (int i = this.k - 1; i >= 0; --i) {
            TokenType t = this.T(i);
            switch (t) {
                case RPAREN: 
                case IDENT: {
                    return true;
                }
                case EOL: {
                    return false;
                }
                case COMMENT: {
                    continue block5;
                }
                default: {
                    return t.getKind() == TokenKind.FUTURESTRICT;
                }
            }
        }
        return false;
    }

    private boolean isRestParameterEndOfArrowFunctionParameterList() {
        TokenType t;
        assert (this.type == TokenType.ELLIPSIS);
        int i = 1;
        while ((t = this.T(this.k + i++)) != TokenType.IDENT) {
            if (t == TokenType.EOL || t == TokenType.COMMENT) continue;
            return false;
        }
        while ((t = this.T(this.k + i++)) != TokenType.RPAREN) {
            if (t == TokenType.EOL || t == TokenType.COMMENT) continue;
            return false;
        }
        while ((t = this.T(this.k + i++)) != TokenType.ARROW) {
            if (t == TokenType.COMMENT) continue;
            return false;
        }
        return true;
    }

    private void endOfLine() {
        switch (this.type) {
            case EOL: 
            case SEMICOLON: {
                this.next();
                break;
            }
            case EOF: 
            case RBRACE: 
            case RPAREN: 
            case RBRACKET: {
                break;
            }
            default: {
                if (this.last == TokenType.EOL) break;
                this.expect(TokenType.SEMICOLON);
            }
        }
    }

    private Expression templateLiteral() {
        TokenType lastLiteralType;
        assert (this.type == TokenType.TEMPLATE || this.type == TokenType.TEMPLATE_HEAD);
        boolean noSubstitutionTemplate = this.type == TokenType.TEMPLATE;
        long lastLiteralToken = this.token;
        LiteralNode<?> literal = this.getLiteral();
        if (noSubstitutionTemplate) {
            return literal;
        }
        if (this.env._parse_only) {
            TokenType lastLiteralType2;
            ArrayList<Expression> exprs = new ArrayList<Expression>();
            exprs.add(literal);
            do {
                Expression expression = this.expression();
                if (this.type != TokenType.TEMPLATE_MIDDLE && this.type != TokenType.TEMPLATE_TAIL) {
                    throw this.error(AbstractParser.message("unterminated.template.expression", new String[0]), this.token);
                }
                exprs.add(expression);
                lastLiteralType2 = this.type;
                literal = this.getLiteral();
                exprs.add(literal);
            } while (lastLiteralType2 == TokenType.TEMPLATE_MIDDLE);
            return new TemplateLiteral(exprs);
        }
        Expression concat = literal;
        do {
            Expression expression = this.expression();
            if (this.type != TokenType.TEMPLATE_MIDDLE && this.type != TokenType.TEMPLATE_TAIL) {
                throw this.error(AbstractParser.message("unterminated.template.expression", new String[0]), this.token);
            }
            concat = new BinaryNode(Token.recast(lastLiteralToken, TokenType.ADD), concat, expression);
            lastLiteralType = this.type;
            lastLiteralToken = this.token;
            literal = this.getLiteral();
            concat = new BinaryNode(Token.recast(lastLiteralToken, TokenType.ADD), concat, literal);
        } while (lastLiteralType == TokenType.TEMPLATE_MIDDLE);
        return concat;
    }

    private List<Expression> templateLiteralArgumentList() {
        assert (this.type == TokenType.TEMPLATE || this.type == TokenType.TEMPLATE_HEAD);
        ArrayList<Expression> argumentList = new ArrayList<Expression>();
        ArrayList<Expression> rawStrings = new ArrayList<Expression>();
        ArrayList<Expression> cookedStrings = new ArrayList<Expression>();
        argumentList.add(null);
        long templateToken = this.token;
        boolean hasSubstitutions = this.type == TokenType.TEMPLATE_HEAD;
        this.addTemplateLiteralString(rawStrings, cookedStrings);
        if (hasSubstitutions) {
            TokenType lastLiteralType;
            do {
                Expression expression = this.expression();
                if (this.type != TokenType.TEMPLATE_MIDDLE && this.type != TokenType.TEMPLATE_TAIL) {
                    throw this.error(AbstractParser.message("unterminated.template.expression", new String[0]), this.token);
                }
                argumentList.add(expression);
                lastLiteralType = this.type;
                this.addTemplateLiteralString(rawStrings, cookedStrings);
            } while (lastLiteralType == TokenType.TEMPLATE_MIDDLE);
        }
        LiteralNode<Expression[]> rawStringArray = LiteralNode.newInstance(templateToken, this.finish, rawStrings);
        LiteralNode<Expression[]> cookedStringArray = LiteralNode.newInstance(templateToken, this.finish, cookedStrings);
        if (!this.env._parse_only) {
            RuntimeNode templateObject = new RuntimeNode(templateToken, this.finish, RuntimeNode.Request.GET_TEMPLATE_OBJECT, rawStringArray, cookedStringArray);
            argumentList.set(0, templateObject);
        } else {
            argumentList.set(0, rawStringArray);
        }
        return Parser.optimizeList(argumentList);
    }

    private void addTemplateLiteralString(ArrayList<Expression> rawStrings, ArrayList<Expression> cookedStrings) {
        long stringToken = this.token;
        String rawString = this.lexer.valueOfRawString(stringToken);
        String cookedString = (String)this.getValue();
        this.next();
        rawStrings.add(LiteralNode.newInstance(stringToken, this.finish, rawString));
        cookedStrings.add(LiteralNode.newInstance(stringToken, this.finish, cookedString));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private FunctionNode module(String moduleName) {
        boolean oldStrictMode = this.isStrictMode;
        try {
            this.isStrictMode = true;
            int functionStart = Math.min(Token.descPosition(Token.withDelimiter(this.token)), this.finish);
            long functionToken = Token.toDesc(TokenType.FUNCTION, functionStart, this.source.getLength() - functionStart);
            int functionLine = this.line;
            IdentNode ident = new IdentNode(functionToken, Token.descPosition(functionToken), moduleName);
            ParserContextFunctionNode script = this.createParserContextFunctionNode(ident, functionToken, FunctionNode.Kind.MODULE, functionLine, Collections.emptyList());
            this.lc.push(script);
            ParserContextModuleNode module = new ParserContextModuleNode(moduleName);
            this.lc.push(module);
            ParserContextBlockNode body = this.newBlock();
            this.functionDeclarations = new ArrayList<Statement>();
            this.moduleBody();
            this.addFunctionDeclarations(script);
            this.functionDeclarations = null;
            this.restoreBlock(body);
            body.setFlag(1);
            Block programBody = new Block(functionToken, this.finish, body.getFlags() | 0x10 | 0x20, body.getStatements());
            this.lc.pop(module);
            this.lc.pop(script);
            script.setLastToken(this.token);
            this.expect(TokenType.EOF);
            script.setModule(module.createModule());
            FunctionNode functionNode = this.createFunctionNode(script, functionToken, ident, Collections.emptyList(), FunctionNode.Kind.MODULE, functionLine, programBody);
            return functionNode;
        }
        finally {
            this.isStrictMode = oldStrictMode;
        }
    }

    private void moduleBody() {
        block4: while (this.type != TokenType.EOF) {
            switch (this.type) {
                case IMPORT: {
                    this.importDeclaration();
                    continue block4;
                }
                case EXPORT: {
                    this.exportDeclaration();
                    continue block4;
                }
            }
            this.statement(true, 0, false, false);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void importDeclaration() {
        int startPosition = this.start;
        this.expect(TokenType.IMPORT);
        ParserContextModuleNode module = this.lc.getCurrentModule();
        if (this.type == TokenType.STRING || this.type == TokenType.ESCSTRING) {
            IdentNode moduleSpecifier = this.createIdentNode(this.token, this.finish, (String)this.getValue());
            this.next();
            module.addModuleRequest(moduleSpecifier);
        } else {
            List<Module.ImportEntry> importEntries;
            if (this.type == TokenType.MUL) {
                importEntries = Collections.singletonList(this.nameSpaceImport(startPosition));
            } else if (this.type == TokenType.LBRACE) {
                importEntries = this.namedImports(startPosition);
            } else {
                if (!this.isBindingIdentifier()) throw this.error(AbstractParser.message("expected.import", new String[0]));
                IdentNode importedDefaultBinding = this.bindingIdentifier("ImportedBinding");
                Module.ImportEntry defaultImport = Module.ImportEntry.importSpecifier(importedDefaultBinding, startPosition, this.finish);
                if (this.type == TokenType.COMMARIGHT) {
                    this.next();
                    importEntries = new ArrayList<Module.ImportEntry>();
                    if (this.type == TokenType.MUL) {
                        importEntries.add(this.nameSpaceImport(startPosition));
                    } else {
                        if (this.type != TokenType.LBRACE) throw this.error(AbstractParser.message("expected.named.import", new String[0]));
                        importEntries.addAll(this.namedImports(startPosition));
                    }
                } else {
                    importEntries = Collections.singletonList(defaultImport);
                }
            }
            IdentNode moduleSpecifier = this.fromClause();
            module.addModuleRequest(moduleSpecifier);
            for (Module.ImportEntry importEntry : importEntries) {
                module.addImportEntry(importEntry.withFrom(moduleSpecifier, this.finish));
            }
        }
        this.expect(TokenType.SEMICOLON);
    }

    private Module.ImportEntry nameSpaceImport(int startPosition) {
        assert (this.type == TokenType.MUL);
        IdentNode starName = this.createIdentNode(Token.recast(this.token, TokenType.IDENT), this.finish, "*");
        this.next();
        long asToken = this.token;
        String as = (String)this.expectValue(TokenType.IDENT);
        if (!"as".equals(as)) {
            throw this.error(AbstractParser.message("expected.as", new String[0]), asToken);
        }
        IdentNode localNameSpace = this.bindingIdentifier("ImportedBinding");
        return Module.ImportEntry.importSpecifier(starName, localNameSpace, startPosition, this.finish);
    }

    private List<Module.ImportEntry> namedImports(int startPosition) {
        assert (this.type == TokenType.LBRACE);
        this.next();
        ArrayList<Module.ImportEntry> importEntries = new ArrayList<Module.ImportEntry>();
        while (this.type != TokenType.RBRACE) {
            boolean bindingIdentifier = this.isBindingIdentifier();
            long nameToken = this.token;
            IdentNode importName = this.getIdentifierName();
            if (this.type == TokenType.IDENT && "as".equals(this.getValue())) {
                this.next();
                IdentNode localName = this.bindingIdentifier("ImportedBinding");
                importEntries.add(Module.ImportEntry.importSpecifier(importName, localName, startPosition, this.finish));
            } else {
                if (!bindingIdentifier) {
                    throw this.error(AbstractParser.message("expected.binding.identifier", new String[0]), nameToken);
                }
                importEntries.add(Module.ImportEntry.importSpecifier(importName, startPosition, this.finish));
            }
            if (this.type != TokenType.COMMARIGHT) break;
            this.next();
        }
        this.expect(TokenType.RBRACE);
        return importEntries;
    }

    private IdentNode fromClause() {
        long fromToken = this.token;
        String name = (String)this.expectValue(TokenType.IDENT);
        if (!"from".equals(name)) {
            throw this.error(AbstractParser.message("expected.from", new String[0]), fromToken);
        }
        if (this.type == TokenType.STRING || this.type == TokenType.ESCSTRING) {
            IdentNode moduleSpecifier = this.createIdentNode(Token.recast(this.token, TokenType.IDENT), this.finish, (String)this.getValue());
            this.next();
            return moduleSpecifier;
        }
        throw this.error(this.expectMessage(TokenType.STRING));
    }

    private void exportDeclaration() {
        this.expect(TokenType.EXPORT);
        int startPosition = this.start;
        ParserContextModuleNode module = this.lc.getCurrentModule();
        switch (this.type) {
            case MUL: {
                IdentNode starName = this.createIdentNode(Token.recast(this.token, TokenType.IDENT), this.finish, "*");
                this.next();
                IdentNode moduleRequest = this.fromClause();
                this.expect(TokenType.SEMICOLON);
                module.addModuleRequest(moduleRequest);
                module.addStarExportEntry(Module.ExportEntry.exportStarFrom(starName, moduleRequest, startPosition, this.finish));
                break;
            }
            case LBRACE: {
                List<Module.ExportEntry> exportEntries = this.exportClause(startPosition);
                if (this.type == TokenType.IDENT && "from".equals(this.getValue())) {
                    IdentNode moduleRequest = this.fromClause();
                    module.addModuleRequest(moduleRequest);
                    for (Module.ExportEntry exportEntry : exportEntries) {
                        module.addIndirectExportEntry(exportEntry.withFrom(moduleRequest, this.finish));
                    }
                } else {
                    for (Module.ExportEntry exportEntry : exportEntries) {
                        module.addLocalExportEntry(exportEntry);
                    }
                }
                this.expect(TokenType.SEMICOLON);
                break;
            }
            case DEFAULT: {
                boolean declaration;
                IdentNode ident;
                Expression assignmentExpression;
                IdentNode defaultName = this.createIdentNode(Token.recast(this.token, TokenType.IDENT), this.finish, "default");
                this.next();
                int lineNumber = this.line;
                long rhsToken = this.token;
                switch (this.type) {
                    case FUNCTION: {
                        assignmentExpression = this.functionExpression(false, true);
                        ident = ((FunctionNode)assignmentExpression).getIdent();
                        declaration = true;
                        break;
                    }
                    case CLASS: {
                        assignmentExpression = this.classDeclaration(true);
                        ident = ((ClassNode)assignmentExpression).getIdent();
                        declaration = true;
                        break;
                    }
                    default: {
                        assignmentExpression = this.assignmentExpression(false);
                        ident = null;
                        declaration = false;
                    }
                }
                if (ident != null) {
                    module.addLocalExportEntry(Module.ExportEntry.exportDefault(defaultName, ident, startPosition, this.finish));
                    break;
                }
                ident = this.createIdentNode(Token.recast(rhsToken, TokenType.IDENT), this.finish, "*default*");
                this.lc.appendStatementToCurrentNode(new VarNode(lineNumber, Token.recast(rhsToken, TokenType.LET), this.finish, ident, assignmentExpression));
                if (!declaration) {
                    this.expect(TokenType.SEMICOLON);
                }
                module.addLocalExportEntry(Module.ExportEntry.exportDefault(defaultName, ident, startPosition, this.finish));
                break;
            }
            case VAR: 
            case LET: 
            case CONST: {
                List<Statement> statements = this.lc.getCurrentBlock().getStatements();
                int previousEnd = statements.size();
                this.variableStatement(this.type);
                for (Statement statement : statements.subList(previousEnd, statements.size())) {
                    if (!(statement instanceof VarNode)) continue;
                    module.addLocalExportEntry(Module.ExportEntry.exportSpecifier(((VarNode)statement).getName(), startPosition, this.finish));
                }
                break;
            }
            case CLASS: {
                ClassNode classDeclaration = this.classDeclaration(false);
                module.addLocalExportEntry(Module.ExportEntry.exportSpecifier(classDeclaration.getIdent(), startPosition, this.finish));
                break;
            }
            case FUNCTION: {
                FunctionNode functionDeclaration = (FunctionNode)this.functionExpression(true, true);
                module.addLocalExportEntry(Module.ExportEntry.exportSpecifier(functionDeclaration.getIdent(), startPosition, this.finish));
                break;
            }
            default: {
                throw this.error(AbstractParser.message("invalid.export", new String[0]), this.token);
            }
        }
    }

    private List<Module.ExportEntry> exportClause(int startPosition) {
        assert (this.type == TokenType.LBRACE);
        this.next();
        ArrayList<Module.ExportEntry> exports = new ArrayList<Module.ExportEntry>();
        while (this.type != TokenType.RBRACE) {
            IdentNode localName = this.getIdentifierName();
            if (this.type == TokenType.IDENT && "as".equals(this.getValue())) {
                this.next();
                IdentNode exportName = this.getIdentifierName();
                exports.add(Module.ExportEntry.exportSpecifier(exportName, localName, startPosition, this.finish));
            } else {
                exports.add(Module.ExportEntry.exportSpecifier(localName, startPosition, this.finish));
            }
            if (this.type != TokenType.COMMARIGHT) break;
            this.next();
        }
        this.expect(TokenType.RBRACE);
        return exports;
    }

    public String toString() {
        return "'JavaScript Parsing'";
    }

    private static void markEval(ParserContext lc) {
        Iterator<ParserContextFunctionNode> iter = lc.getFunctions();
        boolean flaggedCurrentFn = false;
        while (iter.hasNext()) {
            ParserContextFunctionNode fn = iter.next();
            if (!flaggedCurrentFn) {
                fn.setFlag(32);
                flaggedCurrentFn = true;
                if (fn.getKind() == FunctionNode.Kind.ARROW) {
                    Parser.markThis(lc);
                    Parser.markNewTarget(lc);
                }
            } else {
                fn.setFlag(64);
            }
            ParserContextBlockNode body = lc.getFunctionBody(fn);
            body.setFlag(1);
            fn.setFlag(128);
        }
    }

    private void prependStatement(Statement statement) {
        this.lc.prependStatementToCurrentNode(statement);
    }

    private void appendStatement(Statement statement) {
        this.lc.appendStatementToCurrentNode(statement);
    }

    private static void markSuperCall(ParserContext lc) {
        Iterator<ParserContextFunctionNode> iter = lc.getFunctions();
        while (iter.hasNext()) {
            ParserContextFunctionNode fn = iter.next();
            if (fn.getKind() == FunctionNode.Kind.ARROW) continue;
            assert (fn.isSubclassConstructor());
            fn.setFlag(524288);
            break;
        }
    }

    private ParserContextFunctionNode getCurrentNonArrowFunction() {
        Iterator<ParserContextFunctionNode> iter = this.lc.getFunctions();
        while (iter.hasNext()) {
            ParserContextFunctionNode fn = iter.next();
            if (fn.getKind() == FunctionNode.Kind.ARROW) continue;
            return fn;
        }
        return null;
    }

    private static void markThis(ParserContext lc) {
        Iterator<ParserContextFunctionNode> iter = lc.getFunctions();
        while (iter.hasNext()) {
            ParserContextFunctionNode fn = iter.next();
            fn.setFlag(32768);
            if (fn.getKind() == FunctionNode.Kind.ARROW) continue;
            break;
        }
    }

    private static void markNewTarget(ParserContext lc) {
        Iterator<ParserContextFunctionNode> iter = lc.getFunctions();
        while (iter.hasNext()) {
            ParserContextFunctionNode fn = iter.next();
            if (fn.getKind() == FunctionNode.Kind.ARROW) continue;
            if (fn.isProgram()) break;
            fn.setFlag(0x2000000);
            break;
        }
    }

    private boolean inGeneratorFunction() {
        return this.lc.getCurrentFunction().getKind() == FunctionNode.Kind.GENERATOR;
    }

    private static class ParserState
    implements Serializable {
        private final int position;
        private final int line;
        private final int linePosition;
        private static final long serialVersionUID = -2382565130754093694L;

        ParserState(int position, int line, int linePosition) {
            this.position = position;
            this.line = line;
            this.linePosition = linePosition;
        }

        Lexer createLexer(Source source, Lexer lexer, TokenStream stream, boolean scripting, boolean es6) {
            Lexer newLexer = new Lexer(source, this.position, lexer.limit - this.position, stream, scripting, es6, true);
            newLexer.restoreState(new Lexer.State(this.position, Integer.MAX_VALUE, this.line, -1, this.linePosition, TokenType.SEMICOLON));
            return newLexer;
        }
    }

    private static class PropertyFunction {
        final Expression key;
        final FunctionNode functionNode;
        final boolean computed;

        PropertyFunction(Expression key, FunctionNode function, boolean computed) {
            this.key = key;
            this.functionNode = function;
            this.computed = computed;
        }
    }

    private abstract class VerifyDestructuringPatternNodeVisitor
    extends NodeVisitor<LexicalContext> {
        VerifyDestructuringPatternNodeVisitor(LexicalContext lc) {
            super(lc);
        }

        @Override
        public boolean enterLiteralNode(LiteralNode<?> literalNode) {
            if (literalNode.isArray()) {
                if (((LiteralNode.ArrayLiteralNode)literalNode).hasSpread() && ((LiteralNode.ArrayLiteralNode)literalNode).hasTrailingComma()) {
                    throw Parser.this.error("Rest element must be last", literalNode.getElementExpressions().get(literalNode.getElementExpressions().size() - 1).getToken());
                }
                boolean restElement = false;
                for (Expression element : literalNode.getElementExpressions()) {
                    if (element == null) continue;
                    if (restElement) {
                        throw Parser.this.error("Unexpected element after rest element", element.getToken());
                    }
                    if (element.isTokenType(TokenType.SPREAD_ARRAY)) {
                        restElement = true;
                        Expression lvalue = ((UnaryNode)element).getExpression();
                        this.verifySpreadElement(lvalue);
                    }
                    element.accept(this);
                }
                return false;
            }
            return this.enterDefault(literalNode);
        }

        protected abstract void verifySpreadElement(Expression var1);

        @Override
        public boolean enterObjectNode(ObjectNode objectNode) {
            return true;
        }

        @Override
        public boolean enterPropertyNode(PropertyNode propertyNode) {
            if (propertyNode.getValue() != null) {
                propertyNode.getValue().accept(this);
                return false;
            }
            return this.enterDefault(propertyNode);
        }

        @Override
        public boolean enterBinaryNode(BinaryNode binaryNode) {
            if (binaryNode.isTokenType(TokenType.ASSIGN)) {
                binaryNode.lhs().accept(this);
                return false;
            }
            return this.enterDefault(binaryNode);
        }

        @Override
        public boolean enterUnaryNode(UnaryNode unaryNode) {
            if (unaryNode.isTokenType(TokenType.SPREAD_ARRAY)) {
                return true;
            }
            return this.enterDefault(unaryNode);
        }
    }

    private static final class ForVariableDeclarationListResult {
        Expression missingAssignment;
        long declarationWithInitializerToken;
        Expression init;
        Expression firstBinding;
        Expression secondBinding;

        private ForVariableDeclarationListResult() {
        }

        void recordMissingAssignment(Expression binding) {
            if (this.missingAssignment == null) {
                this.missingAssignment = binding;
            }
        }

        void recordDeclarationWithInitializer(long token) {
            if (this.declarationWithInitializerToken == 0L) {
                this.declarationWithInitializerToken = token;
            }
        }

        void addBinding(Expression binding) {
            if (this.firstBinding == null) {
                this.firstBinding = binding;
            } else if (this.secondBinding == null) {
                this.secondBinding = binding;
            }
        }

        void addAssignment(Expression assignment) {
            this.init = this.init == null ? assignment : new BinaryNode(Token.recast(this.init.getToken(), TokenType.COMMARIGHT), this.init, assignment);
        }
    }

    private static final class ClassElementKey {
        private final boolean isStatic;
        private final String propertyName;

        private ClassElementKey(boolean isStatic, String propertyName) {
            this.isStatic = isStatic;
            this.propertyName = propertyName;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.isStatic ? 1231 : 1237);
            result = 31 * result + (this.propertyName == null ? 0 : this.propertyName.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (obj instanceof ClassElementKey) {
                ClassElementKey other = (ClassElementKey)obj;
                return this.isStatic == other.isStatic && Objects.equals(this.propertyName, other.propertyName);
            }
            return false;
        }
    }
}
