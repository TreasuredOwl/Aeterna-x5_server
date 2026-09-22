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
 * Bleeding Fly AI.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class BleedingFly extends AbstractNpcAI
{
	// NPCs
	private static final int BLEEDING_FLY = 25720;
	private static final int PARASITIC_LEECH = 25734;
	// Skills
	private static final SkillHolder SUMMON_PARASITE_LEECH = new SkillHolder(6832, 1);
	private static final SkillHolder NPC_ACUMEN_LEVEL_3 = new SkillHolder(6915, 3);
	// Variables
	private static final String MID_HP_FLAG = "MID_HP_FLAG";
	private static final String LOW_HP_FLAG = "LOW_HP_FLAG";
	private static final String MID_HP_MINION_COUNT = "MID_HP_MINION_COUNT";
	private static final String LOW_HP_MINION_COUNT = "LOW_HP_MINION_COUNT";
	// Timers
	private static final String TIMER_MID_HP = "TIMER_MID_HP";
	private static final String TIMER_LOW_HP = "TIMER_LOW_HP";
	// Misc
	private static final int MAX_CHASE_DIST = 2500;
	private static final double MID_HP_PERCENTAGE = 0.50;
	private static final double MIN_HP_PERCENTAGE = 0.25;
	
	public BleedingFly()
	{
		addAttackId(BLEEDING_FLY);
		addSpawnId(BLEEDING_FLY);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.getVariables().set(MID_HP_MINION_COUNT, 5);
		npc.getVariables().set(LOW_HP_MINION_COUNT, 10);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (LocationUtil.calculateDistance(npc, npc.getSpawn(), false, false) > MAX_CHASE_DIST)
		{
			npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
		}
		
		if ((npc.getCurrentHp() < (npc.getMaxHp() * MID_HP_PERCENTAGE)) && !npc.getVariables().getBoolean(MID_HP_FLAG, false))
		{
			npc.getVariables().set(MID_HP_FLAG, true);
			startQuestTimer(TIMER_MID_HP, 1000, npc, null);
		}
		
		if ((npc.getCurrentHp() < (npc.getMaxHp() * MIN_HP_PERCENTAGE)) && !npc.getVariables().getBoolean(LOW_HP_FLAG, false))
		{
			npc.getVariables().set(MID_HP_FLAG, false);
			npc.getVariables().set(LOW_HP_FLAG, true);
			startQuestTimer(TIMER_LOW_HP, 1000, npc, null);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (npc.isDead())
		{
			return super.onEvent(event, npc, player);
		}
		
		if (TIMER_MID_HP.equals(event))
		{
			if (npc.getVariables().getInt(MID_HP_MINION_COUNT) > 0)
			{
				npc.getVariables().set(MID_HP_MINION_COUNT, npc.getVariables().getInt(MID_HP_MINION_COUNT) - 1);
				addSkillCastDesire(npc, npc, SUMMON_PARASITE_LEECH, 99999);
				addSpawn(PARASITIC_LEECH, npc.getX() + getRandom(150), npc.getY() + getRandom(150), npc.getZ(), npc.getHeading(), false, 0);
				addSpawn(PARASITIC_LEECH, npc.getX() + getRandom(150), npc.getY() + getRandom(150), npc.getZ(), npc.getHeading(), false, 0);
				if (npc.getVariables().getBoolean(MID_HP_FLAG, false))
				{
					startQuestTimer(TIMER_MID_HP, 140000, npc, null);
				}
			}
		}
		else if (TIMER_LOW_HP.equals(event) && (npc.getVariables().getInt(LOW_HP_MINION_COUNT) > 0))
		{
			npc.getVariables().set(LOW_HP_MINION_COUNT, npc.getVariables().getInt(LOW_HP_MINION_COUNT) - 1);
			addSkillCastDesire(npc, npc, SUMMON_PARASITE_LEECH, 99999);
			addSkillCastDesire(npc, npc, NPC_ACUMEN_LEVEL_3, 99999);
			addSpawn(PARASITIC_LEECH, npc.getX() + getRandom(150), npc.getY() + getRandom(150), npc.getZ(), npc.getHeading(), false, 0);
			addSpawn(PARASITIC_LEECH, npc.getX() + getRandom(150), npc.getY() + getRandom(150), npc.getZ(), npc.getHeading(), false, 0);
			if (npc.getVariables().getBoolean(LOW_HP_FLAG, false))
			{
				startQuestTimer(TIMER_LOW_HP, 80000, npc, null);
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	public static void main(String[] args)
	{
		new BleedingFly();
	}
}
