package de.photon.aacadditionpro.util.execute;

import de.photon.aacadditionproold.AACAdditionPro;
import de.photon.aacadditionproold.util.messaging.VerboseSender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExecuteUtil
{
    /**
     * This executes the command synchronously and sends an error message via {@link VerboseSender} if something went wrong.
     * No {@link Placeholders} are allowed to exist in this method, use executeCommandWithPlaceholders() for this.
     *
     * @param command the command that will be run from console.
     */
    public static void executeCommand(final String command)
    {
        Bukkit.getScheduler().runTask(
                AACAdditionPro.getInstance(),
                () -> {
                    //Try catch to prevent console errors if a command couldn't be executed, e.g. if the player has left.
                    try {
                        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                        VerboseSender.getInstance().sendVerboseMessage(ChatColor.GOLD + "Executed command: " + command);
                    } catch (final Exception e) {
                        VerboseSender.getInstance().sendVerboseMessage("Could not execute command /" + command, true, true);
                    }
                });
    }
}