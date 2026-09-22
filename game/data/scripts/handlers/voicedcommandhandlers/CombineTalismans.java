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
package handlers.voicedcommandhandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.l2journey.gameserver.handler.IVoicedCommandHandler;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.network.serverpackets.ItemList;

/**
 * @author KingHanker
 */
public class CombineTalismans implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"combinetalismans"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		List<int[]> _sameIds = new ArrayList<>();
		
		for (Item item : activeChar.getInventory().getItems())
		{
			// Getting talisman
			if ((item.getMana() > 0) && item.getName().contains("Talisman"))
			{
				addTalisman(_sameIds, item.getId());
			}
		}
		int newCount = 0;
		for (int[] idCount : _sameIds)
		{
			// Item Count > 1
			if (idCount[1] > 1)
			{
				int lifeTime = 0;
				Collection<Item> existingTalismans = activeChar.getInventory().getAllItemsByItemId(idCount[0]);
				for (Item existingTalisman : existingTalismans)
				{
					// Take remaining mana of this talisman.
					lifeTime += existingTalisman.getMana();
					// Destroy all talismans from this ID.
					activeChar.getInventory().destroyItem(ItemProcessType.TRANSFER, existingTalisman, activeChar, null);
				}
				
				Item newTalisman = activeChar.addItem(ItemProcessType.COMPENSATE, idCount[0], 1, null, false);
				// Add the total mana to the new talisman.
				newTalisman.setMana(lifeTime);
				// store in DB
				newTalisman.updateDatabase();
				
				newCount++;
			}
		}
		
		if (newCount > 0)
		{
			activeChar.sendMessage("You have combined " + newCount + " talismans.");
			activeChar.sendPacket(new ItemList(activeChar, false));
		}
		else
		{
			activeChar.sendMessage("You don't have Talismans to combine!");
		}
		return true;
	}
	
	private static void addTalisman(List<int[]> sameIds, int itemId)
	{
		for (int i = 0; i < sameIds.size(); i++)
		{
			if (sameIds.get(i)[0] == itemId)
			{
				sameIds.get(i)[1] = sameIds.get(i)[1] + 1;
				return;
			}
		}
		sameIds.add(new int[]
		{
			itemId,
			1
		});
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
