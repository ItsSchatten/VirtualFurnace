package com.shanebeestudios.vf.api.recipe;

import com.shanebeestudios.vf.api.util.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BrewingFuel extends Fuel {

    public static final BrewingFuel BLAZE_POWDER = get("blaze_powder", Material.BLAZE_POWDER, 20);

    private final int duration;

    public BrewingFuel(NamespacedKey name, ItemStack itemStack, int duration) {
        super(name, itemStack, null);
        super.fuelItem.setAmount(1);
        this.duration = duration;
    }

    public BrewingFuel(NamespacedKey name, Material mat, int duration) {
        super(name, new ItemStack(mat, 1), null);
        this.duration = duration;
    }

    public BrewingFuel(NamespacedKey name, Tag<Material> tag, int duration) {
        super(name, null, tag);
        this.duration = duration;
    }


    private static BrewingFuel get(String matName, Material material, int duration) {
        return new BrewingFuel(Util.getKey("mc_brew_fuel_" + matName), new ItemStack(material), duration);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.key;
    }

    public int getDuration() {
        return duration;
    }
}
