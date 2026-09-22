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
package ai.areas.Hellbound;

import java.util.logging.Logger;

import com.l2journey.Config;
import com.l2journey.gameserver.handler.AdminCommandHandler;
import com.l2journey.gameserver.handler.IAdminCommandHandler;
import com.l2journey.gameserver.handler.IVoicedCommandHandler;
import com.l2journey.gameserver.handler.VoicedCommandHandler;

import ai.areas.Hellbound.AI.Amaskari;
import ai.areas.Hellbound.AI.Chimeras;
import ai.areas.Hellbound.AI.DemonPrince;
import ai.areas.Hellbound.AI.HellboundCore;
import ai.areas.Hellbound.AI.Keltas;
import ai.areas.Hellbound.AI.NaiaLock;
import ai.areas.Hellbound.AI.OutpostCaptain;
import ai.areas.Hellbound.AI.Ranku;
import ai.areas.Hellbound.AI.Slaves;
import ai.areas.Hellbound.AI.Typhoon;
import ai.areas.Hellbound.AI.NPC.Bernarde.Bernarde;
import ai.areas.Hellbound.AI.NPC.Budenka.Budenka;
import ai.areas.Hellbound.AI.NPC.Buron.Buron;
import ai.areas.Hellbound.AI.NPC.Deltuva.Deltuva;
import ai.areas.Hellbound.AI.NPC.Falk.Falk;
import ai.areas.Hellbound.AI.NPC.Hude.Hude;
import ai.areas.Hellbound.AI.NPC.Jude.Jude;
import ai.areas.Hellbound.AI.NPC.Kanaf.Kanaf;
import ai.areas.Hellbound.AI.NPC.Kief.Kief;
import ai.areas.Hellbound.AI.NPC.Natives.Natives;
import ai.areas.Hellbound.AI.NPC.Quarry.Quarry;
import ai.areas.Hellbound.AI.NPC.Shadai.Shadai;
import ai.areas.Hellbound.AI.NPC.Solomon.Solomon;
import ai.areas.Hellbound.AI.NPC.Warpgate.Warpgate;
import ai.areas.Hellbound.AI.Zones.AnomicFoundry.AnomicFoundry;
import ai.areas.Hellbound.AI.Zones.BaseTower.BaseTower;
import ai.areas.Hellbound.AI.Zones.TowerOfInfinitum.TowerOfInfinitum;
import ai.areas.Hellbound.AI.Zones.TowerOfNaia.TowerOfNaia;
import ai.areas.Hellbound.AI.Zones.TullyWorkshop.TullyWorkshop;
import ai.areas.Hellbound.Instances.DemonPrinceFloor.DemonPrinceFloor;
import ai.areas.Hellbound.Instances.RankuFloor.RankuFloor;
import ai.areas.Hellbound.Instances.UrbanArea.UrbanArea;
import handlers.admincommandhandlers.AdminHellbound;
import handlers.voicedcommandhandlers.Hellbound;
import quests.Q00130_PathToHellbound.Q00130_PathToHellbound;
import quests.Q00133_ThatsBloodyHot.Q00133_ThatsBloodyHot;

/**
 * Hellbound class-loader.
 * @author Zoey76
 */
public class HellboundLoader
{
	private static final Logger LOGGER = Logger.getLogger(HellboundLoader.class.getName());
	
	private static final Class<?>[] SCRIPTS =
	{
		// Commands
		AdminHellbound.class,
		Hellbound.class,
		// AIs
		Amaskari.class,
		Chimeras.class,
		DemonPrince.class,
		HellboundCore.class,
		Keltas.class,
		NaiaLock.class,
		OutpostCaptain.class,
		Ranku.class,
		Slaves.class,
		Typhoon.class,
		// NPCs
		Bernarde.class,
		Budenka.class,
		Buron.class,
		Deltuva.class,
		Falk.class,
		Hude.class,
		Jude.class,
		Kanaf.class,
		Kief.class,
		Natives.class,
		Quarry.class,
		Shadai.class,
		Solomon.class,
		Warpgate.class,
		// Zones
		AnomicFoundry.class,
		BaseTower.class,
		TowerOfInfinitum.class,
		TowerOfNaia.class,
		TullyWorkshop.class,
		// Instances
		DemonPrinceFloor.class,
		UrbanArea.class,
		RankuFloor.class,
		// Quests
		Q00130_PathToHellbound.class,
		Q00133_ThatsBloodyHot.class,
	};
	
	public static void main(String[] args)
	{
		LOGGER.info(HellboundLoader.class.getSimpleName() + ": Loading Hellbound related scripts:");
		// Data
		HellboundPointData.getInstance();
		HellboundSpawns.getInstance();
		// Engine
		HellboundEngine.getInstance();
		for (Class<?> script : SCRIPTS)
		{
			try
			{
				final Object instance = script.getDeclaredConstructor().newInstance();
				if (instance instanceof IAdminCommandHandler)
				{
					AdminCommandHandler.getInstance().registerHandler((IAdminCommandHandler) instance);
				}
				else if (Config.HELLBOUND_STATUS && (instance instanceof IVoicedCommandHandler))
				{
					VoicedCommandHandler.getInstance().registerHandler((IVoicedCommandHandler) instance);
				}
			}
			catch (Exception e)
			{
				LOGGER.severe(HellboundLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":" + e.getMessage());
			}
		}
	}
}
