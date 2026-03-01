package lombok.core.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.ConfigurationKeys;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import lombok.With;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.JavaIdentifiers;
import lombok.core.LombokNode;
import lombok.core.configuration.AllowHelper;
import lombok.core.configuration.CapitalizationStrategy;
import lombok.core.configuration.ConfigurationKey;
import lombok.core.configuration.FlagUsageType;
import lombok.core.handlers.Singulars;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

public class HandlerUtil {
    public static final List<String> NONNULL_ANNOTATIONS = Collections.unmodifiableList(Arrays.asList("android.annotation.NonNull", "android.support.annotation.NonNull", "android.support.annotation.RecentlyNonNull", "androidx.annotation.NonNull", "androidx.annotation.RecentlyNonNull", "com.android.annotations.NonNull", "com.google.firebase.database.annotations.NotNull", "com.mongodb.lang.NonNull", "com.sun.istack.NotNull", "com.unboundid.util.NotNull", "edu.umd.cs.findbugs.annotations.NonNull", "io.micrometer.core.lang.NonNull", "io.reactivex.annotations.NonNull", "io.reactivex.rxjava3.annotations.NonNull", "jakarta.annotation.Nonnull", "javax.annotation.Nonnull", "libcore.util.NonNull", "lombok.NonNull", "org.checkerframework.checker.nullness.qual.NonNull", "org.checkerframework.checker.nullness.compatqual.NonNullDecl", "org.checkerframework.checker.nullness.compatqual.NonNullType", "org.codehaus.commons.nullanalysis.NotNull", "org.eclipse.jdt.annotation.NonNull", "org.jetbrains.annotations.NotNull", "org.jmlspecs.annotation.NonNull", "org.jspecify.annotations.NonNull", "org.netbeans.api.annotations.common.NonNull", "org.springframework.lang.NonNull", "reactor.util.annotation.NonNull"));
    public static final List<String> BASE_COPYABLE_ANNOTATIONS = Collections.unmodifiableList(Arrays.asList("android.annotation.NonNull", "android.annotation.Nullable", "android.support.annotation.NonNull", "android.support.annotation.Nullable", "android.support.annotation.RecentlyNonNull", "android.support.annotation.RecentlyNullable", "androidx.annotation.NonNull", "androidx.annotation.Nullable", "androidx.annotation.RecentlyNonNull", "androidx.annotation.RecentlyNullable", "com.android.annotations.NonNull", "com.android.annotations.Nullable", "com.google.firebase.database.annotations.NotNull", "com.google.firebase.database.annotations.Nullable", "com.mongodb.lang.NonNull", "com.mongodb.lang.Nullable", "com.sun.istack.NotNull", "com.sun.istack.Nullable", "com.unboundid.util.NotNull", "com.unboundid.util.Nullable", "edu.umd.cs.findbugs.annotations.CheckForNull", "edu.umd.cs.findbugs.annotations.NonNull", "edu.umd.cs.findbugs.annotations.Nullable", "edu.umd.cs.findbugs.annotations.PossiblyNull", "edu.umd.cs.findbugs.annotations.UnknownNullness", "io.micrometer.core.lang.NonNull", "io.micrometer.core.lang.Nullable", "io.reactivex.annotations.NonNull", "io.reactivex.annotations.Nullable", "io.reactivex.rxjava3.annotations.NonNull", "io.reactivex.rxjava3.annotations.Nullable", "jakarta.annotation.Nonnull", "jakarta.annotation.Nullable", "javax.annotation.CheckForNull", "javax.annotation.Nonnull", "javax.annotation.Nullable", "libcore.util.NonNull", "libcore.util.Nullable", "lombok.NonNull", "org.checkerframework.checker.nullness.compatqual.NonNullDecl", "org.checkerframework.checker.nullness.compatqual.NonNullType", "org.checkerframework.checker.nullness.compatqual.NullableDecl", "org.checkerframework.checker.nullness.compatqual.NullableType", "org.checkerframework.checker.nullness.qual.NonNull", "org.checkerframework.checker.nullness.qual.Nullable", "org.codehaus.commons.nullanalysis.NotNull", "org.codehaus.commons.nullanalysis.Nullable", "org.eclipse.jdt.annotation.NonNull", "org.eclipse.jdt.annotation.Nullable", "org.jetbrains.annotations.NotNull", "org.jetbrains.annotations.Nullable", "org.jetbrains.annotations.UnknownNullability", "org.jmlspecs.annotation.NonNull", "org.jmlspecs.annotation.Nullable", "org.jspecify.annotations.Nullable", "org.jspecify.annotations.NonNull", "org.netbeans.api.annotations.common.CheckForNull", "org.netbeans.api.annotations.common.NonNull", "org.netbeans.api.annotations.common.NullAllowed", "org.netbeans.api.annotations.common.NullUnknown", "org.springframework.lang.NonNull", "org.springframework.lang.Nullable", "reactor.util.annotation.NonNull", "reactor.util.annotation.Nullable", "org.checkerframework.checker.builder.qual.CalledMethods", "org.checkerframework.checker.builder.qual.NotCalledMethods", "org.checkerframework.checker.calledmethods.qual.CalledMethods", "org.checkerframework.checker.calledmethods.qual.CalledMethodsBottom", "org.checkerframework.checker.calledmethods.qual.CalledMethodsPredicate", "org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey", "org.checkerframework.checker.compilermsgs.qual.CompilerMessageKeyBottom", "org.checkerframework.checker.compilermsgs.qual.UnknownCompilerMessageKey", "org.checkerframework.checker.fenum.qual.AwtAlphaCompositingRule", "org.checkerframework.checker.fenum.qual.AwtColorSpace", "org.checkerframework.checker.fenum.qual.AwtCursorType", "org.checkerframework.checker.fenum.qual.AwtFlowLayout", "org.checkerframework.checker.fenum.qual.Fenum", "org.checkerframework.checker.fenum.qual.FenumBottom", "org.checkerframework.checker.fenum.qual.FenumTop", "org.checkerframework.checker.fenum.qual.PolyFenum", "org.checkerframework.checker.fenum.qual.SwingBoxOrientation", "org.checkerframework.checker.fenum.qual.SwingCompassDirection", "org.checkerframework.checker.fenum.qual.SwingElementOrientation", "org.checkerframework.checker.fenum.qual.SwingHorizontalOrientation", "org.checkerframework.checker.fenum.qual.SwingSplitPaneOrientation", "org.checkerframework.checker.fenum.qual.SwingTextOrientation", "org.checkerframework.checker.fenum.qual.SwingTitleJustification", "org.checkerframework.checker.fenum.qual.SwingTitlePosition", "org.checkerframework.checker.fenum.qual.SwingVerticalOrientation", "org.checkerframework.checker.formatter.qual.Format", "org.checkerframework.checker.formatter.qual.FormatBottom", "org.checkerframework.checker.formatter.qual.InvalidFormat", "org.checkerframework.checker.formatter.qual.UnknownFormat", "org.checkerframework.checker.guieffect.qual.AlwaysSafe", "org.checkerframework.checker.guieffect.qual.PolyUI", "org.checkerframework.checker.guieffect.qual.UI", "org.checkerframework.checker.i18nformatter.qual.I18nFormat", "org.checkerframework.checker.i18nformatter.qual.I18nFormatBottom", "org.checkerframework.checker.i18nformatter.qual.I18nFormatFor", "org.checkerframework.checker.i18nformatter.qual.I18nInvalidFormat", "org.checkerframework.checker.i18nformatter.qual.I18nUnknownFormat", "org.checkerframework.checker.i18n.qual.LocalizableKey", "org.checkerframework.checker.i18n.qual.LocalizableKeyBottom", "org.checkerframework.checker.i18n.qual.Localized", "org.checkerframework.checker.i18n.qual.UnknownLocalizableKey", "org.checkerframework.checker.i18n.qual.UnknownLocalized", "org.checkerframework.checker.index.qual.GTENegativeOne", "org.checkerframework.checker.index.qual.IndexFor", "org.checkerframework.checker.index.qual.IndexOrHigh", "org.checkerframework.checker.index.qual.IndexOrLow", "org.checkerframework.checker.index.qual.LengthOf", "org.checkerframework.checker.index.qual.LessThan", "org.checkerframework.checker.index.qual.LessThanBottom", "org.checkerframework.checker.index.qual.LessThanUnknown", "org.checkerframework.checker.index.qual.LowerBoundBottom", "org.checkerframework.checker.index.qual.LowerBoundUnknown", "org.checkerframework.checker.index.qual.LTEqLengthOf", "org.checkerframework.checker.index.qual.LTLengthOf", "org.checkerframework.checker.index.qual.LTOMLengthOf", "org.checkerframework.checker.index.qual.NegativeIndexFor", "org.checkerframework.checker.index.qual.NonNegative", "org.checkerframework.checker.index.qual.PolyIndex", "org.checkerframework.checker.index.qual.PolyLength", "org.checkerframework.checker.index.qual.PolyLowerBound", "org.checkerframework.checker.index.qual.PolySameLen", "org.checkerframework.checker.index.qual.PolyUpperBound", "org.checkerframework.checker.index.qual.Positive", "org.checkerframework.checker.index.qual.SameLen", "org.checkerframework.checker.index.qual.SameLenBottom", "org.checkerframework.checker.index.qual.SameLenUnknown", "org.checkerframework.checker.index.qual.SearchIndexBottom", "org.checkerframework.checker.index.qual.SearchIndexFor", "org.checkerframework.checker.index.qual.SearchIndexUnknown", "org.checkerframework.checker.index.qual.SubstringIndexBottom", "org.checkerframework.checker.index.qual.SubstringIndexFor", "org.checkerframework.checker.index.qual.SubstringIndexUnknown", "org.checkerframework.checker.index.qual.UpperBoundBottom", "org.checkerframework.checker.index.qual.UpperBoundLiteral", "org.checkerframework.checker.index.qual.UpperBoundUnknown", "org.checkerframework.checker.initialization.qual.FBCBottom", "org.checkerframework.checker.initialization.qual.Initialized", "org.checkerframework.checker.initialization.qual.UnderInitialization", "org.checkerframework.checker.initialization.qual.UnknownInitialization", "org.checkerframework.checker.interning.qual.Interned", "org.checkerframework.checker.interning.qual.InternedDistinct", "org.checkerframework.checker.interning.qual.PolyInterned", "org.checkerframework.checker.interning.qual.UnknownInterned", "org.checkerframework.checker.lock.qual.GuardedBy", "org.checkerframework.checker.lock.qual.GuardedByBottom", "org.checkerframework.checker.lock.qual.GuardedByUnknown", "org.checkerframework.checker.lock.qual.GuardSatisfied", "org.checkerframework.checker.lock.qual.NewObject", "org.checkerframework.checker.mustcall.qual.MustCall", "org.checkerframework.checker.mustcall.qual.MustCallAlias", "org.checkerframework.checker.mustcall.qual.MustCallUnknown", "org.checkerframework.checker.mustcall.qual.PolyMustCall", "org.checkerframework.checker.nullness.qual.KeyFor", "org.checkerframework.checker.nullness.qual.KeyForBottom", "org.checkerframework.checker.nullness.qual.MonotonicNonNull", "org.checkerframework.checker.nullness.qual.NonNull", "org.checkerframework.checker.nullness.qual.Nullable", "org.checkerframework.checker.nullness.qual.PolyKeyFor", "org.checkerframework.checker.nullness.qual.PolyNull", "org.checkerframework.checker.nullness.qual.UnknownKeyFor", "org.checkerframework.checker.optional.qual.MaybePresent", "org.checkerframework.checker.optional.qual.OptionalBottom", "org.checkerframework.checker.optional.qual.PolyPresent", "org.checkerframework.checker.optional.qual.Present", "org.checkerframework.checker.propkey.qual.PropertyKey", "org.checkerframework.checker.propkey.qual.PropertyKeyBottom", "org.checkerframework.checker.propkey.qual.UnknownPropertyKey", "org.checkerframework.checker.regex.qual.PolyRegex", "org.checkerframework.checker.regex.qual.Regex", "org.checkerframework.checker.regex.qual.RegexBottom", "org.checkerframework.checker.regex.qual.UnknownRegex", "org.checkerframework.checker.signature.qual.ArrayWithoutPackage", "org.checkerframework.checker.signature.qual.BinaryName", "org.checkerframework.checker.signature.qual.BinaryNameOrPrimitiveType", "org.checkerframework.checker.signature.qual.BinaryNameWithoutPackage", "org.checkerframework.checker.signature.qual.CanonicalName", "org.checkerframework.checker.signature.qual.CanonicalNameAndBinaryName", "org.checkerframework.checker.signature.qual.CanonicalNameOrEmpty", "org.checkerframework.checker.signature.qual.CanonicalNameOrPrimitiveType", "org.checkerframework.checker.signature.qual.ClassGetName", "org.checkerframework.checker.signature.qual.ClassGetSimpleName", "org.checkerframework.checker.signature.qual.DotSeparatedIdentifiers", "org.checkerframework.checker.signature.qual.DotSeparatedIdentifiersOrPrimitiveType", "org.checkerframework.checker.signature.qual.FieldDescriptor", "org.checkerframework.checker.signature.qual.FieldDescriptorForPrimitive", "org.checkerframework.checker.signature.qual.FieldDescriptorWithoutPackage", "org.checkerframework.checker.signature.qual.FqBinaryName", "org.checkerframework.checker.signature.qual.FullyQualifiedName", "org.checkerframework.checker.signature.qual.Identifier", "org.checkerframework.checker.signature.qual.IdentifierOrPrimitiveType", "org.checkerframework.checker.signature.qual.InternalForm", "org.checkerframework.checker.signature.qual.MethodDescriptor", "org.checkerframework.checker.signature.qual.PolySignature", "org.checkerframework.checker.signature.qual.PrimitiveType", "org.checkerframework.checker.signature.qual.SignatureBottom", "org.checkerframework.checker.signedness.qual.PolySigned", "org.checkerframework.checker.signedness.qual.Signed", "org.checkerframework.checker.signedness.qual.SignednessBottom", "org.checkerframework.checker.signedness.qual.SignednessGlb", "org.checkerframework.checker.signedness.qual.SignedPositive", "org.checkerframework.checker.signedness.qual.SignedPositiveFromUnsigned", "org.checkerframework.checker.signedness.qual.UnknownSignedness", "org.checkerframework.checker.signedness.qual.Unsigned", "org.checkerframework.checker.tainting.qual.PolyTainted", "org.checkerframework.checker.tainting.qual.Tainted", "org.checkerframework.checker.tainting.qual.Untainted", "org.checkerframework.checker.units.qual.A", "org.checkerframework.checker.units.qual.Acceleration", "org.checkerframework.checker.units.qual.Angle", "org.checkerframework.checker.units.qual.Area", "org.checkerframework.checker.units.qual.C", "org.checkerframework.checker.units.qual.cd", "org.checkerframework.checker.units.qual.Current", "org.checkerframework.checker.units.qual.degrees", "org.checkerframework.checker.units.qual.Force", "org.checkerframework.checker.units.qual.g", "org.checkerframework.checker.units.qual.h", "org.checkerframework.checker.units.qual.K", "org.checkerframework.checker.units.qual.kg", "org.checkerframework.checker.units.qual.km", "org.checkerframework.checker.units.qual.km2", "org.checkerframework.checker.units.qual.km3", "org.checkerframework.checker.units.qual.kmPERh", "org.checkerframework.checker.units.qual.kN", "org.checkerframework.checker.units.qual.Length", "org.checkerframework.checker.units.qual.Luminance", "org.checkerframework.checker.units.qual.m", "org.checkerframework.checker.units.qual.m2", "org.checkerframework.checker.units.qual.m3", "org.checkerframework.checker.units.qual.Mass", "org.checkerframework.checker.units.qual.min", "org.checkerframework.checker.units.qual.mm", "org.checkerframework.checker.units.qual.mm2", "org.checkerframework.checker.units.qual.mm3", "org.checkerframework.checker.units.qual.mol", "org.checkerframework.checker.units.qual.mPERs", "org.checkerframework.checker.units.qual.mPERs2", "org.checkerframework.checker.units.qual.N", "org.checkerframework.checker.units.qual.PolyUnit", "org.checkerframework.checker.units.qual.radians", "org.checkerframework.checker.units.qual.s", "org.checkerframework.checker.units.qual.Speed", "org.checkerframework.checker.units.qual.Substance", "org.checkerframework.checker.units.qual.t", "org.checkerframework.checker.units.qual.Temperature", "org.checkerframework.checker.units.qual.Time", "org.checkerframework.checker.units.qual.UnitsBottom", "org.checkerframework.checker.units.qual.UnknownUnits", "org.checkerframework.checker.units.qual.Volume", "org.checkerframework.common.aliasing.qual.LeakedToResult", "org.checkerframework.common.aliasing.qual.MaybeAliased", "org.checkerframework.common.aliasing.qual.NonLeaked", "org.checkerframework.common.aliasing.qual.Unique", "org.checkerframework.common.initializedfields.qual.InitializedFields", "org.checkerframework.common.initializedfields.qual.InitializedFieldsBottom", "org.checkerframework.common.initializedfields.qual.PolyInitializedFields", "org.checkerframework.common.reflection.qual.ClassBound", "org.checkerframework.common.reflection.qual.ClassVal", "org.checkerframework.common.reflection.qual.ClassValBottom", "org.checkerframework.common.reflection.qual.MethodVal", "org.checkerframework.common.reflection.qual.MethodValBottom", "org.checkerframework.common.reflection.qual.UnknownClass", "org.checkerframework.common.reflection.qual.UnknownMethod", "org.checkerframework.common.returnsreceiver.qual.BottomThis", "org.checkerframework.common.returnsreceiver.qual.This", "org.checkerframework.common.returnsreceiver.qual.UnknownThis", "org.checkerframework.common.subtyping.qual.Bottom", "org.checkerframework.common.util.report.qual.ReportUnqualified", "org.checkerframework.common.value.qual.ArrayLen", "org.checkerframework.common.value.qual.ArrayLenRange", "org.checkerframework.common.value.qual.BoolVal", "org.checkerframework.common.value.qual.BottomVal", "org.checkerframework.common.value.qual.DoubleVal", "org.checkerframework.common.value.qual.EnumVal", "org.checkerframework.common.value.qual.IntRange", "org.checkerframework.common.value.qual.IntVal", "org.checkerframework.common.value.qual.MatchesRegex", "org.checkerframework.common.value.qual.MinLen", "org.checkerframework.common.value.qual.PolyValue", "org.checkerframework.common.value.qual.StringVal", "org.checkerframework.common.value.qual.UnknownVal", "org.checkerframework.framework.qual.PurityUnqualified"));
    public static final List<String> COPY_TO_SETTER_ANNOTATIONS = Collections.unmodifiableList(Arrays.asList("com.fasterxml.jackson.annotation.JacksonInject", "com.fasterxml.jackson.annotation.JsonAlias", "com.fasterxml.jackson.annotation.JsonFormat", "com.fasterxml.jackson.annotation.JsonIgnore", "com.fasterxml.jackson.annotation.JsonIgnoreProperties", "com.fasterxml.jackson.annotation.JsonProperty", "com.fasterxml.jackson.annotation.JsonSetter", "com.fasterxml.jackson.annotation.JsonSubTypes", "com.fasterxml.jackson.annotation.JsonTypeInfo", "com.fasterxml.jackson.annotation.JsonUnwrapped", "com.fasterxml.jackson.annotation.JsonView", "com.fasterxml.jackson.databind.annotation.JsonDeserialize", "com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper", "com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty", "com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText"));
    public static final List<String> COPY_TO_BUILDER_SINGULAR_SETTER_ANNOTATIONS = Collections.unmodifiableList(Arrays.asList("com.fasterxml.jackson.annotation.JsonAnySetter"));
    public static final List<String> JACKSON_COPY_TO_BUILDER_ANNOTATIONS = Collections.unmodifiableList(Arrays.asList("com.fasterxml.jackson.annotation.JsonAutoDetect", "com.fasterxml.jackson.annotation.JsonFormat", "com.fasterxml.jackson.annotation.JsonIgnoreProperties", "com.fasterxml.jackson.annotation.JsonIgnoreType", "com.fasterxml.jackson.annotation.JsonPropertyOrder", "com.fasterxml.jackson.annotation.JsonRootName", "com.fasterxml.jackson.annotation.JsonSubTypes", "com.fasterxml.jackson.annotation.JsonTypeInfo", "com.fasterxml.jackson.annotation.JsonTypeName", "com.fasterxml.jackson.annotation.JsonView", "com.fasterxml.jackson.databind.annotation.JsonNaming"));
    public static final List<String> INVALID_ON_BUILDERS = Collections.unmodifiableList(Arrays.asList(Getter.class.getName(), Setter.class.getName(), With.class.getName(), "lombok.experimental.Wither", ToString.class.getName(), EqualsAndHashCode.class.getName(), RequiredArgsConstructor.class.getName(), AllArgsConstructor.class.getName(), NoArgsConstructor.class.getName(), Data.class.getName(), Value.class.getName(), "lombok.experimental.Value", FieldDefaults.class.getName()));
    public static final String DEFAULT_EXCEPTION_FOR_NON_NULL = "java.lang.NullPointerException";
    private static final Pattern PRIMITIVE_WRAPPER_TYPE_NAME_PATTERN = Pattern.compile("^(?:java\\.lang\\.)?(?:Boolean|Byte|Short|Integer|Long|Float|Double|Character)$");
    private static final Pattern SECTION_FINDER = Pattern.compile("^\\s*\\**\\s*[-*][-*]+\\s*([GS]ETTER|WITH(?:ER)?)\\s*[-*][-*]+\\s*\\**\\s*$", 10);
    private static final Pattern LINE_BREAK_FINDER = Pattern.compile("(\\r?\\n)?");
    private static final Pattern FIND_RETURN = Pattern.compile("^\\s*\\**\\s*@returns?\\s+.*$", 10);

