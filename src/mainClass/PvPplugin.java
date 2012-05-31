package mainClass;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class PvPplugin extends JavaPlugin{

	Logger log = Logger.getLogger("Minecraft");
	
	
	@Override
	public void onEnable() {
		log.info("PvP-plugin enabled");
		super.onEnable();
	}
}
