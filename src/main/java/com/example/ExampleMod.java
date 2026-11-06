package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.UUID;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "bliss";

    public static final Item STRENGTH_GEM = new StrengthGemItem(new Item.Settings().maxCount(1));
    public static final Item VOLCANO_GEM = new VolcanoGemItem(new Item.Settings().maxCount(1));
    public static final Item BLOOD_GEM = new BloodGemItem(new Item.Settings().maxCount(1));
    public static final Item TIME_GEM = new TimeGemItem(new Item.Settings().maxCount(1));
    public static final Item GRAVITY_GEM = new GravityGemItem(new Item.Settings().maxCount(1));
    public static final Item FROSTBITE_GEM = new FrostbiteGemItem(new Item.Settings().maxCount(1));
    public static final Item LIGHTNING_GEM = new LightningGemItem(new Item.Settings().maxCount(1));
    public static final Item SHADOW_GEM = new ShadowGemItem(new Item.Settings().maxCount(1));
    public static final Item EARTH_GEM = new EarthGemItem(new Item.Settings().maxCount(1));
    public static final Item WIND_GEM = new WindGemItem(new Item.Settings().maxCount(1));
    public static final Item PRISM_GEM = new PrismGemItem(new Item.Settings().maxCount(1));
    public static final Item VENOM_GEM = new VenomGemItem(new Item.Settings().maxCount(1));
    public static final Item AQUAMARINE_GEM = new AquamarineGemItem(new Item.Settings().maxCount(1));
    public static final Item ENDER_VOID_GEM = new EnderVoidGemItem(new Item.Settings().maxCount(1));
    public static final Item SHURIKEN_GEM = new ShurikenGemItem(new Item.Settings().maxCount(1));
    public static final Item WEALTH_GEM = new WealthGemItem(new Item.Settings().maxCount(1));
    public static final Item LIFE_STEALER_GEM = new LifeStealerGemItem(new Item.Settings().maxCount(1));
    public static final Item SHUFFLER_ARTIFACT = new Item(new Item.Settings().maxCount(16));
    public static final Item GEM_UPGRADER = new Item(new Item.Settings().maxCount(16));

    @Override
    public void onInitialize() {
        registerItem("strength_gem", STRENGTH_GEM);
        registerItem("volcano_gem", VOLCANO_GEM);
        registerItem("blood_gem", BLOOD_GEM);
        registerItem("time_gem", TIME_GEM);
        registerItem("gravity_gem", GRAVITY_GEM);
        registerItem("frostbite_gem", FROSTBITE_GEM);
        registerItem("lightning_gem", LIGHTNING_GEM);
        registerItem("shadow_gem", SHADOW_GEM);
        registerItem("earth_gem", EARTH_GEM);
        registerItem("wind_gem", WIND_GEM);
        registerItem("prism_gem", PRISM_GEM);
        registerItem("venom_gem", VENOM_GEM);
        registerItem("aquamarine_gem", AQUAMARINE_GEM);
        registerItem("ender_void_gem", ENDER_VOID_GEM);
        registerItem("shuriken_gem", SHURIKEN_GEM);
        registerItem("wealth_gem", WEALTH_GEM);
        registerItem("life_stealer_gem", LIFE_STEALER_GEM);
        registerItem("shuffler_artifact", SHUFFLER_ARTIFACT);
        registerItem("gem_upgrader", GEM_UPGRADER);

        CommandRegistrationCallback.EVENT.register(BlissCommand::register);
    }

    private static void registerItem(String name, Item item) {
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, name), item);
    }
}

