package ch.skyfy.advancedwild.test;

import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class TestToast extends SystemToast {

    public TestToast(Type type, Text title, @Nullable Text description) {
        super(type, title, description);
    }


}
