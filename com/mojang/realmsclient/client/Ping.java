package com.mojang.realmsclient.client;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.Util;

public class Ping {
    public static List<RegionPingResult> func_224867_a(Region ... p_224867_0_) {
        for (Region ping$region : p_224867_0_) {
            Ping.func_224868_a(ping$region.field_224863_j);
        }
        ArrayList<RegionPingResult> list = Lists.newArrayList();
        for (Region ping$region1 : p_224867_0_) {
            list.add(new RegionPingResult(ping$region1.field_224862_i, Ping.func_224868_a(ping$region1.field_224863_j)));
        }
        list.sort(Comparator.comparingInt(RegionPingResult::func_230792_a_));
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int func_224868_a(String p_224868_0_) {
        int i = 700;
        long j = 0L;
        Socket socket = null;
        for (int k = 0; k < 5; ++k) {
            try {
                InetSocketAddress socketaddress = new InetSocketAddress(p_224868_0_, 80);
                socket = new Socket();
                long l = Ping.func_224865_b();
                socket.connect(socketaddress, 700);
                j += Ping.func_224865_b() - l;
                Ping.func_224866_a(socket);
                continue;
            }
            catch (Exception exception) {
                j += 700L;
                continue;
            }
            finally {
                Ping.func_224866_a(socket);
            }
        }
        return (int)((double)j / 5.0);
    }

    private static void func_224866_a(Socket p_224866_0_) {
        try {
            if (p_224866_0_ != null) {
                p_224866_0_.close();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static long func_224865_b() {
        return Util.milliTime();
    }

    public static List<RegionPingResult> func_224864_a() {
        return Ping.func_224867_a(Region.values());
    }

    static enum Region {
        US_EAST_1("us-east-1", "ec2.us-east-1.amazonaws.com"),
        US_WEST_2("us-west-2", "ec2.us-west-2.amazonaws.com"),
        US_WEST_1("us-west-1", "ec2.us-west-1.amazonaws.com"),
        EU_WEST_1("eu-west-1", "ec2.eu-west-1.amazonaws.com"),
        AP_SOUTHEAST_1("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com"),
        AP_SOUTHEAST_2("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com"),
        AP_NORTHEAST_1("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com"),
        SA_EAST_1("sa-east-1", "ec2.sa-east-1.amazonaws.com");

        private final String field_224862_i;
        private final String field_224863_j;

        private Region(String p_i51602_3_, String p_i51602_4_) {
            this.field_224862_i = p_i51602_3_;
            this.field_224863_j = p_i51602_4_;
        }
    }
}
