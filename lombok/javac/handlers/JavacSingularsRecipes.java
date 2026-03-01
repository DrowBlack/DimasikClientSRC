package lombok.javac.handlers;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.core.LombokImmutableList;
import lombok.core.SpiLoadUtil;
import lombok.core.TypeLibrary;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.core.handlers.HandlerUtil;
import lombok.javac.Javac;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.HandleBuilder;
import lombok.javac.handlers.JavacHandlerUtil;

public class JavacSingularsRecipes {
    private static final JavacSingularsRecipes INSTANCE = new JavacSingularsRecipes();
    private final Map<String, JavacSingularizer> singularizers = new HashMap<String, JavacSingularizer>();
    private final TypeLibrary singularizableTypes = new TypeLibrary();

    private JavacSingularsRecipes() {
        try {
            JavacSingularsRecipes.loadAll(this.singularizableTypes, this.singularizers);
            this.singularizableTypes.lock();
        }
        catch (IOException e) {
            System.err.println("Lombok's @Singularizable feature is broken due to misconfigured SPI files: " + e);
        }
    }

    private static void loadAll(TypeLibrary library, Map<String, JavacSingularizer> map) throws IOException {
        for (JavacSingularizer handler : SpiLoadUtil.findServices(JavacSingularizer.class, JavacSingularizer.class.getClassLoader())) {
            for (String type : handler.getSupportedTypes()) {
                JavacSingularizer existingSingularizer = map.get(type);
                if (existingSingularizer != null) {
                    JavacSingularizer toKeep = existingSingularizer.getClass().getName().compareTo(handler.getClass().getName()) > 0 ? handler : existingSingularizer;
                    System.err.println("Multiple singularizers found for type " + type + "; the alphabetically first class is used: " + toKeep.getClass().getName());
                    map.put(type, toKeep);
                    continue;
                }
                map.put(type, handler);
                library.addType(type);
            }
        }
    }

    public static JavacSingularsRecipes get() {
        return INSTANCE;
    }

    public String toQualified(String typeReference) {
        java.util.List<String> q = this.singularizableTypes.toQualifieds(typeReference);
        if (q.isEmpty()) {
            return null;
        }
        return q.get(0);
    }

    public JavacSingularizer getSingularizer(String fqn, JavacNode node) {
        JavacSingularizer singularizer = this.singularizers.get(fqn);
        boolean useGuavaInstead = Boolean.TRUE.equals(node.getAst().readConfiguration(ConfigurationKeys.SINGULAR_USE_GUAVA));
        return useGuavaInstead ? singularizer.getGuavaInstead(node) : singularizer;
    }

    public static interface ExpressionMaker {
        public JCTree.JCExpression make();
    }

    public static abstract class JavacSingularizer {
        public abstract LombokImmutableList<String> getSupportedTypes();

        protected JavacSingularizer getGuavaInstead(JavacNode node) {
            return this;
        }

        protected JCTree.JCModifiers makeMods(JavacTreeMaker maker, JavacNode node, boolean deprecate, AccessLevel access, List<JCTree.JCAnnotation> methodAnnotations) {
            JCTree.JCAnnotation deprecateAnn = deprecate ? maker.Annotation(JavacHandlerUtil.genJavaLangTypeRef(node, "Deprecated"), List.<JCTree.JCExpression>nil()) : null;
            List<JCTree.JCAnnotation> annsOnMethod = deprecateAnn != null ? List.of(deprecateAnn) : List.nil();
            annsOnMethod = JavacHandlerUtil.mergeAnnotations(annsOnMethod, methodAnnotations);
            return maker.Modifiers(JavacHandlerUtil.toJavacModifier(access), annsOnMethod);
        }

