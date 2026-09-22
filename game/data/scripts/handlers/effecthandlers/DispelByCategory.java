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

import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.conditions.Condition;
import com.l2journey.gameserver.model.effects.AbstractEffect;
import com.l2journey.gameserver.model.effects.EffectType;
import com.l2journey.gameserver.model.skill.BuffInfo;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.enums.SkillFinishType;
import com.l2journey.gameserver.model.skill.skillVariation.CancelRestrictions;
import com.l2journey.gameserver.model.stats.Formulas;

/**
 * Dispel By Category effect implementation.
 * @author DS, Adry_85, KingHanker
 */
public class DispelByCategory extends AbstractEffect
{
	private final String _slot;
	private final int _chance;
	private final int _rate;
	private final int _max;
	
	public DispelByCategory(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_slot = params.getString("slot", null);
		_chance = params.getInt("chance", 0);
		_rate = params.getInt("rate", 0);
		_max = params.getInt("max", 0);
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
		if (effected.isDead() || effected.isRaid())
		{
			return;
		}
		
		if ((Rnd.get(100) < _chance) && CancelRestrictions.canTakeCancel(effected))
		{
			final List<BuffInfo> canceled = Formulas.calcCancelStealEffects(effector, effected, skill, _slot, _rate, _max);
			
			// Store buffs with remaining time for restoration if config is active
			final List<BuffInfo> toRestore = new ArrayList<>();
			final List<Integer> remainingTimes = new ArrayList<>();
			for (BuffInfo buff : canceled)
			{
				Skill.storeBuffForRestore(buff, toRestore, remainingTimes);
			}
			
			for (BuffInfo can : canceled)
			{
				effected.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, can.getSkill());
			}
			
			// Schedule buff restoration with original remaining time
			Skill.scheduleBuffRestore(effector, effected, toRestore, remainingTimes);
		}
		else
		{
			if (effector.isPlayer())
			{
				effector.sendMessage(effected.getName() + " resisted the Cancel");
			}
			if (effected.isPlayer())
			{
				effected.sendMessage("You resisted " + effector.getName() + "'s Cancel");
			}
		}
	}
}
