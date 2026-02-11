package net.mat0u5.lifeseries.network.packets.simple;

import net.mat0u5.lifeseries.network.packets.simple.instances.*;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class SimplePackets {
    public static final Map<String, SimplePacket<?, ?>> registeredPackets = new HashMap<>();
    public static final SimpleStringPacket TEST_STRING = new SimpleStringPacket("test_string", (player, payload) -> OtherUtils.log(TextUtils.format("{} says: {}", player, payload.value())), payload -> OtherUtils.log("Server says: " + payload.value()));
    public static final SimpleBooleanPacket TEST_BOOLEAN = new SimpleBooleanPacket("test_boolean", (player, payload) -> OtherUtils.log(TextUtils.format("{} bools: {}", player, payload.value())), payload -> OtherUtils.log("Server bools: " + payload.value()));
    public static final SimpleEmptyPacket TEST_EMPTY = new SimpleEmptyPacket("test_empty", (player, payload) -> OtherUtils.log("Test String packet received on server: "), payload -> OtherUtils.log("Test String packet received on client: "));
}
