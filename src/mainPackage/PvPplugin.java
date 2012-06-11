package mainPackage;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PvPplugin extends JavaPlugin {

	Logger log = Logger.getLogger("Minecraft");
	private PlayerListener playerListener = new PlayerListener(this);
	private Plugin main = this;
	private final Plugin fMain = this;
	public HashMap<Player, Integer> disablePvp = new HashMap<Player, Integer>();

	@Override
	public void onEnable() {
		log.info("PvP-plugin enabled");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		getConfig().options().copyDefaults(true);
        saveConfig();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		log.info("PvP-plugin disabled successfully");
		super.onEnable();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (sender instanceof Player) {
			PermissionManager pex = PermissionsEx.getPermissionManager();
			PermissionUser user = pex.getUser((Player) sender);
			SpoutPlayer player = (SpoutPlayer)sender;
			if (cmd.getName().equalsIgnoreCase("pvp")) {
				if (user.has("PVP.canEnable")) {
					if (args.length == 0 || args[0].equals("")) {
						if (user.has("PVP.hasEnabled")) {
							final PermissionUser fUser = user;
							final CommandSender fSender = sender;
							final SpoutPlayer fPlayer = player;
							sender.sendMessage("Disabling pvp after 30 seconds cooldown...");
							disablePvp .put((Player)sender,getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
								public void run() {
									fUser.removePermission("PVP.hasEnabled");
									fSender.sendMessage("Disabled PVP");
									InGameHUD hud = fPlayer.getMainScreen();
									hud.removeWidgets(fMain);
									((SpoutPlayer)fSender).setTitle(fSender.getName());								
								}
							},20*30));
						} else {
							user.addPermission("PVP.hasEnabled");
							sender.sendMessage("Enabled PVP");
							if (((Player) sender).getGameMode().equals(GameMode.CREATIVE)){
								((Player) sender).setGameMode(GameMode.SURVIVAL);
							}
							InGameHUD hud = player.getMainScreen();
							Label label = new GenericLabel("You are currently in PVP-mode.");
							label.setWidth(160).setHeight(10).setX(0).setY(0);
							label.setTextColor(new Color(1.0F, 0, 0, 1.0F));
							hud.attachWidget(main , label);
							((SpoutPlayer)sender).setTitle("[PVP]\n"+sender.getName());
						}
					} else if (args[0].equals("on")) {
						user.addPermission("PVP.hasEnabled");
						sender.sendMessage("Enabled PVP");
						if (((Player) sender).getGameMode().equals(GameMode.CREATIVE)){
							((Player) sender).setGameMode(GameMode.SURVIVAL);
						}
						InGameHUD hud = player.getMainScreen();
						Label label = new GenericLabel("You are currently in PVP-mode.");
						label.setWidth(160).setHeight(10).setX(0).setY(0);
						label.setTextColor(new Color(1.0F, 0, 0, 1.0F));
						hud.attachWidget(main , label);
						((SpoutPlayer)sender).setTitle("[PVP]\n"+sender.getName());
					} else if (args[0].equals("off")) {
						final PermissionUser fUser = user;
						final CommandSender fSender = sender;
						final SpoutPlayer fPlayer = player;
						sender.sendMessage("Disabling pvp after 30 seconds cooldown...");
						disablePvp .put((Player)sender,getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
							public void run() {
								fUser.removePermission("PVP.hasEnabled");
								fSender.sendMessage("Disabled PVP");
								InGameHUD hud = fPlayer.getMainScreen();
								hud.removeWidgets(fMain);
								((SpoutPlayer)fSender).setTitle(fSender.getName());								
							}
						},20*30));
					}
				}else{
					sender.sendMessage("You don't have access to that command.");
				}
				return true;
			}
		}
		return false;
	}
}
