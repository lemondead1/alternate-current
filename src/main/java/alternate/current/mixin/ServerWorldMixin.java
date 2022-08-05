package alternate.current.mixin;

import alternate.current.interfaces.mixin.IServerWorld;
import alternate.current.wire.WireHandler;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldServer.class)
public class ServerWorldMixin implements IServerWorld {

	private final WireHandler wireHandler = new WireHandler((WorldServer) (Object)this);

	@Override
	public WireHandler getWireHandler() {
		return wireHandler;
	}
}