    private HandlerUtil() {
    }

    public static int primeForHashcode() {
        return 59;
    }

    public static int primeForTrue() {
        return 79;
    }

    public static int primeForFalse() {
        return 97;
    }

    public static int primeForNull() {
        return 43;
    }

    public static boolean checkName(String nameSpec, String identifier, LombokNode<?, ?, ?> errorNode) {
        if (identifier.isEmpty()) {
            errorNode.addError(String.valueOf(nameSpec) + " cannot be the empty string.");
            return false;
        }
        if (!JavaIdentifiers.isValidJavaIdentifier(identifier)) {
            errorNode.addError(String.valueOf(nameSpec) + " must be a valid java identifier.");
            return false;
        }
        return true;
    }

    public static String autoSingularize(String plural) {
        return Singulars.autoSingularize(plural);
    }

    public static void handleFlagUsage(LombokNode<?, ?, ?> node, ConfigurationKey<FlagUsageType> key, String featureName) {
        FlagUsageType fut = ((AST)node.getAst()).readConfiguration(key);
        if (fut == null && AllowHelper.isAllowable(key)) {
            node.addError("Use of " + featureName + " is disabled by default. Please add '" + key.getKeyName() + " = " + (Object)((Object)FlagUsageType.ALLOW) + "' to 'lombok.config' if you want to enable is.");
        }
        if (fut != null) {
            String msg = "Use of " + featureName + " is flagged according to lombok configuration.";
            if (fut == FlagUsageType.WARNING) {
                node.addWarning(msg);
            } else if (fut == FlagUsageType.ERROR) {
                node.addError(msg);
            }
        }
    }

