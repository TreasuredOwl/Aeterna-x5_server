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

import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.conditions.Condition;
import com.l2journey.gameserver.model.effects.AbstractEffect;
import com.l2journey.gameserver.model.effects.EffectType;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.skillVariation.ServitorShareConditions;
import com.l2journey.gameserver.model.stats.Stat;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.SystemMessage;

/**
 * Mana Heal By Level effect implementation.
 * @author UnAfraid, KingHanker
 */
public class ManaHealByLevel extends AbstractEffect
{
	private final double _power;
	
	public ManaHealByLevel(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MANAHEAL_BY_LEVEL;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill)
	{
		if ((effected == null) || effected.isDead() || effected.isDoor() || effected.isInvul())
		{
			return;
		}
		
		double amount = _power;
		
		// Recharged MP influenced by difference between target level and skill level
		amount = effected.calcStat(Stat.MANA_CHARGE, amount, null, null);
		if (effected.getLevel() > skill.getMagicLevel())
		{
			final int levelDiff = effected.getLevel() - skill.getMagicLevel();
			// Apply level difference penalty
			switch (levelDiff)
			{
				case 6:
					amount *= 0.9;
					break;
				case 7:
					amount *= 0.8;
					break;
				case 8:
					amount *= 0.7;
					break;
				case 9:
					amount *= 0.6;
					break;
				case 10:
					amount *= 0.5;
					break;
				case 11:
					amount *= 0.4;
					break;
				case 12:
					amount *= 0.3;
					break;
				case 13:
					amount *= 0.2;
					break;
				case 14:
					amount *= 0.1;
					break;
				default:
					if (levelDiff >= 15)
					{
						amount = 0;
					}
					break;
			}
		}
		
		final double maxRecoverableMp = ServitorShareConditions.getMaxServitorRecoverableMp(effected);
		
		amount = Math.max(Math.min(amount, maxRecoverableMp - effected.getCurrentMp()), 0);
		
		if (amount != 0)
		{
			effected.setCurrentMp(amount + effected.getCurrentMp());
		}
		
		// System message
		final SystemMessage sm = new SystemMessage(effector.getObjectId() != effected.getObjectId() ? SystemMessageId.S2_MP_HAS_BEEN_RESTORED_BY_C1 : SystemMessageId.S1_MP_HAS_BEEN_RESTORED);
		if (effector.getObjectId() != effected.getObjectId())
		{
			sm.addString(effector.getName());
		}
		sm.addInt((int) amount);
		effected.sendPacket(sm);
	}
}
