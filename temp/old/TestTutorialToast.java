package ch.skyfy.advancedwild.test;

import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class TestTutorialToast extends TutorialToast {
    public TestTutorialToast(Type type, Text title, @Nullable Text description, boolean hasProgressBar) {
        super(type, title, description, hasProgressBar);
    }
}
