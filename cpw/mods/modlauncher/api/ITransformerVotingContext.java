package cpw.mods.modlauncher.api;

import cpw.mods.modlauncher.api.ITransformerActivity;
import java.util.List;

public interface ITransformerVotingContext {
    public String getClassName();

    public boolean doesClassExist();

    public byte[] getInitialClassSha256();

    public List<ITransformerActivity> getAuditActivities();

    public String getReason();

    public boolean applyFieldPredicate(FieldPredicate var1);

    public boolean applyMethodPredicate(MethodPredicate var1);

    public boolean applyClassPredicate(ClassPredicate var1);

    public boolean applyInstructionPredicate(InsnPredicate var1);

    public static interface InsnPredicate {
        public boolean test(int var1, int var2, Object ... var3);
    }

    public static interface ClassPredicate {
        public boolean test(int var1, int var2, String var3, String var4, String var5, String[] var6);
    }

    public static interface MethodPredicate {
        public boolean test(int var1, String var2, String var3, String var4, String[] var5);
    }

    public static interface FieldPredicate {
        public boolean test(int var1, String var2, String var3, String var4, Object var5);
    }
}
