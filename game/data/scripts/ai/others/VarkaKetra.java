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

import com.l2journey.gameserver.model.actor.Attackable;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.quest.QuestState;
import com.l2journey.gameserver.util.ArrayUtil;
import com.l2journey.gameserver.util.LocationUtil;

import ai.AbstractNpcAI;
import quests.Q00605_AllianceWithKetraOrcs.Q00605_AllianceWithKetraOrcs;
import quests.Q00606_BattleAgainstVarkaSilenos.Q00606_BattleAgainstVarkaSilenos;
import quests.Q00607_ProveYourCourageKetra.Q00607_ProveYourCourageKetra;
import quests.Q00608_SlayTheEnemyCommanderKetra.Q00608_SlayTheEnemyCommanderKetra;
import quests.Q00609_MagicalPowerOfWaterPart1.Q00609_MagicalPowerOfWaterPart1;
import quests.Q00610_MagicalPowerOfWaterPart2.Q00610_MagicalPowerOfWaterPart2;
import quests.Q00611_AllianceWithVarkaSilenos.Q00611_AllianceWithVarkaSilenos;
import quests.Q00612_BattleAgainstKetraOrcs.Q00612_BattleAgainstKetraOrcs;
import quests.Q00613_ProveYourCourageVarka.Q00613_ProveYourCourageVarka;
import quests.Q00614_SlayTheEnemyCommanderVarka.Q00614_SlayTheEnemyCommanderVarka;
import quests.Q00615_MagicalPowerOfFirePart1.Q00615_MagicalPowerOfFirePart1;
import quests.Q00616_MagicalPowerOfFirePart2.Q00616_MagicalPowerOfFirePart2;

/**
 * Varka Silenos Barracks and Ketra Orc Outpost AI.
 * @author malyelfik
 */
public class VarkaKetra extends AbstractNpcAI
{
	// Monsters
	private static final int[] KETRA =
	{
		21324, // Ketra Orc Footman
		21325, // Ketra's War Hound
		21327, // Ketra Orc Raider
		21328, // Ketra Orc Scout
		21329, // Ketra Orc Shaman
		21331, // Ketra Orc Warrior
		21332, // Ketra Orc Lieutenant
		21334, // Ketra Orc Medium
		21335, // Ketra Orc Elite Soldier
		21336, // Ketra Orc White Captain
		21338, // Ketra Orc Seer
		21339, // Ketra Orc General
		21340, // Ketra Orc Battalion Commander
		21342, // Ketra Orc Grand Seer
		21343, // Ketra Commander
		21344, // Ketra Elite Guard
		21345, // Ketra's Head Shaman
		21346, // Ketra's Head Guard
		21347, // Ketra Prophet
		21348, // Prophet's Guard
		21349, // Prophet's Aide
		25299, // Ketra's Hero Hekaton (Raid Boss)
		25302, // Ketra's Commander Tayr (Raid Boss)
		25305, // Ketra's Chief Brakki (Raid Boss)
		25306, // Soul of Fire Nastron (Raid Boss)
	};
	private static final int[] VARKA =
	{
		21350, // Varka Silenos Recruit
		21351, // Varka Silenos Footman
		21353, // Varka Silenos Scout
		21354, // Varka Silenos Hunter
		21355, // Varka Silenos Shaman
		21357, // Varka Silenos Priest
		21358, // Varka Silenos Warrior
		21360, // Varka Silenos Medium
		21361, // Varka Silenos Magus
		21362, // Varka Silenos Officer
		21364, // Varka Silenos Seer
		21365, // Varka Silenos Great Magus
		21366, // Varka Silenos General
		21368, // Varka Silenos Great Seer
		21369, // Varka's Commander
		21370, // Varka's Elite Guard
		21371, // Varka's Head Magus
		21372, // Varka's Head Guard
		21373, // Varka's Prophet
		21374, // Prophet's Guard
		21375, // Disciple of Prophet
		25309, // Varka's Hero Shadith (Raid Boss)
		25312, // Varka's Commander Mos (Raid Boss)
		25315, // Varka's Chief Horus (Raid Boss)
		25316, // Soul of Water Ashutar (Raid Boss)
	};
	// Items
	private static final int[] KETRA_MARKS =
	{
		7211, // Mark of Ketra's Alliance - Level 1
		7212, // Mark of Ketra's Alliance - Level 2
		7213, // Mark of Ketra's Alliance - Level 3
		7214, // Mark of Ketra's Alliance - Level 4
		7215, // Mark of Ketra's Alliance - Level 5
	};
	private static final int[] VARKA_MARKS =
	{
		7221, // Mark of Varka's Alliance - Level 1
		7222, // Mark of Varka's Alliance - Level 2
		7223, // Mark of Varka's Alliance - Level 3
		7224, // Mark of Varka's Alliance - Level 4
		7225, // Mark of Varka's Alliance - Level 5
	};
	// Quests
	private static final String[] KETRA_QUESTS =
	{
		Q00605_AllianceWithKetraOrcs.class.getSimpleName(),
		Q00606_BattleAgainstVarkaSilenos.class.getSimpleName(),
		Q00607_ProveYourCourageKetra.class.getSimpleName(),
		Q00608_SlayTheEnemyCommanderKetra.class.getSimpleName(),
		Q00609_MagicalPowerOfWaterPart1.class.getSimpleName(),
		Q00610_MagicalPowerOfWaterPart2.class.getSimpleName()
	};
	private static final String[] VARKA_QUESTS =
	{
		Q00611_AllianceWithVarkaSilenos.class.getSimpleName(),
		Q00612_BattleAgainstKetraOrcs.class.getSimpleName(),
		Q00613_ProveYourCourageVarka.class.getSimpleName(),
		Q00614_SlayTheEnemyCommanderVarka.class.getSimpleName(),
		Q00615_MagicalPowerOfFirePart1.class.getSimpleName(),
		Q00616_MagicalPowerOfFirePart2.class.getSimpleName()
	};
	
