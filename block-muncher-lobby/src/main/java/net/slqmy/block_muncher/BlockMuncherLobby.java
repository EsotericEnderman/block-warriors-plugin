package net.slqmy.block_muncher;

import net.slqmy.block_muncher.events.listeners.ConnectionListener;
import net.slqmy.block_muncher.managers.ConfigurationUtility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockMuncherLobby extends JavaPlugin {
	@Override
	public void onEnable() {
		ConfigurationUtility.setUpConfig(this);

		Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
	}
}
