package alternate.current.wire;

import java.util.Arrays;

import alternate.current.wire.WireHandler.Directions;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/**
 * A Node represents a block in the world. It also holds a few other pieces of
 * information that speed up the calculations in the WireHandler class.
 * 
 * @author Space Walker
 */
public class Node {

	// flags that encode the Node type
	private static final int CONDUCTOR = 0b01;
	private static final int SOURCE    = 0b10;

	final LevelAccess level;
	final Node[] neighbors;

	BlockPos pos;
	BlockState state;
	boolean invalid;

	private int flags;

	Node(LevelAccess level) {
		this.level = level;
		this.neighbors = new Node[Directions.ALL.length];
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Node)) {
			return false;
		}

		Node node = (Node)obj;

		return level == node.level && pos.equals(node.pos);
	}

	@Override
	public int hashCode() {
		return pos.hashCode();
	}

	Node update(BlockPos pos, BlockState state, boolean clearNeighbors) {
		if (state.getBlock() instanceof WireBlock) {
			throw new IllegalStateException("Cannot update a regular Node to a WireNode!");
		}

		if (clearNeighbors) {
			Arrays.fill(neighbors, null);
		}

		this.pos = pos;
		this.state = state;
		this.invalid = false;

		this.flags = 0;

		if (this.level.isConductor(this.pos, this.state)) {
			this.flags |= CONDUCTOR;
		}
		if (this.state.getBlock().emitsRedstonePower()) {
			this.flags |= SOURCE;
		}

		return this;
	}

	public boolean isWire() {
		return false;
	}

	public boolean isWire(WireType type) {
		return false;
	}

	public boolean isConductor() {
		return (flags & CONDUCTOR) != 0;
	}

	public boolean isSignalSource() {
		return (flags & SOURCE) != 0;
	}

	public WireNode asWire() {
		throw new UnsupportedOperationException("Not a WireNode!");
	}
}
