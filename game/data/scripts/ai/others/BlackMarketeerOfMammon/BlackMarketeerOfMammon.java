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
package ai.others.BlackMarketeerOfMammon;

import java.util.Calendar;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.itemcontainer.Inventory;
import com.l2journey.gameserver.model.quest.QuestState;
import com.l2journey.gameserver.model.quest.QuestType;
import com.l2journey.gameserver.model.quest.State;

import ai.AbstractNpcAI;

/**
 * Black Marketeer of Mammon - Exchange Adena for AA.
 * @author Adry_85
 */
public class BlackMarketeerOfMammon extends AbstractNpcAI
{
	// NPC
	private static final int BLACK_MARKETEER = 31092;
	// Misc
	private static final int MIN_LEVEL = 60;
	
	private BlackMarketeerOfMammon()
	{
		addStartNpc(BLACK_MARKETEER);
		addTalkId(BLACK_MARKETEER);
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		return exchangeAvailable() ? "31092-01.html" : "31092-02.html";
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		if ("exchange".equals(event))
		{
			if (exchangeAvailable())
			{
				if (player.getLevel() >= MIN_LEVEL)
				{
					final QuestState qs = getQuestState(player, true);
					if ((qs.get("restartTime") != null) && !qs.isNowAvailable())
					{
						htmltext = "31092-03.html";
					}
					else
					{
						if (player.getAdena() >= 2000000)
						{
							qs.setState(State.STARTED);
							takeItems(player, Inventory.ADENA_ID, 2000000);
							giveItems(player, Inventory.ANCIENT_ADENA_ID, 500000);
							htmltext = "31092-04.html";
							qs.exitQuest(QuestType.DAILY, false);
						}
						else
						{
							htmltext = "31092-05.html";
						}
					}
				}
				else
				{
					htmltext = "31092-06.html";
				}
			}
			else
			{
				htmltext = "31092-02.html";
			}
		}
		return htmltext;
	}
	
	private boolean exchangeAvailable()
	{
		final Calendar currentTime = Calendar.getInstance();
		final Calendar minTime = Calendar.getInstance();
		minTime.set(Calendar.HOUR_OF_DAY, 20);
		minTime.set(Calendar.MINUTE, 0);
		minTime.set(Calendar.SECOND, 0);
		final Calendar maxtTime = Calendar.getInstance();
		maxtTime.set(Calendar.HOUR_OF_DAY, 23);
		maxtTime.set(Calendar.MINUTE, 59);
		maxtTime.set(Calendar.SECOND, 59);
		return (currentTime.compareTo(minTime) >= 0) && (currentTime.compareTo(maxtTime) <= 0);
	}
	
	public static void main(String[] args)
	{
		new BlackMarketeerOfMammon();
	}
}
