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
package custom.RaidbossInfo;

import java.util.ArrayList;
import java.util.List;

import com.l2journey.commons.util.StringUtil;
import com.l2journey.gameserver.data.SpawnTable;
import com.l2journey.gameserver.data.xml.NpcData;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.Spawn;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.templates.NpcTemplate;

import ai.AbstractNpcAI;

/**
 * Raidboss Info AI.<br>
 * Original Jython script Kerberos.
 * @author Nyaran
 */
public class RaidbossInfo extends AbstractNpcAI
{
	// @formatter:off
	private static final int[] NPC =
	{
		31729, 31730, 31731, 31732, 31733, 31734, 31735, 31736, 31737, 31738,
		31739, 31740, 31741, 31742, 31743, 31744, 31745, 31746, 31747, 31748,
		31750, 31751, 31752, 31755, 31756, 31757, 31758,
		31759, 31760, 31761, 31762, 31763, 31764, 31765, 31766, 31767, 31768,
		31769, 31770, 31771, 31772, 31773, 31774, 31775, 31776, 31777, 31778,
		31779, 31780, 31781, 31782, 31783, 31784, 31785, 31786, 31787, 31788,
		31789, 31790, 31791, 31792, 31793, 31794, 31795, 31796, 31797, 31798,
		31799, 31800, 31801, 31802, 31803, 31804, 31805, 31806, 31807, 31808,
		31809, 31810, 31811, 31812, 31813, 31814, 31815, 31816, 31817, 31818,
		31819, 31820, 31821, 31822, 31823, 31824, 31825, 31826, 31827, 31828,
		31829, 31830, 31831, 31832, 31833, 31834, 31835, 31836, 31837, 31838,
		31839, 31840, 31841, 32337, 32338, 32339, 32340
	};
	// @formatter:on
	private static final List<Integer> RAIDS = new ArrayList<>();
	
	private RaidbossInfo()
	{
		addStartNpc(NPC);
		addTalkId(NPC);
		
		// Add all Raid Bosses to RAIDS list
		for (NpcTemplate raid : NpcData.getInstance().getAllNpcOfClassType("RaidBoss"))
		{
			RAIDS.add(raid.getId());
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		if (StringUtil.isNumeric(event))
		{
			htmltext = null;
			final int bossId = Integer.parseInt(event);
			if (RAIDS.contains(bossId))
			{
				final Spawn spawn = SpawnTable.getInstance().getAnySpawn(bossId);
				if (spawn != null)
				{
					final Location loc = spawn;
					player.getRadar().addMarker(loc.getX(), loc.getY(), loc.getZ());
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		return "info.htm";
	}
	
	public static void main(String[] args)
	{
		new RaidbossInfo();
	}
}