        public boolean checkForAlreadyExistingNodesAndGenerateError(JavacNode builderType, SingularData data) {
            block4: for (JavacNode child : builderType.down()) {
                switch (child.getKind()) {
                    case FIELD: {
                        JCTree.JCVariableDecl field = (JCTree.JCVariableDecl)child.get();
                        Name name = field.name;
                        if (name == null || JavacHandlerUtil.getGeneratedBy(field) != null) continue block4;
                        for (Name fieldToBeGenerated : this.listFieldsToBeGenerated(data, builderType)) {
                            if (!fieldToBeGenerated.equals(name)) continue;
                            child.addError("Manually adding a field that @Singular @Builder would generate is not supported. If you want to manually manage the builder aspect for this field/parameter, don't use @Singular.");
                            return true;
                        }
                        continue block4;
                    }
                    case METHOD: {
                        JCTree.JCMethodDecl method = (JCTree.JCMethodDecl)child.get();
                        Name name = method.name;
                        if (name == null || JavacHandlerUtil.getGeneratedBy(method) != null) continue block4;
                        for (Name methodToBeGenerated : this.listMethodsToBeGenerated(data, builderType)) {
                            if (!methodToBeGenerated.equals(name)) continue;
                            child.addError("Manually adding a method that @Singular @Builder would generate is not supported. If you want to manually manage the builder aspect for this field/parameter, don't use @Singular.");
                            return true;
                        }
                        continue block4;
                    }
                }
            }
            return false;
        }

        public java.util.List<Name> listFieldsToBeGenerated(SingularData data, JavacNode builderType) {
            return Collections.singletonList(data.pluralName);
        }

        public java.util.List<Name> listMethodsToBeGenerated(SingularData data, JavacNode builderType) {
            Name s;
            Name p = data.pluralName;
            if (p.equals(s = data.singularName)) {
                return Collections.singletonList(p);
            }
            return Arrays.asList(p, s);
        }

        public abstract java.util.List<JavacNode> generateFields(SingularData var1, JavacNode var2, JavacNode var3);

        public void generateMethods(final HandleBuilder.BuilderJob job, SingularData data, boolean deprecate) {
            final JavacTreeMaker maker = job.builderType.getTreeMaker();
            ExpressionMaker returnTypeMaker = new ExpressionMaker(){

                @Override
                public JCTree.JCExpression make() {
                    return job.oldChain ? JavacHandlerUtil.cloneSelfType(job.builderType) : maker.Type(Javac.createVoidType(job.builderType.getSymbolTable(), Javac.CTC_VOID));
                }
            };
            StatementMaker returnStatementMaker = new StatementMaker(){

                @Override
                public JCTree.JCStatement make() {
                    return job.oldChain ? maker.Return(maker.Ident(job.builderType.toName("this"))) : null;
                }
            };
            this.generateMethods(job.checkerFramework, data, deprecate, job.builderType, job.sourceNode, job.oldFluent, returnTypeMaker, returnStatementMaker, job.accessInners);
        }

        public abstract void generateMethods(CheckerFrameworkVersion var1, SingularData var2, boolean var3, JavacNode var4, JavacNode var5, boolean var6, ExpressionMaker var7, StatementMaker var8, AccessLevel var9);

        protected void doGenerateMethods(CheckerFrameworkVersion cfv, SingularData data, boolean deprecate, JavacNode builderType, JavacNode source, boolean fluent, ExpressionMaker returnTypeMaker, StatementMaker returnStatementMaker, AccessLevel access) {
            JavacTreeMaker maker = builderType.getTreeMaker();
            this.generateSingularMethod(cfv, deprecate, maker, returnTypeMaker.make(), returnStatementMaker.make(), data, builderType, source, fluent, access);
            this.generatePluralMethod(cfv, deprecate, maker, returnTypeMaker.make(), returnStatementMaker.make(), data, builderType, source, fluent, access);
            this.generateClearMethod(cfv, deprecate, maker, returnTypeMaker.make(), returnStatementMaker.make(), data, builderType, source, access);
        }

