package io.github.cutyorca.create_more_signals.blocks.crossing_lights;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.cutyorca.create_more_signals.blocks.crossing_lights.CrossingLightsBlockEntity.LightState;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.simibubi.create.foundation.render.RenderTypes;

public class CrossingLightsRenderer extends SafeBlockEntityRenderer<CrossingLightsBlockEntity> {
	public CrossingLightsRenderer(BlockEntityRendererProvider.Context context) {
	}
	@Override
    protected void renderSafe(CrossingLightsBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
			BlockState blockState = te.getBlockState();
            LightState lightState = te.getState();
            ms.pushPose();
	    	float renderTime = AnimationTickHolder.getRenderTime(te.getLevel());

			ms.translate(0.5, 0.5, 0.5);

    		Direction facing = blockState.getValue(HorizontalDirectionalBlock.FACING);
    		float angle = -facing.toYRot();
    		ms.mulPose(Axis.YP.rotationDegrees(angle));

		    ms.translate(-0.5, -0.5, -0.5);
			ms.translate(0, 0, 1/16f);
			if (lightState == LightState.POWERED) {
			if (lightState.isRightLight(renderTime)) {
    		CachedBuffers
        	.partial(CreateMoreSignalsPartialModels.CROSSING_LIGHT_RIGHT, blockState)
        	.light(0xF000F0)
        	.translate(0.001, 0.001, 0.001)
    	    .disableDiffuse()
	        .renderInto(ms, buffer.getBuffer(RenderTypes.additive()));

    		CachedBuffers
        	.partial(CreateMoreSignalsPartialModels.CROSSING_RIGHT, blockState)
        	.light(0xF000F0)
        	.disableDiffuse()
        	.renderInto(ms, buffer.getBuffer(RenderType.cutout()));
			}

			if (lightState.isLeftLight(renderTime)) {
    		CachedBuffers
        	.partial(CreateMoreSignalsPartialModels.CROSSING_LIGHT_LEFT, blockState)
        	.light(0xF000F0)
        	.translate(0.001, 0.001, 0.001)
        	.disableDiffuse()
    	    .renderInto(ms, buffer.getBuffer(RenderTypes.additive()));

    		CachedBuffers
        	.partial(CreateMoreSignalsPartialModels.CROSSING_LEFT, blockState)
        	.light(0xF000F0)
        	.disableDiffuse()
        	.renderInto(ms, buffer.getBuffer(RenderType.cutout()));
			}

			} else {

			CachedBuffers
        	.partial(CreateMoreSignalsPartialModels.CROSSING_LIGHT_OFF, blockState)
        	.light(0xF000F0)
        	.disableDiffuse()
    	    .renderInto(ms, buffer.getBuffer(RenderType.cutout()));
			}
			ms.popPose();

	}
}
