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
package ai.areas.Hellbound.AI.Zones.TowerOfInfinitum;

import java.util.HashMap;
import java.util.Map;

import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.groups.Party;
import com.l2journey.gameserver.util.LocationUtil;

import ai.AbstractNpcAI;
import ai.areas.Hellbound.HellboundEngine;

/**
 * Tower Of Infinitum.
 * @author GKR
 */
public class TowerOfInfinitum extends AbstractNpcAI
{
	// NPCs
	private static final int JERIAN = 32302;
	private static final int GK_FIRST = 32745;
	private static final int GK_LAST = 32752;
	// Skills
	private static final int PASS_SKILL = 2357;
	// Misc
	private static final Map<Integer, Location[]> TELE_COORDS = new HashMap<>();
	static
	{
		TELE_COORDS.put(32745, new Location[]
		{
			new Location(-22208, 277122, -13376),
			null
		});
		TELE_COORDS.put(32746, new Location[]
		{
			new Location(-22208, 277106, -11648),
			new Location(-22208, 277074, -15040)
		});
		TELE_COORDS.put(32747, new Location[]
		{
			new Location(-22208, 277120, -9920),
			new Location(-22208, 277120, -13376)
		});
		TELE_COORDS.put(32748, new Location[]
		{
			new Location(-19024, 277126, -8256),
			new Location(-22208, 277106, -11648)
		});
		TELE_COORDS.put(32749, new Location[]
		{
			new Location(-19024, 277106, -9920),
			new Location(-22208, 277122, -9920)
		});
		TELE_COORDS.put(32750, new Location[]
		{
			new Location(-19008, 277100, -11648),
			new Location(-19024, 277122, -8256)
		});
		TELE_COORDS.put(32751, new Location[]
		{
			new Location(-19008, 277100, -13376),
			new Location(-19008, 277106, -9920)
		});
		TELE_COORDS.put(32752, new Location[]
		{
			new Location(14602, 283179, -7500),
			new Location(-19008, 277100, -11648)
		});
	}
	
	public TowerOfInfinitum()
	{
		addStartNpc(JERIAN);
		addTalkId(JERIAN);
		
		for (int i = GK_FIRST; i <= GK_LAST; i++)
		{
			addStartNpc(i);
			addTalkId(i);
		}
	}
	
	private static final Location ENTER_LOCATION = new Location(-22204, 277056, -15023);
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final int npcId = npc.getId();
		if (event.equalsIgnoreCase("enter") && (npcId == JERIAN))
		{
			if (HellboundEngine.getInstance().getLevel() >= 11)
			{
				final Party party = player.getParty();
				if ((party != null) && (party.getLeaderObjectId() == player.getObjectId()))
				{
					for (Player partyMember : party.getMembers())
					{
						if (!LocationUtil.checkIfInRange(300, partyMember, npc, true) || !partyMember.isAffectedBySkill(PASS_SKILL))
						{
							return "32302-02.htm";
						}
					}
					for (Player partyMember : party.getMembers())
					{
						partyMember.teleToLocation(ENTER_LOCATION, true);
					}
					htmltext = null;
				}
				else
				{
					htmltext = "32302-02a.htm";
				}
			}
			else
			{
				htmltext = "32302-02b.htm";
			}
		}
		else if ((event.equalsIgnoreCase("up") || event.equalsIgnoreCase("down")) && (npcId >= GK_FIRST) && (npcId <= GK_LAST))
		{
			final int direction = event.equalsIgnoreCase("up") ? 0 : 1;
			final Party party = player.getParty();
			if (party == null)
			{
				htmltext = "gk-noparty.htm";
			}
			else if (!party.isLeader(player))
			{
				htmltext = "gk-noreq.htm";
			}
			else
			{
				for (Player partyMember : party.getMembers())
				{
					if (!LocationUtil.checkIfInRange(1000, partyMember, npc, false) || (Math.abs(partyMember.getZ() - npc.getZ()) > 100))
					{
						return "gk-noreq.htm";
					}
				}
				
				final Location tele = TELE_COORDS.get(npcId)[direction];
				if (tele != null)
				{
					for (Player partyMember : party.getMembers())
					{
						partyMember.teleToLocation(tele, true);
					}
				}
				htmltext = null;
			}
		}
		return htmltext;
	}
}