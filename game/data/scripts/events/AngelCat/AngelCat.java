package events.AngelCat;

import java.util.concurrent.TimeUnit;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.model.quest.LongTimeEvent;
import com.l2journey.gameserver.network.serverpackets.PlaySound;

public final class AngelCat extends LongTimeEvent
{
	private static final int ANGEL_CAT = 4308;
	private static final int GIFT_AMOUNT = 1;
	private static final int GIFT_ID = 21726; // Angel Cat's Blessing Event
	private static final int GIFT_REUSE_HOURS = 24; // Retail 24 hours
	private static final String GIFT_REUSE_VAR_NAME = "angelCatReuse";
	
	public AngelCat()
	{
		addFirstTalkId(ANGEL_CAT);
		addStartNpc(ANGEL_CAT);
		addTalkId(ANGEL_CAT);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		// Determine player state
		final long reuse = player.getVariables().getLong(GIFT_REUSE_VAR_NAME, 0);
		if (reuse > System.currentTimeMillis())
		{
			final long remainingTime = (reuse - System.currentTimeMillis()) / 1000;
			final int hours = (int) (remainingTime / 3600);
			final int minutes = (int) ((remainingTime % 3600) / 60);
			
			final String timeMessage = String.format("Angel Cat's Blessing will be available for re-use after %d hour(s) %d minute(s).", hours, minutes);
			player.sendMessage(timeMessage);
			return "4308-1.htm";
		}
		
		if (player.getInventory().getSize() >= player.getInventoryLimit())
		{
			player.sendMessage("Your inventory is full. Please make some space and try again.");
			return "4308-2.htm";
		}
		
		player.addItem(ItemProcessType.REWARD, GIFT_ID, GIFT_AMOUNT, npc, true);
		player.getVariables().set(GIFT_REUSE_VAR_NAME, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(GIFT_REUSE_HOURS));
		player.sendPacket(new PlaySound("ItemSound.quest_finish"));
		
		return "4308-3.htm";
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "4308.htm";
	}
	
	public static void main(String[] args)
	{
		new AngelCat();
	}
}