        private void finishAndInjectMethod(CheckerFrameworkVersion cfv, JavacTreeMaker maker, JCTree.JCExpression returnType, JCTree.JCStatement returnStatement, SingularData data, JavacNode builderType, JavacNode source, boolean deprecate, ListBuffer<JCTree.JCStatement> statements, Name methodName, List<JCTree.JCVariableDecl> jcVariableDecls, List<JCTree.JCAnnotation> methodAnnotations, AccessLevel access, Boolean ignoreNullCollections) {
            if (returnStatement != null) {
                statements.append(returnStatement);
            }
            JCTree.JCBlock body = maker.Block(0L, statements.toList());
            JCTree.JCModifiers mods = this.makeMods(maker, builderType, deprecate, access, methodAnnotations);
            List<JCTree.JCTypeParameter> typeParams = List.nil();
            List<JCTree.JCExpression> thrown = List.nil();
            if (ignoreNullCollections != null) {
                if (ignoreNullCollections.booleanValue()) {
                    for (JCTree.JCVariableDecl d : jcVariableDecls) {
                        JavacHandlerUtil.createRelevantNullableAnnotation(builderType, d);
                    }
                } else {
                    for (JCTree.JCVariableDecl d : jcVariableDecls) {
                        JavacHandlerUtil.createRelevantNonNullAnnotation(builderType, d);
                    }
                }
            }
            returnType = JavacHandlerUtil.addCheckerFrameworkReturnsReceiver(returnType, maker, builderType, cfv);
            JCTree.JCMethodDecl method = maker.MethodDef(mods, methodName, returnType, typeParams, jcVariableDecls, thrown, body, null);
            if (returnStatement != null) {
                JavacHandlerUtil.createRelevantNonNullAnnotation(builderType, method);
            }
            JavacHandlerUtil.recursiveSetGeneratedBy(method, source);
            JavacHandlerUtil.injectMethod(builderType, method);
        }

        private void generateClearMethod(CheckerFrameworkVersion cfv, boolean deprecate, JavacTreeMaker maker, JCTree.JCExpression returnType, JCTree.JCStatement returnStatement, SingularData data, JavacNode builderType, JavacNode source, AccessLevel access) {
            JCTree.JCStatement clearStatement = this.generateClearStatements(maker, data, builderType);
            ListBuffer<JCTree.JCStatement> statements = new ListBuffer<JCTree.JCStatement>();
            statements.append(clearStatement);
            Name methodName = builderType.toName(HandlerUtil.buildAccessorName(source, "clear", data.getPluralName().toString()));
            this.finishAndInjectMethod(cfv, maker, returnType, returnStatement, data, builderType, source, deprecate, statements, methodName, List.<JCTree.JCVariableDecl>nil(), List.<JCTree.JCAnnotation>nil(), access, null);
        }

        protected abstract JCTree.JCStatement generateClearStatements(JavacTreeMaker var1, SingularData var2, JavacNode var3);

        private void generateSingularMethod(CheckerFrameworkVersion cfv, boolean deprecate, JavacTreeMaker maker, JCTree.JCExpression returnType, JCTree.JCStatement returnStatement, SingularData data, JavacNode builderType, JavacNode source, boolean fluent, AccessLevel access) {
            ListBuffer<JCTree.JCStatement> statements = this.generateSingularMethodStatements(maker, data, builderType, source);
            List<JCTree.JCVariableDecl> params = this.generateSingularMethodParameters(maker, data, builderType, source);
            Name name = data.getSingularName();
            String setterPrefix = data.getSetterPrefix();
            if (setterPrefix.isEmpty() && !fluent) {
                setterPrefix = this.getAddMethodName();
            }
            if (!setterPrefix.isEmpty()) {
                name = builderType.toName(HandlerUtil.buildAccessorName(source, setterPrefix, name.toString()));
            }
            statements.prepend(this.createConstructBuilderVarIfNeeded(maker, data, builderType, source));
            List<JCTree.JCAnnotation> methodAnnotations = JavacHandlerUtil.copyAnnotations(JavacHandlerUtil.findCopyableToBuilderSingularSetterAnnotations((JavacNode)data.annotation.up()));
            this.finishAndInjectMethod(cfv, maker, returnType, returnStatement, data, builderType, source, deprecate, statements, name, params, methodAnnotations, access, null);
        }

        protected JCTree.JCVariableDecl generateSingularMethodParameter(int typeIndex, JavacTreeMaker maker, SingularData data, JavacNode builderType, JavacNode source, Name name) {
            long flags = JavacHandlerUtil.addFinalIfNeeded(0x200000000L, builderType.getContext());
            JCTree.JCExpression type = this.cloneParamType(typeIndex, maker, data.getTypeArgs(), builderType, source);
            List<JCTree.JCAnnotation> typeUseAnns = JavacHandlerUtil.getTypeUseAnnotations(type);
            type = JavacHandlerUtil.removeTypeUseAnnotations(type);
            JCTree.JCModifiers mods = typeUseAnns.isEmpty() ? maker.Modifiers(flags) : maker.Modifiers(flags, typeUseAnns);
            return maker.VarDef(mods, name, type, null);
        }

