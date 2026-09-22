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
package ai.areas.ForgeOfTheGods;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.IXmlReader;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.GeoData;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.Spawn;
import com.l2journey.gameserver.model.Territory;
import com.l2journey.gameserver.model.actor.Npc;

/**
 * Tar Beetle zone spawn
 * @author malyelfik
 */
public class TarBeetleSpawn implements IXmlReader
{
	private final List<SpawnZone> zones = new ArrayList<>();
	private ScheduledFuture<?> spawnTask;
	private ScheduledFuture<?> shotTask;
	
	public TarBeetleSpawn()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackFile("data/scripts/ai/areas/ForgeOfTheGods/tar_beetle.xml");
		if (!zones.isEmpty())
		{
			spawnTask = ThreadPool.scheduleAtFixedRate(() -> zones.forEach(SpawnZone::refreshSpawn), 1000, 60000);
			shotTask = ThreadPool.scheduleAtFixedRate(() -> zones.forEach(SpawnZone::refreshShots), 300000, 300000);
		}
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		int i = 0;
		for (Node d = document.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if (d.getNodeName().equals("list"))
			{
				for (Node r = d.getFirstChild(); r != null; r = r.getNextSibling())
				{
					if (r.getNodeName().equals("spawnZone"))
					{
						NamedNodeMap attrs = r.getAttributes();
						final int npcCount = parseInteger(attrs, "maxNpcCount");
						final SpawnZone sp = new SpawnZone(npcCount, i);
						for (Node b = r.getFirstChild(); b != null; b = b.getNextSibling())
						{
							if (b.getNodeName().equals("zone"))
							{
								attrs = b.getAttributes();
								final int minZ = parseInteger(attrs, "minZ");
								final int maxZ = parseInteger(attrs, "maxZ");
								final Zone zone = new Zone();
								for (Node c = b.getFirstChild(); c != null; c = c.getNextSibling())
								{
									attrs = c.getAttributes();
									if (c.getNodeName().equals("point"))
									{
										final int x = parseInteger(attrs, "x");
										final int y = parseInteger(attrs, "y");
										zone.add(x, y, minZ, maxZ, 0);
									}
									else if (c.getNodeName().equals("bannedZone"))
									{
										final Zone bannedZone = new Zone();
										final int bMinZ = parseInteger(attrs, "minZ");
										final int bMaxZ = parseInteger(attrs, "maxZ");
										for (Node h = c.getFirstChild(); h != null; h = h.getNextSibling())
										{
											if (h.getNodeName().equals("point"))
											{
												attrs = h.getAttributes();
												final int x = parseInteger(attrs, "x");
												final int y = parseInteger(attrs, "y");
												bannedZone.add(x, y, bMinZ, bMaxZ, 0);
											}
										}
										zone.addBannedZone(bannedZone);
									}
								}
								sp.addZone(zone);
							}
						}
						zones.add(i++, sp);
					}
				}
			}
		}
	}
	
	public void unload()
	{
		if (spawnTask != null)
		{
			spawnTask.cancel(false);
		}
		if (shotTask != null)
		{
			shotTask.cancel(false);
		}
		zones.forEach(SpawnZone::unload);
		zones.clear();
	}
	
	public void removeBeetle(Npc npc)
	{
		zones.get(npc.getVariables().getInt("zoneIndex", 0)).removeSpawn(npc);
		npc.deleteMe();
	}
	
	private class Zone extends Territory
	{
		private List<Zone> _bannedZones;
		
		public Zone()
		{
			super(1);
		}
		
		@Override
		public Location getRandomPoint()
		{
			Location location = super.getRandomPoint();
			while ((location != null) && isInsideBannedZone(location))
			{
				location = super.getRandomPoint();
			}
			return location;
		}
		
		public void addBannedZone(Zone bZone)
		{
			if (_bannedZones == null)
			{
				_bannedZones = new ArrayList<>();
			}
			_bannedZones.add(bZone);
		}
		
		private final boolean isInsideBannedZone(Location location)
		{
			if (_bannedZones != null)
			{
				for (Zone z : _bannedZones)
				{
					if (z.isInside(location.getX(), location.getY()))
					{
						return true;
					}
				}
			}
			return false;
		}
	}
	
	private class SpawnZone
	{
		private final List<Zone> _zones = new ArrayList<>();
		private final Collection<Npc> _spawn = ConcurrentHashMap.newKeySet();
		private final int _maxNpcCount;
		private final int _index;
		
		public SpawnZone(int maxNpcCount, int index)
		{
			_maxNpcCount = maxNpcCount;
			_index = index;
		}
		
		public void addZone(Zone zone)
		{
			_zones.add(zone);
		}
		
		public void removeSpawn(Npc obj)
		{
			_spawn.remove(obj);
		}
		
		public void unload()
		{
			_spawn.forEach(Npc::deleteMe);
			_spawn.clear();
			_zones.clear();
		}
		
		public void refreshSpawn()
		{
			try
			{
				while (_spawn.size() < _maxNpcCount)
				{
					final Location location = _zones.get(Rnd.get(_zones.size())).getRandomPoint();
					if (location != null)
					{
						final Spawn spawn = new Spawn(18804);
						spawn.setHeading(Rnd.get(65535));
						spawn.setXYZ(location.getX(), location.getY(), GeoData.getInstance().getHeight(location.getX(), location.getY(), location.getZ()));
						final Npc npc = spawn.doSpawn();
						spawn.stopRespawn();
						npc.setRandomWalking(false);
						npc.setImmobilized(true);
						npc.setInvul(true);
						npc.disableCoreAI(true);
						npc.setScriptValue(5);
						npc.getVariables().set("zoneIndex", _index);
						_spawn.add(npc);
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.warning("Problem with TarBeetleSpawn: " + e.getMessage());
			}
		}
		
		public void refreshShots()
		{
			for (Npc npc : _spawn)
			{
				final int val = npc.getScriptValue();
				if (val == 5)
				{
					npc.deleteMe();
					_spawn.remove(npc);
				}
				else
				{
					npc.setScriptValue(val + 1);
				}
			}
		}
	}
}