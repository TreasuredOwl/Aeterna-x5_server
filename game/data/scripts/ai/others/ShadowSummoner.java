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

import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.skill.holders.SkillHolder;
import com.l2journey.gameserver.util.LocationUtil;

import ai.AbstractNpcAI;

/**
 * Shadow Summoner AI.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class ShadowSummoner extends AbstractNpcAI
{
	// NPCs
	private static final int SHADOW_SUMMONER = 25722;
	private static final int DEMONS_BANQUET_1 = 25730;
	private static final int DEMONS_BANQUET_2 = 25731;
	// Skills
	private static final SkillHolder SUMMON_SKELETON = new SkillHolder(6835, 1);
	// Variables
	private static final String LOW_HP_FLAG = "LOW_HP_FLAG";
	private static final String LIMIT_FLAG = "LIMIT_FLAG";
	// Timers
	private static final String SUMMON_TIMER = "SUMMON_TIMER";
	private static final String FEED_TIMER = "FEED_TIMER";
	private static final String LIMIT_TIMER = "LIMIT_TIMER";
	private static final String DELAY_TIMER = "DELAY_TIMER";
	// Misc
	private static final int MAX_CHASE_DIST = 2500;
	private static final double MIN_HP_PERCENTAGE = 0.25;
	
	public ShadowSummoner()
	{
		addAttackId(SHADOW_SUMMONER);
		addCreatureSeeId(SHADOW_SUMMONER);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (LocationUtil.calculateDistance(npc, npc.getSpawn(), false, false) > MAX_CHASE_DIST)
		{
			npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
		}
		
		if ((npc.getCurrentHp() < (npc.getMaxHp() * MIN_HP_PERCENTAGE)) && !npc.getVariables().getBoolean(LOW_HP_FLAG, false))
		{
			npc.getVariables().set(LOW_HP_FLAG, true);
			startQuestTimer(SUMMON_TIMER, 1000, npc, attacker);
			startQuestTimer(FEED_TIMER, 30000, npc, attacker);
			startQuestTimer(LIMIT_TIMER, 600000, npc, attacker);
		}
	}
	
	@Override
	public void onCreatureSee(Npc npc, Creature creature)
	{
		if (!creature.isPlayer() && (creature.getId() == DEMONS_BANQUET_2))
		{
			npc.asAttackable().clearAggroList();
			addAttackDesire(npc, creature, 99999);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (npc.isDead())
		{
			return super.onEvent(event, npc, player);
		}
		
		if (SUMMON_TIMER.equals(event))
		{
			if (!npc.getVariables().getBoolean(LIMIT_FLAG, false))
			{
				startQuestTimer(DELAY_TIMER, 5000, npc, player);
				startQuestTimer(SUMMON_TIMER, 30000, npc, player);
			}
		}
		else if (FEED_TIMER.equals(event))
		{
			if (!npc.getVariables().getBoolean(LIMIT_FLAG, false))
			{
				npc.getAI().setIntention(Intention.ATTACK);
				startQuestTimer(FEED_TIMER, 30000, npc, player);
			}
		}
		else if (LIMIT_TIMER.equals(event))
		{
			npc.getVariables().set(LIMIT_FLAG, true);
		}
		else if (DELAY_TIMER.equals(event))
		{
			addSkillCastDesire(npc, npc, SUMMON_SKELETON, 99999);
			final Npc demonsBanquet = addSpawn(getRandom(2) < 1 ? DEMONS_BANQUET_1 : DEMONS_BANQUET_2, npc.getX() + 150, npc.getY() + 150, npc.getZ(), npc.getHeading(), false, 0);
			addAttackDesire(demonsBanquet, player, 10000);
		}
		return super.onEvent(event, npc, player);
	}
	
	public static void main(String[] args)
	{
		new ShadowSummoner();
	}
}
