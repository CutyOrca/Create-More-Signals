package io.github.cutyorca.create_more_signals.register;

import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.util.entry.BlockEntry;
import io.github.cutyorca.create_more_signals.CreateMoreSignals;

import io.github.cutyorca.create_more_signals.blocks.three_color_light_signal.ThreeColorLightSignalBlock;
import io.github.cutyorca.create_more_signals.blocks.three_color_light_signal.ThreeColorLightSignalItem;
import io.github.cutyorca.create_more_signals.blocks.two_color_light_signal.TwoColorLightSignalBlock;
import io.github.cutyorca.create_more_signals.blocks.two_color_light_signal.TwoColorLightSignalItem;
import io.github.cutyorca.create_more_signals.blocks.crossing_lights.CrossingLightsBlock;
import io.github.cutyorca.create_more_signals.blocks.crossing_lights.CrossingLightsItem;
import io.github.cutyorca.create_more_signals.blocks.signal_pole.SignalPoleBlock;
import io.github.cutyorca.create_more_signals.blocks.signal_pole.SignalPoleItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class CreateMoreSignalsBlocks {


    private static final CreateRegistrate REGISTRATE = CreateMoreSignals.registrate().setCreativeTab(CreateMoreSignalsCreativeModeTabs.MAIN_TAB);

    public static void register() {}


//    public static final BlockEntry<KilnBlock> KILN = REGISTRATE.block("kiln", KilnBlock::new)
//            .initialProperties(SharedProperties::stone)
//            .properties(p -> p  .mapColor(MapColor.METAL)
//                                .lightLevel(s -> s.getValue(KilnBlock.POWERED) ? 15 : 0))
//            .transform(pickaxeOnly())
//            .blockstate(new KilnGenerator()::generate)
//            .transform(CreateMoreSignalsStress.setImpact(4.0))
//            .item()
//            .transform(customItemModel())
//            .register();
//

    public static final BlockEntry<ThreeColorLightSignalBlock> THREE_COLOR_LIGHT_SIGNAL = REGISTRATE.block("three_color_light_signal", ThreeColorLightSignalBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .transform(pickaxeOnly())
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .item(ThreeColorLightSignalItem::new).transform(customItemModel())
        .addLayer(() -> RenderType::cutout)
        .register();

    public static final BlockEntry<TwoColorLightSignalBlock> TWO_COLOR_LIGHT_SIGNAL = REGISTRATE.block("two_color_light_signal", TwoColorLightSignalBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .transform(pickaxeOnly())
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .item(TwoColorLightSignalItem::new).transform(customItemModel())
        .addLayer(() -> RenderType::cutout)
        .register();
    public static final BlockEntry<CrossingLightsBlock> CROSSING_LIGHTS = REGISTRATE.block("crossing_lights", CrossingLightsBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .transform(pickaxeOnly())
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .item(CrossingLightsItem::new).transform(customItemModel())
        .addLayer(() -> RenderType::cutout)
        .register();

    public static final BlockEntry<SignalPoleBlock> SIGNAL_POLE = REGISTRATE.block("signal_pole", SignalPoleBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .transform(pickaxeOnly())
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .properties(BlockBehaviour.Properties::noOcclusion)
        .item(SignalPoleItem::new).transform(customItemModel())
        .addLayer(() -> RenderType::cutout)
        .register();
}