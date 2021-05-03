package com.shanebeestudios.vf.api;

import com.shanebeestudios.vf.api.recipe.BrewingFuel;
import com.shanebeestudios.vf.api.recipe.BrewingRecipe;
import com.shanebeestudios.vf.api.recipe.FurnaceFuel;
import com.shanebeestudios.vf.api.recipe.FurnaceRecipe;
import com.shanebeestudios.vf.api.util.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Recipe manager for {@link com.shanebeestudios.vf.api.machine.Furnace Furnaces}
 * <p>You can get an instance of this class from <b>{@link VirtualFurnaceAPI#getRecipeManager()}</b></p>
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class RecipeManager {

    private final Map<NamespacedKey, FurnaceFuel> furnaceFuelMap;
    private final Map<NamespacedKey, BrewingFuel> brewingFuelMap;
    private final Map<NamespacedKey, FurnaceRecipe> furnaceRecipeMap;
    private final Map<NamespacedKey, BrewingRecipe> brewingRecipeMap;

    RecipeManager() {
        this.furnaceFuelMap = new HashMap<>();
        this.brewingFuelMap = new HashMap<>();
        this.furnaceRecipeMap = new HashMap<>();
        this.brewingRecipeMap = new HashMap<>();

        registerFuels();
        registerRecipes();
    }

    /**
     * Register a new {@link FurnaceFuel}
     *
     * @param fuel new Fuel to register
     * @return true if fuel was registered
     */
    public boolean registerFurnaceFuel(FurnaceFuel fuel) {
        if (this.furnaceFuelMap.containsKey(fuel.getKey())) return false;
        this.furnaceFuelMap.put(fuel.getKey(), fuel);
        return true;
    }

    public boolean registerBrewingFuel(BrewingFuel fuel) {
        if (this.brewingFuelMap.containsKey(fuel.getKey())) return false;
        this.brewingFuelMap.put(fuel.getKey(), fuel);
        return true;
    }


    // Register vanilla fuels in API to make furnace work.
    private void registerFuels() {
        for (FurnaceFuel fuel : FurnaceFuel.getVanillaFuels()) {
            registerFurnaceFuel(fuel);
        }

        registerBrewingFuel(BrewingFuel.BLAZE_POWDER);
    }

    /**
     * Register a new {@link FurnaceRecipe}
     *
     * @param furnaceRecipe new FurnaceRecipe to register
     * @return true if recipe was registered
     */
    public boolean registerFurnaceRecipe(FurnaceRecipe furnaceRecipe) {
        if (this.furnaceRecipeMap.containsKey(furnaceRecipe.getKey())) return false;
        this.furnaceRecipeMap.put(furnaceRecipe.getKey(), furnaceRecipe);
        return true;
    }

    public boolean registerBrewingRecipe(BrewingRecipe brewingRecipe) {
        if (this.brewingRecipeMap.containsKey(brewingRecipe.getKey())) return false;
        this.brewingRecipeMap.put(brewingRecipe.getKey(), brewingRecipe);
        return true;
    }

    // Register vanilla furnace recipes.
    private void registerRecipes() {
        for (FurnaceRecipe recipe : FurnaceRecipe.getVanillaFurnaceRecipes())
            registerFurnaceRecipe(recipe);

        for (BrewingRecipe recipe : BrewingRecipe.getVanillaBrewingRecipes()) {
            registerBrewingRecipe(recipe);
        }
        Util.log("Registered all furnace recipes.");
    }

    /**
     * Get a map of all {@link FurnaceFuel}s
     *
     * @return Map of Fuels
     */
    public Map<NamespacedKey, FurnaceFuel> getFurnaceFuels() {
        return this.furnaceFuelMap;
    }


    public Map<NamespacedKey, BrewingFuel> getBrewingFuels() {
        return this.brewingFuelMap;
    }

    /**
     * Get a map of all {@link FurnaceRecipe}s
     *
     * @return Map of FurnaceRecipes
     */
    public Map<NamespacedKey, FurnaceRecipe> getFurnaceRecipes() {
        return this.furnaceRecipeMap;
    }

    public Map<NamespacedKey, BrewingRecipe> getBrewingRecipes() {
        return this.brewingRecipeMap;
    }

    /**
     * Get a {@link FurnaceFuel} by material
     *
     * @param material Material of Fuel to grab
     * @return Fuel from recipe
     */
    public FurnaceFuel getFuelByMaterial(Material material) {
        for (FurnaceFuel fuel : this.furnaceFuelMap.values()) {
            if (fuel.matchFuel(material)) {
                return fuel;
            }
        }
        return null;
    }


    public BrewingFuel getBrewingFuelByMaterial(Material material) {
        for (BrewingFuel fuel : this.brewingFuelMap.values()) {
            if (fuel.matchFuel(material)) {
                return fuel;
            }
        }
        return null;
    }

    /**
     * Get a {@link FurnaceFuel} by key
     *
     * @param key Key of Fuel
     * @return Fuel from key
     */
    public FurnaceFuel getFuelByKey(NamespacedKey key) {
        return this.furnaceFuelMap.get(key);
    }

    public BrewingFuel getBrewFuelByKey(NamespacedKey key) {
        return this.brewingFuelMap.get(key);
    }

    /**
     * Get a {@link FurnaceRecipe} by ingredient
     *
     * @param ingredient Ingredient of FurnaceRecipe
     * @return FurnaceRecipe from ingredient
     */
    public FurnaceRecipe getByIngredient(Material ingredient) {
        for (FurnaceRecipe recipe : this.furnaceRecipeMap.values()) {
            if (recipe.getIngredient() == ingredient) {
                return recipe;
            }
        }
        return null;
    }

    public BrewingRecipe getBrewingRecipeByIngredient(ItemStack ingredient, ItemStack bottle) {
        for (BrewingRecipe recipe : this.brewingRecipeMap.values()) {
            ItemStack checkItem = ingredient.clone();
            checkItem.setAmount(1);
            if (recipe.getIngredient().equals(checkItem) && recipe.getInputBottle().isSimilar(bottle)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Get a {@link FurnaceRecipe} by key
     *
     * @param key Key of FurnaceRecipe
     * @return FurnaceRecipe from key
     */
    public FurnaceRecipe getFurnaceRecipeByKey(NamespacedKey key) {
        return this.furnaceRecipeMap.get(key);
    }

    public BrewingRecipe getBrewingFurnaceRecipeByKey(NamespacedKey key) {
        return this.brewingRecipeMap.get(key);
    }

}
