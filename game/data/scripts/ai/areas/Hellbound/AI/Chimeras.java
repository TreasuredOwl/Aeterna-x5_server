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

import com.l2journey.Config;
import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.WorldObject;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.skill.Skill;

import ai.AbstractNpcAI;
import ai.areas.Hellbound.HellboundEngine;

/**
 * Chimeras AI.
 * @author DS
 */
public class Chimeras extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		22349, // Chimera of Earth
		22350, // Chimera of Darkness
		22351, // Chimera of Wind
		22352, // Chimera of Fire
	};
	private static final int CELTUS = 22353;
	// Locations
	private static final Location[] LOCATIONS =
	{
		new Location(3678, 233418, -3319),
		new Location(2038, 237125, -3363),
		new Location(7222, 240617, -2033),
		new Location(9969, 235570, -1993)
	};
	// Skills
	private static final int BOTTLE = 2359; // Magic Bottle
	// Items
	private static final int DIM_LIFE_FORCE = 9680;
	private static final int LIFE_FORCE = 9681;
	private static final int CONTAINED_LIFE_FORCE = 9682;
	// Misc
	private static final int CONTAINED_LIFE_FORCE_AMOUNT = Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER > 1 ? (int) Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER : 1; // Retail value is 1
	
	public Chimeras()
	{
		addSkillSeeId(NPCS);
		addSpawnId(CELTUS);
		addSkillSeeId(CELTUS);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		if (HellboundEngine.getInstance().getLevel() == 7) // Have random spawn points only in 7 level
		{
			final Location loc = LOCATIONS[getRandom(LOCATIONS.length)];
			if (!npc.isInsideRadius2D(loc, 200))
			{
				npc.getSpawn().setLocation(loc);
				ThreadPool.schedule(new Teleport(npc, loc), 100);
			}
		}
	}
	
	@Override
	public void onSkillSee(Npc npc, Player caster, Skill skill, List<WorldObject> targets, boolean isSummon)
	{
		if (((skill.getId() == BOTTLE) && !npc.isDead()) //
			&& ((!targets.isEmpty()) && (targets.get(0) == npc)) //
			&& (npc.getCurrentHp() < (npc.getMaxHp() * 0.1)))
		{
			if (HellboundEngine.getInstance().getLevel() == 7)
			{
				HellboundEngine.getInstance().updateTrust(3, true);
			}
			
			npc.setDead(true);
			if (npc.getId() == CELTUS)
			{
				npc.dropItem(caster, CONTAINED_LIFE_FORCE, CONTAINED_LIFE_FORCE_AMOUNT);
			}
			else
			{
				if (getRandom(100) < 80)
				{
					npc.dropItem(caster, DIM_LIFE_FORCE, 1);
				}
				else if (getRandom(100) < 80)
				{
					npc.dropItem(caster, LIFE_FORCE, 1);
				}
			}
			npc.onDecay();
		}
	}
	
	private static class Teleport implements Runnable
	{
		private final Npc _npc;
		private final Location _loc;
		
		public Teleport(Npc npc, Location loc)
		{
			_npc = npc;
			_loc = loc;
		}
		
		@Override
		public void run()
		{
			_npc.teleToLocation(_loc, false);
		}
	}
}