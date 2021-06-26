package com.shanebeestudios.api.machine;

import com.shanebeestudios.api.RecipeManager;
import com.shanebeestudios.api.VirtualFurnaceAPI;
import com.shanebeestudios.api.property.BrewingProperties;
import com.shanebeestudios.api.property.PropertyHolder;
import com.shanebeestudios.api.recipe.BrewingFuel;
import com.shanebeestudios.api.recipe.BrewingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class BrewingStand extends Machine implements PropertyHolder<BrewingProperties>, InventoryHolder, ConfigurationSerializable {

    private final RecipeManager recManager;
    private final Inventory inv;
    private final ItemStack[] potionBottles;
    private final BrewingProperties properties;

    // TODO: Implement this
    private boolean shouldPlaySound;

    private ItemStack fuel = null;
    private ItemStack ingredient = null;
    private int fuelTime = 0;
    private int max = 0;
    private int brewTime = 0;
    private int speed = 1;

    public BrewingStand(String name, BrewingProperties properties) {
        super(UUID.randomUUID(), name);
        this.properties = properties;
        this.recManager = VirtualFurnaceAPI.getInstance().getRecipeManager();
        this.inv = Bukkit.createInventory(this, InventoryType.BREWING, name);
        this.potionBottles = new ItemStack[3];
        this.updateInventory();
    }

    private BrewingStand(UUID uuid, String name, BrewingProperties properties, int brewTime, int maxBrews, int fuelTime, ItemStack fuel, ItemStack ingredient, ItemStack[] bottles) {
        super(uuid, name);
        this.recManager = VirtualFurnaceAPI.getInstance().getRecipeManager();
        this.properties = properties;
        this.brewTime = brewTime;
        this.max = maxBrews;
        this.fuelTime = fuelTime;
        this.fuel = fuel;
        this.ingredient = ingredient;
        this.potionBottles = bottles;
        this.inv = Bukkit.createInventory(this, InventoryType.BREWING, name);
        this.updateInventory();
        this.updateBrewSpeed();
    }

    public static BrewingStand deserialize(Map<String, Object> args) {
        String name = ((String) args.get("name"));
        UUID uuid = UUID.fromString(((String) args.get("uuid")));
        BrewingProperties prop = (BrewingProperties) args.get("properties");
        int brewTime = ((Number) args.get("brewTime")).intValue();
        int fuelTime = ((Number) args.get("fuelTime")).intValue();
        int maxBrews = ((Number) args.get("maxBrews")).intValue();
        ItemStack fuel = (ItemStack) args.get("fuel");
        ItemStack ing = (ItemStack) args.get("ingredient");
        ItemStack[] bottles = new ItemStack[3];
        bottles[0] = (ItemStack) args.get("bottle-1");
        bottles[1] = (ItemStack) args.get("bottle-2");
        bottles[2] = (ItemStack) args.get("bottle-3");
        return new BrewingStand(uuid, name, prop, brewTime, maxBrews, fuelTime, fuel, ing, bottles);
    }

    public RecipeManager getRecManager() {
        return recManager;
    }

    public Inventory getInv() {
        return inv;
    }

    public ItemStack[] getPotionBottles() {
        return potionBottles;
    }

    public ItemStack getFuel() {
        return fuel;
    }

    public void setFuel(ItemStack fuel) {
        this.fuel = fuel;
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(ItemStack ingredient) {
        this.ingredient = ingredient;
    }

    public int getFuelTime() {
        return fuelTime;
    }

    public void setFuelTime(int fuelTime) {
        this.fuelTime = fuelTime;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getBrewTime() {
        return brewTime;
    }

    public void setBrewTime(int brewTime) {
        this.brewTime = brewTime;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public void openInventory(Player player) {
        updateInventory();
        player.openInventory(inv);
    }

    private void updateInventory() {
        this.inv.setItem(0, potionBottles[0]);
        this.inv.setItem(1, potionBottles[1]);
        this.inv.setItem(2, potionBottles[2]);
        this.inv.setItem(3, ingredient);
        this.inv.setItem(4, fuel);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    @Override
    public BrewingProperties getProperties() {
        return this.properties;
    }

    void processBrew() {
        BrewingRecipe recipe = null;
        for (int i = 2; i >= 0; i--) {
            BrewingRecipe recipe1 = recManager.getBrewingRecipeByIngredient(ingredient, potionBottles[i]);
            if (recipe1 != null)
                recipe = recipe1;
        }
        if (recipe == null) return;
        for (int i = 0; i <= 2; i++) {
            if (match(potionBottles[i], recipe.getInputBottle())) {
                potionBottles[i] = recipe.getOutputBottle().clone();
            }
        }
        if (fuelTime > 0) fuelTime--;
        if (ingredient.getAmount() > 1) {
            ingredient.setAmount(ingredient.getAmount() - 1);
        } else
            ingredient = null;
        brewTime = 0;
        updateInventory();

        for (HumanEntity viewer : inv.getViewers()) {
            Player player = (Player) viewer;
            player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
        }
        // TODO: This is where the sound would play on completion
    }

    boolean match(ItemStack one, ItemStack two) {
        if (one == null || two == null) return false;
        return one.equals(two);
    }

    void processFuel() {
        BrewingFuel fuel = recManager.getBrewingFuelByMaterial(this.fuel.getType());
        fuelTime = (int) (fuel.getDuration() * properties.getFuelTimeMultiplier());
        max = fuelTime;
        if (this.fuel.getAmount() > 1) {
            this.fuel.setAmount(this.fuel.getAmount() - 1);
        } else
            this.fuel = null;

        updateInventory();
    }

    boolean canBrew() {
        return getRecipe() != null;
    }

    private BrewingRecipe getRecipe() {
        if (ingredient == null) return null;

        BrewingRecipe recipe;
        for (int i = 0; i <= 2; i++) {
            if (potionBottles[i] != null) {
                recipe = recManager.getBrewingRecipeByIngredient(ingredient, potionBottles[i]);
                if (recipe != null)
                    return recipe;
            }
        }

        return null;
    }

    private void updateBrewSpeed() {
        BrewingRecipe recipe = getRecipe();
        if (recipe != null) {
            speed = (int) ((400 / recipe.getCookTime()) / properties.getBrewMultiplier());
        }
    }

    @Override
    public void tick() {
        try {
            if (fuelTime > 0) {
                if (brewTime > 0) {
                    if (ingredient != null) {
                        brewTime -= speed;
                        if (brewTime <= 0) {
                            processBrew();
                        }
                    } else brewTime = 0;
                } else if (canBrew()) {
                    brewTime = 400;
                    updateBrewSpeed();
                }
            } else if (canBurn()) {
                processFuel();
            }
            updateInventoryView();
        } catch (AssertionError error) {
            ingredient = null;
        }
    }

    private void updateInventoryView() {
        ItemStack bottle1 = this.inv.getItem(0), bottle2 = this.inv.getItem(1), bottle3 = this.inv.getItem(2);
        ItemStack ingred = this.inv.getItem(3);
        ItemStack f = this.inv.getItem(4);

        if (!match(this.potionBottles[0], bottle1)) {
            this.potionBottles[0] = bottle1;
        }
        if (!match(this.potionBottles[1], bottle2)) {
            this.potionBottles[1] = bottle2;
        }
        if (!match(this.potionBottles[2], bottle3)) {
            this.potionBottles[2] = bottle3;
        }
        if (!match(this.ingredient, ingred)) {
            this.ingredient = ingred;
        }
        if (!match(this.fuel, f)) {
            this.fuel = f;
        }

        inv.getViewers().forEach((viewer) -> {
            try {
                InventoryView view = viewer.getOpenInventory();
                view.setProperty(InventoryView.Property.BREW_TIME, brewTime);
                view.setProperty(InventoryView.Property.FUEL_TIME, (int) Math.round(((double) fuelTime) / ((double) (max / 20))));
            } catch (final Exception ignored) {
            }
        });
    }

    private boolean canBurn() {
        if (this.fuel == null) return false;
        return this.recManager.getBrewingFuelByMaterial(this.fuel.getType()) != null;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", this.getName());
        result.put("uuid", this.getUniqueID().toString());
        result.put("properties", this.properties);
        result.put("brewTime", this.brewTime);
        result.put("fuelTime", this.fuelTime);
        result.put("maxBrews", this.max);
        result.put("fuel", this.fuel);
        result.put("ingredient", this.ingredient);
        result.put("bottle-1", this.potionBottles[0]);
        result.put("bottle-2", this.potionBottles[1]);
        result.put("bottle-3", this.potionBottles[2]);
        return result;
    }
}
