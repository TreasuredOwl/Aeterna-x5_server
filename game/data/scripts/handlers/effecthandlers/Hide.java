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

import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.conditions.Condition;
import com.l2journey.gameserver.model.effects.AbstractEffect;
import com.l2journey.gameserver.model.skill.Skill;

/**
 * Hide effect implementation.
 * @author ZaKaX, nBd
 */
public class Hide extends AbstractEffect
{
	public Hide(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if (effected.isPlayer())
		{
			final Player player = effected.asPlayer();
			if (!player.inObserverMode())
			{
				player.setInvisible(false);
			}
		}
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill)
	{
		if (effected.isPlayer())
		{
			final Player player = effected.asPlayer();
			player.setInvisible(true);
			
			if ((player.getAI().getNextIntention() != null) && (player.getAI().getNextIntention().getIntention() == Intention.ATTACK))
			{
				player.getAI().setIntention(Intention.IDLE);
			}
			
			World.getInstance().forEachVisibleObject(player, Creature.class, target ->
			{
				if ((target != null) && (target.getTarget() == player))
				{
					target.setTarget(null);
					target.abortAttack();
					target.abortCast();
					target.getAI().setIntention(Intention.IDLE);
				}
			});
		}
	}
}