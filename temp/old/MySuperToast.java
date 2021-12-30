package ch.skyfy.advancedwild.test;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MySuperToast implements Toast {
    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        AdvancementDisplay advancementDisplay = this.advancement.getDisplay();
        manager.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());


        List<OrderedText> list = manager.getClient().textRenderer.wrapLines(Text.of("Super Toast lets go"), 125);
        list.add(manager.getClient().textRenderer.wrapLines(Text.of("Super Toast lets go 222"), 125).get(0));

        manager.getClient().textRenderer.draw(matrices, (OrderedText)list.get(0), 30.0F, 18.0F, -1);

        int k = MathHelper.floor(MathHelper.clamp((float)(startTime - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
        int var10000 = this.getHeight() / 2;
        int var10001 = list.size();
        int l = var10000 - var10001 * 9 / 2;

        for(Iterator var12 = list.iterator(); var12.hasNext(); l += 9) {
            OrderedText orderedText = (OrderedText)var12.next();
            manager.getClient().textRenderer.draw(matrices, orderedText, 30.0F, (float)l, 16777215 | k);
            Objects.requireNonNull(manager.getClient().textRenderer);
        }

        return Visibility.SHOW;
    }

    @Override
    public Object getType() {
        return Toast.super.getType();
    }

    @Override
    public int getWidth() {
        return 256;
    }

    @Override
    public int getHeight() {
        return 64;
    }
}
