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

import com.l2journey.gameserver.data.xml.DoorData;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.Door;

import ai.AbstractNpcAI;
import ai.areas.Hellbound.HellboundEngine;

/**
 * Outpost Captain's AI.
 * @author DS
 */
public class OutpostCaptain extends AbstractNpcAI
{
	// NPCs
	private static final int CAPTAIN = 18466;
	private static final int[] DEFENDERS =
	{
		22357, // Enceinte Defender
		22358, // Enceinte Defender
	};
	
	public OutpostCaptain()
	{
		addKillId(CAPTAIN);
		addSpawnId(CAPTAIN);
		addSpawnId(DEFENDERS);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Door door = DoorData.getInstance().getDoor(20250001);
		if (door != null)
		{
			door.openMe();
		}
		if (HellboundEngine.getInstance().getLevel() == 8)
		{
			HellboundEngine.getInstance().setLevel(9);
		}
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.setRandomWalking(false);
		
		if (npc.getId() == CAPTAIN)
		{
			final int hellboundLevel = HellboundEngine.getInstance().getLevel();
			if ((hellboundLevel < 7) || (hellboundLevel > 8))
			{
				npc.deleteMe();
				npc.getSpawn().stopRespawn();
			}
			else
			{
				final Door door = DoorData.getInstance().getDoor(20250001);
				if (door != null)
				{
					door.closeMe();
				}
			}
		}
	}
}