    public static boolean shouldAddGenerated(LombokNode<?, ?, ?> node) {
        Boolean add = ((AST)node.getAst()).readConfiguration(ConfigurationKeys.ADD_JAVAX_GENERATED_ANNOTATIONS);
        if (add != null) {
            return add;
        }
        return Boolean.TRUE.equals(((AST)node.getAst()).readConfiguration(ConfigurationKeys.ADD_GENERATED_ANNOTATIONS));
    }

    public static void handleExperimentalFlagUsage(LombokNode<?, ?, ?> node, ConfigurationKey<FlagUsageType> key, String featureName) {
        HandlerUtil.handleFlagUsage(node, key, featureName, ConfigurationKeys.EXPERIMENTAL_FLAG_USAGE, "any lombok.experimental feature");
    }

    public static void handleFlagUsage(LombokNode<?, ?, ?> node, ConfigurationKey<FlagUsageType> key1, String featureName1, ConfigurationKey<FlagUsageType> key2, String featureName2) {
        FlagUsageType fut1 = ((AST)node.getAst()).readConfiguration(key1);
        FlagUsageType fut2 = ((AST)node.getAst()).readConfiguration(key2);
        FlagUsageType fut = null;
        String featureName = null;
        if (fut1 == FlagUsageType.ERROR) {
            fut = fut1;
            featureName = featureName1;
        } else if (fut2 == FlagUsageType.ERROR) {
            fut = fut2;
            featureName = featureName2;
        } else if (fut1 == FlagUsageType.WARNING) {
            fut = fut1;
            featureName = featureName1;
        } else {
            fut = fut2;
            featureName = featureName2;
        }
        if (fut != null) {
            String msg = "Use of " + featureName + " is flagged according to lombok configuration.";
            if (fut == FlagUsageType.WARNING) {
                node.addWarning(msg);
            } else if (fut == FlagUsageType.ERROR) {
                node.addError(msg);
            }
        }
    }

