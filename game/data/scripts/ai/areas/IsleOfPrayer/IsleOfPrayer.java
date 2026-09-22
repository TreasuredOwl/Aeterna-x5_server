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
package ai.areas.IsleOfPrayer;

import java.util.HashMap;
import java.util.Map;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.item.holders.ItemChanceHolder;

import ai.AbstractNpcAI;

/**
 * Isle of Prayer AI.
 * @author Zoey76
 */
public class IsleOfPrayer extends AbstractNpcAI
{
	// Items
	private static final int YELLOW_SEED_OF_EVIL_SHARD = 9593;
	private static final int GREEN_SEED_OF_EVIL_SHARD = 9594;
	private static final int BLUE_SEED_OF_EVIL_SHARD = 9595;
	private static final int RED_SEED_OF_EVIL_SHARD = 9596;
	// Monsters
	private static final Map<Integer, ItemChanceHolder> MONSTERS = new HashMap<>();
	static
	{
		MONSTERS.put(22257, new ItemChanceHolder(YELLOW_SEED_OF_EVIL_SHARD, 2087)); // Island Guardian
		MONSTERS.put(22258, new ItemChanceHolder(YELLOW_SEED_OF_EVIL_SHARD, 2147)); // White Sand Mirage
		MONSTERS.put(22259, new ItemChanceHolder(YELLOW_SEED_OF_EVIL_SHARD, 2642)); // Muddy Coral
		MONSTERS.put(22260, new ItemChanceHolder(YELLOW_SEED_OF_EVIL_SHARD, 2292)); // Kleopora
		MONSTERS.put(22261, new ItemChanceHolder(GREEN_SEED_OF_EVIL_SHARD, 1171)); // Seychelles
		MONSTERS.put(22262, new ItemChanceHolder(GREEN_SEED_OF_EVIL_SHARD, 1173)); // Naiad
		MONSTERS.put(22263, new ItemChanceHolder(GREEN_SEED_OF_EVIL_SHARD, 1403)); // Sonneratia
		MONSTERS.put(22264, new ItemChanceHolder(GREEN_SEED_OF_EVIL_SHARD, 1207)); // Castalia
		MONSTERS.put(22265, new ItemChanceHolder(RED_SEED_OF_EVIL_SHARD, 575)); // Chrysocolla
		MONSTERS.put(22266, new ItemChanceHolder(RED_SEED_OF_EVIL_SHARD, 493)); // Pythia
		MONSTERS.put(22267, new ItemChanceHolder(RED_SEED_OF_EVIL_SHARD, 770)); // Dark Water Dragon
		MONSTERS.put(22268, new ItemChanceHolder(BLUE_SEED_OF_EVIL_SHARD, 987)); // Shade
		MONSTERS.put(22269, new ItemChanceHolder(BLUE_SEED_OF_EVIL_SHARD, 995)); // Shade
		MONSTERS.put(22270, new ItemChanceHolder(BLUE_SEED_OF_EVIL_SHARD, 1008)); // Water Dragon Detractor
		MONSTERS.put(22271, new ItemChanceHolder(BLUE_SEED_OF_EVIL_SHARD, 1008)); // Water Dragon Detractor
	}
	
	private IsleOfPrayer()
	{
		addKillId(MONSTERS.keySet());
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final ItemChanceHolder holder = MONSTERS.get(npc.getId());
		if (getRandom(10000) <= holder.getChance())
		{
			npc.dropItem(killer, holder);
		}
	}
	
	public static void main(String[] args)
	{
		new IsleOfPrayer();
	}
}
