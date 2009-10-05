package admincommands;

import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.ShutdownHook;

public class Server extends AdminCommand {
	
	public Server() {
		super("server");
	}
	
	public void executeCommand(Player admin, String[] params) {
		if(params == null || params.length != 1) {
			PacketSendUtility.sendMessage(admin, "usage: //server [stop|restart|info]");
		}
		else 
		{
			if(params[0].equals("stop")) {
				World w = admin.getActiveRegion().getWorld();
				ShutdownHook sdh = new ShutdownHook(w);
				PacketSendUtility.sendMessage(admin, "Reboot started");
				sdh.run();
			}
			else if(params[0].equals("restart")) {	
				PacketSendUtility.sendMessage(admin, "method not yet implemented");
			}
			else if(params[0].equals("info")) {
				PacketSendUtility.sendMessage(admin, "method not yet implemented");
			}
			else {
				PacketSendUtility.sendMessage(admin, "received shit : '" + params[0].trim() + "'");
			}
		}
	}
}