    public static boolean shouldReturnThis0(AnnotationValues<Accessors> accessors, AST<?, ?, ?> ast) {
        Boolean fluentConfig;
        Boolean chainConfig;
        boolean chainForced = accessors.isExplicit("chain");
        boolean fluentForced = accessors.isExplicit("fluent");
        Accessors instance = accessors.getInstance();
        boolean chain = instance.chain();
        boolean fluent = instance.fluent();
        if (chainForced) {
            return chain;
        }
        if (!chainForced && (chainConfig = ast.readConfiguration(ConfigurationKeys.ACCESSORS_CHAIN)) != null) {
            return chainConfig;
        }
        if (!fluentForced && (fluentConfig = ast.readConfiguration(ConfigurationKeys.ACCESSORS_FLUENT)) != null) {
            fluent = fluentConfig;
        }
        return chain || fluent;
    }

    public static boolean shouldMakeFinal0(AnnotationValues<Accessors> accessors, AST<?, ?, ?> ast) {
        boolean isExplicit = accessors.isExplicit("makeFinal");
        if (isExplicit) {
            return accessors.getAsBoolean("makeFinal");
        }
        Boolean config = ast.readConfiguration(ConfigurationKeys.ACCESSORS_MAKE_FINAL);
        if (config != null) {
            return config;
        }
        return false;
    }

