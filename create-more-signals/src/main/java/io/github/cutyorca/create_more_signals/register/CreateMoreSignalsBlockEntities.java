package io.github.cutyorca.create_more_signals.register;


import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import io.github.cutyorca.create_more_signals.CreateMoreSignals;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsPartialModels;
import io.github.cutyorca.create_more_signals.blocks.three_color_light_signal.ThreeColorLightSignalBlockEntity;
import io.github.cutyorca.create_more_signals.blocks.three_color_light_signal.ThreeColorLightSignalRenderer;
import io.github.cutyorca.create_more_signals.blocks.two_color_light_signal.TwoColorLightSignalBlockEntity;
import io.github.cutyorca.create_more_signals.blocks.two_color_light_signal.TwoColorLightSignalRenderer;
import io.github.cutyorca.create_more_signals.blocks.crossing_lights.CrossingLightsBlockEntity;
import io.github.cutyorca.create_more_signals.blocks.crossing_lights.CrossingLightsRenderer;

public class CreateMoreSignalsBlockEntities {

    private static final CreateRegistrate REGISTRATE = CreateMoreSignals.registrate();

//    public static final BlockEntityEntry<KilnBlockEntity> KILN = REGISTRATE
//            .blockEntity("kiln", KilnBlockEntity::new)
//            .visual(() -> KilnVisual::new)
//            .validBlocks(CreateMoreSignalsBlocks.KILN)
//            .renderer(() -> KilnRenderer::new)
//            .register();

    public static final BlockEntityEntry<ThreeColorLightSignalBlockEntity> THREE_COLOR_LIGHT_SIGNAL = REGISTRATE.blockEntity("three_color_light_signal", ThreeColorLightSignalBlockEntity::new)
        .validBlocks(CreateMoreSignalsBlocks.THREE_COLOR_LIGHT_SIGNAL)
        .renderer(() -> ThreeColorLightSignalRenderer::new)
        .register();

    public static final BlockEntityEntry<TwoColorLightSignalBlockEntity> TWO_COLOR_LIGHT_SIGNAL = REGISTRATE.blockEntity("two_color_light_signal", TwoColorLightSignalBlockEntity::new)
        .validBlocks(CreateMoreSignalsBlocks.TWO_COLOR_LIGHT_SIGNAL)
        .renderer(() -> TwoColorLightSignalRenderer::new)
        .register();

        public static final BlockEntityEntry<CrossingLightsBlockEntity> CROSSING_LIGHTS = REGISTRATE.blockEntity("crossing_lights", CrossingLightsBlockEntity::new)
        .validBlocks(CreateMoreSignalsBlocks.CROSSING_LIGHTS)
        .renderer(() -> CrossingLightsRenderer::new)
        .register();


    public static void register() {}
}