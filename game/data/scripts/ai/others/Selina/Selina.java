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
package ai.others.Selina;

import java.util.HashMap;
import java.util.Map;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.holders.SkillHolder;

import ai.AbstractNpcAI;

/**
 * Mercenary Medic Selina AI.
 * @author Zoey76
 */
public class Selina extends AbstractNpcAI
{
	// NPC
	private static final int SELINA = 31556;
	// Items
	private static final int GOLDEN_RAM_BADGE_RECRUIT = 7246;
	private static final int GOLDEN_RAM_BADGE_SOLDIER = 7247;
	private static final int GOLDEN_RAM_COIN = 7251;
	// Skills
	private static final Map<String, BuffHolder> BUFFS = new HashMap<>();
	static
	{
		BUFFS.put("4359", new BuffHolder(4359, 2, 2)); // Focus
		BUFFS.put("4360", new BuffHolder(4360, 2, 2)); // Death Whisper
		BUFFS.put("4345", new BuffHolder(4345, 3, 3)); // Might
		BUFFS.put("4355", new BuffHolder(4355, 2, 3)); // Acumen
		BUFFS.put("4352", new BuffHolder(4352, 1, 3)); // Berserker Spirit
		BUFFS.put("4354", new BuffHolder(4354, 2, 3)); // Vampiric Rage
		BUFFS.put("4356", new BuffHolder(4356, 1, 6)); // Empower
		BUFFS.put("4357", new BuffHolder(4357, 2, 6)); // Haste
	}
	
	public Selina()
	{
		addStartNpc(SELINA);
		addTalkId(SELINA);
		addFirstTalkId(SELINA);
		addSpellFinishedId(SELINA);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final BuffHolder buff = BUFFS.get(event);
		if (buff != null)
		{
			if ((getQuestItemsCount(player, GOLDEN_RAM_COIN) >= buff.getCost()))
			{
				castSkill(npc, player, buff);
				return super.onEvent(event, npc, player);
			}
		}
		else
		{
			LOGGER.warning(Selina.class.getSimpleName() + " AI: player " + player + " sent invalid bypass: " + event);
		}
		return "31556-02.html";
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final String htmltext;
		if (hasQuestItems(player, GOLDEN_RAM_BADGE_SOLDIER))
		{
			htmltext = "31556-08.html";
		}
		else if (hasQuestItems(player, GOLDEN_RAM_BADGE_RECRUIT))
		{
			htmltext = "31556-01.html";
		}
		else
		{
			htmltext = "31556-09.html";
		}
		return htmltext;
	}
	
	@Override
	public void onSpellFinished(Npc npc, Player player, Skill skill)
	{
		final BuffHolder buff = BUFFS.get(Integer.toString(skill.getId()));
		if (buff != null)
		{
			takeItems(player, GOLDEN_RAM_COIN, buff.getCost());
		}
	}
	
	public static void main(String[] args)
	{
		new Selina();
	}
	
	private static class BuffHolder extends SkillHolder
	{
		private final int _cost;
		
		public BuffHolder(int skillId, int skillLevel, int cost)
		{
			super(skillId, skillLevel);
			_cost = cost;
		}
		
		public int getCost()
		{
			return _cost;
		}
	}
}
