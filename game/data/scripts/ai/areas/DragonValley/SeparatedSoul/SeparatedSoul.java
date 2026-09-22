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
package ai.areas.DragonValley.SeparatedSoul;

import java.util.HashMap;
import java.util.Map;

import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;

import ai.AbstractNpcAI;

/**
 * Separated Soul teleport AI.
 * @author Adry_85
 */
public class SeparatedSoul extends AbstractNpcAI
{
	// NPCs
	private static final int[] SEPARATED_SOULS =
	{
		32864,
		32865,
		32866,
		32867,
		32868,
		32869,
		32870,
		32891
	};
	
	// Items
	private static final int WILL_OF_ANTHARAS = 17266;
	private static final int SEALED_BLOOD_CRYSTAL = 17267;
	private static final int ANTHARAS_BLOOD_CRYSTAL = 17268;
	// Misc
	private static final int MIN_LEVEL = 80;
	// Locations
	private static final Map<String, Location> LOCATIONS = new HashMap<>();
	static
	{
		LOCATIONS.put("HuntersVillage", new Location(117031, 76769, -2696));
		LOCATIONS.put("AntharasLair", new Location(131116, 114333, -3704));
		LOCATIONS.put("AntharasLairDeep", new Location(148447, 110582, -3944));
		LOCATIONS.put("AntharasLairMagicForceFieldBridge", new Location(146129, 111232, -3568));
		LOCATIONS.put("DragonValley", new Location(73122, 118351, -3714));
		LOCATIONS.put("DragonValleyCenter", new Location(99218, 110283, -3696));
		LOCATIONS.put("DragonValleyNorth", new Location(116992, 113716, -3056));
		LOCATIONS.put("DragonValleySouth", new Location(113203, 121063, -3712));
	}
	
	private SeparatedSoul()
	{
		addStartNpc(SEPARATED_SOULS);
		addTalkId(SEPARATED_SOULS);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (LOCATIONS.containsKey(event))
		{
			if (player.getLevel() >= MIN_LEVEL)
			{
				player.teleToLocation(LOCATIONS.get(event), true);
			}
			else
			{
				return "no-level.htm";
			}
		}
		else if ("Synthesis".equals(event)) // Request Item Synthesis
		{
			if (hasQuestItems(player, WILL_OF_ANTHARAS, SEALED_BLOOD_CRYSTAL))
			{
				takeItems(player, WILL_OF_ANTHARAS, 1);
				takeItems(player, SEALED_BLOOD_CRYSTAL, 1);
				giveItems(player, ANTHARAS_BLOOD_CRYSTAL, 1);
			}
			else
			{
				return "no-items.htm";
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	public static void main(String[] args)
	{
		new SeparatedSoul();
	}
}