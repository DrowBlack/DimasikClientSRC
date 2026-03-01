package org.openjdk.nashorn.internal.runtime.linker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.function.Supplier;
import jdk.dynalink.linker.ConversionComparator;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.GuardingTypeConverterFactory;
import jdk.dynalink.linker.LinkRequest;
import jdk.dynalink.linker.LinkerServices;
import jdk.dynalink.linker.TypeBasedGuardingDynamicLinker;
import jdk.dynalink.linker.support.TypeUtilities;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.ConsString;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.Symbol;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;
import org.openjdk.nashorn.internal.runtime.linker.JavaArgumentConverters;

final class NashornPrimitiveLinker
implements TypeBasedGuardingDynamicLinker,
GuardingTypeConverterFactory,
ConversionComparator {
    private static final GuardedInvocation VOID_TO_OBJECT = new GuardedInvocation(MethodHandles.constant(Object.class, ScriptRuntime.UNDEFINED));
    private static final MethodHandle GUARD_PRIMITIVE = NashornPrimitiveLinker.findOwnMH("isJavaScriptPrimitive", Boolean.TYPE, Object.class);

    NashornPrimitiveLinker() {
    }

    @Override
    public boolean canLinkType(Class<?> type) {
        return NashornPrimitiveLinker.canLinkTypeStatic(type);
    }

    private static boolean canLinkTypeStatic(Class<?> type) {
        return type == String.class || type == Boolean.class || type == ConsString.class || type == Integer.class || type == Double.class || type == Float.class || type == Short.class || type == Byte.class || type == Symbol.class;
    }

    @Override
    public GuardedInvocation getGuardedInvocation(LinkRequest request, LinkerServices linkerServices) {
        Object self = request.getReceiver();
        return Bootstrap.asTypeSafeReturn(Global.primitiveLookup(request, self), linkerServices, request.getCallSiteDescriptor());
    }

    @Override
    public GuardedInvocation convertToType(Class<?> sourceType, Class<?> targetType, Supplier<MethodHandles.Lookup> lookupSupplier) {
        MethodHandle mh = JavaArgumentConverters.getConverter(targetType);
        if (mh == null) {
            if (targetType == Object.class && sourceType == Void.TYPE) {
                return VOID_TO_OBJECT;
            }
            return null;
        }
        return new GuardedInvocation(mh, NashornPrimitiveLinker.canLinkTypeStatic(sourceType) ? null : GUARD_PRIMITIVE).asType(mh.type().changeParameterType(0, sourceType));
    }

    @Override
    public ConversionComparator.Comparison compareConversion(Class<?> sourceType, Class<?> targetType1, Class<?> targetType2) {
        Class<?> wrapper1 = NashornPrimitiveLinker.getWrapperTypeOrSelf(targetType1);
        if (sourceType == wrapper1) {
            return ConversionComparator.Comparison.TYPE_1_BETTER;
        }
        Class<?> wrapper2 = NashornPrimitiveLinker.getWrapperTypeOrSelf(targetType2);
        if (sourceType == wrapper2) {
            return ConversionComparator.Comparison.TYPE_2_BETTER;
        }
        if (Number.class.isAssignableFrom(sourceType)) {
            if (Number.class.isAssignableFrom(wrapper1)) {
                if (!Number.class.isAssignableFrom(wrapper2)) {
                    return ConversionComparator.Comparison.TYPE_1_BETTER;
                }
            } else if (Number.class.isAssignableFrom(wrapper2)) {
                return ConversionComparator.Comparison.TYPE_2_BETTER;
            }
            if (Character.class == wrapper1) {
                return ConversionComparator.Comparison.TYPE_1_BETTER;
            }
            if (Character.class == wrapper2) {
                return ConversionComparator.Comparison.TYPE_2_BETTER;
            }
        }
        if (sourceType == String.class || sourceType == Boolean.class || Number.class.isAssignableFrom(sourceType)) {
            Class<?> primitiveType2;
            Class<?> primitiveType1 = NashornPrimitiveLinker.getPrimitiveTypeOrSelf(targetType1);
            if (TypeUtilities.isMethodInvocationConvertible(primitiveType1, primitiveType2 = NashornPrimitiveLinker.getPrimitiveTypeOrSelf(targetType2))) {
                return ConversionComparator.Comparison.TYPE_2_BETTER;
            }
            if (TypeUtilities.isMethodInvocationConvertible(primitiveType2, primitiveType1)) {
                return ConversionComparator.Comparison.TYPE_1_BETTER;
            }
            if (targetType1 == String.class) {
                return ConversionComparator.Comparison.TYPE_1_BETTER;
            }
            if (targetType2 == String.class) {
                return ConversionComparator.Comparison.TYPE_2_BETTER;
            }
        }
        return ConversionComparator.Comparison.INDETERMINATE;
    }

    private static Class<?> getPrimitiveTypeOrSelf(Class<?> type) {
        Class<?> primitive = TypeUtilities.getPrimitiveType(type);
        return primitive == null ? type : primitive;
    }

    private static Class<?> getWrapperTypeOrSelf(Class<?> type) {
        Class<?> wrapper = TypeUtilities.getWrapperType(type);
        return wrapper == null ? type : wrapper;
    }

    private static boolean isJavaScriptPrimitive(Object o) {
        return JSType.isString(o) || o instanceof Boolean || JSType.isNumber(o) || o == null || o instanceof Symbol;
    }

    private static MethodHandle findOwnMH(String name, Class<?> rtype, Class<?> ... types) {
        return Lookup.MH.findStatic(MethodHandles.lookup(), NashornPrimitiveLinker.class, name, Lookup.MH.type(rtype, types));
    }
}
