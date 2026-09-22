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
package handlers.itemhandlers;

import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.GeoData;
import com.l2journey.gameserver.handler.IItemHandler;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Playable;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.zone.ZoneId;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.Dice;
import com.l2journey.gameserver.network.serverpackets.SystemMessage;
import com.l2journey.gameserver.util.Broadcast;
import com.l2journey.gameserver.util.LocationUtil;

public class RollingDice implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final Player player = playable.asPlayer();
		final int itemId = item.getId();
		if (player.isInOlympiadMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
			return false;
		}
		
		final int number = rollDice(player);
		if (number == 0)
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER);
			return false;
		}
		
		// Mobius: Retail dice position land calculation.
		final double angle = LocationUtil.convertHeadingToDegree(player.getHeading());
		final double radian = Math.toRadians(angle);
		final double course = Math.toRadians(180);
		final int x1 = (int) (Math.cos(Math.PI + radian + course) * 40);
		final int y1 = (int) (Math.sin(Math.PI + radian + course) * 40);
		final int x = player.getX() + x1;
		final int y = player.getY() + y1;
		final int z = player.getZ();
		final Location destination = GeoData.getInstance().moveCheck(player.getX(), player.getY(), player.getZ(), x, y, z, player.getInstanceId());
		Broadcast.toSelfAndKnownPlayers(player, new Dice(player.getObjectId(), itemId, number, destination.getX(), destination.getY(), destination.getZ()));
		
		final SystemMessage sm = new SystemMessage(SystemMessageId.C1_HAS_ROLLED_A_S2);
		sm.addString(player.getName());
		sm.addInt(number);
		
		player.sendPacket(sm);
		if (player.isInsideZone(ZoneId.PEACE))
		{
			Broadcast.toKnownPlayers(player, sm);
		}
		else if (player.isInParty()) // TODO: Verify this!
		{
			player.getParty().broadcastToPartyMembers(player, sm);
		}
		return true;
	}
	
	private int rollDice(Player player)
	{
		// Check if the dice is ready
		if (!player.getClient().getFloodProtectors().canRollDice())
		{
			return 0;
		}
		return Rnd.get(1, 6);
	}
}
