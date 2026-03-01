package lombok.patcher.scripts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.patcher.Hook;
import lombok.patcher.StackRequest;
import lombok.patcher.TargetMatcher;
import lombok.patcher.scripts.AddFieldScript;
import lombok.patcher.scripts.ExitFromMethodEarlyScript;
import lombok.patcher.scripts.ReplaceMethodCallScript;
import lombok.patcher.scripts.SetSymbolDuringMethodCallScript;
import lombok.patcher.scripts.WrapMethodCallScript;
import lombok.patcher.scripts.WrapReturnValuesScript;

public class ScriptBuilder {
    private ScriptBuilder() throws NoSuchMethodException {
        throw new NoSuchMethodException("ScriptBuilder cannot be instantiated - just use the static methods.");
    }

    private static void checkTypeSyntaxSlash(String spec) {
        if (spec.indexOf(46) > -1) {
            throw new IllegalArgumentException("Your type specification includes a dot, but this method wants a slash-separated type specification");
        }
    }

    private static void checkTypeSyntaxDot(String spec) {
        if (spec.indexOf(47) > -1) {
            throw new IllegalArgumentException("Your type specification includes a slash, but this method wants a dot-separated type specification");
        }
    }

    public static AddFieldBuilder addField() {
        return new AddFieldBuilder();
    }

    public static ExitEarlyBuilder exitEarly() {
        return new ExitEarlyBuilder();
    }

    public static ReplaceMethodCallBuilder replaceMethodCall() {
        return new ReplaceMethodCallBuilder();
    }

    public static WrapMethodCallBuilder wrapMethodCall() {
        return new WrapMethodCallBuilder();
    }

    public static WrapReturnValueBuilder wrapReturnValue() {
        return new WrapReturnValueBuilder();
    }

    public static SetSymbolDuringMethodCallBuilder setSymbolDuringMethodCall() {
        return new SetSymbolDuringMethodCallBuilder();
    }

    public static class AddFieldBuilder {
        private int accessFlags;
        private List<String> targetClasses = new ArrayList<String>();
        private String fieldName;
        private String fieldType;
        private Object value;
        private static final int NO_ACCESS_LEVELS = -4;

        public AddFieldScript build() {
            if (this.targetClasses.isEmpty()) {
                throw new IllegalStateException("You have to set at least one targetClass.");
            }
            if (this.fieldName == null) {
                throw new IllegalStateException("You have to set a fieldName");
            }
            if (this.fieldType == null) {
                throw new IllegalStateException("You have to set the new field's type by calling fieldType");
            }
            if (this.value != null) {
                this.setStatic();
                this.setFinal();
            }
            return new AddFieldScript(this.targetClasses, this.accessFlags, this.fieldName, this.fieldType, this.value);
        }

        public AddFieldBuilder targetClass(String targetClass) {
            ScriptBuilder.checkTypeSyntaxDot(targetClass);
            this.targetClasses.add(targetClass);
            return this;
        }

        public AddFieldBuilder value(Object value) {
            this.value = value;
            return this;
        }

        public AddFieldBuilder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public AddFieldBuilder fieldType(String fieldType) {
            ScriptBuilder.checkTypeSyntaxSlash(fieldType);
            this.fieldType = fieldType;
            return this;
        }

        public AddFieldBuilder setPublic() {
            this.accessFlags = this.accessFlags & 0xFFFFFFFC | 1;
            return this;
        }

        public AddFieldBuilder setPrivate() {
            this.accessFlags = this.accessFlags & 0xFFFFFFFC | 2;
            return this;
        }

        public AddFieldBuilder setProtected() {
            this.accessFlags = this.accessFlags & 0xFFFFFFFC | 4;
            return this;
        }

        public AddFieldBuilder setPackageAccess() {
            this.accessFlags &= 0xFFFFFFFC;
            return this;
        }

        public AddFieldBuilder setStatic() {
            this.accessFlags |= 8;
            return this;
        }

        public AddFieldBuilder setFinal() {
            this.accessFlags |= 0x10;
            return this;
        }

        public AddFieldBuilder setVolatile() {
            this.accessFlags |= 0x40;
            return this;
        }

        public AddFieldBuilder setTransient() {
            this.accessFlags |= 0x80;
            return this;
        }
    }

