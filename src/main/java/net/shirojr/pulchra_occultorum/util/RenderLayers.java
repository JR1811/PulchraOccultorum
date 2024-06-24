package net.shirojr.pulchra_occultorum.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public class RenderLayers extends RenderLayer {
    public static final RenderLayer SPOTLIGHT_LAMP_RAY =
            RenderLayer.of("spotlight_lamp_ray", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS,
                    1536, false, false,
                    RenderLayer.MultiPhaseParameters.builder()
                            .program(RenderPhase.LIGHTNING_PROGRAM)
                            .writeMaskState(RenderPhase.COLOR_MASK)
                            .transparency(RenderPhase.LIGHTNING_TRANSPARENCY)
                            //.cull(RenderPhase.DISABLE_CULLING)
                            .build(false));

    private RenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize,
                                        boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        // private constructor to avoid instantiation
    }
}
