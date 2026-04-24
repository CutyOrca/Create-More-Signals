package io.github.cutyorca.create_more_signals.register;

import io.github.cutyorca.create_more_signals.CreateMoreSignals;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

import static io.github.cutyorca.create_more_signals.register.CreateMoreSignalsTags.NameSpace.MOD;

public class CreateMoreSignalsTags {
  public enum NameSpace {
    MOD(CreateMoreSignals.MODID, false, true);

    public final String id;
    public final boolean optionalDefault;
    public final boolean alwaysDatagenDefault;

    NameSpace(String id) {
      this(id, true, false);
    }

    NameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
      this.id = id;
      this.optionalDefault = optionalDefault;
      this.alwaysDatagenDefault = alwaysDatagenDefault;
    }

  }

  public enum AllBlockTags {
    SIGNAL_POLES
    ;
        public final TagKey<Block> tag;
    public final boolean alwaysDatagen;


    AllBlockTags() {
      this(MOD);
    }

    AllBlockTags(NameSpace namespace) {
      this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
    }

    AllBlockTags(NameSpace namespace, String path) {
      this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
    }

    AllBlockTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
      this(namespace, null, optional, alwaysDatagen);
    }

    AllBlockTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
      if (optional) {
        tag = optionalTag(BuiltInRegistries.BLOCK, id);
      } else {
        tag = TagKey.create(Registries.BLOCK, id);
      }
      this.alwaysDatagen = alwaysDatagen;
    }

    @SuppressWarnings("deprecation")
    public boolean matches(Block block) {
      return block.builtInRegistryHolder()
          .is(tag);
    }

    public boolean matches(ItemStack stack) {
      return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
    }

    public boolean matches(BlockState state) {
      return state.is(tag);
    }

    public static void register() { }
  }

  public static <T> TagKey<T> optionalTag(Registry<T> registry, ResourceLocation id) {
    return TagKey.create(registry.key(), id);
  }

  // load all classes
  public static void register() {
    AllBlockTags.register();
  }
}
