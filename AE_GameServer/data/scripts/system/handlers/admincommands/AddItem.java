package admincommands;

import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public class AddItem extends AdminCommand {
	
	public AddItem() {
		super("additem");		
	}
	
	public void executeCommand(Player admin, String[] params) {
		PacketSendUtility.sendMessage(admin, "received additem command, adding item 111600396 to inventory");
		PacketSendUtility.sendMessage(admin, "you have id " + admin.getObjectId());
	}
	
}