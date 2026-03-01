package lombok.eclipse.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.core.AgentLauncher;
import lombok.eclipse.agent.EclipseLoaderPatcher;
import lombok.patcher.Filter;
import lombok.patcher.Hook;
import lombok.patcher.MethodTarget;
import lombok.patcher.ScriptManager;
import lombok.patcher.StackRequest;
import lombok.patcher.TargetMatcher;
import lombok.patcher.TransplantMapper;
import lombok.patcher.scripts.ScriptBuilder;

public class EclipsePatcher
implements AgentLauncher.AgentLaunchable {
    @Override
    public void runAgent(String agentArgs, Instrumentation instrumentation, boolean injected, Class<?> launchingContext) throws Exception {
        EclipsePatcher.registerPatchScripts(instrumentation, injected, launchingContext);
    }

    private static void registerPatchScripts(Instrumentation instrumentation, boolean reloadExistingClasses, Class<?> launchingContext) {
        ScriptManager sm = new ScriptManager();
        sm.registerTransformer(instrumentation);
        sm.setFilter(new Filter(){

            @Override
            public boolean shouldTransform(ClassLoader loader, String className, Class<?> classBeingDefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (loader != null) {
                    if (loader.getClass().getName().startsWith("org.sonar.classloader.")) {
                        return false;
                    }
                    if (loader.toString().contains("com.alexnederlof:jasperreports-plugin")) {
                        return false;
                    }
                    if (loader.toString().contains("com.pro-crafting.tools:jasperreports-plugin")) {
                        return false;
                    }
                }
                if (!(loader instanceof URLClassLoader)) {
                    return true;
                }
                ClassLoader parent = loader.getParent();
                if (parent == null) {
                    return true;
                }
                return !parent.getClass().getName().startsWith("org.eclipse.jdt.apt.core.internal.AnnotationProcessorFactoryLoader");
            }
        });
        final boolean forceBaseResourceNames = EclipsePatcher.shouldForceBaseResourceNames();
        sm.setTransplantMapper(new TransplantMapper(){

            @Override
            public String mapResourceName(int classFileFormatVersion, String resourceName) {
                if (classFileFormatVersion < 50 || forceBaseResourceNames) {
                    return resourceName;
                }
                return "Class50/" + resourceName;
            }
        });
        EclipseLoaderPatcher.patchEquinoxLoaders(sm, launchingContext);
        EclipsePatcher.patchCatchReparse(sm);
        EclipsePatcher.patchIdentifierEndReparse(sm);
        EclipsePatcher.patchRetrieveEllipsisStartPosition(sm);
        EclipsePatcher.patchRetrieveRightBraceOrSemiColonPosition(sm);
        EclipsePatcher.patchRetrieveProperRightBracketPosition(sm);
        EclipsePatcher.patchRetrieveStartBlockPosition(sm);
        EclipsePatcher.patchSetGeneratedFlag(sm);
        EclipsePatcher.patchDomAstReparseIssues(sm);
        EclipsePatcher.patchHideGeneratedNodes(sm);
        EclipsePatcher.patchPostCompileHookEclipse(sm);
        EclipsePatcher.patchFixSourceTypeConverter(sm);
        EclipsePatcher.patchListRewriteHandleGeneratedMethods(sm);
        EclipsePatcher.patchSyntaxAndOccurrencesHighlighting(sm);
        EclipsePatcher.patchSortMembersOperation(sm);
        EclipsePatcher.patchExtractInterfaceAndPullUp(sm);
        EclipsePatcher.patchAboutDialog(sm);
        EclipsePatcher.patchEclipseDebugPatches(sm);
        EclipsePatcher.patchJavadoc(sm);
        EclipsePatcher.patchASTConverterLiterals(sm);
        EclipsePatcher.patchASTNodeSearchUtil(sm);
        EclipsePatcher.patchFieldInitializer(sm);
        EclipsePatcher.patchPostCompileHookEcj(sm);
        EclipsePatcher.patchAvoidReparsingGeneratedCode(sm);
        EclipsePatcher.patchLombokizeAST(sm);
        EclipsePatcher.patchEcjTransformers(sm);
        EclipsePatcher.patchExtensionMethod(sm);
        EclipsePatcher.patchRenameField(sm);
        EclipsePatcher.patchInline(sm);
        EclipsePatcher.patchNullCheck(sm);
        EclipsePatcher.patchCrossModuleClassLoading(sm);
        EclipsePatcher.patchForTests(sm);
        if (reloadExistingClasses) {
            sm.reloadClasses(instrumentation);
        }
    }

    private static boolean shouldForceBaseResourceNames() {
        String shadowOverride = System.getProperty("shadow.override.lombok", "");
        if (shadowOverride == null || shadowOverride.length() == 0) {
            return false;
        }
        String[] stringArray = shadowOverride.split("\\s*" + (File.pathSeparatorChar == ';' ? ";" : ":") + "\\s*");
        int n = stringArray.length;
        int n2 = 0;
        while (n2 < n) {
            String part = stringArray[n2];
            if (part.equalsIgnoreCase("lombok.jar")) {
                return false;
            }
            ++n2;
        }
        return true;
    }

    private static void patchRenameField(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.rename.RenameFieldProcessor", "checkAccessorDeclarations", "org.eclipse.ltk.core.refactoring.RefactoringStatus", "org.eclipse.core.runtime.IProgressMonitor", "org.eclipse.jdt.core.IMethod")).methodToWrap(new Hook("org.eclipse.jdt.internal.corext.refactoring.RefactoringSearchEngine", "search", "org.eclipse.jdt.internal.corext.refactoring.SearchResultGroup[]", "org.eclipse.jdt.core.search.SearchPattern", "org.eclipse.jdt.core.search.IJavaSearchScope", "org.eclipse.core.runtime.IProgressMonitor", "org.eclipse.ltk.core.refactoring.RefactoringStatus")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "createFakeSearchResult", "org.eclipse.jdt.internal.corext.refactoring.SearchResultGroup[]", "org.eclipse.jdt.internal.corext.refactoring.SearchResultGroup[]", "java.lang.Object")).requestExtra(StackRequest.THIS).transplant().build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.core.search.matching.MatchingNodeSet", "addPossibleMatch", "void", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).request(StackRequest.PARAM1).transplant().build());
    }

    private static void patchExtractInterfaceAndPullUp(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.SourceElementNotifier", "notifySourceElementRequestor", "void", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration", "org.eclipse.jdt.internal.compiler.ast.ImportReference")).methodToWrap(new Hook("org.eclipse.jdt.internal.compiler.util.HashtableOfObjectToInt", "get", "int", "java.lang.Object")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "getSourceEndFixed", "int", "int", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).requestExtra(StackRequest.PARAM1).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.ExtractInterfaceProcessor", "createMethodDeclaration", "void", "org.eclipse.jdt.internal.corext.refactoring.structure.CompilationUnitRewrite", "org.eclipse.jdt.core.dom.rewrite.ASTRewrite", "org.eclipse.jdt.core.dom.AbstractTypeDeclaration", "org.eclipse.jdt.core.dom.MethodDeclaration")).methodToWrap(new Hook("org.eclipse.jface.text.IDocument", "get", "java.lang.String", "int", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "getRealMethodDeclarationSource", "java.lang.String", "java.lang.String", "java.lang.Object", "org.eclipse.jdt.core.dom.MethodDeclaration")).requestExtra(StackRequest.THIS, StackRequest.PARAM4).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.HierarchyProcessor", "createPlaceholderForSingleVariableDeclaration", "org.eclipse.jdt.core.dom.SingleVariableDeclaration", "org.eclipse.jdt.core.dom.SingleVariableDeclaration", "org.eclipse.jdt.core.ICompilationUnit", "org.eclipse.jdt.core.dom.rewrite.ASTRewrite")).target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.HierarchyProcessor", "createPlaceholderForType", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.core.ICompilationUnit", "org.eclipse.jdt.core.dom.rewrite.ASTRewrite")).methodToWrap(new Hook("org.eclipse.jdt.core.IBuffer", "getText", "java.lang.String", "int", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "getRealNodeSource", "java.lang.String", "java.lang.String", "org.eclipse.jdt.core.dom.ASTNode")).requestExtra(StackRequest.PARAM1).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.core.dom.rewrite.ListRewrite", "insertFirst")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "isListRewriteOnGeneratedNode", "boolean", "org.eclipse.jdt.core.dom.rewrite.ListRewrite")).request(StackRequest.THIS).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.ExtractInterfaceProcessor", "createMethodComment")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.core.dom.ASTNode")).request(StackRequest.PARAM2).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.MemberVisibilityAdjustor$IncomingMemberVisibilityAdjustment", "rewriteVisibility")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "skipRewriteVisibility", "boolean", "org.eclipse.jdt.internal.corext.refactoring.structure.MemberVisibilityAdjustor$IncomingMemberVisibilityAdjustment")).request(StackRequest.THIS).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.ImportRemover", "registerRemovedNode", "void", "org.eclipse.jdt.core.dom.ASTNode")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.core.dom.ASTNode")).request(StackRequest.PARAM1).transplant().build());
    }

    private static void patchInline(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.code.SourceProvider", "getCodeBlocks", "java.lang.String[]", "org.eclipse.jdt.internal.corext.refactoring.code.CallContext", "org.eclipse.jdt.core.dom.rewrite.ImportRewrite")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "getRealCodeBlocks", "java.lang.String[]", "java.lang.String[]", "org.eclipse.jdt.internal.corext.refactoring.code.SourceProvider", "org.eclipse.jdt.internal.corext.refactoring.code.CallContext")).request(StackRequest.RETURN_VALUE, StackRequest.THIS, StackRequest.PARAM1).transplant().build());
    }

    private static void patchAboutDialog(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.core.internal.runtime.Product", "getProperty", "java.lang.String", "java.lang.String")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$LombokDeps", "addLombokNotesToEclipseAboutDialog", "java.lang.String", "java.lang.String", "java.lang.String")).request(StackRequest.RETURN_VALUE, StackRequest.PARAM1).transplant().build());
    }

    private static void patchSyntaxAndOccurrencesHighlighting(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.ui.search.OccurrencesFinder", "addUsage")).target(new MethodTarget("org.eclipse.jdt.internal.ui.search.OccurrencesFinder", "addWrite")).target(new MethodTarget("org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightingReconciler$PositionCollector", "visit", "boolean", "org.eclipse.jdt.core.dom.SimpleName")).target(new MethodTarget("org.eclipse.jdt.internal.ui.javaeditor.SemanticHighlightingReconciler$PositionCollector", "visitLiteral", "boolean", "org.eclipse.jdt.core.dom.Expression")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.core.dom.ASTNode")).valueMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "returnFalse", "boolean", "java.lang.Object")).request(StackRequest.PARAM1).build());
    }

    private static void patchListRewriteHandleGeneratedMethods(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.dom.rewrite.ASTRewriteAnalyzer$ListRewriter", "rewriteList")).methodToReplace(new Hook("org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent", "getChildren", "org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent[]", new String[0])).replacementMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "listRewriteHandleGeneratedMethods", "org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent[]", "org.eclipse.jdt.internal.core.dom.rewrite.RewriteEvent")).build());
    }

    private static void patchSortMembersOperation(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.CompilationUnit")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.CompilationUnit", "types", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.AnnotationTypeDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.AnnotationTypeDeclaration", "bodyDeclarations", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.AnonymousClassDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.AnonymousClassDeclaration", "bodyDeclarations", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.TypeDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.TypeDeclaration", "bodyDeclarations", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.EnumDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.EnumDeclaration", "bodyDeclarations", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SortElementsOperation$2", "visit", "boolean", "org.eclipse.jdt.core.dom.EnumDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.EnumDeclaration", "enumConstants", "java.util.List", new String[0])).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "removeGeneratedNodes", "java.util.List", "java.util.List")).transplant().build());
    }

    private static void patchDomAstReparseIssues(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.dom.rewrite.ASTRewriteAnalyzer", "visit")).methodToReplace(new Hook("org.eclipse.jdt.internal.core.dom.rewrite.TokenScanner", "getTokenEndOffset", "int", "int", "int")).replacementMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "getTokenEndOffsetFixed", "int", "org.eclipse.jdt.internal.core.dom.rewrite.TokenScanner", "int", "int", "java.lang.Object")).requestExtra(StackRequest.PARAM1).transplant().build());
    }

    private static void patchPostCompileHookEclipse(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.builder.IncrementalImageBuilder", "writeClassFileContents")).target(new MethodTarget("org.eclipse.jdt.internal.core.builder.AbstractImageBuilder", "writeClassFileContents")).methodToWrap(new Hook("org.eclipse.jdt.internal.compiler.ClassFile", "getBytes", "byte[]", new String[0])).wrapMethod(new Hook("lombok.launch.PatchFixesHider$LombokDeps", "runPostCompiler", "byte[]", "byte[]", "java.lang.String")).requestExtra(StackRequest.PARAM3).build());
    }

    private static void patchPostCompileHookEcj(ScriptManager sm) {
        sm.addScriptIfNotWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.tool.EclipseCompilerImpl", "outputClassFiles")).methodToWrap(new Hook("javax.tools.JavaFileObject", "openOutputStream", "java.io.OutputStream", new String[0])).wrapMethod(new Hook("lombok.launch.PatchFixesHider$LombokDeps", "runPostCompiler", "java.io.OutputStream", "java.io.OutputStream")).transplant().build());
        sm.addScriptIfNotWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.util.Util", "writeToDisk")).methodToWrap(new Hook("java.io.BufferedOutputStream", "<init>", "void", "java.io.OutputStream", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$LombokDeps", "runPostCompiler", "java.io.BufferedOutputStream", "java.io.BufferedOutputStream", "java.lang.String", "java.lang.String")).requestExtra(StackRequest.PARAM2, StackRequest.PARAM3).transplant().build());
    }

    private static void patchHideGeneratedNodes(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder", "findByNode", "org.eclipse.jdt.core.dom.SimpleName[]", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.core.dom.SimpleName")).target(new MethodTarget("org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder", "findByBinding")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "removeGeneratedSimpleNames", "org.eclipse.jdt.core.dom.SimpleName[]", "org.eclipse.jdt.core.dom.SimpleName[]")).request(StackRequest.RETURN_VALUE).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder", "findByNode", "org.eclipse.jdt.core.dom.Name[]", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.core.dom.Name")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "removeGeneratedNames", "org.eclipse.jdt.core.dom.Name[]", "org.eclipse.jdt.core.dom.Name[]")).request(StackRequest.RETURN_VALUE).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTNode", "accept", "void", "org.eclipse.jdt.core.dom.ASTVisitor")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "isBlockedVisitorAndGenerated", "boolean", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.core.dom.ASTVisitor")).request(StackRequest.THIS, StackRequest.PARAM1).build());
        EclipsePatcher.patchRefactorScripts(sm);
        EclipsePatcher.patchFormatters(sm);
    }

    private static void patchFormatters(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.setSymbolDuringMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.formatter.DefaultCodeFormatter", "formatCompilationUnit")).callToWrap(new Hook("org.eclipse.jdt.internal.core.util.CodeSnippetParsingUtil", "parseCompilationUnit", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration", "char[]", "java.util.Map", "boolean")).symbol("lombok.disable").build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.setSymbolDuringMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.formatter.DefaultCodeFormatter", "parseSourceCode")).callToWrap(new Hook("org.eclipse.jdt.core.dom.ASTParser", "createAST", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.core.runtime.IProgressMonitor")).symbol("lombok.disable").build());
    }

    private static void patchRefactorScripts(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.core.dom.rewrite.ASTRewrite", "replace")).target(new MethodTarget("org.eclipse.jdt.core.dom.rewrite.ASTRewrite", "remove")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "skipRewritingGeneratedNodes", "boolean", "org.eclipse.jdt.core.dom.ASTNode")).transplant().request(StackRequest.PARAM1).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.rename.RenameTypeProcessor", "addConstructorRenames")).methodToWrap(new Hook("org.eclipse.jdt.core.IType", "getMethods", "org.eclipse.jdt.core.IMethod[]", new String[0])).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "removeGeneratedMethods", "org.eclipse.jdt.core.IMethod[]", "org.eclipse.jdt.core.IMethod[]")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.rename.TempOccurrenceAnalyzer", "visit", "boolean", "org.eclipse.jdt.core.dom.SimpleName")).target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.rename.RenameAnalyzeUtil$ProblemNodeFinder$NameNodeVisitor", "visit", "boolean", "org.eclipse.jdt.core.dom.SimpleName")).target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.rename.RenameTypeParameterProcessor$RenameTypeParameterVisitor", "visit", "boolean", "org.eclipse.jdt.core.dom.SimpleName")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.core.dom.ASTNode")).valueMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "returnTrue", "boolean", "java.lang.Object")).request(StackRequest.PARAM1).transplant().build());
    }

    private static void patchCatchReparse(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "retrieveStartingCatchPosition")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "fixRetrieveStartingCatchPosition", "int", "int", "int")).transplant().request(StackRequest.RETURN_VALUE, StackRequest.PARAM1).build());
    }

    private static void patchIdentifierEndReparse(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "retrieveIdentifierEndPosition")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "fixRetrieveIdentifierEndPosition", "int", "int", "int", "int")).transplant().request(StackRequest.RETURN_VALUE, StackRequest.PARAM1, StackRequest.PARAM2).build());
    }

    private static void patchRetrieveEllipsisStartPosition(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "retrieveEllipsisStartPosition")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "fixRetrieveEllipsisStartPosition", "int", "int", "int")).transplant().request(StackRequest.RETURN_VALUE, StackRequest.PARAM2).build());
    }

    private static void patchRetrieveStartBlockPosition(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "retrieveStartBlockPosition")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "fixRetrieveStartBlockPosition", "int", "int", "int")).transplant().request(StackRequest.RETURN_VALUE, StackRequest.PARAM2).build());
    }

    private static void patchRetrieveRightBraceOrSemiColonPosition(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.ASTNode", "boolean", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.ASTConverter", "retrieveRightBraceOrSemiColonPosition", "int", "int", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "fixRetrieveRightBraceOrSemiColonPosition", "int", "int", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration")).requestExtra(StackRequest.PARAM2).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.ASTNode", "boolean", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.ASTConverter", "retrieveRightBrace", "int", "int", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "fixRetrieveRightBraceOrSemiColonPosition", "int", "int", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration")).requestExtra(StackRequest.PARAM2).transplant().build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.FieldDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.ASTConverter", "retrieveRightBrace", "int", "int", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "fixRetrieveRightBraceOrSemiColonPosition", "int", "int", "org.eclipse.jdt.internal.compiler.ast.FieldDeclaration")).requestExtra(StackRequest.PARAM1).transplant().build());
    }

    private static void patchRetrieveProperRightBracketPosition(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "extractSubArrayType", "org.eclipse.jdt.core.dom.ArrayType", "org.eclipse.jdt.core.dom.ArrayType", "int", "int")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.ASTConverter", "retrieveProperRightBracketPosition", "int", "int", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "fixRetrieveProperRightBracketPosition", "int", "int", "org.eclipse.jdt.core.dom.Type")).requestExtra(StackRequest.PARAM1).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToArray", "org.eclipse.jdt.core.dom.ArrayType", "org.eclipse.jdt.core.dom.Type", "int", "int", "int", "org.eclipse.jdt.internal.compiler.ast.Annotation[][]")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.ASTConverter", "retrieveProperRightBracketPosition", "int", "int", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "fixRetrieveProperRightBracketPosition", "int", "int", "org.eclipse.jdt.core.dom.Type")).requestExtra(StackRequest.PARAM1).transplant().build());
    }

    private static void patchSetGeneratedFlag(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.addField().targetClass("org.eclipse.jdt.internal.compiler.ast.ASTNode").fieldName("$generatedBy").fieldType("Lorg/eclipse/jdt/internal/compiler/ast/ASTNode;").setPublic().setTransient().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.addField().targetClass("org.eclipse.jdt.core.dom.ASTNode").fieldName("$isGenerated").fieldType("Z").setPublic().setTransient().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new TargetMatcher(){

            @Override
            public boolean matches(String classSpec, String methodName, String descriptor) {
                if (!"convert".equals(methodName)) {
                    return false;
                }
                List<String> fullDesc = MethodTarget.decomposeFullDesc(descriptor);
                if ("V".equals(fullDesc.get(0))) {
                    return false;
                }
                if (fullDesc.size() < 2) {
                    return false;
                }
                return fullDesc.get(1).startsWith("Lorg/eclipse/jdt/internal/compiler/ast/");
            }

            @Override
            public String describe() {
                return "ASTConverter:[all relevant]";
            }

            @Override
            public Collection<String> getAffectedClasses() {
                return Collections.singleton("org.eclipse.jdt.core.dom.ASTConverter");
            }
        }).request(StackRequest.PARAM1, StackRequest.RETURN_VALUE).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.ASTNode", "boolean", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration")).request(StackRequest.PARAM2, StackRequest.RETURN_VALUE).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToFieldDeclaration", "org.eclipse.jdt.core.dom.FieldDeclaration", "org.eclipse.jdt.internal.compiler.ast.FieldDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToType", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.internal.compiler.ast.NameReference")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertType", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.internal.compiler.ast.TypeReference")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToVariableDeclarationExpression", "org.eclipse.jdt.core.dom.VariableDeclarationExpression", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToSingleVariableDeclaration", "org.eclipse.jdt.core.dom.SingleVariableDeclaration", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToVariableDeclarationFragment", "org.eclipse.jdt.core.dom.VariableDeclarationFragment", "org.eclipse.jdt.internal.compiler.ast.FieldDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToVariableDeclarationFragment", "org.eclipse.jdt.core.dom.VariableDeclarationFragment", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertToVariableDeclarationStatement", "org.eclipse.jdt.core.dom.VariableDeclarationStatement", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "createBaseType", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.internal.compiler.ast.TypeReference", "long[]", "org.eclipse.jdt.internal.compiler.ast.Annotation[][]", "char[][]", "int", "int", "boolean")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "createQualifiedType", "org.eclipse.jdt.core.dom.QualifiedType", "org.eclipse.jdt.internal.compiler.ast.TypeReference", "long[]", "org.eclipse.jdt.internal.compiler.ast.Annotation[][]", "char[][]", "int", "org.eclipse.jdt.core.dom.Type")).request(StackRequest.PARAM1, StackRequest.RETURN_VALUE).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new TargetMatcher(){

            @Override
            public boolean matches(String classSpec, String methodName, String descriptor) {
                if (!methodName.startsWith("convert")) {
                    return false;
                }
                List<String> fullDesc = MethodTarget.decomposeFullDesc(descriptor);
                if (fullDesc.size() < 2) {
                    return false;
                }
                return fullDesc.get(1).startsWith("Lorg/eclipse/jdt/internal/compiler/ast/");
            }

            @Override
            public String describe() {
                return "ASTConverter::(all relevant)";
            }

            @Override
            public Collection<String> getAffectedClasses() {
                return Collections.singleton("org.eclipse.jdt.core.dom.ASTConverter");
            }
        }).methodToWrap(new Hook("org.eclipse.jdt.core.dom.SimpleName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM1).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.ASTNode", "boolean", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.SimpleName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM2).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.ASTNode", "boolean", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.Block", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM2).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertType", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.internal.compiler.ast.TypeReference")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.PrimitiveType", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM1).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertType", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.internal.compiler.ast.TypeReference")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.SimpleType", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM1).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertType", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.internal.compiler.ast.TypeReference")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.ParameterizedType", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM1).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convertType", "org.eclipse.jdt.core.dom.Type", "org.eclipse.jdt.internal.compiler.ast.TypeReference")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.QualifiedType", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM1).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlag", "void", "org.eclipse.jdt.core.dom.ASTNode", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setQualifiedNameNameAndSourceRanges", "org.eclipse.jdt.core.dom.QualifiedName", "char[][]", "long[]", "int", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setQualifiedNameNameAndSourceRanges", "org.eclipse.jdt.core.dom.QualifiedName", "char[][]", "long[]", "int", "org.eclipse.jdt.internal.compiler.ast.TypeReference")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.SimpleName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM4).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setQualifiedNameNameAndSourceRanges", "org.eclipse.jdt.core.dom.QualifiedName", "char[][]", "long[]", "int", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setQualifiedNameNameAndSourceRanges", "org.eclipse.jdt.core.dom.QualifiedName", "char[][]", "long[]", "int", "org.eclipse.jdt.internal.compiler.ast.TypeReference")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.QualifiedName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM4).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setQualifiedNameNameAndSourceRanges", "org.eclipse.jdt.core.dom.QualifiedName", "char[][]", "long[]", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.SimpleName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM3).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setQualifiedNameNameAndSourceRanges", "org.eclipse.jdt.core.dom.QualifiedName", "char[][]", "long[]", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.QualifiedName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM3).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setTypeNameForAnnotation", "void", "org.eclipse.jdt.internal.compiler.ast.Annotation", "org.eclipse.jdt.core.dom.Annotation")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.SimpleName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM1).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setTypeNameForAnnotation", "void", "org.eclipse.jdt.internal.compiler.ast.Annotation", "org.eclipse.jdt.core.dom.Annotation")).methodToWrap(new Hook("org.eclipse.jdt.core.dom.QualifiedName", "<init>", "void", "org.eclipse.jdt.core.dom.AST")).requestExtra(StackRequest.PARAM1).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "setIsGeneratedFlagForName", "void", "org.eclipse.jdt.core.dom.Name", "java.lang.Object")).transplant().build());
    }

    private static void patchAvoidReparsingGeneratedCode(ScriptManager sm) {
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "parse", "void", "org.eclipse.jdt.internal.compiler.ast.MethodDeclaration", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "checkBit24", "boolean", "java.lang.Object")).transplant().request(StackRequest.PARAM1).build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "parse", "void", "org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration", "boolean")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "checkBit24", "boolean", "java.lang.Object")).transplant().request(StackRequest.PARAM1).build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "parse", "void", "org.eclipse.jdt.internal.compiler.ast.Initializer", "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "checkBit24", "boolean", "java.lang.Object")).transplant().request(StackRequest.PARAM1).build());
    }

    private static void patchLombokizeAST(ScriptManager sm) {
        sm.addScript(ScriptBuilder.addField().targetClass("org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration").fieldName("$lombokAST").fieldType("Ljava/lang/Object;").setPublic().setTransient().build());
        sm.addScript(ScriptBuilder.addField().targetClass("org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration").fieldName("$transformationState").fieldType("Ljava/lang/Object;").setPublic().setTransient().build());
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "getMethodBodies", "void", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$Transform", "transform", "void", "java.lang.Object", "java.lang.Object")).request(StackRequest.THIS, StackRequest.PARAM1).build());
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "endParse", "org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$Transform", "transform_swapped", "void", "java.lang.Object", "java.lang.Object")).request(StackRequest.THIS, StackRequest.RETURN_VALUE).build());
    }

    private static void patchEcjTransformers(ScriptManager sm) {
        EclipsePatcher.addPatchesForDelegate(sm);
        EclipsePatcher.addPatchesForVal(sm);
        EclipsePatcher.addPatchesForValEclipse(sm);
    }

    private static void addPatchesForDelegate(ScriptManager sm) {
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.lookup.ClassScope", "buildFieldsAndMethods", "void", new String[0])).request(StackRequest.THIS).decisionMethod(new Hook("lombok.launch.PatchFixesHider$Delegate", "handleDelegateForType", "boolean", "java.lang.Object")).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.setSymbolDuringMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.SelectionRequestor", "acceptSourceMethod")).callToWrap(new Hook("org.eclipse.jdt.core.IType", "getMethods", "org.eclipse.jdt.core.IMethod[]", new String[0])).symbol("lombok.skipdelegates").build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.addField().fieldName("$delegateMethods").fieldType("Ljava/util/Map;").setPublic().setTransient().targetClass("org.eclipse.jdt.internal.core.CompilationUnit").build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.core.SourceTypeElementInfo", "getChildren", "org.eclipse.jdt.core.IJavaElement[]", new String[0])).request(StackRequest.RETURN_VALUE, StackRequest.THIS).wrapMethod(new Hook("lombok.launch.PatchFixesHider$Delegate", "addGeneratedDelegateMethods", "java.lang.Object[]", "java.lang.Object", "java.lang.Object")).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.core.JavaElement", "getElementInfo", "org.eclipse.jdt.internal.compiler.env.IElementInfo", new String[0])).request(StackRequest.THIS).decisionMethod(new Hook("lombok.launch.PatchFixesHider$Delegate", "isDelegateSourceMethod", "boolean", "java.lang.Object")).valueMethod(new Hook("lombok.launch.PatchFixesHider$Delegate", "returnElementInfo", "java.lang.Object", "java.lang.Object")).build());
    }

    private static void addPatchesForValEclipse(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.addField().fieldName("$initCopy").fieldType("Lorg/eclipse/jdt/internal/compiler/ast/ASTNode;").setPublic().setTransient().targetClass("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration").build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.addField().fieldName("$iterableCopy").fieldType("Lorg/eclipse/jdt/internal/compiler/ast/ASTNode;").setPublic().setTransient().targetClass("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration").build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "consumeExitVariableWithInitialization", "void", new String[0])).request(StackRequest.THIS).wrapMethod(new Hook("lombok.launch.PatchFixesHider$ValPortal", "copyInitializationOfLocalDeclaration", "void", "java.lang.Object")).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "consumeEnhancedForStatementHeader", "void", new String[0])).request(StackRequest.THIS).wrapMethod(new Hook("lombok.launch.PatchFixesHider$ValPortal", "copyInitializationOfForEachIterable", "void", "java.lang.Object")).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setModifiers", "void", "org.eclipse.jdt.core.dom.VariableDeclarationStatement", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$ValPortal", "addFinalAndValAnnotationToVariableDeclarationStatement", "void", "java.lang.Object", "java.lang.Object", "java.lang.Object")).request(StackRequest.THIS, StackRequest.PARAM1, StackRequest.PARAM2).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "setModifiers", "void", "org.eclipse.jdt.core.dom.SingleVariableDeclaration", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$ValPortal", "addFinalAndValAnnotationToSingleVariableDeclaration", "void", "java.lang.Object", "java.lang.Object", "java.lang.Object")).request(StackRequest.THIS, StackRequest.PARAM1, StackRequest.PARAM2).build());
    }

    private static void addPatchesForVal(ScriptManager sm) {
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration", "resolve", "void", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).request(StackRequest.THIS, StackRequest.PARAM1).decisionMethod(new Hook("lombok.launch.PatchFixesHider$Val", "handleValForLocalDeclaration", "boolean", "java.lang.Object", "java.lang.Object")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration", "resolve", "void", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration", "resolve", "void", "org.eclipse.jdt.internal.compiler.lookup.BlockScope", "boolean")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.ast.Expression", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).requestExtra(StackRequest.THIS).replacementMethod(new Hook("lombok.launch.PatchFixesHider$Val", "skipResolveInitializerIfAlreadyCalled2", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.ast.Expression", "org.eclipse.jdt.internal.compiler.lookup.BlockScope", "org.eclipse.jdt.internal.compiler.ast.LocalDeclaration")).transplant().build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.ForeachStatement", "resolve", "void", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.ast.Expression", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).replacementMethod(new Hook("lombok.launch.PatchFixesHider$Val", "skipResolveInitializerIfAlreadyCalled", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.ast.Expression", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).transplant().build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.ForeachStatement", "resolve", "void", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).request(StackRequest.THIS, StackRequest.PARAM1).decisionMethod(new Hook("lombok.launch.PatchFixesHider$Val", "handleValForForEach", "boolean", "java.lang.Object", "java.lang.Object")).build());
    }

    private static void patchFixSourceTypeConverter(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.SourceTypeConverter", "convertAnnotations", "org.eclipse.jdt.internal.compiler.ast.Annotation[]", "org.eclipse.jdt.core.IAnnotatable")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "convertAnnotations", "org.eclipse.jdt.internal.compiler.ast.Annotation[]", "org.eclipse.jdt.internal.compiler.ast.Annotation[]", "org.eclipse.jdt.core.IAnnotatable")).request(StackRequest.PARAM1, StackRequest.RETURN_VALUE).build());
    }

    private static void patchEclipseDebugPatches(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTNode", "setSourceRange", "void", "int", "int")).request(StackRequest.THIS).request(StackRequest.PARAM1).request(StackRequest.PARAM2).decisionMethod(new Hook("lombok.eclipse.agent.PatchDiagnostics", "setSourceRangeCheck", "boolean", "java.lang.Object", "int", "int")).build());
    }

    private static void patchExtensionMethod(ScriptManager sm) {
        sm.addScript(ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.MessageSend", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).request(StackRequest.RETURN_VALUE).request(StackRequest.THIS).request(StackRequest.PARAM1).wrapMethod(new Hook("lombok.launch.PatchFixesHider$ExtensionMethod", "resolveType", "java.lang.Object", "java.lang.Object", "java.lang.Object", "java.lang.Object")).cast().build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.MessageSend", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "errorNoMethodFor", "void", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding[]")).replacementMethod(new Hook("lombok.launch.PatchFixesHider$ExtensionMethod", "errorNoMethodFor", "void", "java.lang.Object", "java.lang.Object", "java.lang.Object", "java.lang.Object")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.MessageSend", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "invalidMethod", "void", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).replacementMethod(new Hook("lombok.launch.PatchFixesHider$ExtensionMethod", "invalidMethod", "void", "java.lang.Object", "java.lang.Object", "java.lang.Object")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.MessageSend", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "invalidMethod", "void", "org.eclipse.jdt.internal.compiler.ast.MessageSend", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding", "org.eclipse.jdt.internal.compiler.lookup.Scope")).replacementMethod(new Hook("lombok.launch.PatchFixesHider$ExtensionMethod", "invalidMethod", "void", "java.lang.Object", "java.lang.Object", "java.lang.Object", "java.lang.Object")).build());
        sm.addScript(ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.MessageSend", "resolveType", "org.eclipse.jdt.internal.compiler.lookup.TypeBinding", "org.eclipse.jdt.internal.compiler.lookup.BlockScope")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "nonStaticAccessToStaticMethod", "void", "org.eclipse.jdt.internal.compiler.ast.ASTNode", "org.eclipse.jdt.internal.compiler.lookup.MethodBinding")).replacementMethod(new Hook("lombok.launch.PatchFixesHider$ExtensionMethod", "nonStaticAccessToStaticMethod", "void", "java.lang.Object", "java.lang.Object", "java.lang.Object", "java.lang.Object")).requestExtra(StackRequest.THIS).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.ui.text.java.CompletionProposalCollector", "getJavaCompletionProposals", "org.eclipse.jdt.ui.text.java.IJavaCompletionProposal[]", new String[0])).request(StackRequest.RETURN_VALUE).request(StackRequest.THIS).wrapMethod(new Hook("lombok.eclipse.agent.PatchExtensionMethodCompletionProposalPortal", "getJavaCompletionProposals", "org.eclipse.jdt.ui.text.java.IJavaCompletionProposal[]", "java.lang.Object[]", "java.lang.Object")).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.core.search.SearchPattern", "createPattern", "org.eclipse.jdt.core.search.SearchPattern", "org.eclipse.jdt.core.IJavaElement", "int", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$ExtensionMethod", "modifyMethodPattern", "java.lang.Object", "java.lang.Object")).cast().request(StackRequest.RETURN_VALUE).build());
    }

    private static void patchNullCheck(ScriptManager sm) {
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "expressionNonNullComparison", "boolean", "org.eclipse.jdt.internal.compiler.ast.Expression", "boolean")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).valueMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "returnTrue", "boolean", "java.lang.Object")).request(StackRequest.PARAM1).transplant().build());
        sm.addScript(ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.problem.ProblemReporter", "fakeReachable", "void", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "isGenerated", "boolean", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).request(StackRequest.PARAM1).transplant().build());
    }

    private static void patchJavadoc(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.manipulation.internal.javadoc.CoreJavadocAccess", "getHTMLContent", "java.lang.String", "org.eclipse.jdt.core.IJavaElement", "boolean")).methodToWrap(new Hook("org.eclipse.jdt.core.manipulation.internal.javadoc.CoreJavadocAccess", "getHTMLContentFromSource", "java.lang.String", "org.eclipse.jdt.core.IJavaElement")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$Javadoc", "getHTMLContentFromSource", "java.lang.String", "java.lang.String", "java.lang.Object", "org.eclipse.jdt.core.IJavaElement")).requestExtra(StackRequest.THIS, StackRequest.PARAM1).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2", "getHTMLContent", "java.lang.String", "org.eclipse.jdt.core.IJavaElement", "boolean")).methodToWrap(new Hook("org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2", "getHTMLContentFromSource", "java.lang.String", "org.eclipse.jdt.core.IJavaElement")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$Javadoc", "getHTMLContentFromSource", "java.lang.String", "java.lang.String", "org.eclipse.jdt.core.IJavaElement")).requestExtra(StackRequest.PARAM1).build());
        sm.addScript(ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.ls.core.internal.javadoc.JavadocContentAccess2", "getHTMLContent", "java.lang.String", "org.eclipse.jdt.core.IJavaElement", "boolean")).methodToWrap(new Hook("org.eclipse.jdt.ls.core.internal.javadoc.JavadocContentAccess2", "getHTMLContentFromSource", "java.lang.String", "org.eclipse.jdt.core.IJavaElement")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$Javadoc", "getHTMLContentFromSource", "java.lang.String", "java.lang.String", "org.eclipse.jdt.core.IJavaElement")).requestExtra(StackRequest.PARAM1).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2", "getHTMLContent", "java.lang.String", "org.eclipse.jdt.core.IMember", "boolean")).methodToWrap(new Hook("org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2", "getHTMLContentFromSource", "java.lang.String", "org.eclipse.jdt.core.IMember")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$Javadoc", "getHTMLContentFromSource", "java.lang.String", "java.lang.String", "org.eclipse.jdt.core.IJavaElement")).requestExtra(StackRequest.PARAM1).build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.addField().fieldName("$javadoc").fieldType("Ljava/util/Map;").setPublic().setTransient().targetClass("org.eclipse.jdt.internal.core.CompilationUnit").build());
    }

    private static void patchASTConverterLiterals(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.Expression", "org.eclipse.jdt.internal.compiler.ast.StringLiteral")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.Expression", "org.eclipse.jdt.internal.compiler.ast.TextBlock")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.CharacterLiteral", "org.eclipse.jdt.internal.compiler.ast.CharLiteral")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.NumberLiteral", "org.eclipse.jdt.internal.compiler.ast.DoubleLiteral")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.NumberLiteral", "org.eclipse.jdt.internal.compiler.ast.FloatLiteral")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.NumberLiteral", "org.eclipse.jdt.internal.compiler.ast.LongLiteral")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.NumberLiteral", "org.eclipse.jdt.internal.compiler.ast.LongLiteralMinValue")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.NumberLiteral", "org.eclipse.jdt.internal.compiler.ast.IntLiteral")).target(new MethodTarget("org.eclipse.jdt.core.dom.ASTConverter", "convert", "org.eclipse.jdt.core.dom.NumberLiteral", "org.eclipse.jdt.internal.compiler.ast.IntLiteralMinValue")).methodToWrap(new Hook("java.lang.String", "<init>", "void", "char[]", "int", "int")).requestExtra(StackRequest.PARAM1).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "getRealNodeSource", "java.lang.String", "java.lang.String", "org.eclipse.jdt.internal.compiler.ast.ASTNode")).transplant().build());
    }

    private static void patchASTNodeSearchUtil(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.corext.refactoring.structure.ASTNodeSearchUtil", "getMethodDeclarationNode", "org.eclipse.jdt.core.dom.MethodDeclaration", "org.eclipse.jdt.core.IMethod", "org.eclipse.jdt.core.dom.CompilationUnit")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$PatchFixes", "getRealMethodDeclarationNode", "org.eclipse.jdt.core.dom.MethodDeclaration", "org.eclipse.jdt.core.dom.MethodDeclaration", "org.eclipse.jdt.core.IMethod", "org.eclipse.jdt.core.dom.CompilationUnit")).request(StackRequest.RETURN_VALUE, StackRequest.PARAM1, StackRequest.PARAM2).transplant().build());
    }

    private static void patchFieldInitializer(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.addField().targetClass("org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor").fieldName("$fieldInfo").fieldType("Ljava/lang/Object;").build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.addField().targetClass("org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor").fieldName("$sourceFieldElementInfo").fieldType("Lorg/eclipse/jdt/internal/core/SourceFieldElementInfo;").build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor", "exitField", "void", "int", "int", "int")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$FieldInitializer", "storeFieldInfo", "boolean", "org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor")).request(StackRequest.THIS).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor", "exitField", "void", "int", "int", "int")).methodToWrap(new Hook("org.eclipse.jdt.internal.core.SourceFieldElementInfo", "<init>", "void", new String[0])).wrapMethod(new Hook("lombok.launch.PatchFixesHider$FieldInitializer", "storeSourceFieldElementInfo", "void", "org.eclipse.jdt.internal.core.SourceFieldElementInfo", "org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor")).requestExtra(StackRequest.THIS).transplant().build());
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor", "exitField", "void", "int", "int", "int")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$FieldInitializer", "overwriteInitializer", "void", "org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor")).request(StackRequest.THIS).transplant().build());
    }

    private static void patchCrossModuleClassLoading(ScriptManager sm) {
        sm.addScriptIfWitness(EclipseLoaderPatcher.OSGI_TYPES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.eclipse.jdt.internal.compiler.parser.Parser", "<clinit>")).wrapMethod(new Hook("lombok.launch.PatchFixesHider$ModuleClassLoading", "parserClinit", "void", new String[0])).build());
    }

    private static void patchForTests(ScriptManager sm) {
        String[] ECLIPSE_TEST_CLASSES = new String[]{"lombok/transform/TestWithEcj", "lombok/eclipse/EclipseRunner"};
        sm.addScriptIfWitness(ECLIPSE_TEST_CLASSES, ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.TypeDeclaration", "printBody", "java.lang.StringBuilder", "int", "java.lang.StringBuilder")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "print", "java.lang.StringBuilder", "int", "java.lang.StringBuilder")).replacementMethod(new Hook("lombok.launch.PatchFixesHider$Tests", "printMethod", "java.lang.StringBuilder", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "int", "java.lang.StringBuilder", "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration")).requestExtra(StackRequest.THIS).build());
        sm.addScriptIfWitness(ECLIPSE_TEST_CLASSES, ScriptBuilder.replaceMethodCall().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.TypeDeclaration", "printBody", "java.lang.StringBuffer", "int", "java.lang.StringBuffer")).methodToReplace(new Hook("org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "print", "java.lang.StringBuffer", "int", "java.lang.StringBuffer")).replacementMethod(new Hook("lombok.launch.PatchFixesHider$Tests", "printMethod", "java.lang.StringBuffer", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "int", "java.lang.StringBuffer", "org.eclipse.jdt.internal.compiler.ast.TypeDeclaration")).requestExtra(StackRequest.THIS).build());
        sm.addScriptIfWitness(ECLIPSE_TEST_CLASSES, ScriptBuilder.wrapReturnValue().target(new MethodTarget("org.osgi.framework.FrameworkUtil", "getBundle", "org.osgi.framework.Bundle", "java.lang.Class")).request(StackRequest.RETURN_VALUE, StackRequest.PARAM1).wrapMethod(new Hook("lombok.launch.PatchFixesHider$Tests", "getBundle", "java.lang.Object", "java.lang.Object", "java.lang.Class")).build());
        sm.addScriptIfWitness(ECLIPSE_TEST_CLASSES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "print", "java.lang.StringBuilder", "int", "java.lang.StringBuilder")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$Tests", "isImplicitCanonicalConstructor", "boolean", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "java.lang.Object")).valueMethod(new Hook("lombok.launch.PatchFixesHider$Tests", "returnStringBuilder", "java.lang.StringBuilder", "java.lang.Object", "java.lang.StringBuilder")).request(StackRequest.THIS, StackRequest.PARAM2).transplant().build());
        sm.addScriptIfWitness(ECLIPSE_TEST_CLASSES, ScriptBuilder.exitEarly().target(new MethodTarget("org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "print", "java.lang.StringBuffer", "int", "java.lang.StringBuffer")).decisionMethod(new Hook("lombok.launch.PatchFixesHider$Tests", "isImplicitCanonicalConstructor", "boolean", "org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration", "java.lang.Object")).valueMethod(new Hook("lombok.launch.PatchFixesHider$Tests", "returnStringBuffer", "java.lang.StringBuffer", "java.lang.Object", "java.lang.StringBuffer")).request(StackRequest.THIS, StackRequest.PARAM2).transplant().build());
    }
}
