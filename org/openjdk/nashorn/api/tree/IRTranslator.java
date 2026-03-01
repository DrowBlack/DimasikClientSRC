package org.openjdk.nashorn.api.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.openjdk.nashorn.api.tree.ArrayAccessTreeImpl;
import org.openjdk.nashorn.api.tree.ArrayLiteralTreeImpl;
import org.openjdk.nashorn.api.tree.AssignmentTree;
import org.openjdk.nashorn.api.tree.AssignmentTreeImpl;
import org.openjdk.nashorn.api.tree.BinaryTreeImpl;
import org.openjdk.nashorn.api.tree.BlockTree;
import org.openjdk.nashorn.api.tree.BlockTreeImpl;
import org.openjdk.nashorn.api.tree.BreakTreeImpl;
import org.openjdk.nashorn.api.tree.CaseTreeImpl;
import org.openjdk.nashorn.api.tree.CatchTreeImpl;
import org.openjdk.nashorn.api.tree.ClassDeclarationTreeImpl;
import org.openjdk.nashorn.api.tree.ClassExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.CompilationUnitTree;
import org.openjdk.nashorn.api.tree.CompilationUnitTreeImpl;
import org.openjdk.nashorn.api.tree.CompoundAssignmentTreeImpl;
import org.openjdk.nashorn.api.tree.ConditionalExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.ContinueTreeImpl;
import org.openjdk.nashorn.api.tree.DebuggerTreeImpl;
import org.openjdk.nashorn.api.tree.DestructuringDeclTreeImpl;
import org.openjdk.nashorn.api.tree.DoWhileLoopTreeImpl;
import org.openjdk.nashorn.api.tree.EmptyStatementTreeImpl;
import org.openjdk.nashorn.api.tree.ErroneousTreeImpl;
import org.openjdk.nashorn.api.tree.ExpressionStatementTreeImpl;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.ForInLoopTreeImpl;
import org.openjdk.nashorn.api.tree.ForLoopTreeImpl;
import org.openjdk.nashorn.api.tree.ForOfLoopTreeImpl;
import org.openjdk.nashorn.api.tree.FunctionCallTreeImpl;
import org.openjdk.nashorn.api.tree.FunctionDeclarationTreeImpl;
import org.openjdk.nashorn.api.tree.FunctionExpressionTree;
import org.openjdk.nashorn.api.tree.FunctionExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.IdentifierTreeImpl;
import org.openjdk.nashorn.api.tree.IfTreeImpl;
import org.openjdk.nashorn.api.tree.InstanceOfTreeImpl;
import org.openjdk.nashorn.api.tree.LabeledStatementTreeImpl;
import org.openjdk.nashorn.api.tree.LiteralTreeImpl;
import org.openjdk.nashorn.api.tree.MemberSelectTreeImpl;
import org.openjdk.nashorn.api.tree.ModuleTree;
import org.openjdk.nashorn.api.tree.ModuleTreeImpl;
import org.openjdk.nashorn.api.tree.NewTreeImpl;
import org.openjdk.nashorn.api.tree.ObjectLiteralTreeImpl;
import org.openjdk.nashorn.api.tree.PropertyTree;
import org.openjdk.nashorn.api.tree.PropertyTreeImpl;
import org.openjdk.nashorn.api.tree.RegExpLiteralTreeImpl;
import org.openjdk.nashorn.api.tree.ReturnTreeImpl;
import org.openjdk.nashorn.api.tree.SpreadTreeImpl;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.SwitchTreeImpl;
import org.openjdk.nashorn.api.tree.TemplateLiteralTreeImpl;
import org.openjdk.nashorn.api.tree.ThrowTreeImpl;
import org.openjdk.nashorn.api.tree.TryTreeImpl;
import org.openjdk.nashorn.api.tree.UnaryTreeImpl;
import org.openjdk.nashorn.api.tree.VariableTreeImpl;
import org.openjdk.nashorn.api.tree.WhileLoopTreeImpl;
import org.openjdk.nashorn.api.tree.WithTreeImpl;
import org.openjdk.nashorn.api.tree.YieldTreeImpl;
import org.openjdk.nashorn.internal.ir.AccessNode;
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
import org.openjdk.nashorn.internal.ir.ExpressionStatement;
import org.openjdk.nashorn.internal.ir.ForNode;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.IdentNode;
import org.openjdk.nashorn.internal.ir.IfNode;
import org.openjdk.nashorn.internal.ir.IndexNode;
import org.openjdk.nashorn.internal.ir.LabelNode;
import org.openjdk.nashorn.internal.ir.LiteralNode;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.ObjectNode;
import org.openjdk.nashorn.internal.ir.PropertyNode;
import org.openjdk.nashorn.internal.ir.ReturnNode;
import org.openjdk.nashorn.internal.ir.RuntimeNode;
import org.openjdk.nashorn.internal.ir.SplitNode;
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
import org.openjdk.nashorn.internal.ir.visitor.SimpleNodeVisitor;
import org.openjdk.nashorn.internal.parser.Lexer;
import org.openjdk.nashorn.internal.parser.TokenType;

