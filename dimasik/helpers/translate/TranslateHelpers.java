package dimasik.helpers.translate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.client.ClientManagers;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class TranslateHelpers
implements IFastAccess {
    public String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private String read() {
        return this.readInputStream(Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation("main/lang/" + ClientManagers.getLanguage() + ".json")).getInputStream());
    }

    public String get(String text) {
        JsonParser parser = new JsonParser();
        JsonObject translate = (JsonObject)parser.parse(this.read());
        return translate.get(text).getAsString();
    }

    public boolean has(String text) {
        JsonParser parser = new JsonParser();
        JsonObject translate = (JsonObject)parser.parse(this.read());
        return translate.has(text);
    }
}
