package alternate.current.core;

import alternate.current.AlternateCurrentMod;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class AlternateCurrentSetupHook implements IFMLCallHook {
  @Override
  public void injectData(Map<String, Object> data) {

  }

  @Override
  public Void call() {
    MixinBootstrap.init();
    Mixins.addConfiguration(AlternateCurrentMod.MOD_ID + ".mixins.json");
    return null;
  }
}