    public static class ExitEarlyBuilder {
        private List<TargetMatcher> matchers = new ArrayList<TargetMatcher>();
        private Hook decisionMethod;
        private Hook valueMethod;
        private Set<StackRequest> requests = new HashSet<StackRequest>();
        private boolean transplant;
        private boolean insert;

        public ExitFromMethodEarlyScript build() {
            if (this.matchers.isEmpty()) {
                throw new IllegalStateException("You have to set a target method matcher");
            }
            return new ExitFromMethodEarlyScript(this.matchers, this.decisionMethod, this.valueMethod, this.transplant, this.insert, this.requests);
        }

        public ExitEarlyBuilder target(TargetMatcher matcher) {
            this.matchers.add(matcher);
            return this;
        }

        public ExitEarlyBuilder decisionMethod(Hook hook) {
            this.decisionMethod = hook;
            return this;
        }

        public ExitEarlyBuilder valueMethod(Hook hook) {
            this.valueMethod = hook;
            return this;
        }

        public ExitEarlyBuilder transplant() {
            this.transplant = true;
            this.insert = false;
            return this;
        }

        public ExitEarlyBuilder insert() {
            this.transplant = false;
            this.insert = true;
            return this;
        }

        public ExitEarlyBuilder request(StackRequest ... requests) {
            StackRequest[] stackRequestArray = requests;
            int n = requests.length;
            int n2 = 0;
            while (n2 < n) {
                StackRequest r = stackRequestArray[n2];
                if (r == StackRequest.RETURN_VALUE) {
                    throw new IllegalArgumentException("You cannot ask for the tentative return value in ExitFromMethodEarlyScript");
                }
                this.requests.add(r);
                ++n2;
            }
            return this;
        }
    }

    public static class ReplaceMethodCallBuilder {
        private List<TargetMatcher> matchers = new ArrayList<TargetMatcher>();
        private Hook replacementMethod;
        private Hook methodToReplace;
        private Set<StackRequest> extraRequests = new HashSet<StackRequest>();
        private boolean transplant;
        private boolean insert;

        public ReplaceMethodCallScript build() {
            if (this.matchers.isEmpty()) {
                throw new IllegalStateException("You have to set a target method matcher");
            }
            if (this.replacementMethod == null) {
                throw new IllegalStateException("You have to set a replacement method");
            }
            if (this.methodToReplace == null) {
                throw new IllegalStateException("You have to set a method call to replace");
            }
            return new ReplaceMethodCallScript(this.matchers, this.methodToReplace, this.replacementMethod, this.transplant, this.insert, this.extraRequests);
        }

        public ReplaceMethodCallBuilder target(TargetMatcher matcher) {
            this.matchers.add(matcher);
            return this;
        }

        public ReplaceMethodCallBuilder replacementMethod(Hook hook) {
            this.replacementMethod = hook;
            return this;
        }

        public ReplaceMethodCallBuilder methodToReplace(Hook hook) {
            this.methodToReplace = hook;
            return this;
        }

        public ReplaceMethodCallBuilder transplant() {
            this.transplant = true;
            this.insert = false;
            return this;
        }

        public ReplaceMethodCallBuilder insert() {
            this.transplant = false;
            this.insert = true;
            return this;
        }

        public ReplaceMethodCallBuilder requestExtra(StackRequest ... requests) {
            StackRequest[] stackRequestArray = requests;
            int n = requests.length;
            int n2 = 0;
            while (n2 < n) {
                StackRequest r = stackRequestArray[n2];
                if (r == StackRequest.RETURN_VALUE) {
                    throw new IllegalArgumentException("You cannot ask for the tentative return value in ReplaceMethodCallScript");
                }
                this.extraRequests.add(r);
                ++n2;
            }
            return this;
        }
    }

    public static class SetSymbolDuringMethodCallBuilder {
        private List<TargetMatcher> matchers = new ArrayList<TargetMatcher>();
        private Hook callToWrap;
        private String symbol;
        private boolean report;

        public SetSymbolDuringMethodCallScript build() {
            if (this.matchers.isEmpty()) {
                throw new IllegalStateException("You have to set a target method matcher");
            }
            if (this.callToWrap == null) {
                throw new IllegalStateException("You have to set a method that needs to set the symbol during its invocation");
            }
            if (this.symbol == null) {
                throw new IllegalStateException("You have to specify the symbol that is on the stack during callToWrap's invocation");
            }
            return new SetSymbolDuringMethodCallScript(this.matchers, this.callToWrap, this.symbol, this.report);
        }