    public static CharSequence removePrefix(CharSequence fieldName, List<String> prefixes) {
        if (prefixes == null || prefixes.isEmpty()) {
            return fieldName;
        }
        fieldName = fieldName.toString();
        block0: for (String prefix : prefixes) {
            if (prefix.length() == 0) {
                return fieldName;
            }
            if (fieldName.length() <= prefix.length()) continue;
            int i = 0;
            while (i < prefix.length()) {
                if (fieldName.charAt(i) != prefix.charAt(i)) continue block0;
                ++i;
            }
            char followupChar = fieldName.charAt(prefix.length());
            if (Character.isLetter(prefix.charAt(prefix.length() - 1)) && Character.isLowerCase(followupChar)) continue;
            return "" + Character.toLowerCase(followupChar) + fieldName.subSequence(prefix.length() + 1, fieldName.length());
        }
        return null;
    }

    public static String toGetterName(AST<?, ?, ?> ast, AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        return HandlerUtil.toAccessorName(ast, accessors, fieldName, isBoolean, "is", "get", true);
    }

    public static String toSetterName(AST<?, ?, ?> ast, AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        return HandlerUtil.toAccessorName(ast, accessors, fieldName, isBoolean, "set", "set", true);
    }

    public static String toWithName(AST<?, ?, ?> ast, AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        return HandlerUtil.toAccessorName(ast, accessors, fieldName, isBoolean, "with", "with", false);
    }

