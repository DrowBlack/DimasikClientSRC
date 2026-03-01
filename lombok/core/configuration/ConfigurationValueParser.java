package lombok.core.configuration;

interface ConfigurationValueParser {
    public Object parse(String var1);

    public String description();

    public String exampleValue();
}
