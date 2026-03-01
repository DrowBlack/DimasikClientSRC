package lombok.patcher;

public interface TransplantMapper {
    public static final TransplantMapper IDENTITY_MAPPER = new TransplantMapper(){

        public String mapResourceName(int classFileFormatVersion, String resourceName) {
            return resourceName;
        }
    };

    public String mapResourceName(int var1, String var2);
}
