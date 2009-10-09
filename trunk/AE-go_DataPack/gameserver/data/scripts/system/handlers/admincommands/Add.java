/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package admincommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.aionemu.commons.database.DB;
import com.aionemu.gameserver.dataholders.ItemData;
import com.aionemu.gameserver.dataholders.SpawnData;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.model.templates.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author dragoon112
 * @modifier xitanium
 */

public class Add extends AdminCommand
{
    public Add()
    {
        super("additem", 2);
    }

    /*
    *  (non-Javadoc)
    * @see com.aionemu.gameserver.utils.chathandlers.admincommands.AdminCommand#executeCommand(com.aionemu.gameserver.gameobjects.Player, java.lang.String[])
    */
    @Override
    public void executeCommand(Player admin, String... params)
    {
        if(params == null || params.length == 0 || params.length > 2)
        {
            PacketSendUtility.sendMessage(admin, "Usage: //additem <item_id> <count>");
            PacketSendUtility.sendMessage(admin, "Add (or remove) item from your target (or yourself if no target is selected). To add more of this item, specify <count>. To remove item(s) from your target, specify a negative <count> value.");
            return;
        }
        int itemId = 0;
        int count = 1;
        try
        {
            itemId = Integer.parseInt(params[0]);
            if(params.length == 2) {
            	count = Integer.parseInt(params[1]);
            }
        }
        catch (NumberFormatException e)
        {
            PacketSendUtility.sendMessage(admin, "You must specify numbers for both parameters.");
            return;
        }
        //int activePlayer = admin.getObjectId();
        int targetPlayerId = 0;
        Player targetPlayer = null;
        if(admin.getTarget() instanceof Player) {
        	targetPlayer = (Player)admin.getTarget();
        }
        else {
        	targetPlayer = admin;
        }
        targetPlayerId = targetPlayer.getObjectId();
        try{
        	try{
        		PreparedStatement ps = DB.prepareStatement("SELECT id FROM item_list WHERE `id`=" + itemId);
        		ResultSet rs = ps.executeQuery();
        		rs.last();
        		itemId = rs.getInt("id");
        	}catch(Exception e)
        	{
        		itemId = 0;
        	}
        	
        	if(itemId!=0){
			Inventory itemsDbOfPlayerCount = new Inventory(); // wrong
			itemsDbOfPlayerCount.getInventoryFromDb(targetPlayerId);
			int totalItemsCount = itemsDbOfPlayerCount.getItemsCount();

			Inventory equipedItems = new Inventory();
			equipedItems.getEquipedItemsFromDb(targetPlayerId);
			int totalEquipedItemsCount = equipedItems.getEquipedItemsCount();
			
			
			int cubes = 1;
			int cubesize = 27;
			int allowItemsCount = cubesize*cubes-1;
			
			if (totalItemsCount + count<=allowItemsCount){
			Inventory items = new Inventory();
			items.putItemToDb(targetPlayerId, itemId, count);
			items.getLastUniqueIdFromDb();
			int newItemUniqueId = items.getnewItemUniqueIdValue();
				
			PacketSendUtility.sendPacket(targetPlayer, new SM_INVENTORY_INFO(newItemUniqueId, itemId, count, 1, 8));
	        PacketSendUtility.sendMessage(admin, "Added Item.");
			}else{
		        PacketSendUtility.sendMessage(admin, "Target's cube has not enough space for that item(s)");
			}
        	}else{
        		PacketSendUtility.sendMessage(admin, "Invalid item ID");
        	}
			
        }catch(Exception e)
        {
            PacketSendUtility.sendMessage(admin, "There was an error in the Code.");

            return;
        }
        
    }
}