        public SetSymbolDuringMethodCallBuilder target(TargetMatcher matcher) {
            this.matchers.add(matcher);
            return this;
        }

        public SetSymbolDuringMethodCallBuilder callToWrap(Hook callToWrap) {
            this.callToWrap = callToWrap;
            return this;
        }

        public SetSymbolDuringMethodCallBuilder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public SetSymbolDuringMethodCallBuilder report() {
            this.report = true;
            return this;
        }
    }

    public static class WrapMethodCallBuilder {
        private List<TargetMatcher> matchers = new ArrayList<TargetMatcher>();
        private Hook wrapMethod;
        private Hook methodToWrap;
        private Set<StackRequest> extraRequests = new HashSet<StackRequest>();
        private boolean transplant;
        private boolean insert;

        public WrapMethodCallScript build() {
            if (this.matchers.isEmpty()) {
                throw new IllegalStateException("You have to set a target method matcher");
            }
            if (this.wrapMethod == null) {
                throw new IllegalStateException("You have to set method to wrap with");
            }
            if (this.methodToWrap == null) {
                throw new IllegalStateException("You have to set a method call to wrap");
            }
            return new WrapMethodCallScript(this.matchers, this.methodToWrap, this.wrapMethod, this.transplant, this.insert, this.extraRequests);
        }

        public WrapMethodCallBuilder target(TargetMatcher matcher) {
            this.matchers.add(matcher);
            return this;
        }

        public WrapMethodCallBuilder wrapMethod(Hook hook) {
            this.wrapMethod = hook;
            return this;
        }

        public WrapMethodCallBuilder methodToWrap(Hook hook) {
            this.methodToWrap = hook;
            return this;
        }

        public WrapMethodCallBuilder transplant() {
            this.transplant = true;
            this.insert = false;
            return this;
        }

        public WrapMethodCallBuilder insert() {
            this.transplant = false;
            this.insert = true;
            return this;
        }

        public WrapMethodCallBuilder requestExtra(StackRequest ... requests) {
            StackRequest[] stackRequestArray = requests;
            int n = requests.length;
            int n2 = 0;
            while (n2 < n) {
                StackRequest r = stackRequestArray[n2];
                if (r == StackRequest.RETURN_VALUE) {
                    throw new IllegalArgumentException("You cannot ask for the tentative return value in WrapMethodCallBuilder");
                }
                this.extraRequests.add(r);
                ++n2;
            }
            return this;
        }
    }

    public static class WrapReturnValueBuilder {
        private List<TargetMatcher> matchers = new ArrayList<TargetMatcher>();
        private Hook wrapMethod;
        private Set<StackRequest> requests = new HashSet<StackRequest>();
        private boolean transplant;
        private boolean insert;
        private boolean cast;

        public WrapReturnValuesScript build() {
            if (this.matchers.isEmpty()) {
                throw new IllegalStateException("You have to set a target method matcher");
            }
            if (this.wrapMethod == null) {
                throw new IllegalStateException("You have to set a method you'd like to wrap the return values with");
            }
            return new WrapReturnValuesScript(this.matchers, this.wrapMethod, this.transplant, this.insert, this.cast, this.requests);
        }

        public WrapReturnValueBuilder target(TargetMatcher matcher) {
            this.matchers.add(matcher);
            return this;
        }

        public WrapReturnValueBuilder wrapMethod(Hook hook) {
            this.wrapMethod = hook;
            return this;
        }

        public WrapReturnValueBuilder transplant() {
            this.transplant = true;
            this.insert = false;
            return this;
        }

        public WrapReturnValueBuilder insert() {
            if (this.cast) {
                throw new IllegalArgumentException("cast and insert are mutually exlusive");
            }
            this.transplant = false;
            this.insert = true;
            return this;
        }

        public WrapReturnValueBuilder cast() {
            if (this.insert) {
                throw new IllegalArgumentException("insert and cast are mutually exlusive");
            }
            this.cast = true;
            return this;
        }

        public WrapReturnValueBuilder request(StackRequest ... requests) {
            StackRequest[] stackRequestArray = requests;
            int n = requests.length;
            int n2 = 0;
            while (n2 < n) {
                StackRequest r = stackRequestArray[n2];
                this.requests.add(r);
                ++n2;
            }
            return this;
        }
    }
}