    public static String toWithByName(AST<?, ?, ?> ast, AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        return String.valueOf(HandlerUtil.toAccessorName(ast, accessors, fieldName, isBoolean, "with", "with", false)) + "By";
    }

    private static String toAccessorName(AST<?, ?, ?> ast, AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean, String booleanPrefix, String normalPrefix, boolean adhereToFluent) {
        if ((fieldName = fieldName.toString()).length() == 0) {
            return null;
        }
        if (Boolean.TRUE.equals(ast.readConfiguration(ConfigurationKeys.GETTER_CONSEQUENT_BOOLEAN))) {
            isBoolean = false;
        }
        boolean explicitPrefix = accessors != null && accessors.isExplicit("prefix");
        boolean explicitFluent = accessors != null && accessors.isExplicit("fluent");
        boolean explicitJavaBeansSpecCapitalization = accessors != null && accessors.isExplicit("javaBeansSpecCapitalization");
        Accessors ac = explicitPrefix || explicitFluent || explicitJavaBeansSpecCapitalization ? accessors.getInstance() : null;
        List<String> prefix = explicitPrefix ? Arrays.asList(ac.prefix()) : ast.readConfiguration(ConfigurationKeys.ACCESSORS_PREFIX);
        boolean fluent = explicitFluent ? ac.fluent() : Boolean.TRUE.equals(ast.readConfiguration(ConfigurationKeys.ACCESSORS_FLUENT));
        CapitalizationStrategy capitalizationStrategy = ast.readConfigurationOr(ConfigurationKeys.ACCESSORS_JAVA_BEANS_SPEC_CAPITALIZATION, CapitalizationStrategy.defaultValue());
        if ((fieldName = HandlerUtil.removePrefix(fieldName, prefix)) == null) {
            return null;
        }
        String fName = fieldName.toString();
        if (adhereToFluent && fluent) {
            return fName;
        }
        if (isBoolean && fName.startsWith("is") && fieldName.length() > 2 && !Character.isLowerCase(fieldName.charAt(2))) {
            return String.valueOf(booleanPrefix) + fName.substring(2);
        }
        return HandlerUtil.buildAccessorName(isBoolean ? booleanPrefix : normalPrefix, fName, capitalizationStrategy);
    }

    public static List<String> toAllGetterNames(AST<?, ?, ?> ast, AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        return HandlerUtil.toAllAccessorNames(ast, accessors, fieldName, isBoolean, "is", "get", true);
    }