        protected JCTree.JCStatement generateSingularMethodAddStatement(JavacTreeMaker maker, JavacNode builderType, Name argumentName, String builderFieldName) {
            JCTree.JCExpression thisDotFieldDotAdd = JavacHandlerUtil.chainDots(builderType, "this", builderFieldName, "add");
            JCTree.JCMethodInvocation invokeAdd = maker.Apply(List.<JCTree.JCExpression>nil(), thisDotFieldDotAdd, List.of(maker.Ident(argumentName)));
            return maker.Exec(invokeAdd);
        }

        protected abstract ListBuffer<JCTree.JCStatement> generateSingularMethodStatements(JavacTreeMaker var1, SingularData var2, JavacNode var3, JavacNode var4);

        protected abstract List<JCTree.JCVariableDecl> generateSingularMethodParameters(JavacTreeMaker var1, SingularData var2, JavacNode var3, JavacNode var4);

        private void generatePluralMethod(CheckerFrameworkVersion cfv, boolean deprecate, JavacTreeMaker maker, JCTree.JCExpression returnType, JCTree.JCStatement returnStatement, SingularData data, JavacNode builderType, JavacNode source, boolean fluent, AccessLevel access) {
            ListBuffer<JCTree.JCStatement> statements = this.generatePluralMethodStatements(maker, data, builderType, source);
            Name name = data.getPluralName();
            String setterPrefix = data.getSetterPrefix();
            if (setterPrefix.isEmpty() && !fluent) {
                setterPrefix = String.valueOf(this.getAddMethodName()) + "All";
            }
            if (!setterPrefix.isEmpty()) {
                name = builderType.toName(HandlerUtil.buildAccessorName(source, setterPrefix, name.toString()));
            }
            JCTree.JCExpression paramType = this.getPluralMethodParamType(builderType);
            paramType = this.addTypeArgs(this.getTypeArgumentsCount(), true, builderType, paramType, data.getTypeArgs(), source);
            long paramFlags = JavacHandlerUtil.addFinalIfNeeded(0x200000000L, builderType.getContext());
            boolean ignoreNullCollections = data.isIgnoreNullCollections();
            JCTree.JCModifiers paramMods = maker.Modifiers(paramFlags);
            JCTree.JCVariableDecl param = maker.VarDef(paramMods, data.getPluralName(), paramType, null);
            statements.prepend(this.createConstructBuilderVarIfNeeded(maker, data, builderType, source));
            if (ignoreNullCollections) {
                JCTree.JCBinary incomingIsNotNull = maker.Binary(Javac.CTC_NOT_EQUAL, maker.Ident(data.getPluralName()), maker.Literal(Javac.CTC_BOT, null));
                JCTree.JCBlock onNotNull = maker.Block(0L, statements.toList());
                statements = new ListBuffer();
                statements.append(maker.If(incomingIsNotNull, onNotNull, null));
            } else {
                statements.prepend(JavacHandlerUtil.generateNullCheck(maker, null, data.getPluralName(), builderType, "%s cannot be null"));
            }
            List<JCTree.JCAnnotation> methodAnnotations = JavacHandlerUtil.copyAnnotations(JavacHandlerUtil.findCopyableToSetterAnnotations((JavacNode)data.annotation.up()));
            this.finishAndInjectMethod(cfv, maker, returnType, returnStatement, data, builderType, source, deprecate, statements, name, List.of(param), methodAnnotations, access, ignoreNullCollections);
        }

        protected ListBuffer<JCTree.JCStatement> generatePluralMethodStatements(JavacTreeMaker maker, SingularData data, JavacNode builderType, JavacNode source) {
            ListBuffer<JCTree.JCStatement> statements = new ListBuffer<JCTree.JCStatement>();
            JCTree.JCExpression thisDotFieldDotAdd = JavacHandlerUtil.chainDots(builderType, "this", data.getPluralName().toString(), String.valueOf(this.getAddMethodName()) + "All");
            JCTree.JCMethodInvocation invokeAdd = maker.Apply(List.<JCTree.JCExpression>nil(), thisDotFieldDotAdd, List.of(maker.Ident(data.getPluralName())));
            statements.append(maker.Exec(invokeAdd));
            return statements;
        }

