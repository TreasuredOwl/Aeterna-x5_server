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
package ai.areas.PaganTemple;

import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.itemcontainer.PlayerInventory;

import ai.AbstractNpcAI;

/**
 * @author KingHanker, Zoinha
 */
public class DoormanZombie extends AbstractNpcAI
{
	private static final int DOORMAN_ZOMBIE = 22136;
	
	private static final int VISITORS_MARK = 8064;
	private static final int FADED_VISITORS_MARK = 8065;
	private static final int PAGANS_MARK = 8067;
	
	public DoormanZombie()
	{
		addSpawnId(DOORMAN_ZOMBIE);
		addAggroRangeEnterId(DOORMAN_ZOMBIE);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.setImmobilized(true);
	}
	
	@Override
	public void onAggroRangeEnter(Npc npc, Player player, boolean isSummon)
	{
		final PlayerInventory inventory = player.getInventory();
		final boolean haveRequiredItem = (inventory.getItemByItemId(VISITORS_MARK) != null) || (inventory.getItemByItemId(FADED_VISITORS_MARK) != null) || (inventory.getItemByItemId(PAGANS_MARK) != null);
		
		if (haveRequiredItem)
		{
			npc.asAttackable().getAggroList().remove(player);
			npc.setTarget(null);
			npc.getAI().setIntention(Intention.IDLE);
		}
	}
	
	public static void main(String[] args)
	{
		new DoormanZombie();
	}
}
