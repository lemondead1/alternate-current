package alternate.current.command;

import alternate.current.AlternateCurrentMod;
import alternate.current.util.profiler.ProfilerResults;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlternateCurrentCommand extends CommandBase {

	@Override
	public String getName() {
		return "alternatecurrent";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsage(ICommandSender source) {
		return AlternateCurrentMod.DEBUG ? "/alternatecurrent [on/off/resetProfiler]" : "/alternatecurrent [on/off]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender source, String[] args) throws CommandException {
		switch (args.length) {
			case 0:
				query(source);
				return;
			case 1:
				String arg = args[0];

				switch (arg) {
					case "on":
						set(source, true);
						return;
					case "off":
						set(source, false);
						return;
					case "resetProfiler":
						if (AlternateCurrentMod.DEBUG) {
							notifyCommandListener(source, this, "profiler results have been cleared!");

							ProfilerResults.log();
							ProfilerResults.clear();

							return;
						}
				}

				break;
		}

		throw new WrongUsageException(getUsage(source));
	}

	private void query(ICommandSender source) {
		String state = AlternateCurrentMod.on ? "enabled" : "disabled";
		source.sendMessage(new TextComponentString(String.format("Alternate Current is currently %s", state)));
	}

	private void set(ICommandSender source, boolean on) {
		AlternateCurrentMod.on = on;

		String state = AlternateCurrentMod.on ? "enabled" : "disabled";
		notifyCommandListener(source, this, String.format("Alternate Current has been %s!", state));
	}
}
