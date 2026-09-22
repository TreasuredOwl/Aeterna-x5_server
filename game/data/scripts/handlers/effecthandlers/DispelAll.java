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
package handlers.effecthandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.conditions.Condition;
import com.l2journey.gameserver.model.effects.AbstractEffect;
import com.l2journey.gameserver.model.effects.EffectType;
import com.l2journey.gameserver.model.skill.BuffInfo;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.skillVariation.CancelRestrictions;

/**
 * Dispel All effect implementation.
 * @author UnAfraid, KingHanker
 */
public class DispelAll extends AbstractEffect
{
	public DispelAll(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DISPEL;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill)
	{
		/**
		 * chance: Define a chance de ocorrer o efeito (25%)<br>
		 * random: gera um numero aleatorio entre 0 e 1
		 */
		double chance = 0.25;
		double random = new Random().nextDouble();
		
		if ((random < chance) && CancelRestrictions.canTakeCancel(effected))
		{
			// Store buffs with remaining time before removal if config is active
			final List<BuffInfo> removedBuffs = new ArrayList<>();
			final List<Integer> remainingTimes = new ArrayList<>();
			for (BuffInfo buff : effected.getEffectList().getEffects())
			{
				Skill.storeBuffForRestore(buff, removedBuffs, remainingTimes);
			}
			
			effected.stopAllEffects();
			
			// Schedule buff restoration with original remaining time
			Skill.scheduleBuffRestore(effector, effected, removedBuffs, remainingTimes);
		}
		else
		{
			if (effected.isPlayer())
			{
				effected.sendMessage("You resisted the Cancel");
			}
		}
	}
}
