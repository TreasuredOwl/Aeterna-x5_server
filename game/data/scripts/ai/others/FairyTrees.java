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
package ai.others;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Playable;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.skill.holders.SkillHolder;

import ai.AbstractNpcAI;

/**
 * Fairy Trees AI.
 * @author Charus
 */
public class FairyTrees extends AbstractNpcAI
{
	// NPC
	private static final int SOUL_GUARDIAN = 27189; // Soul of Tree Guardian
	
	private static final int[] MOBS =
	{
		27185, // Fairy Tree of Wind
		27186, // Fairy Tree of Star
		27187, // Fairy Tree of Twilight
		27188, // Fairy Tree of Abyss
	};
	
	// Skill
	private static final SkillHolder VENOMOUS_POISON = new SkillHolder(4243, 1); // Venomous Poison
	
	// Misc
	private static final int MIN_DISTANCE = 1500;
	
	private FairyTrees()
	{
		addKillId(MOBS);
		addSpawnId(MOBS);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.calculateDistance3D(killer) <= MIN_DISTANCE)
		{
			for (int i = 0; i < 20; i++)
			{
				final Npc guardian = addSpawn(SOUL_GUARDIAN, npc, false, 30000);
				final Playable attacker = isSummon ? killer.getSummon() : killer;
				addAttackDesire(guardian, attacker);
				if (getRandomBoolean())
				{
					guardian.setTarget(attacker);
					guardian.doCast(VENOMOUS_POISON.getSkill());
				}
			}
		}
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.setRandomWalking(false);
		npc.setImmobilized(true);
	}
	
	public static void main(String[] args)
	{
		new FairyTrees();
	}
}