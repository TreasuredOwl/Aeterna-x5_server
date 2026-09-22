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
package ai.areas.Hellbound.AI;

import java.util.List;

import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.Monster;
import com.l2journey.gameserver.network.NpcStringId;
import com.l2journey.gameserver.network.enums.ChatType;
import com.l2journey.gameserver.taskmanagers.DecayTaskManager;

import ai.AbstractNpcAI;
import ai.areas.Hellbound.HellboundEngine;

/**
 * Hellbound Slaves AI.
 * @author DS
 */
public class Slaves extends AbstractNpcAI
{
	// NPCs
	private static final int[] MASTERS =
	{
		22320, // Junior Watchman
		22321, // Junior Summoner
	};
	// Locations
	private static final Location MOVE_TO = new Location(-25451, 252291, -3252, 3500);
	// Misc
	private static final int TRUST_REWARD = 10;
	
	public Slaves()
	{
		addSpawnId(MASTERS);
		addKillId(MASTERS);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.asMonster().enableMinions(HellboundEngine.getInstance().getLevel() < 5);
		npc.asMonster().setOnKillDelay(1000);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.asMonster().getMinionList() != null)
		{
			final List<Monster> slaves = npc.asMonster().getMinionList().getSpawnedMinions();
			if ((slaves != null) && !slaves.isEmpty())
			{
				for (Monster slave : slaves)
				{
					if ((slave == null) || slave.isDead())
					{
						continue;
					}
					slave.clearAggroList();
					slave.abortAttack();
					slave.abortCast();
					slave.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THANK_YOU_FOR_SAVING_ME_FROM_THE_CLUTCHES_OF_EVIL);
					if ((HellboundEngine.getInstance().getLevel() >= 1) && (HellboundEngine.getInstance().getLevel() <= 2))
					{
						HellboundEngine.getInstance().updateTrust(TRUST_REWARD, false);
					}
					slave.getAI().setIntention(Intention.MOVE_TO, MOVE_TO);
					DecayTaskManager.getInstance().add(slave);
				}
			}
		}
	}
}