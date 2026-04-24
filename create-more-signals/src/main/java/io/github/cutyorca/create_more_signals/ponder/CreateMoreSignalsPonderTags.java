package io.github.cutyorca.create_more_signals.ponder;

import com.tterrag.registrate.util.entry.RegistryEntry;
import io.github.cutyorca.create_more_signals.CreateMoreSignals;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsBlocks;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.TRAIN_RELATED;
import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.KINETIC_APPLIANCES;

public class CreateMoreSignalsPonderTags {

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.addToTag(TRAIN_RELATED)
                .add(CreateMoreSignalsBlocks.CROSSING_LIGHTS);

    }
}
