/*
 * Copyright (c) 2025 L2Journey Project
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * ---
 * 
 * Portions of this software are derived from the L2JMobius Project, 
 * shared under the MIT License. The original license terms are preserved where 
 * applicable..
 * 
 */
package handlers.admincommandhandlers;

import com.l2journey.EventsConfig;
import com.l2journey.gameserver.handler.IAdminCommandHandler;
import com.l2journey.gameserver.managers.TownManager;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.zone.type.TownZone;
import com.l2journey.gameserver.util.Broadcast;

/**
 * @author Mobius, Zoinha
 */
public class AdminTownWar implements IAdminCommandHandler
{
	private static final String WAR_START = "admin_townwar_start";
	private static final String WAR_END = "admin_townwar_end";
	private static final int[] TOWNS =
	{
		1,
		2,
		3,
		4,
		5,
		6,
		7,
		8,
		9,
		10,
		11,
		12,
		13,
		14,
		15,
		16,
		17,
		20
	};
	private static final String[] ADMIN_COMMANDS =
	{
		WAR_START,
		WAR_END
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (!activeChar.isGM())
		{
			return false;
		}
		
		if (command.startsWith(WAR_START))
		{
			startTownWar();
		}
		else if (command.startsWith(WAR_END))
		{
			endTownWar();
		}
		
		return true;
	}
	
	private void startTownWar()
	{
		updateTownWarZones(true);
		Broadcast.toAllOnlinePlayers("Town War: " + getTownWarStatusMessage(true));
	}
	
	private void endTownWar()
	{
		updateTownWarZones(false);
		Broadcast.toAllOnlinePlayers("Town War: " + getTownWarStatusMessage(false));
	}
	
	private void updateTownWarZones(boolean isWarZone)
	{
		if (!EventsConfig.TW_ALL_TOWNS)
		{
			TownZone town = TownManager.getTown(EventsConfig.TW_TOWN_ID);
			if (town == null)
			{
				return;
			}
			
			town.setIsTWZone(isWarZone);
			town.updateForCharactersInside();
			return;
		}
		
		for (int i = 1; i <= TOWNS.length; i++)
		{
			TownZone town = TownManager.getTown(i);
			if (town == null)
			{
				continue;
			}
			town.setIsTWZone(isWarZone);
			town.updateForCharactersInside();
		}
	}
	
	private String getTownWarStatusMessage(boolean isStarting)
	{
		if (EventsConfig.TW_ALL_TOWNS)
		{
			return isStarting ? "All towns are now war zones!" : "All towns have returned to normal.";
		}
		
		String townName = EventsConfig.TW_TOWN_NAME;
		return isStarting ? townName + " is now a war zone!" : townName + " has returned to normal.";
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
