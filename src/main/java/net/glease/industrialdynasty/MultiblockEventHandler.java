package net.glease.industrialdynasty;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.glease.industrialdynasty.multiblock.MultiblockRegistry;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

abstract class MultiblockEventHandler {
	public static final MultiblockEventHandler WORLD_EVENT_HANDLER = new MultiblockEventHandler() {
		@SubscribeEvent
		public void onWorldLoad(WorldEvent.Load e) {
			if (!e.world.isRemote)
				MultiblockRegistry.INSTANCE.loadWorld(e.world);
		}

		@SubscribeEvent
		public void onChunkLoaded(ChunkEvent.Load e) {
			if (!e.getChunk().worldObj.isRemote)
				MultiblockRegistry.INSTANCE.loadChunk(e.getChunk());
		}

		@SubscribeEvent
		public void onChunkUnloaded(ChunkEvent.Unload e) {
			if (!e.getChunk().worldObj.isRemote)
				MultiblockRegistry.INSTANCE.unloadChunk(e.getChunk());
		}
	};
	public static final MultiblockEventHandler TICK_EVENT_HANDLER = new MultiblockEventHandler() {
		@SubscribeEvent
		public void onServerTick(TickEvent.WorldTickEvent e) {
			if (e.phase == TickEvent.Phase.START)
				MultiblockRegistry.INSTANCE.tick();
		}
	};
}
