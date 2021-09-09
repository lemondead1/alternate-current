package alternate.current.redstone;

import alternate.current.interfaces.mixin.IBlock;
import alternate.current.interfaces.mixin.IBlockStorage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.BlockStorage;
import net.minecraft.world.chunk.Chunk;

public class WorldAccess {
	
	private static final int Y_MIN = 0;
	private static final int Y_MAX = 256;
	
	private final WireBlock wireBlock;
	private final ServerWorld world;
	private final WireHandler wireHandler;
	
	public WorldAccess(WireBlock wireBlock, ServerWorld world) {
		this.wireBlock = wireBlock;
		this.world = world;
		this.wireHandler = new WireHandler(this.wireBlock, this);
	}
	
	public WireHandler getWireHandler() {
		return wireHandler;
	}
	
	/**
	 * A slightly optimized version of World.getBlockState.
	 */
	public BlockState getBlockState(BlockPos pos) {
		int y = pos.getY();
		
		if (y < Y_MIN || y >= Y_MAX) {
			return Blocks.AIR.getDefaultState();
		}
		
		int x = pos.getX();
		int z = pos.getZ();
		
		Chunk chunk = world.getChunk(x >> 4, z >> 4);
		BlockStorage storage = chunk.getBlockStorage()[y >> 4];
		
		if (storage == null) {
			return Blocks.AIR.getDefaultState();
		}
		
		return storage.getBlockState(x & 15, y & 15, z & 15);
	}
	
	/**
	 * An optimized version of World.setBlockState. Since this method is
	 * only used to update redstone wire block states, lighting checks,
	 * height map updates, and block entity updates.
	 */
	public boolean setWireState(BlockPos pos, BlockState state) {
		int y = pos.getY();
		
		if (y < Y_MIN || y >= Y_MAX) {
			return false;
		}
		
		int x = pos.getX();
		int z = pos.getZ();
		
		Chunk chunk = world.getChunk(x >> 4, z >> 4);
		BlockStorage storage = chunk.getBlockStorage()[y >> 4];
		
		if (storage == null) {
			return false;
		}
		
		x &= 15;
		y &= 15;
		z &= 15;
		
		BlockState prevState = storage.getBlockState(x, y, z);
		
		if (state == prevState) {
			return false;
		}
		
		storage.method_1424(x, y, z, state);
		
		// notify clients of the BlockState change
		world.onBlockUpdate(pos);
		// mark the chunk for saving
		chunk.setModified(true);
		
		return true;
	}
	
	public WireNode getWire(BlockPos pos, boolean create, boolean update) {
		int y = pos.getY();
		
		if (y < Y_MIN || y >= Y_MAX) {
			return null;
		}
		
		int x = pos.getX();
		int z = pos.getZ();
		
		Chunk chunk = world.getChunk(x >> 4, z >> 4);
		BlockStorage storage = chunk.getBlockStorage()[y >> 4];
		
		if (storage == null) {
			return null;
		}
		
		x &= 15;
		y &= 15;
		z &= 15;
		
		WireNode wire = ((IBlockStorage)storage).getWire(x, y, z);
		
		if (wire == null || !wire.isOf(wireBlock)) {
			wire = null;
			
			if (create) {
				BlockState state = storage.getBlockState(x, y, z);
				
				if (wireBlock.isOf(state)) {
					wire = new WireNode(wireBlock, this, pos, state);
					((IBlockStorage)storage).setWire(x, y, z, wire);
					
					if (update) {
						wire.connections.update();
					}
				}
			}
		}
		
		return wire;
	}
	
	public boolean placeWire(WireNode wire) {
		if (setWire(wire.pos, wire)) {
			wire.shouldBreak = false;
			wire.removed = false;
			
			return true;
		}
		
		return false;
	}
	
	public boolean removeWire(WireNode wire) {
		if (setWire(wire.pos, null)) {
			wire.removed = true;
			
			return true;
		}
		
		return false;
	}
	
	private boolean setWire(BlockPos pos, WireNode wire) {
		int y = pos.getY();
		
		if (y < Y_MIN || y >= Y_MAX) {
			return false;
		}
		
		int x = pos.getX();
		int z = pos.getZ();
		
		Chunk chunk = world.getChunk(x >> 4, z >> 4);
		BlockStorage storage = chunk.getBlockStorage()[y >> 4];
		
		if (storage == null) {
			return false;
		}
		
		((IBlockStorage)storage).setWire(x & 15, y & 15, z & 15, wire);
		
		return true;
	}
	
	// move this to the WireBlock class eventually
	// it is only used to break wire blocks anyway...
	public boolean breakBlock(BlockPos pos, BlockState state) {
		state.getBlock().dropAsItem(world, pos, state, 0);
		return world.setAir(pos);
	}
	
	public void updateNeighborBlock(BlockPos pos, Block fromBlock) {
		BlockState state = getBlockState(pos);
		state.getBlock().neighborUpdate(world, pos, state, fromBlock);
	}
	
	public boolean isSolidBlock(BlockPos pos) {
		return getBlockState(pos).getBlock().isFullCube();
	}
	
	public boolean emitsWeakPowerTo(BlockPos pos, BlockState state, Direction dir) {
		return ((IBlock)state.getBlock()).emitsWeakPowerTo(world, pos, state, dir);
	}
	
	public boolean emitsStrongPowerTo(BlockPos pos, BlockState state, Direction dir) {
		return ((IBlock)state.getBlock()).emitsStrongPowerTo(world, pos, state, dir);
	}
	
	public int getWeakPowerFrom(BlockPos pos, BlockState state, Direction dir) {
		return state.getBlock().getWeakRedstonePower(world, pos, state, dir);
	}
	
	public int getStrongPowerFrom(BlockPos pos, BlockState state, Direction dir) {
		return state.getBlock().getStrongRedstonePower(world, pos, state, dir);
	}
	
	public boolean shouldBreak(BlockPos pos, BlockState state) {
		return !state.getBlock().canBePlacedAtPos(world, pos);
	}
}
