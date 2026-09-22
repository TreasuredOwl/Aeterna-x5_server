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
package ai.others;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.skill.holders.SkillHolder;
import com.l2journey.gameserver.util.LocationUtil;

import ai.AbstractNpcAI;

/**
 * Dust Rider AI.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class DustRider extends AbstractNpcAI
{
	private static final int DUST_RIDER = 25719;
	// Skills
	private static final SkillHolder NPC_HASTE_LEVEL_3 = new SkillHolder(6914, 3);
	// Variables
	private static final String CAST_FLAG = "CAST_FLAG";
	// Misc
	private static final int MAX_CHASE_DIST = 2500;
	private static final double MIN_HP_PERCENTAGE = 0.30;
	
	public DustRider()
	{
		addAttackId(DUST_RIDER);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (LocationUtil.calculateDistance(npc, npc.getSpawn(), false, false) > MAX_CHASE_DIST)
		{
			npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
		}
		
		if (!npc.getVariables().getBoolean(CAST_FLAG, false) && (npc.getCurrentHp() < (npc.getMaxHp() * MIN_HP_PERCENTAGE)))
		{
			npc.getVariables().set(CAST_FLAG, true);
			addSkillCastDesire(npc, npc, NPC_HASTE_LEVEL_3, 99999);
		}
	}
	
	public static void main(String[] args)
	{
		new DustRider();
	}
}
