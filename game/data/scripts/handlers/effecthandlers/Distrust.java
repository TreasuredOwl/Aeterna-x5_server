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

import java.util.List;

import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.ai.AttackableAI;
import com.l2journey.gameserver.ai.DistrustAI;
import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Attackable;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.instance.Chest;
import com.l2journey.gameserver.model.actor.instance.Monster;
import com.l2journey.gameserver.model.conditions.Condition;
import com.l2journey.gameserver.model.effects.AbstractEffect;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.network.SystemMessageId;

/**
 * @author Naker
 */
public class Distrust extends AbstractEffect
{
	public Distrust(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill)
	{
		if (effected.isDead() || !effected.isMonster() || (effected instanceof Chest) || effected.isRaid() || effected.isRaidMinion())
		{
			effector.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Monster targetMonster = effected.asMonster();
		final List<Monster> targets = World.getInstance().getVisibleObjectsInRange(targetMonster, Monster.class, 1100, m -> (m != targetMonster) && !m.isDead() && m.isAttackable() && !(m instanceof Chest) && !m.isRaid() && !m.isRaidMinion());
		if (targets.isEmpty())
		{
			return;
		}
		
		final Monster newTarget = targets.get(Rnd.get(targets.size()));
		if ((newTarget == null) || (newTarget == effected))
		{
			return;
		}
		
		targetMonster.setAI(new DistrustAI(effected.asAttackable(), newTarget));
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		final Attackable monster = effected.asAttackable();
		if ((monster == null) || monster.isDead())
		{
			return;
		}
		
		monster.setAI(new AttackableAI(monster));
		monster.setTarget(null);
		monster.setWalking();
		monster.getAI().setIntention(Intention.ACTIVE);
	}
}
