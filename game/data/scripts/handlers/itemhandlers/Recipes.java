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

import com.l2journey.Config;
import com.l2journey.gameserver.data.xml.RecipeData;
import com.l2journey.gameserver.handler.IItemHandler;
import com.l2journey.gameserver.model.RecipeList;
import com.l2journey.gameserver.model.actor.Playable;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Zoey76
 */
public class Recipes implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		if (!Config.IS_CRAFTING_ENABLED)
		{
			playable.sendMessage("Crafting is disabled, you cannot register this recipe.");
			return false;
		}
		
		final Player player = playable.asPlayer();
		if (player.isCrafting())
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_ALTER_YOUR_RECIPE_BOOK_WHILE_ENGAGED_IN_MANUFACTURING);
			return false;
		}
		
		final RecipeList rp = RecipeData.getInstance().getRecipeByItemId(item.getId());
		if (rp == null)
		{
			return false;
		}
		
		if (player.hasRecipeList(rp.getId()))
		{
			player.sendPacket(SystemMessageId.THAT_RECIPE_IS_ALREADY_REGISTERED);
			return false;
		}
		
		boolean canCraft = false;
		boolean recipeLevel = false;
		boolean recipeLimit = false;
		if (rp.isDwarvenRecipe())
		{
			canCraft = player.hasDwarvenCraft();
			recipeLevel = (rp.getLevel() > player.getDwarvenCraft());
			recipeLimit = (player.getDwarvenRecipeBook().size() >= player.getDwarfRecipeLimit());
		}
		else
		{
			canCraft = player.hasCommonCraft();
			recipeLevel = (rp.getLevel() > player.getCommonCraft());
			recipeLimit = (player.getCommonRecipeBook().size() >= player.getCommonRecipeLimit());
		}
		
		if (!canCraft)
		{
			player.sendPacket(SystemMessageId.THE_RECIPE_CANNOT_BE_REGISTERED_YOU_DO_NOT_HAVE_THE_ABILITY_TO_CREATE_ITEMS);
			return false;
		}
		
		if (recipeLevel)
		{
			player.sendPacket(SystemMessageId.YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
			return false;
		}
		
		if (recipeLimit)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_BE_REGISTERED);
			sm.addInt(rp.isDwarvenRecipe() ? player.getDwarfRecipeLimit() : player.getCommonRecipeLimit());
			player.sendPacket(sm);
			return false;
		}
		
		if (rp.isDwarvenRecipe())
		{
			player.registerDwarvenRecipeList(rp, true);
		}
		else
		{
			player.registerCommonRecipeList(rp, true);
		}
		
		player.destroyItem(ItemProcessType.NONE, item.getObjectId(), 1, null, false);
		final SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED);
		sm.addItemName(item);
		player.sendPacket(sm);
		return true;
	}
}
