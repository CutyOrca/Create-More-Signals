package io.github.cutyorca.create_more_signals.ponder;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import io.github.cutyorca.create_more_signals.ponder.scenes.train_related.CrossingLightsScenes;
import io.github.cutyorca.create_more_signals.register.CreateMoreSignalsBlocks;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.REDSTONE;
import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.TRAIN_RELATED;

public class CreateMoreSignalsPonderScenes {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(CreateMoreSignalsBlocks.CROSSING_LIGHTS)
                .addStoryBoard("crossing_lights", CrossingLightsScenes::crossingLights, REDSTONE, TRAIN_RELATED);

        HELPER.forComponents(CreateMoreSignalsBlocks.CROSSING_LIGHTS)
                .addStoryBoard("full_crossing", CrossingLightsScenes::fullCrossing, REDSTONE, TRAIN_RELATED);
    }

}