        protected abstract JCTree.JCExpression getPluralMethodParamType(JavacNode var1);

        protected abstract JCTree.JCStatement createConstructBuilderVarIfNeeded(JavacTreeMaker var1, SingularData var2, JavacNode var3, JavacNode var4);

        public abstract void appendBuildCode(SingularData var1, JavacNode var2, JavacNode var3, ListBuffer<JCTree.JCStatement> var4, Name var5, String var6);

        public boolean shadowedDuringBuild() {
            return true;
        }

        public boolean requiresCleaning() {
            try {
                return !this.getClass().getMethod("appendCleaningCode", SingularData.class, JavacNode.class, JCTree.class, ListBuffer.class).getDeclaringClass().equals(JavacSingularizer.class);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                return false;
            }
        }

        public void appendCleaningCode(SingularData data, JavacNode builderType, JavacNode source, ListBuffer<JCTree.JCStatement> statements) {
        }

        protected JCTree.JCExpression addTypeArgs(int count, boolean addExtends, JavacNode node, JCTree.JCExpression type, List<JCTree.JCExpression> typeArgs, JavacNode source) {
            JavacTreeMaker maker = node.getTreeMaker();
            List<JCTree.JCExpression> clonedAndFixedTypeArgs = this.createTypeArgs(count, addExtends, node, typeArgs, source);
            return maker.TypeApply(type, clonedAndFixedTypeArgs);
        }

