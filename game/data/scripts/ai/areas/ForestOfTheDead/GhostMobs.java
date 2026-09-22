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
package ai.areas.ForestOfTheDead;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.events.EventType;
import com.l2journey.gameserver.model.events.ListenerRegisterType;
import com.l2journey.gameserver.model.events.annotations.RegisterEvent;
import com.l2journey.gameserver.model.events.annotations.RegisterType;
import com.l2journey.gameserver.model.events.holders.OnDayNightChange;
import com.l2journey.gameserver.taskmanagers.GameTimeTaskManager;

import ai.AbstractNpcAI;

/**
 * Ghost Mobs AI for Forest of the Dead. Spawns undead mobs at night with a phantasmagoric mechanic: each mob independently fades in and out at random intervals during the night. All mobs disappear at dawn.
 * @author Mafias, KingHanker
 */
public class GhostMobs extends AbstractNpcAI
{
	// @formatter:off
	// Resurrected Knight (21548)
	private static final int RESURRECTED_KNIGHT = 21548;
	private static final Location[] RESURRECTED_KNIGHT_LOCS =
	{
		new Location(47300, -58346, -2336, 29655),
		new Location(47822, -57091, -2822, 0),
		new Location(48426, -57447, -2822, 0),
		new Location(48729, -57625, -2822, 0),
		new Location(49051, -57981, -2728, 13613),
		new Location(50668, -56789, -3099, 0),
		new Location(50750, -56306, -2635, 14089),
		new Location(50828, -54706, -3071, 11121),
		new Location(51070, -56789, -3099, 0),
		new Location(51170, -57323, -3099, 0),
	};
	
	// Corrupted Guard (21549)
	private static final int CORRUPTED_GUARD = 21549;
	private static final Location[] CORRUPTED_GUARD_LOCS =
	{
		new Location(48085, -57568, -2578, 16933),
		new Location(48280, -58502, -2700, 45039),
		new Location(48426, -56525, -2671, 32552),
		new Location(49330, -61979, -2885, 0),
		new Location(49550, -56382, -2612, 45542),
		new Location(49971, -55948, -2643, 9114),
		new Location(50883, -59952, -3364, 0),
		new Location(51150, -61008, -2746, 13444),
		new Location(51271, -56789, -3099, 0),
		new Location(51647, -58538, -3020, 34169),
		new Location(52499, -60914, -3394, 0),
		new Location(52608, -58211, -3106, 3125),
		new Location(53507, -60558, -3016, 46424),
		new Location(54408, -59942, -3734, 0),
		new Location(54642, -60557, -3145, 45247),
	};
	
	// Resurrected Guard (21551)
	private static final int RESURRECTED_GUARD = 21551;
	private static final Location[] RESURRECTED_GUARD_LOCS =
	{
		new Location(51035, -53627, -3195, 0),
		new Location(51075, -52419, -2904, 22954),
		new Location(51083, -51510, -2968, 30518),
		new Location(52463, -54323, -3099, 2331),
		new Location(53070, -53773, -3095, 47201),
		new Location(53294, -54403, -3009, 50176),
		new Location(56563, -58159, -3540, 5448),
		new Location(56972, -57588, -3607, 0),
		new Location(57307, -55905, -3256, 23233),
		new Location(58504, -54981, -3217, 22538),
		new Location(59463, -56381, -3050, 13029),
	};
	
	// Slaughter Executioner (21555)
	private static final int SLAUGHTER_EXECUTIONER = 21555;
	private static final Location[] SLAUGHTER_EXECUTIONER_LOCS =
	{
		new Location(47509, -57360, -2408, 28536),
		new Location(48628, -57803, -2822, 0),
		new Location(49632, -62157, -2885, 0),
		new Location(50086, -56500, -2595, 12913),
		new Location(50461, -55869, -2680, 55456),
		new Location(50840, -56068, -2626, 44585),
		new Location(50969, -60527, -2771, 3585),
		new Location(53308, -57909, -3321, 17049),
		new Location(54108, -61270, -3394, 0),
	};
	// @formatter:on
	
	// Time each ghost stays visible before fading out (ms).
	private static final int MIN_VISIBLE_TIME = 25000;
	private static final int MAX_VISIBLE_TIME = 60000;
	// Time each ghost stays hidden before fading back in (ms).
	private static final int MIN_HIDDEN_TIME = 15000;
	private static final int MAX_HIDDEN_TIME = 45000;
	
	private static final String FADE_OUT = "FADE_OUT_";
	private static final String FADE_IN = "FADE_IN_";
	private static final String COMBAT_DESPAWN = "COMBAT_DESPAWN";
	
