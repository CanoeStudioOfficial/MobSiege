package org.canoestudios.mobsiege.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import org.canoestudios.mobsiege.handlers.ConfigHandler;

import javax.annotation.Nonnull;

public class CommandReload extends CommandBase
{
    @Nonnull
    @Override
    public String getName() {
        return "mobsiege_reload";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/mobsiege_reload";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        sender.sendMessage(new TextComponentString("Reloading MobSiege configs..."));
        ConfigHandler.config.save();
        ConfigHandler.initConfigs();
        sender.sendMessage(new TextComponentString("Done."));
    }
}
