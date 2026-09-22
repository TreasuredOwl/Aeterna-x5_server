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
package ai.areas.BeastFarm.Tunatun;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.quest.QuestState;

import ai.AbstractNpcAI;
import quests.Q00020_BringUpWithLove.Q00020_BringUpWithLove;

/**
 * Beast Herder Tunatun AI.
 * @author Adry_85
 */
public class Tunatun extends AbstractNpcAI
{
	// NPC
	private static final int TUNATUN = 31537;
	// Item
	private static final int BEAST_HANDLERS_WHIP = 15473;
	// Misc
	private static final int MIN_LEVEL = 82;
	
	private Tunatun()
	{
		addStartNpc(TUNATUN);
		addFirstTalkId(TUNATUN);
		addTalkId(TUNATUN);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if ("Whip".equals(event))
		{
			if (hasQuestItems(player, BEAST_HANDLERS_WHIP))
			{
				return "31537-01.html";
			}
			
			final QuestState qs = player.getQuestState(Q00020_BringUpWithLove.class.getSimpleName());
			if ((qs == null) && (player.getLevel() < MIN_LEVEL))
			{
				return "31537-02.html";
			}
			else if ((qs != null) || (player.getLevel() >= MIN_LEVEL))
			{
				giveItems(player, BEAST_HANDLERS_WHIP, 1);
				return "31537-03.html";
			}
		}
		return event;
	}
	
	public static void main(String[] args)
	{
		new Tunatun();
	}
}