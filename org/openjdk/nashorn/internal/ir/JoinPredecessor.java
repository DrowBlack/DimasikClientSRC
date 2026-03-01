package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LocalVariableConversion;

public interface JoinPredecessor {
    public JoinPredecessor setLocalVariableConversion(LexicalContext var1, LocalVariableConversion var2);

    public LocalVariableConversion getLocalVariableConversion();
}
