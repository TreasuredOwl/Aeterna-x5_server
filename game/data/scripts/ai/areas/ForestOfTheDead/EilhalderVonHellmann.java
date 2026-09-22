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

import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.events.EventType;
import com.l2journey.gameserver.model.events.ListenerRegisterType;
import com.l2journey.gameserver.model.events.annotations.RegisterEvent;
import com.l2journey.gameserver.model.events.annotations.RegisterType;
import com.l2journey.gameserver.model.events.holders.OnDayNightChange;
import com.l2journey.gameserver.network.enums.ChatType;
import com.l2journey.gameserver.taskmanagers.GameTimeTaskManager;

import ai.AbstractNpcAI;

/**
 * Boss noturno Eilhalder Von Hellmann na Forest of the Dead.<br>
 * Aparece ao anoitecer e desaparece ao amanhecer.
 * @author Mobius, Mafias, KingHanker
 */
public class EilhalderVonHellmann extends AbstractNpcAI
{
	// NPC
	private static final int EILHALDER_VON_HELLMANN = 25328;
	// Location
	private static final Location SPAWN_LOCATION = new Location(59090, -42188, -3003);
	
	private Npc _npcInstance;
	
	private EilhalderVonHellmann()
	{
		addAttackId(EILHALDER_VON_HELLMANN);
		
		if (GameTimeTaskManager.getInstance().isNight())
		{
			_npcInstance = addSpawn(EILHALDER_VON_HELLMANN, SPAWN_LOCATION);
		}
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (!npc.getVariables().getBoolean("firstAttack", false))
		{
			npc.getVariables().set("firstAttack", true);
			npc.broadcastSay(ChatType.NPC_GENERAL, "You dare challenge the son of Von Hellmann?");
		}
		
		final double hpRatio = npc.getCurrentHp() / npc.getMaxHp();
		
		if (!npc.getVariables().getBoolean("talked50", false) && (hpRatio < 0.5))
		{
			npc.getVariables().set("talked50", true);
			npc.broadcastSay(ChatType.NPC_GENERAL, "Your blood will stain this forest forever!");
		}
		
		if (!npc.getVariables().getBoolean("talked10", false) && (hpRatio < 0.1))
		{
			npc.getVariables().set("talked10", true);
			npc.broadcastSay(ChatType.NPC_GENERAL, "The Von Hellmann family will not be forgotten!");
		}
	}
	
	@RegisterEvent(EventType.ON_DAY_NIGHT_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void onDayNightChange(OnDayNightChange event)
	{
		if (event.isNight())
		{
			_npcInstance = addSpawn(EILHALDER_VON_HELLMANN, SPAWN_LOCATION);
			return;
		}
		
		final Npc instance = _npcInstance;
		if ((instance != null) && !instance.isDead())
		{
			instance.broadcastSay(ChatType.NPC_GENERAL, "The sun... it burns! But my revenge is not over!");
			instance.deleteMe();
		}
		_npcInstance = null;
	}
	
	public static void main(String[] args)
	{
		new EilhalderVonHellmann();
	}
}