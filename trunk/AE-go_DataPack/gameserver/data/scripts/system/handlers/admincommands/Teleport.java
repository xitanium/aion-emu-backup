package admincommands;

import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.dao.AdminCommandsDAO;
import com.aionemu.commons.database.dao.DAOManager;

public class Teleport extends AdminCommand {
	
	public Teleport() {
		super("tele");
	}
	
	public void executeCommand(Player admin, String... params) {
		
		
		World world = admin.getActiveRegion().getWorld();
		AdminCommandsDAO dao = DAOManager.getDAO(AdminCommandsDAO.class);
		
		if (params.length == 0) {
            PacketSendUtility.sendMessage(admin, "Usage : //tele [add|del|name] <teleport_name>");
            PacketSendUtility.sendMessage(admin, "<teleport_name> : The name of the teleportation point");
		}
		else if(params.length == 1) {
			if(params[0].trim().equals("add")) {
				PacketSendUtility.sendMessage(admin, "Usage : //tele add <teleport_name>");
	            PacketSendUtility.sendMessage(admin, "<teleport_name> : The name of the teleportation point to add");
			}
			else if(params[0].trim().equals("del")) {
				PacketSendUtility.sendMessage(admin, "Usage : //tele del <teleport_name>");
	            PacketSendUtility.sendMessage(admin, "<teleport_name> : The name of the teleportation point to delete");
			}
			else if(params[0].trim().equals("name")) {
				PacketSendUtility.sendMessage(admin, "Usage : //tele name <player_name> <teleport_name>");
				PacketSendUtility.sendMessage(admin, "<player_name> : The player to teleport");
	            PacketSendUtility.sendMessage(admin, "<teleport_name> : The name of the teleportation point to add");
			}
			else {
				if(!dao.isExistingTeleport(params[0].trim())) {
					PacketSendUtility.sendMessage(admin, "Error : Teleport location '" + params[0].trim() + "' was not found in database.");
				}
				else {
					float[] teleportCoords = dao.loadTeleport(params[0].trim());
					world.despawn(admin);
					world.setPosition(admin, (int) teleportCoords[0], teleportCoords[1], teleportCoords[2], teleportCoords[3], (byte) teleportCoords[4]);
					PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
					PacketSendUtility.sendMessage(admin, "You were successfully teleported to " + params[0].trim());
				}
			}
		}
		else if(params.length == 2) {
			if(params[0].trim().equals("add")) {
				if(!params[1].trim().equals("")) {
					if(dao.isExistingTeleport(params[1].trim())) {
						PacketSendUtility.sendMessage(admin, "Error : The specified teleport name is already in use. Please choose another one.");
					}
					else {
						int curMapId = admin.getActiveRegion().getMapId();
						float curMapX = admin.getX();
						float curMapY = admin.getY();
						float curMapZ = admin.getZ();
						byte curMapH = admin.getHeading();
						if(dao.saveTeleport(params[1].trim(), curMapId, curMapX, curMapY, curMapZ, curMapH)) {
							PacketSendUtility.sendMessage(admin, "Successful !");
							PacketSendUtility.sendMessage(admin, "=== NEW TELEPORT LOCATION ADDED ===");
							PacketSendUtility.sendMessage(admin, "Teleport Name : " + params[1].trim());
							PacketSendUtility.sendMessage(admin, "Coords : 	map			= " + curMapId);
							PacketSendUtility.sendMessage(admin, "              	x				= " + curMapX);
							PacketSendUtility.sendMessage(admin, "              	y				= " + curMapY);
							PacketSendUtility.sendMessage(admin, "              	z				= " + curMapZ);
							PacketSendUtility.sendMessage(admin, "              	heading	= " + curMapH);
						}
						else {
							PacketSendUtility.sendMessage(admin, "An error has occured when adding teleport location to database. Please report to developers.");
						}
					}
				}
				else {
					PacketSendUtility.sendMessage(admin, "Error : You must specify a name for the new teleport location");
				}
			}
			else if(params[0].trim().equals("del")) {
				PacketSendUtility.sendMessage(admin, "Usage : //tele del <teleport_name>");
	            PacketSendUtility.sendMessage(admin, "<teleport_name> : The name of the teleportation point to delete");
			}
			else if(params[0].trim().equals("name")) {
				PacketSendUtility.sendMessage(admin, "Usage : //tele name <player_name> <teleport_name>");
				PacketSendUtility.sendMessage(admin, "<player_name> : The player to teleport");
	            PacketSendUtility.sendMessage(admin, "<teleport_name> : The name of the teleportation point to add");
			}
			else {
				PacketSendUtility.sendMessage(admin, "There is no such subcommand : //tele " + params[0].trim());
			}
		}
		else if(params.length == 3) {
			
		}
		
	}
	
}