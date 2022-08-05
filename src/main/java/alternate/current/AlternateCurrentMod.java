package alternate.current;

import alternate.current.command.AlternateCurrentCommand;
import alternate.current.util.profiler.ACProfiler;
import alternate.current.util.profiler.Profiler;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = AlternateCurrentMod.MOD_ID,
    name = AlternateCurrentMod.MOD_NAME,
    version = AlternateCurrentMod.MOD_VERSION
)
public class AlternateCurrentMod {

  public static final String MOD_ID = "alternate-current";
  public static final String MOD_NAME = "Alternate Current";
  public static final String MOD_VERSION = "1.4.0";
  public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
  public static final boolean DEBUG = false;

  public static boolean on = true;

  @Mod.EventHandler
  public void onInitialize(FMLInitializationEvent event) {
    if (DEBUG) {
      LOGGER.warn(String.format("You are running a DEBUG version of %s!", MOD_NAME));
    }
  }

  @Mod.EventHandler
  public void onServerStarting(FMLServerStartingEvent event) {
    MinecraftServer server = event.getServer();
    ServerCommandManager commands = (ServerCommandManager) server.getCommandManager();
    commands.registerCommand(new AlternateCurrentCommand());
  }

  public static Profiler createProfiler() {
    return DEBUG ? new ACProfiler() : Profiler.DUMMY;
  }
}
