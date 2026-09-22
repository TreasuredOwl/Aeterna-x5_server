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

import java.util.List;

import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.ai.Action;
import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.conditions.Condition;
import com.l2journey.gameserver.model.effects.AbstractEffect;
import com.l2journey.gameserver.model.effects.EffectFlag;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.stats.Formulas;

/**
 * Confuse effect implementation.
 * @author littlecrow
 */
public class Confuse extends AbstractEffect
{
	private final int _chance;
	
	public Confuse(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_chance = params.getInt("chance", 100);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		return Formulas.calcProbability(_chance, effector, effected, skill);
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.CONFUSED.getMask();
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if (!effected.isPlayer())
		{
			effected.getAI().notifyAction(Action.THINK);
		}
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill)
	{
		effected.getAI().notifyAction(Action.CONFUSED);
		
		// Getting the possible targets.
		final List<Creature> targetList = World.getInstance().getVisibleObjects(effected, Creature.class);
		
		// If there is no target, exit function.
		if (!targetList.isEmpty())
		{
			// Choosing randomly a new target.
			final Creature target = targetList.get(Rnd.get(targetList.size()));
			// Attacking the target.
			effected.setTarget(target);
			effected.getAI().setIntention(Intention.ATTACK, target);
		}
	}
}
