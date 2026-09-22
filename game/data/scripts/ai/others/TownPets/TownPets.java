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
package ai.others.TownPets;

import com.l2journey.gameserver.GeoData;
import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.Spawn;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;

import ai.AbstractNpcAI;

/**
 * Town Pet AI
 * @author Mobius
 */
public class TownPets extends AbstractNpcAI
{
	// NPCs
	private static final int[] PETS =
	{
		31202, // Maximus
		31203, // Moon Dancer
		31204, // Georgio
		31205, // Katz
		31206, // Ten Ten
		31207, // Sardinia
		31208, // La Grange
		31209, // Misty Rain
		31266, // Kaiser
		31593, // Dorothy
		31758, // Rafi
		31955, // Ruby
	};
	// Misc
	private static final int MOVE_INTERVAL = 5000;
	
	private TownPets()
	{
		addSpawnId(PETS);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if ((npc != null) && npc.isSpawned() && event.equals("move"))
		{
			if (npc.getWorldRegion().isActive())
			{
				final Spawn spawn = npc.getSpawn();
				final int locX = spawn.getX() + getRandom(-50, 50);
				final int locY = spawn.getY() + getRandom(-50, 50);
				final Location moveLocation = GeoData.getInstance().moveCheck(npc.getX(), npc.getY(), npc.getZ(), locX, locY, npc.getZ(), 0);
				if (npc.calculateDistance3D(moveLocation) > 20)
				{
					npc.getAI().setIntention(Intention.MOVE_TO, moveLocation);
				}
				startQuestTimer("move", MOVE_INTERVAL, npc, null);
			}
			else
			{
				startQuestTimer("move", MOVE_INTERVAL * 3, npc, null);
			}
		}
		return null;
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.setRunning();
		startQuestTimer("move", MOVE_INTERVAL, npc, null);
	}
	
	public static void main(String[] args)
	{
		new TownPets();
	}
}
