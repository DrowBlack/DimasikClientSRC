package lombok.eclipse.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.core.LombokImmutableList;
import lombok.core.SpiLoadUtil;
import lombok.core.TypeLibrary;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.HandleBuilder;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class EclipseSingularsRecipes {
    private static final EclipseSingularsRecipes INSTANCE = new EclipseSingularsRecipes();
    private final Map<String, EclipseSingularizer> singularizers = new HashMap<String, EclipseSingularizer>();
    private final TypeLibrary singularizableTypes = new TypeLibrary();

    private EclipseSingularsRecipes() {
        try {
            EclipseSingularsRecipes.loadAll(this.singularizableTypes, this.singularizers);
            this.singularizableTypes.lock();
        }
        catch (IOException e) {
            System.err.println("Lombok's @Singularizable feature is broken due to misconfigured SPI files: " + e);
        }
    }

    private static void loadAll(TypeLibrary library, Map<String, EclipseSingularizer> map) throws IOException {
        for (EclipseSingularizer handler : SpiLoadUtil.findServices(EclipseSingularizer.class, EclipseSingularizer.class.getClassLoader())) {
            for (String type : handler.getSupportedTypes()) {
                EclipseSingularizer existingSingularizer = map.get(type);
                if (existingSingularizer != null) {
                    EclipseSingularizer toKeep = existingSingularizer.getClass().getName().compareTo(handler.getClass().getName()) > 0 ? handler : existingSingularizer;
                    System.err.println("Multiple singularizers found for type " + type + "; the alphabetically first class is used: " + toKeep.getClass().getName());
                    map.put(type, toKeep);
                    continue;
                }
                map.put(type, handler);
                library.addType(type);
            }
        }
    }

    public static EclipseSingularsRecipes get() {
        return INSTANCE;
    }

    public String toQualified(String typeReference) {
        List<String> q = this.singularizableTypes.toQualifieds(typeReference);
        if (q.isEmpty()) {
            return null;
        }
        return q.get(0);
    }

    public EclipseSingularizer getSingularizer(String fqn) {
        return this.singularizers.get(fqn);
    }

    public static abstract class EclipseSingularizer {
        protected static final long[] NULL_POSS = new long[1];
        private static final char[] SIZE_TEXT = new char[]{'s', 'i', 'z', 'e'};

        public abstract LombokImmutableList<String> getSupportedTypes();

        public boolean checkForAlreadyExistingNodesAndGenerateError(EclipseNode builderType, SingularData data) {
            block4: for (EclipseNode child : builderType.down()) {
                switch (child.getKind()) {
                    case FIELD: {
                        FieldDeclaration fd = (FieldDeclaration)child.get();
                        char[] name = fd.name;
                        if (name == null || EclipseHandlerUtil.getGeneratedBy((ASTNode)fd) != null) continue block4;
                        for (char[] fieldToBeGenerated : this.listFieldsToBeGenerated(data, builderType)) {
                            if (!Arrays.equals(name, fieldToBeGenerated)) continue;
                            child.addError("Manually adding a field that @Singular @Builder would generate is not supported. If you want to manually manage the builder aspect for this field/parameter, don't use @Singular.");
                            return true;
                        }
                        continue block4;
                    }
                    case METHOD: {
                        AbstractMethodDeclaration method = (AbstractMethodDeclaration)child.get();
                        char[] name = method.selector;
                        if (name == null || EclipseHandlerUtil.getGeneratedBy((ASTNode)method) != null) continue block4;
                        for (char[] methodToBeGenerated : this.listMethodsToBeGenerated(data, builderType)) {
                            if (!Arrays.equals(name, methodToBeGenerated)) continue;
                            child.addError("Manually adding a method that @Singular @Builder would generate is not supported. If you want to manually manage the builder aspect for this field/parameter, don't use @Singular.");
                            return true;
                        }
                        continue block4;
                    }
                }
            }
            return false;
        }

        public List<char[]> listFieldsToBeGenerated(SingularData data, EclipseNode builderType) {
            return Collections.singletonList(data.pluralName);
        }

        public List<char[]> listMethodsToBeGenerated(SingularData data, EclipseNode builderType) {
            char[] s;
            char[] p = data.pluralName;
            if (Arrays.equals(p, s = data.singularName)) {
                return Collections.singletonList(p);
            }
            return Arrays.asList(p, s);
        }

        public abstract List<EclipseNode> generateFields(SingularData var1, EclipseNode var2);

        public void generateMethods(final HandleBuilder.BuilderJob job, SingularData data, boolean deprecate) {
            TypeReferenceMaker returnTypeMaker = new TypeReferenceMaker(){

                @Override
                public TypeReference make() {
                    return job.oldChain ? EclipseHandlerUtil.cloneSelfType(job.builderType) : TypeReference.baseTypeReference((int)6, (int)0);
                }
            };
            StatementMaker returnStatementMaker = new StatementMaker(){

                public ReturnStatement make() {
                    return job.oldChain ? new ReturnStatement((Expression)new ThisReference(0, 0), 0, 0) : null;
                }
            };
            this.generateMethods(job.checkerFramework, data, deprecate, job.builderType, job.oldFluent, returnTypeMaker, returnStatementMaker, job.accessInners);
        }

        public abstract void generateMethods(CheckerFrameworkVersion var1, SingularData var2, boolean var3, EclipseNode var4, boolean var5, TypeReferenceMaker var6, StatementMaker var7, AccessLevel var8);

        public abstract void appendBuildCode(SingularData var1, EclipseNode var2, List<Statement> var3, char[] var4, String var5);

        public boolean shadowedDuringBuild() {
            return true;
        }

        public boolean requiresCleaning() {
            try {
                return !this.getClass().getMethod("appendCleaningCode", SingularData.class, EclipseNode.class, List.class).getDeclaringClass().equals(EclipseSingularizer.class);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                return false;
            }
        }

        public void appendCleaningCode(SingularData data, EclipseNode builderType, List<Statement> statements) {
        }

        protected Annotation[] generateSelfReturnAnnotations(boolean deprecate, ASTNode source) {
            MarkerAnnotation deprecated;
            MarkerAnnotation markerAnnotation = deprecated = deprecate ? EclipseHandlerUtil.generateDeprecatedAnnotation(source) : null;
            if (deprecated == null) {
                return null;
            }
            return new Annotation[]{deprecated};
        }

        protected TypeReference addTypeArgs(int count, boolean addExtends, EclipseNode node, TypeReference type, List<TypeReference> typeArgs) {
            TypeReference[] clonedAndFixedArgs = this.createTypeArgs(count, addExtends, node, typeArgs);
            if (type instanceof SingleTypeReference) {
                type = new ParameterizedSingleTypeReference(((SingleTypeReference)type).token, clonedAndFixedArgs, 0, 0L);
            } else if (type instanceof QualifiedTypeReference) {
                QualifiedTypeReference qtr = (QualifiedTypeReference)type;
                TypeReference[][] trs = new TypeReference[qtr.tokens.length][];
                trs[qtr.tokens.length - 1] = clonedAndFixedArgs;
                type = new ParameterizedQualifiedTypeReference(((QualifiedTypeReference)type).tokens, (TypeReference[][])trs, 0, NULL_POSS);
            } else {
                node.addError("Don't know how to clone-and-parameterize type: " + type);
            }
            return type;
        }

        protected TypeReference[] createTypeArgs(int count, boolean addExtends, EclipseNode node, List<TypeReference> typeArgs) {
            if (count < 0) {
                throw new IllegalArgumentException("count is negative");
            }
            if (count == 0) {
                return null;
            }
            ArrayList<Object> arguments = new ArrayList<Object>();
            if (typeArgs != null) {
                for (TypeReference orig : typeArgs) {
                    Wildcard w;
                    Wildcard wildcard;
                    Wildcard wildcard2 = wildcard = orig instanceof Wildcard ? (Wildcard)orig : null;
                    if (!addExtends) {
                        if (wildcard != null && (wildcard.kind == 0 || wildcard.kind == 2)) {
                            arguments.add(new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, NULL_POSS));
                        } else if (wildcard != null && wildcard.kind == 1) {
                            try {
                                arguments.add(EclipseHandlerUtil.copyType(wildcard.bound));
                            }
                            catch (Exception exception) {
                                arguments.add(new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, NULL_POSS));
                            }
                        } else {
                            arguments.add(EclipseHandlerUtil.copyType(orig));
                        }
                    } else if (wildcard != null && (wildcard.kind == 0 || wildcard.kind == 2)) {
                        w = new Wildcard(0);
                        arguments.add(w);
                    } else if (wildcard != null && wildcard.kind == 1) {
                        arguments.add(EclipseHandlerUtil.copyType(orig));
                    } else {
                        w = new Wildcard(1);
                        w.bound = EclipseHandlerUtil.copyType(orig);
                        arguments.add(w);
                    }
                    if (--count == 0) break;
                }
            }
            while (count-- > 0) {
                if (addExtends) {
                    arguments.add(new Wildcard(0));
                    continue;
                }
                arguments.add(new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, NULL_POSS));
            }
            if (arguments.isEmpty()) {
                return null;
            }
            return arguments.toArray(new TypeReference[0]);
        }

        protected Expression getSize(EclipseNode builderType, char[] name, boolean nullGuard, String builderVariable) {
            MessageSend invoke = new MessageSend();
            Reference thisRef = EclipseSingularizer.getBuilderReference(builderVariable);
            FieldReference thisDotName = new FieldReference(name, 0L);
            thisDotName.receiver = thisRef;
            invoke.receiver = thisDotName;
            invoke.selector = SIZE_TEXT;
            if (!nullGuard) {
                return invoke;
            }
            Reference cdnThisRef = EclipseSingularizer.getBuilderReference(builderVariable);
            FieldReference cdnThisDotName = new FieldReference(name, 0L);
            cdnThisDotName.receiver = cdnThisRef;
            NullLiteral nullLiteral = new NullLiteral(0, 0);
            EqualExpression isNull = new EqualExpression((Expression)cdnThisDotName, (Expression)nullLiteral, OperatorIds.EQUAL_EQUAL);
            IntLiteral zeroLiteral = EclipseHandlerUtil.makeIntLiteral(new char[]{'0'}, null);
            ConditionalExpression conditional = new ConditionalExpression((Expression)isNull, (Expression)zeroLiteral, (Expression)invoke);
            return conditional;
        }

        protected TypeReference cloneParamType(int index, List<TypeReference> typeArgs, EclipseNode builderType) {
            if (typeArgs != null && typeArgs.size() > index) {
                TypeReference originalType = typeArgs.get(index);
                if (originalType instanceof Wildcard) {
                    Wildcard wOriginalType = (Wildcard)originalType;
                    if (wOriginalType.kind == 1) {
                        try {
                            return EclipseHandlerUtil.copyType(wOriginalType.bound);
                        }
                        catch (Exception exception) {}
                    }
                } else {
                    return EclipseHandlerUtil.copyType(originalType);
                }
            }
            return new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, NULL_POSS);
        }

        protected static Reference getBuilderReference(String builderVariable) {
            if ("this".equals(builderVariable)) {
                return new ThisReference(0, 0);
            }
            return new SingleNameReference(builderVariable.toCharArray(), 0L);
        }

        protected void nullBehaviorize(EclipseNode typeNode, SingularData data, List<Statement> statements, Argument arg, MethodDeclaration md) {
            boolean ignoreNullCollections = data.isIgnoreNullCollections();
            if (ignoreNullCollections) {
                EqualExpression isNotNull = new EqualExpression((Expression)new SingleNameReference(data.getPluralName(), 0L), (Expression)new NullLiteral(0, 0), OperatorIds.NOT_EQUAL);
                Block b = new Block(0);
                b.statements = statements.toArray(new Statement[statements.size()]);
                statements.clear();
                statements.add((Statement)new IfStatement((Expression)isNotNull, (Statement)b, 0, 0));
                EclipseHandlerUtil.createRelevantNullableAnnotation(typeNode, arg, md);
                return;
            }
            EclipseHandlerUtil.createRelevantNonNullAnnotation(typeNode, arg, md);
            Statement nullCheck = EclipseHandlerUtil.generateNullCheck(null, data.getPluralName(), typeNode, "%s cannot be null");
            statements.add(0, nullCheck);
        }

        protected abstract int getTypeArgumentsCount();

        protected abstract char[][] getEmptyMakerReceiver(String var1);

        protected abstract char[] getEmptyMakerSelector(String var1);

        public MessageSend getEmptyExpression(String targetFqn, SingularData data, EclipseNode typeNode, ASTNode source) {
            MessageSend send = new MessageSend();
            send.receiver = EclipseHandlerUtil.generateQualifiedNameRef(source, this.getEmptyMakerReceiver(targetFqn));
            send.selector = this.getEmptyMakerSelector(targetFqn);
            send.typeArguments = this.createTypeArgs(this.getTypeArgumentsCount(), false, typeNode, data.getTypeArgs());
            return send;
        }
    }

    public static final class SingularData {
        private final EclipseNode annotation;
        private final char[] singularName;
        private final char[] pluralName;
        private final char[] setterPrefix;
        private final List<TypeReference> typeArgs;
        private final String targetFqn;
        private final EclipseSingularizer singularizer;
        private final boolean ignoreNullCollections;
        private final ASTNode source;

        public SingularData(EclipseNode annotation, char[] singularName, char[] pluralName, List<TypeReference> typeArgs, String targetFqn, EclipseSingularizer singularizer, ASTNode source, boolean ignoreNullCollections) {
            this(annotation, singularName, pluralName, typeArgs, targetFqn, singularizer, source, ignoreNullCollections, new char[0]);
        }

        public SingularData(EclipseNode annotation, char[] singularName, char[] pluralName, List<TypeReference> typeArgs, String targetFqn, EclipseSingularizer singularizer, ASTNode source, boolean ignoreNullCollections, char[] setterPrefix) {
            this.annotation = annotation;
            this.singularName = singularName;
            this.pluralName = pluralName;
            this.typeArgs = typeArgs;
            this.targetFqn = targetFqn;
            this.singularizer = singularizer;
            this.source = source;
            this.ignoreNullCollections = ignoreNullCollections;
            this.setterPrefix = setterPrefix;
        }

        public void setGeneratedByRecursive(ASTNode target) {
            SetGeneratedByVisitor visitor = new SetGeneratedByVisitor(this.source);
            if (target instanceof AbstractMethodDeclaration) {
                ((AbstractMethodDeclaration)target).traverse((ASTVisitor)visitor, null);
            } else if (target instanceof FieldDeclaration) {
                ((FieldDeclaration)target).traverse((ASTVisitor)visitor, null);
            } else {
                target.traverse((ASTVisitor)visitor, null);
            }
        }

        public ASTNode getSource() {
            return this.source;
        }

        public EclipseNode getAnnotation() {
            return this.annotation;
        }

        public char[] getSingularName() {
            return this.singularName;
        }

        public char[] getPluralName() {
            return this.pluralName;
        }

        public char[] getSetterPrefix() {
            return this.setterPrefix;
        }

        public List<TypeReference> getTypeArgs() {
            return this.typeArgs;
        }

        public String getTargetFqn() {
            return this.targetFqn;
        }

        public EclipseSingularizer getSingularizer() {
            return this.singularizer;
        }

        public boolean isIgnoreNullCollections() {
            return this.ignoreNullCollections;
        }

        public String getTargetSimpleType() {
            int idx = this.targetFqn.lastIndexOf(".");
            return idx == -1 ? this.targetFqn : this.targetFqn.substring(idx + 1);
        }
    }

    public static interface StatementMaker {
        public Statement make();
    }

    public static interface TypeReferenceMaker {
        public TypeReference make();
    }
}
