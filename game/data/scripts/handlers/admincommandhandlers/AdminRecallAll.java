package handlers.admincommandhandlers;

import com.l2journey.gameserver.handler.IAdminCommandHandler;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Player;

public class AdminRecallAll implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_recallall"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_recallall"))
		{
			for (Player onlinePlayer : World.getInstance().getPlayers())
			{
				if (!onlinePlayer.isInStoreMode())
				{
					teleportTo(onlinePlayer, activeChar.getX(), activeChar.getY(), activeChar.getZ());
				}
			}
			return true;
		}
		return false;
	}
	
	private void teleportTo(Player activeChar, int x, int y, int z)
	{
		activeChar.teleToLocation(x, y, z, false);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
