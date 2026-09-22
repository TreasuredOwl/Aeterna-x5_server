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

import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.holders.SkillHolder;
import com.l2journey.gameserver.util.LocationUtil;

import ai.AbstractNpcAI;

/**
 * Emerald Horn AI.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class EmeraldHorn extends AbstractNpcAI
{
	private static final int EMERALD_HORN = 25718;
	// Skills
	private static final SkillHolder REFLECT_ATTACK = new SkillHolder(6823, 1);
	private static final SkillHolder PIERCING_STORM = new SkillHolder(6824, 1);
	private static final SkillHolder BLEED_LEVEL_1 = new SkillHolder(6825, 1);
	private static final SkillHolder BLEED_LEVEL_2 = new SkillHolder(6825, 2);
	// Variables
	private static final String HIGH_DAMAGE_FLAG = "HIGH_DAMAGE_FLAG";
	private static final String TOTAL_DAMAGE_COUNT = "TOTAL_DAMAGE_COUNT";
	private static final String CAST_FLAG = "CAST_FLAG";
	// Timers
	private static final String DAMAGE_TIMER_15S = "DAMAGE_TIMER_15S";
	// Misc
	private static final int MAX_CHASE_DIST = 2500;
	
	public EmeraldHorn()
	{
		addAttackId(EMERALD_HORN);
		addSpellFinishedId(EMERALD_HORN);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (LocationUtil.calculateDistance(npc, npc.getSpawn(), false, false) > MAX_CHASE_DIST)
		{
			npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
		}
		
		if (npc.isAffectedBySkill(REFLECT_ATTACK.getSkillId()) && npc.getVariables().getBoolean(CAST_FLAG, false))
		{
			npc.getVariables().set(TOTAL_DAMAGE_COUNT, npc.getVariables().getInt(TOTAL_DAMAGE_COUNT) + damage);
		}
		
		if (npc.getVariables().getInt(TOTAL_DAMAGE_COUNT) > 5000)
		{
			addSkillCastDesire(npc, attacker, BLEED_LEVEL_2, 99999);
			npc.getVariables().set(TOTAL_DAMAGE_COUNT, 0);
			npc.getVariables().set(CAST_FLAG, false);
			npc.getVariables().set(HIGH_DAMAGE_FLAG, true);
		}
		
		if (npc.getVariables().getInt(TOTAL_DAMAGE_COUNT) > 10000)
		{
			addSkillCastDesire(npc, attacker, BLEED_LEVEL_1, 99999);
			npc.getVariables().set(TOTAL_DAMAGE_COUNT, 0);
			npc.getVariables().set(CAST_FLAG, false);
			npc.getVariables().set(HIGH_DAMAGE_FLAG, true);
		}
	}
	
	@Override
	public void onSpellFinished(Npc npc, Player player, Skill skill)
	{
		if (getRandom(5) < 1)
		{
			npc.getVariables().set(TOTAL_DAMAGE_COUNT, 0);
			npc.getVariables().set(CAST_FLAG, true);
			addSkillCastDesire(npc, npc, REFLECT_ATTACK, 99999);
			startQuestTimer(DAMAGE_TIMER_15S, 15 * 1000, npc, player);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (DAMAGE_TIMER_15S.equals(event))
		{
			if (!npc.getVariables().getBoolean(HIGH_DAMAGE_FLAG, false))
			{
				final Creature mostHated = npc.asAttackable().getMostHated();
				if (mostHated != null)
				{
					if (mostHated.isDead())
					{
						npc.asAttackable().stopHating(mostHated);
					}
					else
					{
						addSkillCastDesire(npc, mostHated, PIERCING_STORM, 99999);
					}
				}
			}
			npc.getVariables().set(CAST_FLAG, false);
		}
		return super.onEvent(event, npc, player);
	}
	
	public static void main(String[] args)
	{
		new EmeraldHorn();
	}
}
