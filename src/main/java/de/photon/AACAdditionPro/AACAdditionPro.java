package de.photon.AACAdditionPro;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.collect.ImmutableSet;
import de.photon.AACAdditionPro.command.MainCommand;
import de.photon.AACAdditionPro.events.APILoadedEvent;
import de.photon.AACAdditionPro.modules.ModuleManager;
import de.photon.AACAdditionPro.modules.additions.BrandHider;
import de.photon.AACAdditionPro.modules.additions.LogBot;
import de.photon.AACAdditionPro.modules.checks.AutoPotion;
import de.photon.AACAdditionPro.modules.checks.Esp;
import de.photon.AACAdditionPro.modules.checks.Fastswitch;
import de.photon.AACAdditionPro.modules.checks.ImpossibleChat;
import de.photon.AACAdditionPro.modules.checks.Pingspoof;
import de.photon.AACAdditionPro.modules.checks.SkinBlinker;
import de.photon.AACAdditionPro.modules.checks.Teaming;
import de.photon.AACAdditionPro.modules.checks.Tower;
import de.photon.AACAdditionPro.modules.checks.autofish.AutoFish;
import de.photon.AACAdditionPro.modules.checks.inventory.Inventory;
import de.photon.AACAdditionPro.modules.checks.keepalive.KeepAlive;
import de.photon.AACAdditionPro.modules.checks.packetanalysis.PacketAnalysis;
import de.photon.AACAdditionPro.modules.checks.scaffold.Scaffold;
import de.photon.AACAdditionPro.modules.clientcontrol.BetterSprintingControl;
import de.photon.AACAdditionPro.modules.clientcontrol.DamageIndicator;
import de.photon.AACAdditionPro.modules.clientcontrol.FiveZigControl;
import de.photon.AACAdditionPro.modules.clientcontrol.ForgeControl;
import de.photon.AACAdditionPro.modules.clientcontrol.LabyModControl;
import de.photon.AACAdditionPro.modules.clientcontrol.LiteLoaderControl;
import de.photon.AACAdditionPro.modules.clientcontrol.OldLabyModControl;
import de.photon.AACAdditionPro.modules.clientcontrol.PXModControl;
import de.photon.AACAdditionPro.modules.clientcontrol.SchematicaControl;
import de.photon.AACAdditionPro.modules.clientcontrol.VapeControl;
import de.photon.AACAdditionPro.modules.clientcontrol.VersionControl;
import de.photon.AACAdditionPro.modules.clientcontrol.WorldDownloaderControl;
import de.photon.AACAdditionPro.user.UserManager;
import de.photon.AACAdditionPro.util.VerboseSender;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

import java.io.File;
import java.util.Arrays;

public class AACAdditionPro extends JavaPlugin
{
    /**
     * Indicates if the loading process is completed.
     */
    @Getter
    private boolean loaded = false;

    // Cache the config for better performance
    private FileConfiguration cachedConfig;

    @Getter
    private ModuleManager moduleManager;

    @Getter
    private ViaAPI<Player> viaAPI;

    /**
     * This will get the object of the plugin registered on the server.
     *
     * @return the active instance of this plugin on the server.
     */
    public static AACAdditionPro getInstance()
    {
        return AACAdditionPro.getPlugin(AACAdditionPro.class);
    }