	private final List<GhostSpawn> _ghostSpawns = new CopyOnWriteArrayList<>();
	private final AtomicBoolean _isActive = new AtomicBoolean(false);
	
	private static class GhostSpawn
	{
		final int npcId;
		final Location location;
		Npc npc;
		
		GhostSpawn(int npcId, Location location)
		{
			this.npcId = npcId;
			this.location = location;
		}
	}
	
	private GhostMobs()
	{
		if (GameTimeTaskManager.getInstance().isNight())
		{
			initAndSpawnAll();
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.startsWith(FADE_OUT))
		{
			if (!_isActive.get())
			{
				return null;
			}
			
			final int index = Integer.parseInt(event.substring(FADE_OUT.length()));
			if (index >= _ghostSpawns.size())
			{
				return null;
			}
			
			final GhostSpawn gs = _ghostSpawns.get(index);
			if (gs.npc != null)
			{
				if (gs.npc.isInCombat())
				{
					startQuestTimer(event, 5000, null, null);
				}
				else
				{
					gs.npc.deleteMe();
					gs.npc = null;
					// Only schedule fade-in if still active (night).
					if (_isActive.get())
					{
						startRandomTimer(FADE_IN + index, MIN_HIDDEN_TIME, MAX_HIDDEN_TIME);
					}
				}
			}
		}
		else if (event.startsWith(FADE_IN))
		{
			if (!_isActive.get())
			{
				return null;
			}
			
			final int index = Integer.parseInt(event.substring(FADE_IN.length()));
			if (index >= _ghostSpawns.size())
			{
				return null;
			}
			
			final GhostSpawn gs = _ghostSpawns.get(index);
			gs.npc = addSpawn(gs.npcId, gs.location);
			startRandomTimer(FADE_OUT + index, MIN_VISIBLE_TIME, MAX_VISIBLE_TIME);
		}
		else if (event.equals(COMBAT_DESPAWN) && (npc != null))
		{
			if (npc.isInCombat())
			{
				startQuestTimer(COMBAT_DESPAWN, 30000, npc, null);
			}
			else
			{
				npc.deleteMe();
			}
		}
		return null;
	}
	
	@RegisterEvent(EventType.ON_DAY_NIGHT_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void onDayNightChange(OnDayNightChange event)
	{
		if (event.isNight())
		{
			initAndSpawnAll();
		}
		else
		{
			despawnAll();
		}
	}
	
	private void initAndSpawnAll()
	{
		_ghostSpawns.clear();
		addGroup(RESURRECTED_KNIGHT, RESURRECTED_KNIGHT_LOCS);
		addGroup(CORRUPTED_GUARD, CORRUPTED_GUARD_LOCS);
		addGroup(RESURRECTED_GUARD, RESURRECTED_GUARD_LOCS);
		addGroup(SLAUGHTER_EXECUTIONER, SLAUGHTER_EXECUTIONER_LOCS);
		
		_isActive.set(true);
		
		for (int i = 0; i < _ghostSpawns.size(); i++)
		{
			final GhostSpawn gs = _ghostSpawns.get(i);
			gs.npc = addSpawn(gs.npcId, gs.location);
			startRandomTimer(FADE_OUT + i, MIN_VISIBLE_TIME, MAX_VISIBLE_TIME);
		}
	}
	
	private void addGroup(int npcId, Location[] locations)
	{
		for (Location loc : locations)
		{
			_ghostSpawns.add(new GhostSpawn(npcId, loc));
		}
	}
	
	private void startRandomTimer(String name, int min, int max)
	{
		startQuestTimer(name, ThreadLocalRandom.current().nextInt(min, max + 1), null, null);
	}
	
	private void despawnAll()
	{
		// Flag inactive first to prevent any pending timer from spawning new NPCs.
		_isActive.set(false);
		
		// Cancel all pending fade timers.
		final int size = _ghostSpawns.size();
		for (int i = 0; i < size; i++)
		{
			cancelQuestTimers(FADE_OUT + i);
			cancelQuestTimers(FADE_IN + i);
		}
		
		// Delete all currently visible NPCs.
		for (GhostSpawn gs : _ghostSpawns)
		{
			if (gs.npc != null)
			{
				if (gs.npc.isInCombat())
				{
					startQuestTimer(COMBAT_DESPAWN, 30000, gs.npc, null);
				}
				else
				{
					gs.npc.deleteMe();
				}
				gs.npc = null;
			}
		}
		
		_ghostSpawns.clear();
	}
	
	public static void main(String[] args)
	{
		new GhostMobs();
	}
}