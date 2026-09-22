package handlers.admincommandhandlers;

import com.l2journey.gameserver.handler.IAdminCommandHandler;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.network.serverpackets.WareHouseWithdrawalList;

/**
 * Bazooka.rpm
 */
public class AdminPlayerWarehouse implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_open_wh"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player player)
	{
		if (!player.isGM())
		{
			return false;
		}
		
		final String[] commandParts = command.split(" ");
		if (commandParts.length < 2)
		{
			player.sendMessage("Usage: //open_wh <playerName>");
			return true;
		}
		
		final Player target = World.getInstance().getPlayer(commandParts[1]);
		if (target == null)
		{
			player.sendMessage("Player offline");
			return true;
		}
		
		target.setActiveWarehouse(target.getWarehouse());
		player.sendPacket(new WareHouseWithdrawalList(target, WareHouseWithdrawalList.PRIVATE));
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
