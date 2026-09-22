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
package ai.others.WeaverOlf;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.itemcontainer.Inventory;
import com.l2journey.gameserver.network.NpcStringId;
import com.l2journey.gameserver.network.enums.ChatType;
import com.l2journey.gameserver.network.serverpackets.NpcSay;

import ai.AbstractNpcAI;

/**
 * Weaver Olf - Pins And Pouch Unseal AI.
 * @author Gigiikun, Bloodshed, Adry_85
 */
public class WeaverOlf extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCs =
	{
		32610, // Olf Kanore
		32612, // Olf Adams
	};
	
	private static final int[] UNSEAL_PRICE =
	{
		3200,
		11800,
		26500,
		136600
	};
	
	private static final int[] CHANCES =
	{
		1, // top
		10, // high
		40, // mid
		100, // low
	};
	
	private static final int[][] PINS =
	{
		{
			13898, // Sealed Magic Pin (C-Grade)
			13905, // Top-Grade Magic Pin (C-Grade)
			13904, // High-Grade Magic Pin (C-Grade)
			13903, // Mid-Grade Magic Pin (C-Grade)
			13902, // Low-Grade Magic Pin (C-Grade)
		},
		{
			13899, // Sealed Magic Pin (B-Grade)
			13909, // Top-Grade Magic Pin (B-Grade)
			13908, // High-Grade Magic Pin (B-Grade)
			13907, // Mid-Grade Magic Pin (B-Grade)
			13906, // Low-Grade Magic Pin (B-Grade)
		},
		{
			13900, // Sealed Magic Pin (A-Grade)
			13913, // Top-Grade Magic Pin (A-Grade)
			13912, // High-Grade Magic Pin (A-Grade)
			13911, // Mid-Grade Magic Pin (A-Grade)
			13910, // Low-Grade Magic Pin (A-Grade)
		},
		{
			13901, // Sealed Magic Pin (S-Grade)
			13917, // Top-Grade Magic Pin (S-Grade)
			13916, // High-Grade Magic Pin (S-Grade)
			13915, // Mid-Grade Magic Pin (S-Grade)
			13914, // Low-Grade Magic Pin (S-Grade)
		}
	};
	
	private static final int[][] POUCHS =
	{
		{
			13918, // Sealed Magic Pouch (C-Grade)
			13925, // Top-Grade Magic Pouch (C-Grade)
			13924, // High-Grade Magic Pouch (C-Grade)
			13923, // Mid-Grade Magic Pouch (C-Grade)
			13922, // Low-Grade Magic Pouch (C-Grade)
		},
		{
			13919, // Sealed Magic Pouch (B-Grade)
			13929, // Top-Grade Magic Pouch (B-Grade)
			13928, // High-Grade Magic Pouch (B-Grade)
			13927, // Mid-Grade Magic Pouch (B-Grade)
			13926, // Low-Grade Magic Pouch (B-Grade)
		},
		{
			13920, // Sealed Magic Pouch (A-Grade)
			13933, // Top-Grade Magic Pouch (A-Grade)
			13932, // High-Grade Magic Pouch (A-Grade)
			13931, // Mid-Grade Magic Pouch (A-Grade)
			13930, // Low-Grade Magic Pouch (A-Grade)
		},
		{
			13921, // Sealed Magic Pouch (S-Grade)
			13937, // Top-Grade Magic Pouch (S-Grade)
			13936, // High-Grade Magic Pouch (S-Grade)
			13935, // Mid-Grade Magic Pouch (S-Grade)
			13934, // Low-Grade Magic Pouch (S-Grade)
		}
	};
	
	private static final int[][] CLIPS_ORNAMENTS =
	{
		{
			14902, // Sealed Magic Rune Clip (A-Grade)
			14909, // Top-level Magic Rune Clip (A-Grade)
			14908, // High-level Magic Rune Clip (A-Grade)
			14907, // Mid-level Magic Rune Clip (A-Grade)
			14906, // Low-level Magic Rune Clip (A-Grade)
		},
		{
			14903, // Sealed Magic Rune Clip (S-Grade)
			14913, // Top-level Magic Rune Clip (S-Grade)
			14912, // High-level Magic Rune Clip (S-Grade)
			14911, // Mid-level Magic Rune Clip (S-Grade)
			14910, // Low-level Magic Rune Clip (S-Grade)
		},
		{
			14904, // Sealed Magic Ornament (A-Grade)
			14917, // Top-grade Magic Ornament (A-Grade)
			14916, // High-grade Magic Ornament (A-Grade)
			14915, // Mid-grade Magic Ornament (A-Grade)
			14914, // Low-grade Magic Ornament (A-Grade)
		},
		{
			14905, // Sealed Magic Ornament (S-Grade)
			14921, // Top-grade Magic Ornament (S-Grade)
			14920, // High-grade Magic Ornament (S-Grade)
			14919, // Mid-grade Magic Ornament (S-Grade)
			14918, // Low-grade Magic Ornament (S-Grade)
		}
	};
	
	private WeaverOlf()
	{
		addStartNpc(NPCs);
		addTalkId(NPCs);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.contains("_grade_"))
		{
			final int grade = Integer.parseInt(event.substring(0, 1));
			int price;
			int[] itemIds;
			if (event.endsWith("_pin"))
			{
				price = UNSEAL_PRICE[grade];
				itemIds = PINS[grade];
			}
			else if (event.endsWith("_pouch"))
			{
				price = UNSEAL_PRICE[grade];
				itemIds = POUCHS[grade];
			}
			else if (event.endsWith("_clip"))
			{
				price = UNSEAL_PRICE[grade];
				itemIds = CLIPS_ORNAMENTS[grade - 2];
			}
			else if (event.endsWith("_ornament"))
			{
				price = UNSEAL_PRICE[grade];
				itemIds = CLIPS_ORNAMENTS[grade];
			}
			else
			{
				return super.onEvent(event, npc, player);
			}
			
			if (hasQuestItems(player, itemIds[0]))
			{
				if (player.getAdena() > price)
				{
					takeItems(player, Inventory.ADENA_ID, price);
					takeItems(player, itemIds[0], 1);
					final int rand = getRandom(200);
					if (rand <= CHANCES[0])
					{
						giveItems(player, itemIds[1], 1);
					}
					else if (rand <= CHANCES[1])
					{
						giveItems(player, itemIds[2], 1);
					}
					else if (rand <= CHANCES[2])
					{
						giveItems(player, itemIds[3], 1);
					}
					else if (rand <= CHANCES[3])
					{
						giveItems(player, itemIds[4], 1);
					}
					else
					{
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSFUL));
					}
				}
				else
				{
					return npc.getId() + "-low.htm";
				}
			}
			else
			{
				return npc.getId() + "-no.htm";
			}
			return super.onEvent(event, npc, player);
		}
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		return npc.getId() + "-1.htm";
	}
	
	public static void main(String[] args)
	{
		new WeaverOlf();
	}
}
