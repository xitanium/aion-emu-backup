package admincommands;

import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.ShutdownHook;

public class ServerManage extends AdminCommand {
	
	public ServerManage() {
		super("servermanage");
	}
	
	public void executeCommand(Player admin, String[] params) {
		World w = admin.getActiveRegion().getWorld();
		ShutdownHook sdh = new ShutdownHook(w);
		PacketSendUtility.sendMessage(admin, "Reboot started");
	}
}