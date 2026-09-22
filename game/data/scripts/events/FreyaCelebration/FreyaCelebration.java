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
package events.FreyaCelebration;

import java.util.List;

import com.l2journey.gameserver.model.WorldObject;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.model.itemcontainer.Inventory;
import com.l2journey.gameserver.model.quest.LongTimeEvent;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.network.NpcStringId;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.enums.ChatType;
import com.l2journey.gameserver.network.serverpackets.CreatureSay;
import com.l2journey.gameserver.network.serverpackets.SystemMessage;
import com.l2journey.gameserver.util.ArrayUtil;

/**
 * Freya Celebration event AI.
 * @author Gnacik
 */
public class FreyaCelebration extends LongTimeEvent
{
	// NPC
	private static final int FREYA = 13296;
	// Items
	private static final int FREYA_POTION = 15440;
	private static final int FREYA_GIFT = 17138;
	// Misc
	private static final String RESET_VAR = "FreyaCelebration";
	private static final int HOURS = 20;
	
	private static final int[] SKILLS =
	{
		9150,
		9151,
		9152,
		9153,
		9154,
		9155,
		9156
	};
	
	private static final NpcStringId[] FREYA_TEXT =
	{
		NpcStringId.EVEN_THOUGH_YOU_BRING_SOMETHING_CALLED_A_GIFT_AMONG_YOUR_HUMANS_IT_WOULD_JUST_BE_PROBLEMATIC_FOR_ME,
		NpcStringId.I_JUST_DON_T_KNOW_WHAT_EXPRESSION_I_SHOULD_HAVE_IT_APPEARED_ON_ME_ARE_HUMAN_S_EMOTIONS_LIKE_THIS_FEELING,
		NpcStringId.THE_FEELING_OF_THANKS_IS_JUST_TOO_MUCH_DISTANT_MEMORY_FOR_ME,
		NpcStringId.BUT_I_KIND_OF_MISS_IT_LIKE_I_HAD_FELT_THIS_FEELING_BEFORE,
		NpcStringId.I_AM_ICE_QUEEN_FREYA_THIS_FEELING_AND_EMOTION_ARE_NOTHING_BUT_A_PART_OF_MELISSA_A_MEMORIES
	};
	
	private FreyaCelebration()
	{
		addStartNpc(FREYA);
		addFirstTalkId(FREYA);
		addTalkId(FREYA);
		addSkillSeeId(FREYA);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.equalsIgnoreCase("give_potion"))
		{
			if (getQuestItemsCount(player, Inventory.ADENA_ID) > 1)
			{
				final long currentTime = System.currentTimeMillis();
				final long reuseTime = player.getVariables().getLong(RESET_VAR, 0);
				if (currentTime > reuseTime)
				{
					takeItems(player, Inventory.ADENA_ID, 1);
					giveItems(player, FREYA_POTION, 1);
					player.getVariables().set(RESET_VAR, currentTime + (HOURS * 3600000));
				}
				else
				{
					final long remainingTime = (reuseTime - currentTime) / 1000;
					final int hours = (int) (remainingTime / 3600);
					final int minutes = (int) ((remainingTime % 3600) / 60);
					final SystemMessage sm = new SystemMessage(SystemMessageId.S1_WILL_BE_AVAILABLE_FOR_RE_USE_AFTER_S2_HOUR_S_S3_MINUTE_S);
					sm.addItemName(FREYA_POTION);
					sm.addInt(hours);
					sm.addInt(minutes);
					player.sendPacket(sm);
				}
			}
			else
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.S2_UNIT_S_OF_THE_ITEM_S1_IS_ARE_REQUIRED);
				sm.addItemName(Inventory.ADENA_ID);
				sm.addInt(1);
				player.sendPacket(sm);
			}
		}
		return null;
	}
	
	@Override
	public void onSkillSee(Npc npc, Player caster, Skill skill, List<WorldObject> targets, boolean isSummon)
	{
		if ((caster == null) || (npc == null))
		{
			return;
		}
		
		if ((npc.getId() == FREYA) && targets.contains(npc) && ArrayUtil.contains(SKILLS, skill.getId()))
		{
			if (getRandom(100) < 5)
			{
				final CreatureSay cs = new CreatureSay(npc, ChatType.NPC_GENERAL, NpcStringId.DEAR_S1_THINK_OF_THIS_AS_MY_APPRECIATION_FOR_THE_GIFT_TAKE_THIS_WITH_YOU_THERE_S_NOTHING_STRANGE_ABOUT_IT_IT_S_JUST_A_BIT_OF_MY_CAPRICIOUSNESS);
				cs.addStringParameter(caster.getName());
				
				npc.broadcastPacket(cs);
				
				caster.addItem(ItemProcessType.REWARD, FREYA_GIFT, 1, npc, true);
			}
			else if (getRandom(10) < 2)
			{
				npc.broadcastPacket(new CreatureSay(npc, ChatType.NPC_GENERAL, getRandomEntry(FREYA_TEXT)));
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "13296.htm";
	}
	
	public static void main(String[] args)
	{
		new FreyaCelebration();
	}
}
