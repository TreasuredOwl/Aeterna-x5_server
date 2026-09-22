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

import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.conditions.Condition;
import com.l2journey.gameserver.model.effects.AbstractEffect;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.itemcontainer.Inventory;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.taskmanagers.ItemEnergyTaskManager;

/**
 * Unsummon Agathion effect implementation.
 * @author Zoey76, KingHanker
 */
public class UnsummonAgathion extends AbstractEffect
{
	public UnsummonAgathion(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill)
	{
		final Player player = effector.asPlayer();
		if (player != null)
		{
			// Stop energy consumption on bracelet
			final Item bracelet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
			if ((bracelet != null) && bracelet.isAgathionItem())
			{
				bracelet.stopConsumeEnergyTask();
				ItemEnergyTaskManager.getInstance().remove(bracelet);
			}
			
			player.setAgathionId(0);
			player.broadcastUserInfo();
		}
	}
}
