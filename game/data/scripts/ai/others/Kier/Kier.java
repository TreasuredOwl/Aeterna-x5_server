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
package ai.others.Kier;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.quest.QuestState;

import ai.AbstractNpcAI;
import quests.Q00115_TheOtherSideOfTruth.Q00115_TheOtherSideOfTruth;
import quests.Q10283_RequestOfIceMerchant.Q10283_RequestOfIceMerchant;

/**
 * Kier AI.
 * @author Adry_85
 * @since 2.6.0.0
 */
public class Kier extends AbstractNpcAI
{
	// NPC
	private static final int KIER = 32022;
	
	private Kier()
	{
		addFirstTalkId(KIER);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		final QuestState qs115 = player.getQuestState(Q00115_TheOtherSideOfTruth.class.getSimpleName());
		if (qs115 == null)
		{
			htmltext = "32022-02.html";
		}
		else if (!qs115.isCompleted())
		{
			htmltext = "32022-01.html";
		}
		
		final QuestState qs10283 = player.getQuestState(Q10283_RequestOfIceMerchant.class.getSimpleName());
		if (qs10283 != null)
		{
			if (qs10283.isMemoState(2))
			{
				htmltext = "32022-03.html";
			}
			else if (qs10283.isCompleted())
			{
				htmltext = "32022-04.html";
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Kier();
	}
}