    /**
     * Registers a new {@link Listener} for AACAdditionPro.
     *
     * @param listener the {@link Listener} which should be registered in the {@link org.bukkit.plugin.PluginManager}
     */
    public void registerListener(final Listener listener)
    {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    @NotNull
    @Override
    public FileConfiguration getConfig()
    {
        if (cachedConfig == null) {
            this.saveDefaultConfig();
            cachedConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
        }

        return cachedConfig;
    }

    @Override
    public void onEnable()
    {
        try {
            // ------------------------------------------------------------------------------------------------------ //
            //                                      Unsupported server version                                        //
            // ------------------------------------------------------------------------------------------------------ //
            if (ServerVersion.getActiveServerVersion() == null ||
                // Unsupported
                !ServerVersion.getActiveServerVersion().isSupported())
            {
                VerboseSender.getInstance().sendVerboseMessage("Server version is not supported.", true, true);

                // Print the complete message
                VerboseSender.getInstance().sendVerboseMessage(
                        "Supported versions:" +
                        String.join(
                                // Versions should be divided by commas.
                                ", ",
                                // Create a List of all the possible server versions
                                Arrays.stream(ServerVersion.values()).filter(ServerVersion::isSupported).map(ServerVersion::getVersionOutputString).toArray(String[]::new)),
                        true, true);
                return;
            }

            // ------------------------------------------------------------------------------------------------------ //
            //                                                Metrics                                                 //
            // ------------------------------------------------------------------------------------------------------ //

            final Metrics metrics = new Metrics(this);

            // The first getConfig call will automatically saveToFile and cache the config.

            // ------------------------------------------------------------------------------------------------------ //
            //                                              Plugin hooks                                              //
            // ------------------------------------------------------------------------------------------------------ //

            // Call is correct here as Bukkit always has a player api.
            if (this.getServer().getPluginManager().getPlugin("ViaVersion") != null) {
                //noinspection unchecked
                viaAPI = Via.getAPI();
                metrics.addCustomChart(new Metrics.SimplePie("viaversion", () -> "Used"));
            } else {
                metrics.addCustomChart(new Metrics.SimplePie("viaversion", () -> "Not used"));
            }


            // ------------------------------------------------------------------------------------------------------ //
            //                                                Features                                                //
            // ------------------------------------------------------------------------------------------------------ //

            // Managers
            this.registerListener(new UserManager());
            this.moduleManager = new ModuleManager(ImmutableSet.of(
                    // Additions
                    new BrandHider(),
                    new LogBot(),

                    // ClientControl
                    new BetterSprintingControl(),
                    new DamageIndicator(),
                    new FiveZigControl(),
                    new ForgeControl(),
                    new LabyModControl(),
                    new LiteLoaderControl(),
                    new OldLabyModControl(),
                    new PXModControl(),
                    new SchematicaControl(),
                    new VapeControl(),
                    new VersionControl(),
                    new WorldDownloaderControl(),

                    // Normal checks
                    new AutoFish(),
                    new AutoPotion(),
                    new Esp(),
                    new Fastswitch(),
                    new ImpossibleChat(),
                    new Inventory(),
                    new KeepAlive(),
                    new PacketAnalysis(),
                    new Pingspoof(),
                    new Scaffold(),
                    new SkinBlinker(),
                    new Teaming(),
                    new Tower())
            );

            // Commands
            this.getCommand(MainCommand.getInstance().getMainCommandName()).setExecutor(MainCommand.getInstance());

            // ------------------------------------------------------------------------------------------------------ //
            //                                          Enabled-Verbose + API                                         //
            // ------------------------------------------------------------------------------------------------------ //
            this.getLogger().info(this.getName() + " Version " + this.getDescription().getVersion() + " enabled");

            // API loading finished
            this.loaded = true;
            this.getServer().getPluginManager().callEvent(new APILoadedEvent());

        } catch (final Exception e) {
            // ------------------------------------------------------------------------------------------------------ //
            //                                              Failed loading                                            //
            // ------------------------------------------------------------------------------------------------------ //
            this.getLogger().severe("Loading failed:");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable()
    {
        // Plugin is already disabled -> VerboseSender is not allowed to register a task
        VerboseSender.getInstance().setAllowedToRegisterTasks(false);

        // Remove all the Listeners, PacketListeners
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        HandlerList.unregisterAll(AACAdditionPro.getInstance());

        VerboseSender.getInstance().sendVerboseMessage("AACAdditionPro disabled.", true, false);
        VerboseSender.getInstance().sendVerboseMessage(" ", true, false);

        // Task scheduling
        loaded = false;
    }
}
