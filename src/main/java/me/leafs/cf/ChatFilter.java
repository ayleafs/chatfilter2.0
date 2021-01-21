package me.leafs.cf;

import lombok.Getter;
import me.leafs.cf.commands.OpenGui;
import me.leafs.cf.config.ChatFilterConfig;
import me.leafs.cf.config.ConfigHandler;
import me.leafs.cf.events.FilterHandler;
import me.leafs.cf.events.ScreenRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

@Mod(modid = ChatFilter.MODID, version = ChatFilter.VERSION)
public class ChatFilter {
    public static final String MODID = "cf";
    public static final String VERSION = "2.0";

    @Mod.Instance
    public static ChatFilter instance;

    @Getter private ConfigHandler handler;
    @Getter private ChatFilterConfig config;

    @Getter private final KeyBinding quickSend  = new KeyBinding("chatfilter.control.quick", Keyboard.KEY_RETURN, "chatfilter.control.title");
    @Getter private final KeyBinding cancelSend = new KeyBinding("chatfilter.control.cancel", Keyboard.KEY_BACK, "chatfilter.control.title");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // make and read the config file
        handler = new ConfigHandler(event.getModConfigurationDirectory());
        config = handler.readConfig();

        // add a shutdown hook to save on the game exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> handler.populateConfig(config)));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        registerEvents();

        // register client command
        ClientCommandHandler.instance.registerCommand(new OpenGui());

        // register both the confirm and sending binds
        ClientRegistry.registerKeyBinding(quickSend);
        ClientRegistry.registerKeyBinding(cancelSend);
    }

    private void registerEvents() {
        EventBus bus = MinecraftForge.EVENT_BUS;

        bus.register(new FilterHandler());
        bus.register(new ScreenRenderer());
    }
}
