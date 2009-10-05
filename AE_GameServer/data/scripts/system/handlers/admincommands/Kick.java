package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

public class Kick extends AdminCommand {
	
	public Kick() {
		super("kick");
	}
	
	public void executeCommand(Player admin, String[] params) {
		
		if(params.length != 1) {
			PacketSendUtility.sendMessage(admin, "usage: //kick [player_name]");
		}
		else {
			World w = admin.getActiveRegion().getWorld();
			Player toKick = w.findPlayer(params[0].trim());
			if(toKick != null) {
				toKick.getClientConnection().close(true);
			}
			else {
				PacketSendUtility.sendMessage(admin, "Player " + params[0].trim() + " cannot be found");
			}
		}
		
	}
	
}