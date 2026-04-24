package io.github.cutyorca.create_more_signals.register;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import io.github.cutyorca.create_more_signals.CreateMoreSignals;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class CreateMoreSignalsPartialModels {
    public static final PartialModel
            THREE_COLOR_LIGHT_SIGNAL_LIGHT_RED = block("three_color_light_signal/light_red"),
            THREE_COLOR_LIGHT_SIGNAL_LIGHT_GREEN = block("three_color_light_signal/light_green"),
            THREE_COLOR_LIGHT_SIGNAL_LIGHT_YELLOW = block("three_color_light_signal/light_yellow"),
            THREE_COLOR_LIGHT_SIGNAL_GLOW_RED = block("three_color_light_signal/glow_red"),
            THREE_COLOR_LIGHT_SIGNAL_GLOW_GREEN = block("three_color_light_signal/glow_green"),
            THREE_COLOR_LIGHT_SIGNAL_GLOW_YELLOW = block("three_color_light_signal/glow_yellow"),
            TWO_COLOR_LIGHT_SIGNAL_LIGHT_RED = block("two_color_light_signal/light_red_yellow"),
            TWO_COLOR_LIGHT_SIGNAL_LIGHT_GREEN = block("two_color_light_signal/light_green"),
            TWO_COLOR_LIGHT_SIGNAL_LIGHT_YELLOW = block("two_color_light_signal/light_red_yellow"),
            TWO_COLOR_LIGHT_SIGNAL_GLOW_RED = block("two_color_light_signal/glow_red"),
            TWO_COLOR_LIGHT_SIGNAL_GLOW_GREEN = block("two_color_light_signal/glow_green"),
            TWO_COLOR_LIGHT_SIGNAL_GLOW_YELLOW = block("two_color_light_signal/glow_yellow"),
            CROSSING_LIGHT_RIGHT = block("crossing_lights/light_right"),
            CROSSING_LIGHT_LEFT = block("crossing_lights/light_left"),
            CROSSING_RIGHT = block("crossing_lights/right"),
            CROSSING_LEFT = block("crossing_lights/left"),
            CROSSING_LIGHT_OFF = block("crossing_lights/off");

    public static final Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>> STEEL_PIPE_ATTACHMENTS =
            new EnumMap<>(FluidTransportBehaviour.AttachmentTypes.ComponentPartials.class);


    private static PartialModel block(String path) {
        return PartialModel.of(CreateMoreSignals.asResource("block/" + path));
    }

    private static PartialModel item(String path) {
        return PartialModel.of(CreateMoreSignals.asResource("item/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of(CreateMoreSignals.asResource("entity/" + path));
    }

    public static void init() {
        // init static fields
    }

}
