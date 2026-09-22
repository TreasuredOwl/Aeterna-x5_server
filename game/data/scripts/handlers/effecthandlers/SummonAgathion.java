/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import java.util.Collections;

import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.conditions.Condition;
import com.l2journey.gameserver.model.effects.AbstractEffect;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.itemcontainer.Inventory;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.ExBrAgathionEnergyInfo;

/**
 * Summon Agathion effect implementation.
 * @author Zoey76, KingHanker
 */
public class SummonAgathion extends AbstractEffect
{
	private final int _npcId;
	
	public SummonAgathion(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
		
		if (params.isEmpty())
		{
			LOGGER.warning(getClass().getSimpleName() + ": must have parameters.");
		}
		
		_npcId = params.getInt("npcId", 0);
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
		
		// Check agathion energy on bracelet
		final Item bracelet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		if ((bracelet != null) && bracelet.isAgathionItem())
		{
			// Check if bracelet has energy
			if (bracelet.getAgathionEnergy() <= 0)
			{
				player.sendPacket(SystemMessageId.THE_ENERGY_IS_DEPLETED);
				return;
			}
			
			// Start energy consumption
			bracelet.scheduleConsumeEnergyTask();
			
			// Send energy info to client
			player.sendPacket(new ExBrAgathionEnergyInfo(Collections.singletonList(bracelet)));
		}
		
		player.setAgathionId(_npcId);
		player.broadcastUserInfo();
	}
}