	private VarkaKetra()
	{
		addKillId(KETRA);
		addKillId(VARKA);
		addNpcHateId(KETRA);
		addNpcHateId(VARKA);
	}
	
	@Override
	public void actionForEachPlayer(Player player, Npc npc, boolean isSummon)
	{
		if (LocationUtil.checkIfInRange(1500, player, npc, false))
		{
			if (ArrayUtil.contains(KETRA, npc.getId()) && hasAtLeastOneQuestItem(player, KETRA_MARKS))
			{
				decreaseAlliance(player, KETRA_MARKS);
				exitQuests(player, KETRA_QUESTS);
			}
			else if (ArrayUtil.contains(VARKA, npc.getId()) && hasAtLeastOneQuestItem(player, VARKA_MARKS))
			{
				decreaseAlliance(player, VARKA_MARKS);
				exitQuests(player, VARKA_QUESTS);
			}
		}
	}
	
	private void decreaseAlliance(Player player, int[] marks)
	{
		for (int i = 0; i < marks.length; i++)
		{
			if (hasQuestItems(player, marks[i]))
			{
				takeItems(player, marks[i], -1);
				if (i > 0)
				{
					giveItems(player, marks[i - 1], 1);
				}
				return;
			}
		}
	}
	
	private void exitQuests(Player player, String[] quests)
	{
		for (String quest : quests)
		{
			final QuestState qs = player.getQuestState(quest);
			if ((qs != null) && qs.isStarted())
			{
				qs.exitQuest(true);
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
	}
	
	@Override
	public boolean onNpcHate(Attackable mob, Player player, boolean isSummon)
	{
		if (ArrayUtil.contains(KETRA, mob.getId()))
		{
			return !hasAtLeastOneQuestItem(player, KETRA_MARKS);
		}
		return !hasAtLeastOneQuestItem(player, VARKA_MARKS);
	}
	
	public static void main(String[] args)
	{
		new VarkaKetra();
	}
}