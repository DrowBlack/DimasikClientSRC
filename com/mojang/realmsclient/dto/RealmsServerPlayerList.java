package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerPlayerList
extends ValueObject {
    private static final Logger field_230611_c_ = LogManager.getLogger();
    private static final JsonParser field_237698_d_ = new JsonParser();
    public long field_230609_a_;
    public List<String> field_230610_b_;

    public static RealmsServerPlayerList func_230785_a_(JsonObject p_230785_0_) {
        RealmsServerPlayerList realmsserverplayerlist = new RealmsServerPlayerList();
        try {
            JsonElement jsonelement;
            realmsserverplayerlist.field_230609_a_ = JsonUtils.func_225169_a("serverId", p_230785_0_, -1L);
            String s = JsonUtils.func_225171_a("playerList", p_230785_0_, null);
            realmsserverplayerlist.field_230610_b_ = s != null ? ((jsonelement = field_237698_d_.parse(s)).isJsonArray() ? RealmsServerPlayerList.func_230784_a_(jsonelement.getAsJsonArray()) : Lists.newArrayList()) : Lists.newArrayList();
        }
        catch (Exception exception) {
            field_230611_c_.error("Could not parse RealmsServerPlayerList: " + exception.getMessage());
        }
        return realmsserverplayerlist;
    }

    private static List<String> func_230784_a_(JsonArray p_230784_0_) {
        ArrayList<String> list = Lists.newArrayList();
        for (JsonElement jsonelement : p_230784_0_) {
            try {
                list.add(jsonelement.getAsString());
            }
            catch (Exception exception) {}
        }
        return list;
    }
}
