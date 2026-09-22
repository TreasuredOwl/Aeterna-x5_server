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
package ai.others.DelusionTeleport;

import java.util.HashMap;
import java.util.Map;

import com.l2journey.gameserver.managers.TownManager;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.zone.type.TownZone;

import ai.AbstractNpcAI;

/**
 * Chambers of Delusion teleport AI.
 * @author GKR
 */
public class DelusionTeleport extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		32484, // Pathfinder Worker
		32658, // Guardian of Eastern Seal
		32659, // Guardian of Western Seal
		32660, // Guardian of Southern Seal
		32661, // Guardian of Northern Seal
		32662, // Guardian of Great Seal
		32663, // Guardian of Tower of Seal
	};
	// Location
	private static final Location[] HALL_LOCATIONS =
	{
		new Location(-114597, -152501, -6750),
		new Location(-114589, -154162, -6750)
	};
	// Player Variables
	private static final String DELUSION_RETURN = "DELUSION_RETURN";
	
	private static final Map<Integer, Location> RETURN_LOCATIONS = new HashMap<>();
	static
	{
		RETURN_LOCATIONS.put(0, new Location(43835, -47749, -792)); // Undefined origin, return to Rune
		RETURN_LOCATIONS.put(7, new Location(-14023, 123677, -3112)); // Gludio
		RETURN_LOCATIONS.put(8, new Location(18101, 145936, -3088)); // Dion
		RETURN_LOCATIONS.put(10, new Location(80905, 56361, -1552)); // Oren
		RETURN_LOCATIONS.put(14, new Location(42772, -48062, -792)); // Rune
		RETURN_LOCATIONS.put(15, new Location(108469, 221690, -3592)); // Heine
		RETURN_LOCATIONS.put(17, new Location(85991, -142234, -1336)); // Schuttgart
	}
	
	private DelusionTeleport()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (npc.getId() == NPCS[0]) // Pathfinder Worker
		{
			final TownZone town = TownManager.getTown(npc.getX(), npc.getY(), npc.getZ());
			final int townId = ((town == null) ? 0 : town.getTownId());
			player.getVariables().set(DELUSION_RETURN, townId);
			player.teleToLocation(getRandomEntry(HALL_LOCATIONS), false);
		}
		else
		{
			final int townId = player.getVariables().getInt(DELUSION_RETURN, 0);
			player.teleToLocation(RETURN_LOCATIONS.get(townId), true);
			player.getVariables().remove(DELUSION_RETURN);
		}
		return super.onTalk(npc, player);
	}
	
	public static void main(String[] args)
	{
		new DelusionTeleport();
	}
}