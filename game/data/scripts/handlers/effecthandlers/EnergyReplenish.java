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
 */
package handlers.effecthandlers;

import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.conditions.Condition;
import com.l2journey.gameserver.model.effects.AbstractEffect;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.itemcontainer.Inventory;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.SystemMessage;

/**
 * Energy Replenish effect implementation.<br>
 * Replenishes agathion energy on the equipped bracelet.
 * @author L2Journey
 */
public class EnergyReplenish extends AbstractEffect
{
	private final int _energy;
	
	public EnergyReplenish(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_energy = params.getInt("energy", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill)
	{
		if ((effected == null) || !effected.isPlayer())
		{
			return;
		}
		
		final Player player = effected.asPlayer();
		final Item bracelet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		
		if ((bracelet == null) || !bracelet.isAgathionItem())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_REPLENISH_ENERGY_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
			return;
		}
		
		if (_energy <= 0)
		{
			player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			return;
		}
		
		final int maxEnergy = bracelet.getTemplate().getAgathionMaxEnergy();
		final int currentEnergy = bracelet.getAgathionEnergy();
		final int spaceAvailable = maxEnergy - currentEnergy;
		
		// Check if bracelet is already at max energy
		if (spaceAvailable <= 0)
		{
			player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			return;
		}
		
		// Check if energy to replenish exceeds available space
		if (_energy > spaceAvailable)
		{
			player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			return;
		}
		
		// Track if energy was depleted before replenishing
		final boolean wasEmpty = (currentEnergy == 0);
		
		// Replenish energy
		bracelet.increaseAgathionEnergy(_energy);
		
		// Send success message
		final SystemMessage sm = new SystemMessage(SystemMessageId.ENERGY_WAS_REPLENISHED_BY_S1);
		sm.addInt(_energy);
		player.sendPacket(sm);
		
		// Update item list if energy was previously empty
		if (wasEmpty)
		{
			player.sendItemList(false);
		}
	}
}
