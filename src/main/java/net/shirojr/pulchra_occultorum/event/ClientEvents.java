package net.shirojr.pulchra_occultorum.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import net.shirojr.pulchra_occultorum.network.packet.UnicycleMovementPacket;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

import java.util.Arrays;
import java.util.Optional;

public class ClientEvents {
    private static final UnicycleEntity.DirectionInput[] directionInputs = new UnicycleEntity.DirectionInput[3];
    private static boolean wasRightPressed = false, wasLeftPressed = false, wasJumpPressed = false, shouldSendPacket = false;

    static {
        ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::handleTick);
        HudRenderCallback.EVENT.register(ClientEvents::handleHud);

    }

    private static void handleTick(MinecraftClient client) {
        if (!(client.world instanceof ClientWorld)) return;

        if (client.options.leftKey.isPressed()) {
            if (!wasLeftPressed) {
                // on press
                directionInputs[0] = UnicycleEntity.DirectionInput.LEFT;
                wasLeftPressed = true;
                shouldSendPacket = true;
            }
        } else {
            if (wasLeftPressed) {
                // on release
                directionInputs[0] = null;
                wasLeftPressed = false;
                shouldSendPacket = true;
            }
        }

        if (client.options.rightKey.isPressed()) {
            if (!wasRightPressed) {
                // on press
                directionInputs[1] = UnicycleEntity.DirectionInput.RIGHT;
                wasRightPressed = true;
                shouldSendPacket = true;
            }
        } else {
            if (wasRightPressed) {
                // on release
                directionInputs[1] = null;
                wasRightPressed = false;
                shouldSendPacket = true;
            }
        }

        if (client.options.jumpKey.isPressed()) {
            if (!wasJumpPressed) {
                // on press
                directionInputs[2] = UnicycleEntity.DirectionInput.JUMP;
                wasJumpPressed = true;
                shouldSendPacket = true;
            }
        } else {
            if (wasJumpPressed) {
                // on release
                directionInputs[2] = null;
                wasJumpPressed = false;
                shouldSendPacket = true;
            }
        }


        if (shouldSendPacket) {
            if (client.player instanceof ClientPlayerEntity player && player.getVehicle() instanceof UnicycleEntity unicycleEntity) {
                LoggerUtil.devLogger(Arrays.toString(directionInputs));
                if (unicycleEntity.isLogicalSideForUpdatingMovement()) {
                    unicycleEntity.setDirectionInputs(directionInputs);
                } else {
                    ClientPlayNetworking.send(new UnicycleMovementPacket(
                            Optional.ofNullable(directionInputs[0]),
                            Optional.ofNullable(directionInputs[1]),
                            Optional.ofNullable(directionInputs[2]))
                    );
                }
            }
            shouldSendPacket = false;
        }
    }

    private static void handleHud(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.player instanceof ClientPlayerEntity player)) return;
        if (!(player.getVehicle() instanceof UnicycleEntity unicycleEntity)) return;
        unicycleEntity.setLeftImportantState((MathHelper.sin(unicycleEntity.age * UnicycleEntity.INTERVAL_SPEED)));

        Identifier pedalIdentifier = PulchraOccultorum.getId("textures/gui/unicycle_pedals.png");
        float alphaLeft = MathHelper.clamp(unicycleEntity.getLeftImportantState(), 0, 1);
        float alphaRight = MathHelper.abs(MathHelper.clamp(unicycleEntity.getLeftImportantState(), -1, 0));

        int imageSize = 32, pedalsSize = 11;
        int spaceBetween = 10;
        int x = drawContext.getScaledWindowWidth() / 2 - pedalsSize, y = drawContext.getScaledWindowHeight() / 2 - pedalsSize;
        int xLeft = (x - spaceBetween), xRight = (x + pedalsSize + spaceBetween);
        int z = 2;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, pedalIdentifier);

        drawContext.setShaderColor(1.0f, 1.0f, 1.0f, alphaLeft);
        drawContext.drawTexture(pedalIdentifier, xLeft, y, z, 0, 0, pedalsSize, pedalsSize, imageSize, imageSize);
        drawContext.setShaderColor(1.0f, 1.0f, 1.0f, alphaRight);
        drawContext.drawTexture(pedalIdentifier, xRight, y, z, pedalsSize, 0, pedalsSize, pedalsSize, imageSize, imageSize);
        drawContext.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized Client Events");
    }
}