    public static List<String> toAllSetterNames(AST<?, ?, ?> ast, AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        return HandlerUtil.toAllAccessorNames(ast, accessors, fieldName, isBoolean, "set", "set", true);
    }

    public static List<String> toAllWithNames(AST<?, ?, ?> ast, AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        return HandlerUtil.toAllAccessorNames(ast, accessors, fieldName, isBoolean, "with", "with", false);
    }

    public static List<String> toAllWithByNames(AST<?, ?, ?> ast, AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean) {
        List<String> in = HandlerUtil.toAllAccessorNames(ast, accessors, fieldName, isBoolean, "with", "with", false);
        if (!(in instanceof ArrayList)) {
            in = new ArrayList<String>(in);
        }
        int i = 0;
        while (i < in.size()) {
            in.set(i, String.valueOf(in.get(i)) + "By");
            ++i;
        }
        return in;
    }

    private static List<String> toAllAccessorNames(AST<?, ?, ?> ast, AnnotationValues<Accessors> accessors, CharSequence fieldName, boolean isBoolean, String booleanPrefix, String normalPrefix, boolean adhereToFluent) {
        if (Boolean.TRUE.equals(ast.readConfiguration(ConfigurationKeys.GETTER_CONSEQUENT_BOOLEAN))) {
            isBoolean = false;
        }
        if (!isBoolean) {
            String accessorName = HandlerUtil.toAccessorName(ast, accessors, fieldName, false, booleanPrefix, normalPrefix, adhereToFluent);
            return accessorName == null ? Collections.emptyList() : Collections.singletonList(accessorName);
        }
        boolean explicitPrefix = accessors != null && accessors.isExplicit("prefix");
        boolean explicitFluent = accessors != null && accessors.isExplicit("fluent");
        Accessors ac = explicitPrefix || explicitFluent ? accessors.getInstance() : null;
        List<String> prefix = explicitPrefix ? Arrays.asList(ac.prefix()) : ast.readConfiguration(ConfigurationKeys.ACCESSORS_PREFIX);
        boolean fluent = explicitFluent ? ac.fluent() : Boolean.TRUE.equals(ast.readConfiguration(ConfigurationKeys.ACCESSORS_FLUENT));
        CapitalizationStrategy capitalizationStrategy = ast.readConfigurationOr(ConfigurationKeys.ACCESSORS_JAVA_BEANS_SPEC_CAPITALIZATION, CapitalizationStrategy.defaultValue());
        if ((fieldName = HandlerUtil.removePrefix(fieldName, prefix)) == null) {
            return Collections.emptyList();
        }
        List<String> baseNames = HandlerUtil.toBaseNames(fieldName, isBoolean, fluent);
        HashSet<String> names = new HashSet<String>();
        for (String baseName : baseNames) {
            if (adhereToFluent && fluent) {
                names.add(baseName);
                continue;
            }
            names.add(HandlerUtil.buildAccessorName(normalPrefix, baseName, capitalizationStrategy));
            if (normalPrefix.equals(booleanPrefix)) continue;
            names.add(HandlerUtil.buildAccessorName(booleanPrefix, baseName, capitalizationStrategy));
        }
        return new ArrayList<String>(names);
    }

