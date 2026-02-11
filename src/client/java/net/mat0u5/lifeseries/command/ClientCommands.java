package net.mat0u5.lifeseries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.simple.SimplePacket;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.network.packets.simple.instances.SimpleNumberPacket;
import net.mat0u5.lifeseries.network.packets.simple.instances.SimpleStringPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

//? if <= 1.21.11 {
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
//?} else {
/*import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
*///?}

public class ClientCommands {
    public static Minecraft client = Minecraft.getInstance();
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                CommandBuildContext commandRegistryAccess) {
        if (Main.DEBUG) {
            dispatcher.register(
                    literal("lsc")
                            .executes(context -> execute(context.getSource()))
                            .then(literal("packet")
                                    .then(literal("string")
                                            .then(argument("name", StringArgumentType.string())
                                                    .then(argument("value", StringArgumentType.string())
                                                            .executes(context -> sendStringPacket(
                                                                    context.getSource(),
                                                                    StringArgumentType.getString(context, "name"),
                                                                    StringArgumentType.getString(context, "value"))
                                                            )
                                                    )
                                                    .executes(context -> sendStringPacket(
                                                            context.getSource(),
                                                            StringArgumentType.getString(context, "name"),
                                                            "")
                                                    )
                                            )
                                    )
                                    .then(literal("number")
                                            .then(argument("name", StringArgumentType.string())
                                                    .then(argument("value", DoubleArgumentType.doubleArg())
                                                            .executes(context -> sendNumberPacket(
                                                                    context.getSource(),
                                                                    StringArgumentType.getString(context, "name"),
                                                                    DoubleArgumentType.getDouble(context, "value"))
                                                            )
                                                    )
                                            )
                                    )
                                    .then(literal("handshake")
                                            .executes(context -> sendHandshakePacket(
                                                    context.getSource())
                                            )
                                    )
                                    .then(literal("config")
                                            .then(argument("configType", StringArgumentType.string())
                                                    .then(argument("id", StringArgumentType.string())
                                                            .then(argument("args", StringArgumentType.string())
                                                                    .executes(context -> sendConfigPacket(
                                                                            context.getSource(),
                                                                            StringArgumentType.getString(context, "configType"),
                                                                            StringArgumentType.getString(context, "id"),
                                                                            StringArgumentType.getString(context, "args"))
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                            .then(literal("test")
                                    .executes(context -> test(
                                            context.getSource())
                                    )
                            )
            );
        }
    }

    public static int test(FabricClientCommandSource source)  {
        return 1;
    }

    public static int execute(FabricClientCommandSource source)  {
        source.sendFeedback(Component.nullToEmpty("Life Series client command text."));
        return 1;
    }

    public static int sendStringPacket(FabricClientCommandSource source, String name, String value)  {
        final Player self = source.getPlayer();
        SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(name);
        if (packet == null) return -1;
        if (!(packet instanceof SimpleStringPacket simpleStringPacket)) return -1;
        simpleStringPacket.sendToServer(value);
        self.displayClientMessage(Component.nullToEmpty("String packet sent."), false);
        return 1;
    }

    public static int sendNumberPacket(FabricClientCommandSource source, String name, double value)  {
        final Player self = source.getPlayer();
        SimplePacket<?, ?> packet = SimplePackets.registeredPackets.get(name);
        if (packet == null) return -1;
        if (!(packet instanceof SimpleNumberPacket simpleNumberPacket)) return -1;
        simpleNumberPacket.sendToServer(value);
        self.displayClientMessage(Component.nullToEmpty("Number packet sent."), false);
        return 1;
    }

    public static int sendHandshakePacket(FabricClientCommandSource source)  {
        final Player self = source.getPlayer();
        NetworkHandlerClient.sendHandshake();
        self.displayClientMessage(Component.nullToEmpty("Handshake packet sent."), false);
        return 1;
    }

    public static int sendConfigPacket(FabricClientCommandSource source, String configType, String id, String argsStr)  {
        final Player self = source.getPlayer();
        List<String> args = new ArrayList<>();
        if (argsStr.contains(";")) {
            for (String arg : argsStr.split(";")) {
                if (!arg.isEmpty()) {
                    args.add(arg);
                }
            }
        }
        else {
            args = List.of(argsStr);
        }

        NetworkHandlerClient.sendConfigUpdate(configType, id, args);
        self.displayClientMessage(Component.nullToEmpty("Config packet sent."), false);
        return 1;
    }

}
