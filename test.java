import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import java.lang.reflect.Method;
public class Test {
    public static void main(String[] args) {
        for (Method m : ModelLoadingPlugin.PluginContext.class.getMethods()) {
            System.out.println(m.getName() + " -> " + m.getReturnType().getName());
        }
    }
}
