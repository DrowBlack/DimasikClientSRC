package lombok.patcher.scripts;

public class WrapperMethodDescriptor {
    private final int count;
    private final int opcode;
    private final String owner;
    private final String name;
    private final String wrapperDescriptor;
    private final String targetDescriptor;
    private final boolean itf;

    public WrapperMethodDescriptor(int count, int opcode, String owner, String name, String wrapperDescriptor, String targetDescriptor, boolean itf) {
        this.count = count;
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.wrapperDescriptor = wrapperDescriptor;
        this.targetDescriptor = targetDescriptor;
        this.itf = itf;
    }

    public int getCount() {
        return this.count;
    }

    public int getOpcode() {
        return this.opcode;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public String getWrapperDescriptor() {
        return this.wrapperDescriptor;
    }

    public String getTargetDescriptor() {
        return this.targetDescriptor;
    }

    public boolean isItf() {
        return this.itf;
    }

    public String getWrapperName() {
        return "$lombok$$wrapper$" + this.count + "$" + this.name;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + this.count;
        result = 31 * result + (this.itf ? 1231 : 1237);
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 31 * result + this.opcode;
        result = 31 * result + (this.owner == null ? 0 : this.owner.hashCode());
        result = 31 * result + (this.targetDescriptor == null ? 0 : this.targetDescriptor.hashCode());
        result = 31 * result + (this.wrapperDescriptor == null ? 0 : this.wrapperDescriptor.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        WrapperMethodDescriptor other = (WrapperMethodDescriptor)obj;
        if (this.count != other.count) {
            return false;
        }
        if (this.itf != other.itf) {
            return false;
        }
        if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
            return false;
        }
        if (this.opcode != other.opcode) {
            return false;
        }
        if (this.owner == null ? other.owner != null : !this.owner.equals(other.owner)) {
            return false;
        }
        if (this.targetDescriptor == null ? other.targetDescriptor != null : !this.targetDescriptor.equals(other.targetDescriptor)) {
            return false;
        }
        return !(this.wrapperDescriptor == null ? other.wrapperDescriptor != null : !this.wrapperDescriptor.equals(other.wrapperDescriptor));
    }

    public String toString() {
        return "WrapperMethodDescriptor[count=" + this.count + ", opcode=" + this.opcode + ", owner=" + this.owner + ", name=" + this.name + ", wrapperDescriptor=" + this.wrapperDescriptor + ", targetDescriptor=" + this.targetDescriptor + ", itf=" + this.itf + "]";
    }
}
