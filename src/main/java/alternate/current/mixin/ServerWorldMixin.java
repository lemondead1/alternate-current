package alternate.current.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;

import alternate.current.interfaces.mixin.IServerWorld;
import alternate.current.redstone.WireBlock;
import alternate.current.redstone.WorldAccess;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.logging.Logger;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements IServerWorld {
	
	private final Map<WireBlock, WorldAccess> access = new HashMap<>();
	
	public ServerWorldMixin(SaveHandler saveHandler, String string, Dimension dimension, LevelInfo levelInfo, Profiler profiler, Logger logger) {
		super(saveHandler, string, dimension, levelInfo, profiler, logger);
	}
	
	@Override
	public WorldAccess getAccess(WireBlock wireBlock) {
		return access.computeIfAbsent(wireBlock, key -> new WorldAccess(wireBlock, (ServerWorld)(Object)this));
	}
}
