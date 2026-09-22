package handlers.admincommandhandlers;

import com.l2journey.gameserver.handler.IAdminCommandHandler;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.clan.Clan;
import com.l2journey.gameserver.network.serverpackets.WareHouseWithdrawalList;

/**
 * Bazooka.rpm
 */
public class AdminClanWarehouse implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_open_clan_wh"
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
			player.sendMessage("Usage: //open_clan_wh <playerName>");
			return false;
		}
		
		final String targetName = commandParts[1];
		final Player target = World.getInstance().getPlayer(targetName);
		if ((target == null) || (target.getClan() == null))
		{
			player.sendMessage("Player offline or without clan.");
			return true;
		}
		
		final Clan clan = target.getClan();
		final Player clanLeader = World.getInstance().getPlayer(clan.getLeaderId());
		if (clanLeader == null)
		{
			player.sendMessage("Clan leader must be online.");
			return true;
		}
		
		clanLeader.setActiveWarehouse(clan.getWarehouse());
		player.sendPacket(new WareHouseWithdrawalList(clanLeader, WareHouseWithdrawalList.CLAN));
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
