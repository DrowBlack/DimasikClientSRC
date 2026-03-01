package dimasik.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import dimasik.proxy.Proxy;
import dimasik.proxy.ProxyServer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;
import ru.dreamix.class2native.CompileNativeCalls;

public class ProxyConfig {
    private static final String CONFIG_PATH = String.valueOf(Minecraft.getInstance().gameDir) + "dimasik/client/proxyconfig.json";
    public static HashMap<String, Proxy> accounts = new HashMap();
    public static String lastPlayerName = "";

    @CompileNativeCalls
    public static void loadConfig() {
        File configFile = new File(CONFIG_PATH);
        try {
            if (!configFile.exists()) {
                if (!configFile.createNewFile()) {
                    System.out.println("Error creating proxyconfig.json file");
                }
                return;
            }
            String configString = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
            if (!configString.isEmpty()) {
                JsonObject configJson = new JsonParser().parse(configString).getAsJsonObject();
                ProxyServer.proxyEnabled = configJson.get("proxy-enabled").getAsBoolean();
                Type type = new TypeToken<HashMap<String, Proxy>>(){}.getType();
                accounts = (HashMap)new Gson().fromJson(configJson.get("accounts"), type);
                if (accounts == null) {
                    accounts = new HashMap();
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error reading proxyconfig.json file");
            e.printStackTrace();
        }
    }

    public static void setDefaultProxy(Proxy proxy) {
        accounts.put("", proxy);
    }

    public static void saveConfig() {
        try {
            JsonElement accountsJsonObject = new Gson().toJsonTree(accounts);
            JsonObject configJson = new JsonObject();
            configJson.addProperty("proxy-enabled", ProxyServer.proxyEnabled);
            configJson.add("accounts", accountsJsonObject);
            Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
            FileUtils.write(new File(CONFIG_PATH), (CharSequence)gsonPretty.toJson(configJson), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            System.out.println("Error writing proxyconfig.json file");
            e.printStackTrace();
        }
    }
}