    private static List<String> toBaseNames(CharSequence fieldName, boolean isBoolean, boolean fluent) {
        ArrayList<String> baseNames = new ArrayList<String>();
        baseNames.add(fieldName.toString());
        String fName = fieldName.toString();
        if (fName.startsWith("is") && fName.length() > 2 && !Character.isLowerCase(fName.charAt(2))) {
            String baseName = fName.substring(2);
            if (fluent) {
                baseNames.add(Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1));
            } else {
                baseNames.add(baseName);
            }
        }
        return baseNames;
    }

    public static String buildAccessorName(AST<?, ?, ?> ast, String prefix, String suffix) {
        CapitalizationStrategy capitalizationStrategy = ast.readConfigurationOr(ConfigurationKeys.ACCESSORS_JAVA_BEANS_SPEC_CAPITALIZATION, CapitalizationStrategy.defaultValue());
        return HandlerUtil.buildAccessorName(prefix, suffix, capitalizationStrategy);
    }

    public static String buildAccessorName(LombokNode<?, ?, ?> node, String prefix, String suffix) {
        CapitalizationStrategy capitalizationStrategy = ((AST)node.getAst()).readConfigurationOr(ConfigurationKeys.ACCESSORS_JAVA_BEANS_SPEC_CAPITALIZATION, (CapitalizationStrategy)CapitalizationStrategy.defaultValue());
        return HandlerUtil.buildAccessorName(prefix, suffix, capitalizationStrategy);
    }

    private static String buildAccessorName(String prefix, String suffix, CapitalizationStrategy capitalizationStrategy) {
        if (suffix.length() == 0) {
            return prefix;
        }
        if (prefix.length() == 0) {
            return suffix;
        }
        return String.valueOf(prefix) + capitalizationStrategy.capitalize(suffix);
    }

    public static String camelCaseToConstant(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        b.append(Character.toUpperCase(fieldName.charAt(0)));
        int i = 1;
        while (i < fieldName.length()) {
            char c = fieldName.charAt(i);
            if (Character.isUpperCase(c)) {
                b.append('_');
            }
            b.append(Character.toUpperCase(c));
            ++i;
        }
        return b.toString();
    }

    public static int defaultEqualsAndHashcodeIncludeRank(String typeName) {
        if (JavaIdentifiers.isPrimitive(typeName)) {
            return 1000;
        }
        if (PRIMITIVE_WRAPPER_TYPE_NAME_PATTERN.matcher(typeName).matches()) {
            return 800;
        }
        return 0;
    }

    public static String stripLinesWithTagFromJavadoc(String javadoc, JavadocTag ... tags) {
        if (javadoc == null || javadoc.isEmpty()) {
            return javadoc;
        }
        String result = javadoc;
        JavadocTag[] javadocTagArray = tags;
        int n = tags.length;
        int n2 = 0;
        while (n2 < n) {
            JavadocTag tag = javadocTagArray[n2];
            result = tag.pattern.matcher(result).replaceAll("").trim();
            ++n2;
        }
        return result;
    }

    public static String stripSectionsFromJavadoc(String javadoc) {
        if (javadoc == null || javadoc.isEmpty()) {
            return javadoc;
        }
        Matcher sectionMatcher = SECTION_FINDER.matcher(javadoc);
        if (!sectionMatcher.find()) {
            return javadoc;
        }
        return javadoc.substring(0, sectionMatcher.start());
    }

    public static String getJavadocSection(String javadoc, String sectionNameSpec) {
        if (javadoc == null || javadoc.isEmpty()) {
            return null;
        }
        String[] sectionNames = sectionNameSpec.split("\\|");
        Matcher sectionMatcher = SECTION_FINDER.matcher(javadoc);
        Matcher lineBreakMatcher = LINE_BREAK_FINDER.matcher(javadoc);
        int sectionStart = -1;
        int sectionEnd = -1;
        while (sectionMatcher.find()) {
            boolean found = false;
            String[] stringArray = sectionNames;
            int n = sectionNames.length;
            int n2 = 0;
            while (n2 < n) {
                String sectionName = stringArray[n2];
                if (sectionMatcher.group(1).equalsIgnoreCase(sectionName)) {
                    found = true;
                    break;
                }
                ++n2;
            }
            if (found) {
                lineBreakMatcher.find(sectionMatcher.end());
                sectionStart = lineBreakMatcher.end();
                continue;
            }
            if (sectionStart == -1) continue;
            sectionEnd = sectionMatcher.start();
        }
        if (sectionStart != -1) {
            if (sectionEnd != -1) {
                return javadoc.substring(sectionStart, sectionEnd);
            }
            return javadoc.substring(sectionStart);
        }
        return null;
    }

    public static String addReturnsThisIfNeeded(String in) {
        if (in != null && FIND_RETURN.matcher(in).find()) {
            return in;
        }
        return HandlerUtil.addJavadocLine(in, "@return {@code this}.");
    }

    public static String addReturnsUpdatedSelfIfNeeded(String in) {
        if (in != null && FIND_RETURN.matcher(in).find()) {
            return in;
        }
        return HandlerUtil.addJavadocLine(in, "@return a clone of this object, except with this updated property (returns {@code this} if an identical value is passed).");
    }

    public static String addJavadocLine(String in, String line) {
        if (in == null) {
            return line;
        }
        if (in.endsWith("\n")) {
            return String.valueOf(in) + line;
        }
        return String.valueOf(in) + "\n" + line;
    }

    public static String getParamJavadoc(String methodComment, String param) {
        if (methodComment == null || methodComment.isEmpty()) {
            return methodComment;
        }
        Pattern pattern = Pattern.compile("@param " + param + " (\\S|\\s)+?(?=^ ?@|\\z)", 10);
        Matcher matcher = pattern.matcher(methodComment);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getConstructorJavadocHeader(String typeName) {
        return "Creates a new {@code " + typeName + "} instance.\n\n";
    }

    public static String getConstructorParameterJavadoc(String paramName, String fieldJavadoc) {
        String fieldBaseJavadoc = HandlerUtil.stripSectionsFromJavadoc(fieldJavadoc);
        String paramJavadoc = HandlerUtil.getParamJavadoc(fieldBaseJavadoc, paramName);
        if (paramJavadoc != null) {
            return paramJavadoc;
        }
        String javadocWithoutTags = HandlerUtil.stripLinesWithTagFromJavadoc(fieldBaseJavadoc, JavadocTag.PARAM, JavadocTag.RETURN);
        if (javadocWithoutTags != null) {
            return "@param " + paramName + " " + javadocWithoutTags;
        }
        return null;
    }

    public static enum FieldAccess {
        GETTER,
        PREFER_FIELD,
        ALWAYS_FIELD;

    }

    public static enum JavadocTag {
        PARAM("@param(?:eter)?"),
        RETURN("@returns?");

        private Pattern pattern;

        private JavadocTag(String regexpFragment) {
            this.pattern = Pattern.compile("\\s?^[ \\t]*\\**[ \\t]*" + regexpFragment + "(\\S|\\s)*?(?=(\\s^\\s*\\**\\s*@|\\Z))", 10);
        }
    }
}
