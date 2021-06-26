package com.shanebeestudios.api.property;

import com.shanebeestudios.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class BrewingProperties extends Properties implements ConfigurationSerializable {

    public static final BrewingProperties NORMAL = build("brewing", 1.0, 1.0);

    private double brewMultiplier;
    private double fuelTimeMultiplier;

    BrewingProperties(String key) {
        super(Util.getKey(key));
    }

    private static BrewingProperties getProperties(String key) {
        for (NamespacedKey nameKey : KEY_MAP.keySet())
            if (nameKey.getKey().equalsIgnoreCase(key)) {
                Properties property = KEY_MAP.get(nameKey);
                if (property instanceof BrewingProperties)
                    return (BrewingProperties) KEY_MAP.get(nameKey);
            }

        return null;
    }

    private static BrewingProperties build(String key, double brewMultiplier, double fuelTimeMultiplier) {
        return new BrewingProperties("brewer_prop_" + key).setBrewMultiplier(brewMultiplier).setFuelTimeMultiplier(fuelTimeMultiplier);
    }

    public static BrewingProperties deserialize(Map<String, Object> args) {
        String stringKey = ((String) args.get("key")).split(":")[1];
        double brew = (double) args.get("brewMultiplier");
        double fuel = (double) args.get("fuelTimeMultiplier");
        BrewingProperties brewerProperties = getProperties(stringKey);
        if (brewerProperties != null) {
            return brewerProperties;
        } else {
            return new BrewingProperties(stringKey).setBrewMultiplier(brew).setFuelTimeMultiplier(fuel);
        }
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialized = new LinkedHashMap<>();
        serialized.put("key", this.getKey().toString());
        serialized.put("brewMultiplier", getBrewMultiplier());
        serialized.put("fuelTimeMultiplier", getFuelTimeMultiplier());
        return serialized;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BrewingProperties objProp = (BrewingProperties) obj;
        return Double.compare(objProp.getBrewMultiplier(), getBrewMultiplier()) == 0 && Double.compare(objProp.getFuelTimeMultiplier(), getFuelTimeMultiplier()) == 0;
    }

    public double getBrewMultiplier() {
        return brewMultiplier;
    }

    public BrewingProperties setBrewMultiplier(double amount) {
        this.brewMultiplier = amount;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBrewMultiplier(), getFuelTimeMultiplier());
    }

    public double getFuelTimeMultiplier() {
        return fuelTimeMultiplier;
    }

    public BrewingProperties setFuelTimeMultiplier(double amount) {
        this.fuelTimeMultiplier = amount;
        return this;
    }

    @Override
    public String toString() {
        return "BrewingProperties{" +
                "key= " + key +
                ", brewMultiplier=" + brewMultiplier +
                ", fuelTime=" + fuelTimeMultiplier +
                '}';
    }
}
