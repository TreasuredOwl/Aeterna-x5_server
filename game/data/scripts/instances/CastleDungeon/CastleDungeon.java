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
package instances.CastleDungeon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.l2journey.gameserver.managers.FortManager;
import com.l2journey.gameserver.managers.InstanceManager;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.groups.Party;
import com.l2journey.gameserver.model.instancezone.InstanceWorld;
import com.l2journey.gameserver.model.siege.Castle;
import com.l2journey.gameserver.model.siege.Fort;
import com.l2journey.gameserver.util.ArrayUtil;

import instances.AbstractInstance;

/**
 * Castle Dungeon instance zone.
 * @author Adry_85
 * @since 2.6.0.0
 */
public class CastleDungeon extends AbstractInstance
{
	// Locations
	private static final Location[] ENTER_LOC =
	{
		new Location(12188, -48770, -3008),
		new Location(12218, -48770, -3008),
		new Location(12248, -48770, -3008),
	};
	
	private static final Location RAIDS_LOC = new Location(11793, -49190, -3008, 0);
	// Misc
	private static final Map<Integer, Integer> CASTLE_DUNGEON = new HashMap<>();
	private static final Map<Integer, List<Integer>> FORTRESS = new HashMap<>();
	static
	{
		CASTLE_DUNGEON.put(36403, 13); // Gludio
		CASTLE_DUNGEON.put(36404, 14); // Dion
		CASTLE_DUNGEON.put(36405, 15); // Giran
		CASTLE_DUNGEON.put(36406, 16); // Oren
		CASTLE_DUNGEON.put(36407, 17); // Aden
		CASTLE_DUNGEON.put(36408, 18); // Innadril
		CASTLE_DUNGEON.put(36409, 19); // Goddard
		CASTLE_DUNGEON.put(36410, 20); // Rune
		CASTLE_DUNGEON.put(36411, 21); // Schuttgart
		FORTRESS.put(1, Arrays.asList(101, 102, 112, 113)); // Gludio Castle
		FORTRESS.put(2, Arrays.asList(103, 112, 114, 115)); // Dion Castle
		FORTRESS.put(3, Arrays.asList(104, 114, 116, 118, 119)); // Giran Castle
		FORTRESS.put(4, Arrays.asList(105, 113, 115, 116, 117)); // Oren Castle
		FORTRESS.put(5, Arrays.asList(106, 107, 117, 118)); // Aden Castle
		FORTRESS.put(6, Arrays.asList(108, 119)); // Innadril Castle
		FORTRESS.put(7, Arrays.asList(109, 117, 120)); // Goddard Castle
		FORTRESS.put(8, Arrays.asList(110, 120, 121)); // Rune Castle
		FORTRESS.put(9, Arrays.asList(111, 121)); // Schuttgart Castle
	}
	
	// Raid Bosses
	protected static final int[] RAIDS1 =
	{
		25546, // Rhianna the Traitor
		25549, // Tesla the Deceiver
		25552, // Soul Hunter Chakundel
	};
	protected static final int[] RAIDS2 =
	{
		25553, // Durango the Crusher
		25554, // Brutus the Obstinate
		25557, // Ranger Karankawa
		25560, // Sargon the Mad
	};
	protected static final int[] RAIDS3 =
	{
		25563, // Beautiful Atrielle
		25566, // Nagen the Tomboy
		25569, // Jax the Destroyer
	};
	
	private CastleDungeon()
	{
		addFirstTalkId(CASTLE_DUNGEON.keySet());
		addStartNpc(CASTLE_DUNGEON.keySet());
		addTalkId(CASTLE_DUNGEON.keySet());
		addKillId(RAIDS1);
		addKillId(RAIDS2);
		addKillId(RAIDS3);
	}
	
	@Override
	public void onEnterInstance(Player player, InstanceWorld world, boolean firstEntrance)
	{
		if (firstEntrance)
		{
			final Party party = player.getParty();
			if (party == null)
			{
				teleportPlayer(player, getRandomEntry(ENTER_LOC), world.getInstanceId());
				world.addAllowed(player);
			}
			else
			{
				for (Player partyMember : party.getMembers())
				{
					teleportPlayer(partyMember, getRandomEntry(ENTER_LOC), world.getInstanceId());
					world.addAllowed(partyMember);
				}
			}
			
			spawnRaid(world);
		}
		else
		{
			teleportPlayer(player, getRandomEntry(ENTER_LOC), world.getInstanceId());
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "36403.html";
	}
	
	@Override
	public void onKill(Npc npc, Player player, boolean isSummon)
	{
		final InstanceWorld world = InstanceManager.getInstance().getWorld(npc);
		if (world != null)
		{
			if (ArrayUtil.contains(RAIDS3, npc.getId()))
			{
				finishInstance(world);
			}
			else
			{
				world.incStatus();
				spawnRaid(world);
			}
		}
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final Party party = player.getParty();
		if (party == null)
		{
			return "36403-01.html";
		}
		
		final Castle castle = npc.getCastle();
		if (castle.getSiege().isInProgress())
		{
			return "36403-04.html";
		}
		
		if ((npc.isMyLord(player) || ((player.getClan() != null) && (npc.getCastle().getResidenceId() == player.getClan().getCastleId()) && (player.getClan().getCastleId() > 0))))
		{
			final int numFort = ((castle.getResidenceId() == 1) || (castle.getResidenceId() == 5)) ? 2 : 1;
			final List<Integer> fort = FORTRESS.get(castle.getResidenceId());
			for (int i = 0; i < numFort; i++)
			{
				final Fort fortress = FortManager.getInstance().getFortById(fort.get(i));
				if (fortress.getFortState() == 0)
				{
					return "36403-05.html";
				}
			}
		}
		
		for (Player partyMember : party.getMembers())
		{
			if ((partyMember.getClan() == null) || (partyMember.getClan().getCastleId() != castle.getResidenceId()))
			{
				return "36403-02.html";
			}
			
			if (System.currentTimeMillis() < InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), CASTLE_DUNGEON.get(npc.getId())))
			{
				return "36403-03.html";
			}
		}
		
		enterInstance(player, CASTLE_DUNGEON.get(npc.getId()));
		return super.onTalk(npc, player);
	}
	
	protected void spawnRaid(InstanceWorld world)
	{
		int spawnId;
		if (world.getStatus() == 0)
		{
			spawnId = getRandomEntry(RAIDS1);
		}
		else if (world.getStatus() == 1)
		{
			spawnId = getRandomEntry(RAIDS2);
		}
		else
		{
			spawnId = getRandomEntry(RAIDS3);
		}
		
		addSpawn(spawnId, RAIDS_LOC, false, 0, false, world.getInstanceId());
	}
	
	public static void main(String[] args)
	{
		new CastleDungeon();
	}
}
