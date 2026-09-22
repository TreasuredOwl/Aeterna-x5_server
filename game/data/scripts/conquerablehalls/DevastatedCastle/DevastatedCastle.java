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
package conquerablehalls.DevastatedCastle;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.data.sql.ClanTable;
import com.l2journey.gameserver.data.xml.SkillData;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.clan.Clan;
import com.l2journey.gameserver.model.siege.clanhalls.ClanHallSiegeEngine;
import com.l2journey.gameserver.network.NpcStringId;
import com.l2journey.gameserver.network.enums.ChatType;

/**
 * Devastated Castle clan hall siege script.
 * @author BiggBoss
 */
public class DevastatedCastle extends ClanHallSiegeEngine
{
	private static final int GUSTAV = 35410;
	private static final int MIKHAIL = 35409;
	private static final int DIETRICH = 35408;
	
	private final Map<Integer, Integer> _damageToGustav = new ConcurrentHashMap<>();
	
	private DevastatedCastle()
	{
		super(DEVASTATED_CASTLE);
		addKillId(GUSTAV);
		addSpawnId(MIKHAIL, DIETRICH);
		addAttackId(GUSTAV);
	}
	
	@Override
	public void onSiegeStarts()
	{
		_damageToGustav.clear();
		super.onSiegeStarts();
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		if (npc.getId() == MIKHAIL)
		{
			npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.GLORY_TO_ADEN_THE_KINGDOM_OF_THE_LION_GLORY_TO_SIR_GUSTAV_OUR_IMMORTAL_LORD);
		}
		else if (npc.getId() == DIETRICH)
		{
			npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.SOLDIERS_OF_GUSTAV_GO_FORTH_AND_DESTROY_THE_INVADERS);
		}
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (!_hall.isInSiege())
		{
			return;
		}
		
		final Clan clan = attacker.getClan();
		if ((clan != null) && checkIsAttacker(clan))
		{
			_damageToGustav.merge(clan.getId(), damage, Integer::sum);
		}
		
		synchronized (this)
		{
			if (!npc.isCastingNow() && (npc.getCurrentHp() < (npc.getMaxHp() / 12)))
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THIS_IS_UNBELIEVABLE_HAVE_I_REALLY_BEEN_DEFEATED_I_SHALL_RETURN_AND_TAKE_YOUR_HEAD);
				npc.getAI().setIntention(Intention.CAST, SkillData.getInstance().getSkill(4235, 1), npc);
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (!_hall.isInSiege())
		{
			return;
		}
		
		_missionAccomplished = true;
		cancelSiegeTask();
		endSiege();
	}
	
	@Override
	public Clan getWinner()
	{
		if (_damageToGustav.isEmpty())
		{
			return null;
		}
		
		final int clanId = Collections.max(_damageToGustav.entrySet(), Map.Entry.comparingByValue()).getKey();
		return ClanTable.getInstance().getClan(clanId);
	}
	
	public static void main(String[] args)
	{
		new DevastatedCastle();
	}
}