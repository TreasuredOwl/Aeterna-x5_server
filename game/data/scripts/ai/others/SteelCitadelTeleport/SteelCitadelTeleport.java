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
package ai.others.SteelCitadelTeleport;

import com.l2journey.Config;
import com.l2journey.gameserver.managers.GrandBossManager;
import com.l2journey.gameserver.managers.ZoneManager;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.groups.CommandChannel;
import com.l2journey.gameserver.model.groups.Party;
import com.l2journey.gameserver.model.zone.type.BossZone;

import ai.AbstractNpcAI;

/**
 * Steel Citadel teleport AI.
 * @author GKR
 */
public class SteelCitadelTeleport extends AbstractNpcAI
{
	// NPCs
	private static final int BELETH = 29118;
	private static final int NAIA_CUBE = 32376;
	// Location
	private static final Location TELEPORT_CITADEL = new Location(16342, 209557, -9352);
	
	private SteelCitadelTeleport()
	{
		addStartNpc(NAIA_CUBE);
		addTalkId(NAIA_CUBE);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final int belethStatus = GrandBossManager.getInstance().getStatus(BELETH);
		if (belethStatus == 3)
		{
			return "32376-02.htm";
		}
		
		if (belethStatus > 0)
		{
			return "32376-03.htm";
		}
		
		final CommandChannel channel = player.getParty() == null ? null : player.getParty().getCommandChannel();
		if ((channel == null) || (channel.getLeader().getObjectId() != player.getObjectId()) || (channel.getMemberCount() < Config.BELETH_MIN_PLAYERS))
		{
			return "32376-02a.htm";
		}
		
		final BossZone zone = (BossZone) ZoneManager.getInstance().getZoneById(12018);
		if (zone != null)
		{
			GrandBossManager.getInstance().setStatus(BELETH, 1);
			for (Party party : channel.getParties())
			{
				if (party == null)
				{
					continue;
				}
				
				for (Player pl : party.getMembers())
				{
					if (pl.isInsideRadius3D(npc.getX(), npc.getY(), npc.getZ(), 3000))
					{
						zone.allowPlayerEntry(pl, 30);
						pl.teleToLocation(TELEPORT_CITADEL, true);
					}
				}
			}
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new SteelCitadelTeleport();
	}
}