final class IRTranslator
extends SimpleNodeVisitor {
    private StatementTreeImpl curStat;
    private ExpressionTreeImpl curExpr;

    CompilationUnitTree translate(FunctionNode node) {
        if (node == null) {
            return null;
        }
        assert (node.getKind() == FunctionNode.Kind.SCRIPT || node.getKind() == FunctionNode.Kind.MODULE) : "script or module function expected";
        Block body = node.getBody();
        return new CompilationUnitTreeImpl(node, this.translateStats(body != null ? this.getOrderedStatements(body.getStatements()) : null), this.translateModule(node));
    }

    @Override
    public boolean enterAccessNode(AccessNode accessNode) {
        this.curExpr = new MemberSelectTreeImpl(accessNode, this.translateExpr(accessNode.getBase()));
        return false;
    }

    @Override
    public boolean enterBlock(Block block) {
        return this.handleBlock(block, false);
    }

    @Override
    public boolean enterBinaryNode(BinaryNode binaryNode) {
        if (binaryNode.isAssignment()) {
            ExpressionTreeImpl srcTree = this.translateExpr(binaryNode.getAssignmentSource());
            ExpressionTreeImpl destTree = this.translateExpr(binaryNode.getAssignmentDest());
            this.curExpr = binaryNode.isTokenType(TokenType.ASSIGN) ? new AssignmentTreeImpl(binaryNode, destTree, srcTree) : new CompoundAssignmentTreeImpl(binaryNode, destTree, srcTree);
        } else {
            ExpressionTreeImpl leftTree = this.translateExpr(binaryNode.lhs());
            ExpressionTreeImpl rightTree = this.translateExpr(binaryNode.rhs());
            this.curExpr = binaryNode.isTokenType(TokenType.INSTANCEOF) ? new InstanceOfTreeImpl(binaryNode, leftTree, rightTree) : new BinaryTreeImpl(binaryNode, leftTree, rightTree);
        }
        return false;
    }

    @Override
    public boolean enterBreakNode(BreakNode breakNode) {
        this.curStat = new BreakTreeImpl(breakNode);
        return false;
    }

    @Override
    public boolean enterCallNode(CallNode callNode) {
        this.curExpr = null;
        callNode.getFunction().accept(this);
        ExpressionTreeImpl funcTree = this.curExpr;
        List<? extends ExpressionTree> argTrees = this.translateExprs(callNode.getArgs());
        this.curExpr = new FunctionCallTreeImpl(callNode, funcTree, argTrees);
        return false;
    }

    @Override
    public boolean enterCaseNode(CaseNode caseNode) {
        assert (false) : "should not reach here!";
        return false;
    }

    @Override
    public boolean enterCatchNode(CatchNode catchNode) {
        assert (false) : "should not reach here";
        return false;
    }

    @Override
    public boolean enterContinueNode(ContinueNode continueNode) {
        this.curStat = new ContinueTreeImpl(continueNode);
        return false;
    }

    @Override
    public boolean enterDebuggerNode(DebuggerNode debuggerNode) {
        this.curStat = new DebuggerTreeImpl(debuggerNode);
        return false;
    }

    @Override
    public boolean enterEmptyNode(EmptyNode emptyNode) {
        this.curStat = new EmptyStatementTreeImpl(emptyNode);
        return false;
    }

    @Override
    public boolean enterErrorNode(ErrorNode errorNode) {
        this.curExpr = new ErroneousTreeImpl(errorNode);
        return false;
    }

    @Override
    public boolean enterExpressionStatement(ExpressionStatement expressionStatement) {
        if (expressionStatement.destructuringDeclarationType() != null) {
            ExpressionTreeImpl expr = this.translateExpr(expressionStatement.getExpression());
            assert (expr instanceof AssignmentTree) : "destructuring decl. statement does not have assignment";
            AssignmentTree assign = (AssignmentTree)((Object)expr);
            this.curStat = new DestructuringDeclTreeImpl(expressionStatement, assign.getVariable(), assign.getExpression());
        } else {
            this.curStat = new ExpressionStatementTreeImpl(expressionStatement, this.translateExpr(expressionStatement.getExpression()));
        }
        return false;
    }

    @Override
    public boolean enterBlockStatement(BlockStatement blockStatement) {
        Block block = blockStatement.getBlock();
        if (blockStatement.isSynthetic()) {
            assert (block != null && block.getStatements() != null && block.getStatements().size() == 1);
            this.curStat = this.translateStat(block.getStatements().get(0));
        } else {
            this.curStat = new BlockTreeImpl(blockStatement, this.translateStats(block != null ? block.getStatements() : null));
        }
        return false;
    }

    @Override
    public boolean enterForNode(ForNode forNode) {
        this.curStat = forNode.isForIn() ? new ForInLoopTreeImpl(forNode, this.translateExpr(forNode.getInit()), this.translateExpr(forNode.getModify()), this.translateBlock(forNode.getBody())) : (forNode.isForOf() ? new ForOfLoopTreeImpl(forNode, this.translateExpr(forNode.getInit()), this.translateExpr(forNode.getModify()), this.translateBlock(forNode.getBody())) : new ForLoopTreeImpl(forNode, this.translateExpr(forNode.getInit()), this.translateExpr(forNode.getTest()), this.translateExpr(forNode.getModify()), this.translateBlock(forNode.getBody())));
        return false;
    }

    @Override
    public boolean enterFunctionNode(FunctionNode functionNode) {
        assert (!functionNode.isDeclared() || functionNode.isAnonymous()) : "should not reach here for function declaration";
        List<? extends ExpressionTree> paramTrees = this.translateParameters(functionNode);
        BlockTree blockTree = (BlockTree)this.translateBlock(functionNode.getBody(), true);
        this.curExpr = new FunctionExpressionTreeImpl(functionNode, paramTrees, blockTree);
        return false;
    }

    @Override
    public boolean enterIdentNode(IdentNode identNode) {
        this.curExpr = new IdentifierTreeImpl(identNode);
        return false;
    }

    @Override
    public boolean enterIfNode(IfNode ifNode) {
        this.curStat = new IfTreeImpl(ifNode, this.translateExpr(ifNode.getTest()), this.translateBlock(ifNode.getPass()), this.translateBlock(ifNode.getFail()));
        return false;
    }

    @Override
    public boolean enterIndexNode(IndexNode indexNode) {
        this.curExpr = new ArrayAccessTreeImpl(indexNode, this.translateExpr(indexNode.getBase()), this.translateExpr(indexNode.getIndex()));
        return false;
    }

    @Override
    public boolean enterLabelNode(LabelNode labelNode) {
        this.curStat = new LabeledStatementTreeImpl(labelNode, this.translateBlock(labelNode.getBody()));
        return false;
    }

    @Override
    public boolean enterLiteralNode(LiteralNode<?> literalNode) {
        Object value = literalNode.getValue();
        if (value instanceof Lexer.RegexToken) {
            this.curExpr = new RegExpLiteralTreeImpl(literalNode);
        } else if (literalNode.isArray()) {
            List<Expression> exprNodes = literalNode.getElementExpressions();
            ArrayList<ExpressionTreeImpl> exprTrees = new ArrayList<ExpressionTreeImpl>(exprNodes.size());
            for (Node node : exprNodes) {
                if (node == null) {
                    exprTrees.add(null);
                    continue;
                }
                this.curExpr = null;
                node.accept(this);
                assert (this.curExpr != null) : "null for " + node;
                exprTrees.add(this.curExpr);
            }
            this.curExpr = new ArrayLiteralTreeImpl(literalNode, exprTrees);
        } else {
            this.curExpr = new LiteralTreeImpl(literalNode);
        }
        return false;
    }

    @Override
    public boolean enterObjectNode(ObjectNode objectNode) {
        List<PropertyNode> propNodes = objectNode.getElements();
        List<? extends PropertyTree> propTrees = this.translateProperties(propNodes);
        this.curExpr = new ObjectLiteralTreeImpl(objectNode, propTrees);
        return false;
    }

    @Override
    public boolean enterPropertyNode(PropertyNode propertyNode) {
        assert (false) : "should not reach here!";
        return false;
    }

    @Override
    public boolean enterReturnNode(ReturnNode returnNode) {
        this.curStat = new ReturnTreeImpl(returnNode, this.translateExpr(returnNode.getExpression()));
        return false;
    }

    @Override
    public boolean enterRuntimeNode(RuntimeNode runtimeNode) {
        assert (false) : "should not reach here: RuntimeNode";
        return false;
    }

    @Override
    public boolean enterSplitNode(SplitNode splitNode) {
        assert (false) : "should not reach here!";
        return false;
    }

    @Override
    public boolean enterSwitchNode(SwitchNode switchNode) {
        List<CaseNode> caseNodes = switchNode.getCases();
        ArrayList<CaseTreeImpl> caseTrees = new ArrayList<CaseTreeImpl>(caseNodes.size());
        for (CaseNode caseNode : caseNodes) {
            Block body = caseNode.getBody();
            caseTrees.add(new CaseTreeImpl(caseNode, this.translateExpr(caseNode.getTest()), this.translateStats(body != null ? body.getStatements() : null)));
        }
        this.curStat = new SwitchTreeImpl(switchNode, this.translateExpr(switchNode.getExpression()), caseTrees);
        return false;
    }

    @Override
    public boolean enterTemplateLiteral(TemplateLiteral templateLiteral) {
        this.curExpr = new TemplateLiteralTreeImpl(templateLiteral, this.translateExprs(templateLiteral.getExpressions()));
        return false;
    }

    @Override
    public boolean enterTernaryNode(TernaryNode ternaryNode) {
        this.curExpr = new ConditionalExpressionTreeImpl(ternaryNode, this.translateExpr(ternaryNode.getTest()), this.translateExpr(ternaryNode.getTrueExpression()), this.translateExpr(ternaryNode.getFalseExpression()));
        return false;
    }

    @Override
    public boolean enterThrowNode(ThrowNode throwNode) {
        this.curStat = new ThrowTreeImpl(throwNode, this.translateExpr(throwNode.getExpression()));
        return false;
    }

    @Override
    public boolean enterTryNode(TryNode tryNode) {
        List<CatchNode> catchNodes = tryNode.getCatches();
        ArrayList<CatchTreeImpl> catchTrees = new ArrayList<CatchTreeImpl>(catchNodes.size());
        for (CatchNode catchNode : catchNodes) {
            catchTrees.add(new CatchTreeImpl(catchNode, this.translateExpr(catchNode.getException()), (BlockTree)this.translateBlock(catchNode.getBody()), this.translateExpr(catchNode.getExceptionCondition())));
        }
        this.curStat = new TryTreeImpl(tryNode, (BlockTree)this.translateBlock(tryNode.getBody()), catchTrees, (BlockTree)this.translateBlock(tryNode.getFinallyBody()));
        return false;
    }

    @Override
    public boolean enterUnaryNode(UnaryNode unaryNode) {
        this.curExpr = unaryNode.isTokenType(TokenType.NEW) ? new NewTreeImpl(unaryNode, this.translateExpr(unaryNode.getExpression())) : (unaryNode.isTokenType(TokenType.YIELD) || unaryNode.isTokenType(TokenType.YIELD_STAR) ? new YieldTreeImpl(unaryNode, this.translateExpr(unaryNode.getExpression())) : (unaryNode.isTokenType(TokenType.SPREAD_ARGUMENT) || unaryNode.isTokenType(TokenType.SPREAD_ARRAY) ? new SpreadTreeImpl(unaryNode, this.translateExpr(unaryNode.getExpression())) : new UnaryTreeImpl(unaryNode, this.translateExpr(unaryNode.getExpression()))));
        return false;
    }

    @Override
    public boolean enterVarNode(VarNode varNode) {
        Expression initNode = varNode.getInit();
        if (initNode instanceof FunctionNode && ((FunctionNode)initNode).isDeclared()) {
            FunctionNode funcNode = (FunctionNode)initNode;
            List<? extends ExpressionTree> paramTrees = this.translateParameters(funcNode);
            BlockTree blockTree = (BlockTree)this.translateBlock(funcNode.getBody(), true);
            this.curStat = new FunctionDeclarationTreeImpl(varNode, paramTrees, blockTree);
        } else if (initNode instanceof ClassNode && ((ClassNode)initNode).isStatement()) {
            ClassNode classNode = (ClassNode)initNode;
            this.curStat = new ClassDeclarationTreeImpl(varNode, IRTranslator.translateIdent(classNode.getIdent()), this.translateExpr(classNode.getClassHeritage()), this.translateProperty(classNode.getConstructor()), this.translateProperties(classNode.getClassElements()));
        } else {
            this.curStat = new VariableTreeImpl(varNode, IRTranslator.translateIdent(varNode.getName()), this.translateExpr(initNode));
        }
        return false;
    }

    @Override
    public boolean enterWhileNode(WhileNode whileNode) {
        ExpressionTreeImpl condTree = this.translateExpr(whileNode.getTest());
        StatementTree statTree = this.translateBlock(whileNode.getBody());
        this.curStat = whileNode.isDoWhile() ? new DoWhileLoopTreeImpl(whileNode, condTree, statTree) : new WhileLoopTreeImpl(whileNode, condTree, statTree);
        return false;
    }

    @Override
    public boolean enterWithNode(WithNode withNode) {
        this.curStat = new WithTreeImpl(withNode, this.translateExpr(withNode.getExpression()), this.translateBlock(withNode.getBody()));
        return false;
    }

    @Override
    public boolean enterClassNode(ClassNode classNode) {
        assert (!classNode.isStatement()) : "should not reach here for class declaration";
        IdentNode className = classNode.getIdent();
        this.curExpr = new ClassExpressionTreeImpl(classNode, className != null ? IRTranslator.translateIdent(className) : null, this.translateExpr(classNode.getClassHeritage()), this.translateProperty(classNode.getConstructor()), this.translateProperties(classNode.getClassElements()));
        return false;
    }

    private StatementTree translateBlock(Block blockNode) {
        return this.translateBlock(blockNode, false);
    }

    private StatementTree translateBlock(Block blockNode, boolean sortStats) {
        if (blockNode == null) {
            return null;
        }
        this.curStat = null;
        this.handleBlock(blockNode, sortStats);
        return this.curStat;
    }

    private boolean handleBlock(Block block, boolean sortStats) {
        if (block.isSynthetic()) {
            int statCount = block.getStatementCount();
            switch (statCount) {
                case 0: {
                    EmptyNode emptyNode = new EmptyNode(-1, block.getToken(), block.getFinish());
                    this.curStat = new EmptyStatementTreeImpl(emptyNode);
                    return false;
                }
                case 1: {
                    this.curStat = this.translateStat(block.getStatements().get(0));
                    return false;
                }
            }
        }
        List<Statement> stats = block.getStatements();
        this.curStat = new BlockTreeImpl(block, this.translateStats(sortStats ? this.getOrderedStatements(stats) : stats));
        return false;
    }

    private List<? extends Statement> getOrderedStatements(List<? extends Statement> stats) {
        ArrayList<? extends Statement> statList = new ArrayList<Statement>(stats);
        statList.sort(Comparator.comparingInt(Node::getSourceOrder));
        return statList;
    }

    private List<? extends StatementTree> translateStats(List<? extends Statement> stats) {
        if (stats == null) {
            return null;
        }
        ArrayList<StatementTreeImpl> statTrees = new ArrayList<StatementTreeImpl>(stats.size());
        for (Statement statement : stats) {
            this.curStat = null;
            statement.accept(this);
            assert (this.curStat != null);
            statTrees.add(this.curStat);
        }
        return statTrees;
    }

    private List<? extends ExpressionTree> translateParameters(FunctionNode func) {
        Map<IdentNode, Expression> paramExprs = func.getParameterExpressions();
        if (paramExprs != null) {
            List<IdentNode> params = func.getParameters();
            ArrayList<ExpressionTreeImpl> exprTrees = new ArrayList<ExpressionTreeImpl>(params.size());
            for (IdentNode ident : params) {
                IdentNode expr = paramExprs.containsKey(ident) ? paramExprs.get(ident) : ident;
                this.curExpr = null;
                ((Node)expr).accept(this);
                assert (this.curExpr != null);
                exprTrees.add(this.curExpr);
            }
            return exprTrees;
        }
        return this.translateExprs(func.getParameters());
    }

    private List<? extends ExpressionTree> translateExprs(List<? extends Expression> exprs) {
        if (exprs == null) {
            return null;
        }
        ArrayList<ExpressionTreeImpl> exprTrees = new ArrayList<ExpressionTreeImpl>(exprs.size());
        for (Expression expression : exprs) {
            this.curExpr = null;
            expression.accept(this);
            assert (this.curExpr != null);
            exprTrees.add(this.curExpr);
        }
        return exprTrees;
    }

    private ExpressionTreeImpl translateExpr(Expression expr) {
        if (expr == null) {
            return null;
        }
        this.curExpr = null;
        expr.accept(this);
        assert (this.curExpr != null) : "null for " + expr;
        return this.curExpr;
    }

    private StatementTreeImpl translateStat(Statement stat) {
        if (stat == null) {
            return null;
        }
        this.curStat = null;
        stat.accept(this);
        assert (this.curStat != null) : "null for " + stat;
        return this.curStat;
    }

    private static IdentifierTree translateIdent(IdentNode ident) {
        return new IdentifierTreeImpl(ident);
    }

    private List<? extends PropertyTree> translateProperties(List<PropertyNode> propNodes) {
        ArrayList<PropertyTree> propTrees = new ArrayList<PropertyTree>(propNodes.size());
        for (PropertyNode propNode : propNodes) {
            propTrees.add(this.translateProperty(propNode));
        }
        return propTrees;
    }

    private PropertyTree translateProperty(PropertyNode propNode) {
        return new PropertyTreeImpl(propNode, this.translateExpr(propNode.getKey()), this.translateExpr(propNode.getValue()), (FunctionExpressionTree)((Object)this.translateExpr(propNode.getGetter())), (FunctionExpressionTree)((Object)this.translateExpr(propNode.getSetter())));
    }

    private ModuleTree translateModule(FunctionNode func) {
        return func.getKind() == FunctionNode.Kind.MODULE ? ModuleTreeImpl.create(func) : null;
    }
}
