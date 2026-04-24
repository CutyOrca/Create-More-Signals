package io.github.cutyorca.create_more_signals.blocks.three_color_light_signal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsPartialModels;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.data.Iterate;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.render.RenderTypes;

public class ThreeColorLightSignalRenderer extends SafeBlockEntityRenderer<ThreeColorLightSignalBlockEntity> {
	public ThreeColorLightSignalRenderer(BlockEntityRendererProvider.Context context) {
	}
	@Override
    protected void renderSafe(ThreeColorLightSignalBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		ms.pushPose();
		float renderTime = AnimationTickHolder.getRenderTime(te.getLevel());
		BlockState blockState = te.getBlockState();

		ms.translate(0.5, 0.5, 0.5);

		Direction facing = blockState.getValue(HorizontalDirectionalBlock.FACING);
		float angle = -facing.toYRot();
		ms.mulPose(Axis.YP.rotationDegrees(angle));

	    ms.translate(-0.5, -0.5, -0.5);
		ms.translate(0, 0, 1/16f);

		boolean green;
		boolean yellow;
		boolean red;

		if (te.signalState == null) {
    	green = false;
   		yellow = false;
   		red = true;
		} else {
    		green = te.signalState.isGreenLight(renderTime);
    		yellow = te.signalState.isYellowLight(renderTime);
    		red = te.signalState.isRedLight(renderTime);
		}

		for (boolean first : Iterate.trueAndFalse) {
    		// first pass = green light
   			if (first && !green)
        		continue;

    		// second pass = red OR yellow
    		if (!first && !red && !yellow)
        		continue;
		float pos = te.lightPosition.getValue(partialTicks);

		float target = te.lightPosition.getChaseTarget();

		PartialModel arm;
			CachedBuffers
			.partial(first ? CreateMoreSignalsPartialModels.THREE_COLOR_LIGHT_SIGNAL_LIGHT_GREEN : yellow ? CreateMoreSignalsPartialModels.THREE_COLOR_LIGHT_SIGNAL_LIGHT_YELLOW : CreateMoreSignalsPartialModels.THREE_COLOR_LIGHT_SIGNAL_LIGHT_RED, blockState)
			.light(0xf000f0)
			.disableDiffuse()
			.renderInto(ms, buffer.getBuffer(RenderType.solid()));

			CachedBuffers
			.partial(first ? CreateMoreSignalsPartialModels.THREE_COLOR_LIGHT_SIGNAL_GLOW_GREEN : yellow ? CreateMoreSignalsPartialModels.THREE_COLOR_LIGHT_SIGNAL_GLOW_YELLOW : CreateMoreSignalsPartialModels.THREE_COLOR_LIGHT_SIGNAL_GLOW_RED, blockState)
			.light(0xf000f0)
			.translate(0.001, 0.001, 0.001)
			.disableDiffuse()
			.renderInto(ms, buffer.getBuffer(RenderTypes.additive()));
		}
		ms.popPose();
	}
}
