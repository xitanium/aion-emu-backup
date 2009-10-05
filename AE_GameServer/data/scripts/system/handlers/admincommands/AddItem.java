package admincommands;

import com.aionemu.gameserver.utils.PacketSendUtility;

import com.aionemu.gameserver.utils.chathandlers.GMCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.world.World;

public class AddItem extends GMCommand {
	
	public World world;
	
	public AddItem() {
		super("additem");		
	}
	
	public void executeCommand(Player admin, String[] params) {
		PacketSendUtility.sendMessage(admin, "received additem command, adding item 111600396 to inventory");
		if(params == null) 
		{
			PacketSendUtility.sendMessage(admin, "usage: //additem player_name itemID [count]");	
		}
		else 
		{
			String pTarget = "";
			Integer itemID = 0;
			if(params.length >= 2) 
			{
				try 
				{
					pTarget = params[0];
					itemID = Integer.parseInt(params[1]);
				}
				catch(NumberFormatException ex)
				{
					PacketSendUtility.sendMessage(admin, "invalid item id");
					return;
				}
				Player targetPlayer = world.findPlayer(pTarget);
				//TODO: Additems to player using Inventory
			}
			PacketSendUtility.sendMessage(admin, "usage: //additem [to player_name] itemID [count]");	
		}
	}
	
}