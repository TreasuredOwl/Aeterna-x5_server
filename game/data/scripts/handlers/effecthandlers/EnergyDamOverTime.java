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
import com.l2journey.gameserver.taskmanagers.ItemEnergyTaskManager;

/**
 * Energy Damage Over Time effect implementation.<br>
 * This effect consumes agathion energy over time for toggle skills.<br>
 * Used by agathion toggle skills like Blessing of Resistance - Stun.<br>
 * <br>
 * Energy consumption:<br>
 * - Initial cost: defined by the skill's {@code energyConsume} attribute<br>
 * - Ongoing cost: +1 energy per minute added to the base consumption<br>
 * <br>
 * Combined with the base agathion energy consumption (1/min), toggle skills consume 2 energy/minute total.<br>
 * The agathion requirement should be defined using {@code <player hasAgathion="true" />} condition in the skill XML.
 * @author L2Journey, KingHanker
 */
public class EnergyDamOverTime extends AbstractEffect
{
	/**
	 * Constructor for EnergyDamOverTime effect.
	 * @param attachCond the attach condition
	 * @param applyCond the apply condition
	 * @param set the effect attributes
	 * @param params the effect parameters
	 */
	public EnergyDamOverTime(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		if (!effected.isPlayer())
		{
			return false;
		}
		
		final Player player = effected.asPlayer();
		final Item agathionBracelet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		
		if ((agathionBracelet == null) || !agathionBracelet.isAgathionItem())
		{
			player.sendPacket(SystemMessageId.THE_SKILL_WAS_CANCELED_DUE_TO_INSUFFICIENT_ENERGY);
			return false;
		}
		
		// Check if there's enough energy to activate the skill (uses skill's energyConsume)
		final int initialCost = skill.getEnergyConsume();
		if ((initialCost > 0) && (agathionBracelet.getAgathionEnergy() < initialCost))
		{
			player.sendPacket(SystemMessageId.THE_SKILL_WAS_CANCELED_DUE_TO_INSUFFICIENT_ENERGY);
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill)
	{
		if (!effected.isPlayer())
		{
			return;
		}
		
		final Player player = effected.asPlayer();
		final Item agathionBracelet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		
		if ((agathionBracelet == null) || !agathionBracelet.isAgathionItem())
		{
			return;
		}
		
		// Consume initial energy cost (from skill's energyConsume)
		final int initialCost = skill.getEnergyConsume();
		if (initialCost > 0)
		{
			agathionBracelet.decreaseAgathionEnergy(false, initialCost);
		}
		
		// Increase energy consumption multiplier (+1 energy/minute for this skill)
		ItemEnergyTaskManager.getInstance().increaseEnergyMultiplier(agathionBracelet);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if (!effected.isPlayer())
		{
			return;
		}
		
		final Player player = effected.asPlayer();
		final Item agathionBracelet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		
		if ((agathionBracelet == null) || !agathionBracelet.isAgathionItem())
		{
			return;
		}
		
		// Decrease energy consumption multiplier when skill is deactivated
		ItemEnergyTaskManager.getInstance().decreaseEnergyMultiplier(agathionBracelet);
	}
}