class GemUpgradeSystem {
    public static final Item[] ALL_GEMS = {
        ExampleMod.STRENGTH_GEM, ExampleMod.VOLCANO_GEM, ExampleMod.BLOOD_GEM, ExampleMod.TIME_GEM,
        ExampleMod.GRAVITY_GEM, ExampleMod.FROSTBITE_GEM, ExampleMod.LIGHTNING_GEM, ExampleMod.SHADOW_GEM,
        ExampleMod.EARTH_GEM, ExampleMod.WIND_GEM, ExampleMod.PRISM_GEM, ExampleMod.VENOM_GEM,
        ExampleMod.AQUAMARINE_GEM, ExampleMod.ENDER_VOID_GEM, ExampleMod.SHURIKEN_GEM, ExampleMod.WEALTH_GEM,
        ExampleMod.LIFE_STEALER_GEM
    };
    public static int getGemTier(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.contains("GemTier")) nbt.putInt("GemTier", 1);
        return nbt.getInt("GemTier");
    }
}

class BlissCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment env) {
        dispatcher.register(CommandManager.literal("bliss")
            .then(CommandManager.literal("help").executes(c -> {
                c.getSource().sendMessage(Text.of("§b=== Bliss Powers Guide ==="));
                c.getSource().sendMessage(Text.of("§eUnlock abilities by upgrading GemTier from 1 to 3!"));
                return 1;
            }))
            .then(CommandManager.literal("resetcooldown").requires(s -> s.hasPermissionLevel(2)).executes(c -> {
                for (Item g : GemUpgradeSystem.ALL_GEMS) {
                    try { c.getSource().getPlayer().getItemCooldownManager().remove(g); } catch (Exception e) {}
                }
                c.getSource().sendMessage(Text.of("§aCooldowns reset successfully!"));
                return 1;
            }))
        );
    }
}

class StrengthGemItem extends Item {
    public StrengthGemItem(Settings s) { super(s); }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean sel) {
        if (!world.isClient && entity instanceof PlayerEntity player && (player.getMainHandStack() == stack || player.getOffHandStack() == stack)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, 2, false, false, true));
        }
    }
}

class VolcanoGemItem extends Item { public VolcanoGemItem(Settings s) { super(s); } }
class BloodGemItem extends Item { public BloodGemItem(Settings s) { super(s); } }
class TimeGemItem extends Item { public TimeGemItem(Settings s) { super(s); } }
class GravityGemItem extends Item { public GravityGemItem(Settings s) { super(s); } }
class FrostbiteGemItem extends Item { public FrostbiteGemItem(Settings s) { super(s); } }

class LightningGemItem extends Item {
    public LightningGemItem(Settings s) { super(s); }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean sel) {
        if (!world.isClient && entity instanceof PlayerEntity player && (player.getMainHandStack() == stack || player.getOffHandStack() == stack)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 4, false, false, true));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 40, 4, false, false, true));
        }
    }
}

class ShadowGemItem extends Item {
    public ShadowGemItem(Settings s) { super(s); }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean sel) {
        if (!world.isClient && entity instanceof PlayerEntity player && (player.getMainHandStack() == stack || player.getOffHandStack() == stack)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 40, 0, false, false, true));
        }
    }
}

class EarthGemItem extends Item {
    public EarthGemItem(Settings s) { super(s); }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean sel) {
        if (!world.isClient && entity instanceof PlayerEntity player && (player.getMainHandStack() == stack || player.getOffHandStack() == stack)) {
            player.setStepHeight(2.06F);
        }
    }
}

class WindGemItem extends Item {
    public WindGemItem(Settings s) { super(s); }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean sel) {
        if (!world.isClient && entity instanceof PlayerEntity player && (player.getMainHandStack() == stack || player.getOffHandStack() == stack)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 40, 4, false, false, true));
        }
    }
}

class PrismGemItem extends Item {
    public PrismGemItem(Settings s) { super(s); }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean sel) {
        if (!world.isClient && entity instanceof PlayerEntity player && (player.getMainHandStack() == stack || player.getOffHandStack() == stack)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 0, false, false, true));
        }
    }
}

class VenomGemItem extends Item {
    public VenomGemItem(Settings s) { super(s); }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean sel) {
        if (!world.isClient && entity instanceof PlayerEntity player && player.hasStatusEffect(StatusEffects.POISON)) {
            player.removeStatusEffect(StatusEffects.POISON);
        }
    }
}

