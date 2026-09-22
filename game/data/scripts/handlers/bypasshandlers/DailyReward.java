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
 */
package handlers.bypasshandlers;

import com.l2journey.Config;
import com.l2journey.gameserver.cache.HtmCache;
import com.l2journey.gameserver.data.holders.DailyRewardHolder;
import com.l2journey.gameserver.handler.CommunityBoardHandler;
import com.l2journey.gameserver.handler.IBypassHandler;
import com.l2journey.gameserver.managers.DailyRewardManager;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;

/**
 * Bypass handler for the Daily Reward system.
 * @author L2Journey
 */
public class DailyReward implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"dailyreward",
		"dailyreward_show",
		"dailyreward_claim"
	};
	
	private static final String HTML_FOLDER = "data/html/mods/DailyReward/";
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		if (!Config.DAILY_REWARD_ENABLED)
		{
			player.sendMessage("Daily Reward system is disabled.");
			return false;
		}
		
		final String[] parts = command.split("_");
		final String action = parts.length > 1 ? parts[1] : "show";
		
		switch (action)
		{
			case "claim":
				claimReward(player);
				break;
			case "show":
			default:
				showMainWindow(player);
				break;
		}
		
		return true;
	}
	
	/**
	 * Shows the main daily reward window.
	 * @param player the player
	 */
	public void showMainWindow(Player player)
	{
		final DailyRewardManager manager = DailyRewardManager.getInstance();
		final int currentDay = manager.getCurrentDay(player);
		final int totalDays = manager.getTotalDays();
		final boolean canClaim = manager.canClaimReward(player);
		
		String html = HtmCache.getInstance().getHtm(player, HTML_FOLDER + "dailyreward.htm");
		if (html == null)
		{
			player.sendMessage("Daily Reward HTML not found.");
			return;
		}
		
		// Replace basic placeholders
		html = html.replace("%currentDay%", String.valueOf(currentDay));
		html = html.replace("%totalDays%", String.valueOf(totalDays));
		html = html.replace("%resetTime%", String.format("%02d:%02d", 6, 30));
		
		// Build reward grid (visual table with all days)
		html = html.replace("%rewardList%", buildRewardGrid(player));
		
		// Build status area
		String statusArea;
		if (canClaim)
		{
			statusArea = "<table width=750><tr><td align=center>" + "<font color=\"00FF00\">Reward Available!</font><br1>" + "<button value=\"Claim Reward\" action=\"bypass -h dailyreward_claim\" width=120 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"/>" + "</td></tr></table>";
		}
		else
		{
			final long timeRemaining = manager.getTimeUntilNextReward(player);
			statusArea = "<table width=750><tr><td align=center>" + "<font color=\"FF6600\">Already claimed today!</font><br1>" + "<font color=\"999999\">Next reward in: " + DailyRewardManager.formatTime(timeRemaining) + "</font>" + "</td></tr></table>";
		}
		html = html.replace("%statusArea%", statusArea);
		
		CommunityBoardHandler.separateAndSend(html, player);
	}
	
	/**
	 * Claims the daily reward for the player.
	 * @param player the player
	 */
	private void claimReward(Player player)
	{
		final DailyRewardManager manager = DailyRewardManager.getInstance();
		
		if (!manager.canClaimReward(player))
		{
			player.sendMessage("You have already claimed your daily reward today.");
			showMainWindow(player);
			return;
		}
		
		if (manager.claimReward(player))
		{
			player.sendMessage("Daily reward claimed successfully!");
		}
		
		// Reload the main window to show updated status
		showMainWindow(player);
	}
	
	/**
	 * Builds the visual reward grid HTML showing all days.
	 * @param player the player
	 * @return HTML string with the reward grid
	 */
	private String buildRewardGrid(Player player)
	{
		final DailyRewardManager manager = DailyRewardManager.getInstance();
		final int currentDay = manager.getCurrentDay(player);
		final int totalDays = manager.getTotalDays();
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<table width=750 cellspacing=2 cellpadding=2>");
		
		final int COLUMNS = 7;
		int dayCounter = 1;
		
		while (dayCounter <= totalDays)
		{
			// Row for day numbers
			sb.append("<tr>");
			for (int col = 0; (col < COLUMNS) && ((dayCounter + col) <= totalDays); col++)
			{
				final int day = dayCounter + col;
				sb.append("<td width=75 align=center valign=bottom>");
				sb.append("<font color=\"LEVEL\">").append(day).append("</font>");
				sb.append("</td>");
			}
			sb.append("</tr>");
			
			// Row for icons/status
			sb.append("<tr>");
			for (int col = 0; (col < COLUMNS) && ((dayCounter + col) <= totalDays); col++)
			{
				final int day = dayCounter + col;
				final DailyRewardHolder reward = manager.getReward(day);
				
				sb.append("<td width=75 height=40 align=center valign=top>");
				
				// O 'currentDay' representa o PROXIMO dia a ser resgatado.
				// Tudo abaixo dele ja foi entregue; o proprio currentDay e os subsequentes ainda nao.
				if (day < currentDay)
				{
					// Already claimed - show claimed icon
					sb.append("<img src=\"branchSys.PremiumItemBtn_disable\" width=32 height=32>");
				}
				else
				{
					// Available or future - show item icon
					sb.append("<img src=\"").append(getRewardIcon(reward)).append("\" width=32 height=32>");
				}
				
				sb.append("</td>");
			}
			sb.append("</tr>");
			
			dayCounter += COLUMNS;
		}
		
		sb.append("</table>");
		return sb.toString();
	}
	
	/**
	 * Gets the display icon for a reward.
	 * @param reward the reward holder
	 * @return icon path
	 */
	private String getRewardIcon(DailyRewardHolder reward)
	{
		if (reward == null)
		{
			return "icon.NOIMAGE";
		}
		
		if (!reward.getIcon().isEmpty())
		{
			return reward.getIcon();
		}
		
		// Use first item's icon
		final DailyRewardManager manager = DailyRewardManager.getInstance();
		for (Integer itemId : reward.getRewards().keySet())
		{
			return manager.getItemIcon(itemId);
		}
		
		return "icon.NOIMAGE";
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
