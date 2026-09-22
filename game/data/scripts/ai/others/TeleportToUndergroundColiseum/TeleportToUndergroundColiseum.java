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
package ai.others.TeleportToUndergroundColiseum;

import com.l2journey.commons.util.StringUtil;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.util.ArrayUtil;

import ai.AbstractNpcAI;

/**
 * Underground Coliseum teleport AI
 * @author malyelfik
 */
public class TeleportToUndergroundColiseum extends AbstractNpcAI
{
	// NPCs
	private static final int COLISEUM_HELPER = 32491;
	private static final int PADDIES = 32378;
	private static final int[] MANAGERS =
	{
		32377,
		32513,
		32514,
		32515,
		32516
	};
	
	// Locations
	private static final Location[] COLISEUM_LOCS =
	{
		new Location(-81896, -49589, -10352),
		new Location(-82271, -49196, -10352),
		new Location(-81886, -48784, -10352),
		new Location(-81490, -49167, -10352)
	};
	
	private static final Location[] RETURN_LOCS =
	{
		new Location(-59161, -56954, -2036),
		new Location(-59155, -56831, -2036),
		new Location(-59299, -56955, -2036),
		new Location(-59224, -56837, -2036),
		new Location(-59134, -56899, -2036)
	};
	
	private static final Location[][] MANAGERS_LOCS =
	{
		{
			new Location(-84451, -45452, -10728),
			new Location(-84580, -45587, -10728)
		},
		{
			new Location(-86154, -50429, -10728),
			new Location(-86118, -50624, -10728)
		},
		{
			new Location(-82009, -53652, -10728),
			new Location(-81802, -53665, -10728)
		},
		{
			new Location(-77603, -50673, -10728),
			new Location(-77586, -50503, -10728)
		},
		{
			new Location(-79186, -45644, -10728),
			new Location(-79309, -45561, -10728)
		}
	};
	
	private TeleportToUndergroundColiseum()
	{
		addStartNpc(MANAGERS);
		addStartNpc(COLISEUM_HELPER, PADDIES);
		addTalkId(MANAGERS);
		addTalkId(COLISEUM_HELPER, PADDIES);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.endsWith(".htm"))
		{
			return event;
		}
		else if (event.equals("return"))
		{
			player.teleToLocation(getRandomEntry(RETURN_LOCS), false);
		}
		else if (StringUtil.isNumeric(event))
		{
			final int val = Integer.parseInt(event) - 1;
			player.teleToLocation(getRandomEntry(MANAGERS_LOCS[val]), false);
		}
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (ArrayUtil.contains(MANAGERS, npc.getId()))
		{
			player.teleToLocation(getRandomEntry(RETURN_LOCS), false);
		}
		else
		{
			player.teleToLocation(getRandomEntry(COLISEUM_LOCS), false);
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new TeleportToUndergroundColiseum();
	}
}