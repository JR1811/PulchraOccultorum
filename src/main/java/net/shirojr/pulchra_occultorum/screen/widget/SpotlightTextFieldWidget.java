package net.shirojr.pulchra_occultorum.screen.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SpotlightTextFieldWidget extends TextFieldWidget {
    public SpotlightTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            this.setFocused(false);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void setFocused(boolean focused) {
        boolean prevFocusState = this.isFocused();
        super.setFocused(focused);
        if (prevFocusState != this.isFocused() && !focused) {
            if (this.changedListener != null) this.changedListener.accept(this.getText());
        }
    }
}
