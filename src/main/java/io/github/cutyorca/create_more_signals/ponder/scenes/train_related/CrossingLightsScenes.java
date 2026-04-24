package io.github.cutyorca.create_more_signals.ponder.scenes.train_related;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.redstone.RoseQuartzLampBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import io.github.cutyorca.create_more_signals.blocks.crossing_lights.CrossingLightsBlock;
import io.github.cutyorca.create_more_signals.blocks.crossing_lights.CrossingLightsBlockEntity;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.ParrotElement;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class CrossingLightsScenes {
    public static void crossingLights(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("crossing_lights", "Crossing Lights and Redstone");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().fromTo(0, 0, 0, 4, 0, 4), Direction.UP);
        scene.idle(10);

        BlockPos crossingLights = util.grid().at(2, 3, 2);
        Selection sidePower = util.select().position(3, 3, 2);
        BlockPos bottomPole = util.grid().at(2, 1, 2);


        scene.idle(10);
        scene.world().showSection(util.select().fromTo(2, 3, 2, 2, 1, 2), Direction.DOWN);
        scene.overlay().showText(40)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Crossing Lights will activate with redstone power")
                .pointAt(Vec3.atCenterOf(crossingLights));
        scene.world().showSection(sidePower, Direction.DOWN);

        scene.idle(80);
        scene.overlay().showText(40)
                .placeNearTarget()
                .attachKeyFrame()
                .text("They can also be powered through non-air blocks below")
                .pointAt(Vec3.atCenterOf(crossingLights));
        scene.world().destroyBlock(bottomPole);
        scene.idle(10);
        scene.world().hideSection(sidePower, Direction.UP);
        scene.world().setBlock(bottomPole, Blocks.REDSTONE_BLOCK.defaultBlockState(), true);
    }
    public static void fullCrossing(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("full_crossing", "Using Crossing Lights to decorate railroad crossings");
        scene.configureBasePlate(12, 0, 9);
		scene.scaleSceneView(.75f);
        scene.setSceneOffsetY(-1);
        scene.showBasePlate();

        Selection tracks = util.select().fromTo(0, 1, 4, 32, 1, 4);
        Selection train = util.select().fromTo(1, 2, 3, 3, 3, 5);
        BlockPos middleTrack = util.grid().at(16, 1, 4);
        BlockPos initialControlsPos = util.grid().at(3, 3, 4);
        Selection main = util.select().fromTo(12, 1, 0, 20, 3, 8).substract(tracks);
        Selection crossingLights = util.select().position(0, 0, 0);

        scene.world().showSection(tracks, Direction.DOWN);

        scene.idle(10);
        scene.world().showSection(main, Direction.DOWN);
        scene.overlay().showText(60)
                .placeNearTarget()
                .attachKeyFrame()
                .text("With the help of Signals, Comparators, and Redstone Links, you can create an automated railroad crossing")
                .pointAt(Vec3.atCenterOf(middleTrack));
    
        scene.idle(60);
		ElementLink<WorldSectionElement> trainElement = scene.world().showIndependentSection(train, Direction.DOWN);
		Vec3 birbVec = util.vector().topOf(util.grid().at(2, 3, 4));
		ElementLink<ParrotElement> birb = scene.special().createBirb(birbVec, ParrotPose.FacePointOfInterestPose::new);
        scene.world().toggleControls(initialControlsPos);

        scene.idle(10);
		scene.world().moveSection(trainElement, util.vector().of(31, 0, 0), 150);
		scene.world().animateBogey(util.grid().at(2, 2, 4), -32f, 150);
        scene.idle(10);
        scene.world().toggleRedstonePower(main);

    }
}