        protected List<JCTree.JCExpression> createTypeArgs(int count, boolean addExtends, JavacNode node, List<JCTree.JCExpression> typeArgs, JavacNode source) {
            JavacTreeMaker maker = node.getTreeMaker();
            if (count < 0) {
                throw new IllegalArgumentException("count is negative");
            }
            if (count == 0) {
                return List.nil();
            }
            ListBuffer<JCTree.JCExpression> arguments = new ListBuffer<JCTree.JCExpression>();
            if (typeArgs != null) {
                for (JCTree.JCExpression orig : typeArgs) {
                    if (!addExtends) {
                        if (orig.getKind() == Tree.Kind.UNBOUNDED_WILDCARD || orig.getKind() == Tree.Kind.SUPER_WILDCARD) {
                            arguments.append(JavacHandlerUtil.genJavaLangTypeRef(node, "Object"));
                        } else if (orig.getKind() == Tree.Kind.EXTENDS_WILDCARD) {
                            JCTree.JCExpression inner;
                            try {
                                inner = (JCTree.JCExpression)((JCTree.JCWildcard)orig).inner;
                            }
                            catch (Exception exception) {
                                inner = JavacHandlerUtil.genJavaLangTypeRef(node, "Object");
                            }
                            arguments.append(JavacHandlerUtil.cloneType(maker, inner, source));
                        } else {
                            arguments.append(JavacHandlerUtil.cloneType(maker, orig, source));
                        }
                    } else if (orig.getKind() == Tree.Kind.UNBOUNDED_WILDCARD || orig.getKind() == Tree.Kind.SUPER_WILDCARD) {
                        arguments.append(maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null));
                    } else if (orig.getKind() == Tree.Kind.EXTENDS_WILDCARD) {
                        arguments.append(JavacHandlerUtil.cloneType(maker, orig, source));
                    } else {
                        arguments.append(maker.Wildcard(maker.TypeBoundKind(BoundKind.EXTENDS), JavacHandlerUtil.cloneType(maker, orig, source)));
                    }
                    if (--count == 0) break;
                }
            }
            while (count-- > 0) {
                if (addExtends) {
                    arguments.append(maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null));
                    continue;
                }
                arguments.append(JavacHandlerUtil.genJavaLangTypeRef(node, "Object"));
            }
            return arguments.toList();
        }

        protected JCTree.JCExpression getSize(JavacTreeMaker maker, JavacNode builderType, Name name, boolean nullGuard, boolean parens, String builderVariable) {
            Name thisName = builderType.toName(builderVariable);
            JCTree.JCFieldAccess fn = maker.Select(maker.Select(maker.Ident(thisName), name), builderType.toName("size"));
            JCTree.JCMethodInvocation sizeInvoke = maker.Apply(List.<JCTree.JCExpression>nil(), fn, List.<JCTree.JCExpression>nil());
            if (nullGuard) {
                JCTree.JCBinary isNull = maker.Binary(Javac.CTC_EQUAL, maker.Select(maker.Ident(thisName), name), maker.Literal(Javac.CTC_BOT, null));
                JCTree.JCConditional out = maker.Conditional(isNull, maker.Literal(Javac.CTC_INT, 0), sizeInvoke);
                if (parens) {
                    return maker.Parens(out);
                }
                return out;
            }
            return sizeInvoke;
        }

        protected JCTree.JCExpression cloneParamType(int index, JavacTreeMaker maker, List<JCTree.JCExpression> typeArgs, JavacNode builderType, JavacNode source) {
            if (typeArgs == null || typeArgs.size() <= index) {
                return JavacHandlerUtil.genJavaLangTypeRef(builderType, "Object");
            }
            JCTree.JCExpression originalType = typeArgs.get(index);
            if (originalType.getKind() == Tree.Kind.UNBOUNDED_WILDCARD || originalType.getKind() == Tree.Kind.SUPER_WILDCARD) {
                return JavacHandlerUtil.genJavaLangTypeRef(builderType, "Object");
            }
            if (originalType.getKind() == Tree.Kind.EXTENDS_WILDCARD) {
                try {
                    return JavacHandlerUtil.cloneType(maker, (JCTree.JCExpression)((JCTree.JCWildcard)originalType).inner, source);
                }
                catch (Exception exception) {
                    return JavacHandlerUtil.genJavaLangTypeRef(builderType, "Object");
                }
            }
            return JavacHandlerUtil.cloneType(maker, originalType, source);
        }

        protected abstract String getAddMethodName();

        protected abstract int getTypeArgumentsCount();

        protected abstract String getEmptyMaker(String var1);

        public JCTree.JCExpression getEmptyExpression(String target, JavacTreeMaker maker, SingularData data, JavacNode builderType, JavacNode source) {
            String emptyMaker = this.getEmptyMaker(target);
            List<JCTree.JCExpression> typeArgs = this.createTypeArgs(this.getTypeArgumentsCount(), false, builderType, data.getTypeArgs(), source);
            return maker.Apply(typeArgs, JavacHandlerUtil.chainDots(builderType, emptyMaker.split("\\.")), List.<JCTree.JCExpression>nil());
        }
    }

    public static final class SingularData {
        private final JavacNode annotation;
        private final Name singularName;
        private final Name pluralName;
        private final List<JCTree.JCExpression> typeArgs;
        private final String targetFqn;
        private final JavacSingularizer singularizer;
        private final String setterPrefix;
        private final boolean ignoreNullCollections;

        public SingularData(JavacNode annotation, Name singularName, Name pluralName, List<JCTree.JCExpression> typeArgs, String targetFqn, JavacSingularizer singularizer, boolean ignoreNullCollections) {
            this(annotation, singularName, pluralName, typeArgs, targetFqn, singularizer, ignoreNullCollections, "");
        }

        public SingularData(JavacNode annotation, Name singularName, Name pluralName, List<JCTree.JCExpression> typeArgs, String targetFqn, JavacSingularizer singularizer, boolean ignoreNullCollections, String setterPrefix) {
            this.annotation = annotation;
            this.singularName = singularName;
            this.pluralName = pluralName;
            this.typeArgs = typeArgs;
            this.targetFqn = targetFqn;
            this.singularizer = singularizer;
            this.setterPrefix = setterPrefix;
            this.ignoreNullCollections = ignoreNullCollections;
        }

        public JavacNode getAnnotation() {
            return this.annotation;
        }

        public Name getSingularName() {
            return this.singularName;
        }

        public Name getPluralName() {
            return this.pluralName;
        }

        public String getSetterPrefix() {
            return this.setterPrefix;
        }

        public List<JCTree.JCExpression> getTypeArgs() {
            return this.typeArgs;
        }

        public String getTargetFqn() {
            return this.targetFqn;
        }

        public JavacSingularizer getSingularizer() {
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
        public JCTree.JCStatement make();
    }
}
