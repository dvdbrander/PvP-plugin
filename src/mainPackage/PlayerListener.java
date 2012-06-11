package mainPackage;

import java.util.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.player.SpoutPlayer;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PlayerListener implements Listener {
	private PvPplugin main;

	public PlayerListener(PvPplugin main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void damage(EntityDamageByEntityEvent e) {
		PermissionManager pex = PermissionsEx.getPermissionManager();
		if (e.getEntity() instanceof Player) {
			if (e.getDamager() instanceof Player
					|| (e.getDamager() instanceof Arrow && ((Arrow) e
							.getDamager()).getShooter() instanceof Player)) {
				Player damager = null;
				if (e.getDamager() instanceof Player) {
					damager = (Player) e.getDamager();
				} else {
					damager = (Player) ((Arrow) e.getDamager()).getShooter();
				}
				Player damagee = (Player) e.getEntity();
				PermissionUser PDamager = pex.getUser(damager);
				PermissionUser PDamagee = pex.getUser(damagee);
				if ((PDamager.has("PVP.hasEnabled"))
						&& PDamagee.has("PVP.hasEnabled")) {
					if (main.disablePvp.containsKey(damager)) {
						main.getServer().getScheduler()
								.cancelTask(main.disablePvp.get(damager));
						main.disablePvp.remove(damager);
					}
					if (main.disablePvp.containsKey(damagee)) {
						main.getServer().getScheduler()
								.cancelTask(main.disablePvp.get(damagee));
						main.disablePvp.remove(damagee);
					}
					e.setCancelled(false);
				} else {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void login(PlayerLoginEvent e) {
		PermissionManager pex = PermissionsEx.getPermissionManager();
		pex.getUser(e.getPlayer()).removePermission("PVP.hasEnabled");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void death(PlayerDeathEvent e) {
		PermissionManager pex = PermissionsEx.getPermissionManager();
		PermissionUser user = pex.getUser(e.getEntity());
		user.removePermission("PVP.hasEnabled");
		e.getEntity().sendMessage("Disabled PVP");
		InGameHUD hud = ((SpoutPlayer) e.getEntity()).getMainScreen();
		hud.removeWidgets(main);
		((SpoutPlayer) e.getEntity()).setTitle(e.getEntity().getName());

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void chat(PlayerCommandPreprocessEvent e) {
		String[] split = e.getMessage().split(" ");
		List<String> list = main.getConfig().getStringList(
				"disabledCommandsInPvp");
		PermissionManager pex = PermissionsEx.getPermissionManager();
		if (pex.getUser(e.getPlayer()).has("PVP.hasEnabled")) {
			if (list.contains(split[0])) {
				e.getPlayer().sendMessage(
						"You cannot use this command while in pvp mode.");
				e.setCancelled(true);
			}
		}
	}

}
