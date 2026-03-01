package org.openjdk.nashorn.internal.codegen.types;

import java.lang.invoke.MethodHandle;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.codegen.types.ArrayType;
import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.Undefined;

class ObjectType
extends Type {
    private static final long serialVersionUID = 1L;

    protected ObjectType() {
        this(Object.class);
    }

    protected ObjectType(Class<?> clazz) {
        super("object", clazz, clazz == Object.class ? 20 : 10, 1);
    }

    @Override
    public String toString() {
        return "object" + (String)(this.getTypeClass() != Object.class ? "<type=" + this.getTypeClass().getSimpleName() + ">" : "");
    }

    @Override
    public String getShortDescriptor() {
        return this.getTypeClass() == Object.class ? "Object" : this.getTypeClass().getSimpleName();
    }

    @Override
    public Type add(MethodVisitor method, int programPoint) {
        ObjectType.invokestatic(method, ScriptRuntime.ADD);
        return Type.OBJECT;
    }

    @Override
    public Type load(MethodVisitor method, int slot) {
        assert (slot != -1);
        method.visitVarInsn(25, slot);
        return this;
    }

    @Override
    public void store(MethodVisitor method, int slot) {
        assert (slot != -1);
        method.visitVarInsn(58, slot);
    }

    @Override
    public Type loadUndefined(MethodVisitor method) {
        method.visitFieldInsn(178, CompilerConstants.className(ScriptRuntime.class), "UNDEFINED", CompilerConstants.typeDescriptor(Undefined.class));
        return UNDEFINED;
    }

    @Override
    public Type loadForcedInitializer(MethodVisitor method) {
        method.visitInsn(1);
        return OBJECT;
    }

    @Override
    public Type loadEmpty(MethodVisitor method) {
        method.visitFieldInsn(178, CompilerConstants.className(ScriptRuntime.class), "EMPTY", CompilerConstants.typeDescriptor(Undefined.class));
        return UNDEFINED;
    }

    @Override
    public Type ldc(MethodVisitor method, Object c) {
        if (c != null) {
            if (c instanceof Undefined) {
                return this.loadUndefined(method);
            }
            if (c instanceof String) {
                method.visitLdcInsn(c);
                return STRING;
            }
            if (c instanceof Handle) {
                method.visitLdcInsn(c);
                return Type.typeFor(MethodHandle.class);
            }
            throw new UnsupportedOperationException("implementation missing for class " + c.getClass() + " value=" + c);
        }
        method.visitInsn(1);
        return Type.OBJECT;
    }

    @Override
    public Type convert(MethodVisitor method, Type to) {
        boolean toString = to.isString();
        if (!toString) {
            if (to.isArray()) {
                Type elemType = ((ArrayType)to).getElementType();
                if (elemType.isString()) {
                    method.visitTypeInsn(192, CompilerConstants.className(String[].class));
                } else if (elemType.isNumber()) {
                    method.visitTypeInsn(192, CompilerConstants.className(double[].class));
                } else if (elemType.isLong()) {
                    method.visitTypeInsn(192, CompilerConstants.className(long[].class));
                } else if (elemType.isInteger()) {
                    method.visitTypeInsn(192, CompilerConstants.className(int[].class));
                } else {
                    method.visitTypeInsn(192, CompilerConstants.className(Object[].class));
                }
                return to;
            }
            if (to.isObject()) {
                Class<?> toClass = to.getTypeClass();
                if (!toClass.isAssignableFrom(this.getTypeClass())) {
                    method.visitTypeInsn(192, CompilerConstants.className(toClass));
                }
                return to;
            }
        } else if (this.isString()) {
            return to;
        }
        if (to.isInteger()) {
            ObjectType.invokestatic(method, JSType.TO_INT32);
        } else if (to.isNumber()) {
            ObjectType.invokestatic(method, JSType.TO_NUMBER);
        } else if (to.isLong()) {
            ObjectType.invokestatic(method, JSType.TO_LONG);
        } else if (to.isBoolean()) {
            ObjectType.invokestatic(method, JSType.TO_BOOLEAN);
        } else if (to.isString()) {
            ObjectType.invokestatic(method, JSType.TO_PRIMITIVE_TO_STRING);
        } else if (to.isCharSequence()) {
            ObjectType.invokestatic(method, JSType.TO_PRIMITIVE_TO_CHARSEQUENCE);
        } else {
            throw new UnsupportedOperationException("Illegal conversion " + this + " -> " + to + " " + this.isString() + " " + toString);
        }
        return to;
    }

    @Override
    public void _return(MethodVisitor method) {
        method.visitInsn(176);
    }

    @Override
    public char getBytecodeStackType() {
        return 'A';
    }
}
