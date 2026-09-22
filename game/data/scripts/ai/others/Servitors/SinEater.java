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
package ai.others.Servitors;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.Summon;
import com.l2journey.gameserver.model.events.EventType;
import com.l2journey.gameserver.model.events.ListenerRegisterType;
import com.l2journey.gameserver.model.events.annotations.Id;
import com.l2journey.gameserver.model.events.annotations.RegisterEvent;
import com.l2journey.gameserver.model.events.annotations.RegisterType;
import com.l2journey.gameserver.model.events.holders.actor.creature.OnCreatureAttacked;
import com.l2journey.gameserver.model.events.holders.actor.creature.OnCreatureDeath;
import com.l2journey.gameserver.network.NpcStringId;
import com.l2journey.gameserver.network.enums.ChatType;
import com.l2journey.gameserver.network.serverpackets.NpcSay;

import ai.AbstractNpcAI;

/**
 * Sin Eater AI.
 * @author St3eT
 */
public class SinEater extends AbstractNpcAI
{
	// NPCs
	private static final int SIN_EATER = 12564;
	
	private SinEater()
	{
		addSummonSpawnId(SIN_EATER);
		addSummonTalkId(SIN_EATER);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equals("TALK") && (player != null) && (player.getSummon() != null))
		{
			if (getRandom(100) < 30)
			{
				final int random = getRandom(100);
				final Summon summon = player.getSummon();
				if (random < 20)
				{
					broadcastSummonSay(summon, NpcStringId.YAWWWWN_IT_S_SO_BORING_HERE_WE_SHOULD_GO_AND_FIND_SOME_ACTION);
				}
				else if (random < 40)
				{
					broadcastSummonSay(summon, NpcStringId.HEY_IF_YOU_CONTINUE_TO_WASTE_TIME_YOU_WILL_NEVER_FINISH_YOUR_PENANCE);
				}
				else if (random < 60)
				{
					broadcastSummonSay(summon, NpcStringId.I_KNOW_YOU_DON_T_LIKE_ME_THE_FEELING_IS_MUTUAL);
				}
				else if (random < 80)
				{
					broadcastSummonSay(summon, NpcStringId.I_NEED_A_DRINK);
				}
				else
				{
					broadcastSummonSay(summon, NpcStringId.OH_THIS_IS_DRAGGING_ON_TOO_LONG_AT_THIS_RATE_I_WON_T_MAKE_IT_HOME_BEFORE_THE_SEVEN_SEALS_ARE_BROKEN);
				}
			}
			startQuestTimer("TALK", 60000, null, player);
		}
		return super.onEvent(event, npc, player);
	}
	
	@RegisterEvent(EventType.ON_CREATURE_DEATH)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(SIN_EATER)
	public void onCreatureKill(OnCreatureDeath event)
	{
		final int random = getRandom(100);
		final Summon summon = event.getTarget().asSummon();
		if (random < 30)
		{
			broadcastSummonSay(summon, NpcStringId.OH_THIS_IS_JUST_GREAT_WHAT_ARE_YOU_GOING_TO_DO_NOW);
		}
		else if (random < 70)
		{
			broadcastSummonSay(summon, NpcStringId.YOU_INCONSIDERATE_MORON_CAN_T_YOU_EVEN_TAKE_CARE_OF_LITTLE_OLD_ME);
		}
		else
		{
			broadcastSummonSay(summon, NpcStringId.OH_NO_THE_MAN_WHO_EATS_ONE_S_SINS_HAS_DIED_PENITENCE_IS_FURTHER_AWAY);
		}
	}
	
	@RegisterEvent(EventType.ON_CREATURE_ATTACKED)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(SIN_EATER)
	public void onCreatureAttacked(OnCreatureAttacked event)
	{
		if (getRandom(100) < 30)
		{
			final int random = getRandom(100);
			final Summon summon = event.getTarget().asSummon();
			if (random < 35)
			{
				broadcastSummonSay(summon, NpcStringId.OH_THAT_SMARTS);
			}
			else if (random < 70)
			{
				broadcastSummonSay(summon, NpcStringId.HEY_MASTER_PAY_ATTENTION_I_M_DYING_OVER_HERE);
			}
			else
			{
				broadcastSummonSay(summon, NpcStringId.WHAT_HAVE_I_DONE_TO_DESERVE_THIS);
			}
		}
	}
	
	@Override
	public void onSummonSpawn(Summon summon)
	{
		broadcastSummonSay(summon, getRandomBoolean() ? NpcStringId.HEY_IT_SEEMS_LIKE_YOU_NEED_MY_HELP_DOESN_T_IT : NpcStringId.ALMOST_GOT_IT_OUCH_STOP_DAMN_THESE_BLOODY_MANACLES);
		startQuestTimer("TALK", 60000, null, summon.getOwner());
	}
	
	@Override
	public void onSummonTalk(Summon summon)
	{
		if (getRandom(100) < 10)
		{
			final int random = getRandom(100);
			if (random < 25)
			{
				broadcastSummonSay(summon, NpcStringId.USING_A_SPECIAL_SKILL_HERE_COULD_TRIGGER_A_BLOODBATH);
			}
			else if (random < 50)
			{
				broadcastSummonSay(summon, NpcStringId.HEY_WHAT_DO_YOU_EXPECT_OF_ME);
			}
			else if (random < 75)
			{
				broadcastSummonSay(summon, NpcStringId.UGGGGGH_PUSH_IT_S_NOT_COMING_OUT);
			}
			else
			{
				broadcastSummonSay(summon, NpcStringId.AH_I_MISSED_THE_MARK);
			}
		}
	}
	
	private void broadcastSummonSay(Summon summon, NpcStringId npcstringId)
	{
		summon.broadcastPacket(new NpcSay(summon.getObjectId(), ChatType.NPC_GENERAL, summon.getId(), npcstringId));
	}
	
	public static void main(String[] args)
	{
		new SinEater();
	}
}