package it.mikeslab.labutil.inventories.inventory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import it.mikeslab.labutil.inventories.cache.InvData;
import it.mikeslab.labutil.inventories.component.CustomInventory;
import it.mikeslab.labutil.inventories.component.CustomItem;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.InventoryHolder;

@Getter
@Setter
public class Builder {
    private final String name;
    private final YamlConfiguration config;
    private CustomInventory inventory;
    private InventoryHolder holder;
    private Material fillerMaterial;

    public Builder(String name, InventoryHolder holder) {
        this.name = name;
        this.config = InvData.CACHE.get(name);
        this.holder = holder;
    }

    public CustomInventory build() {
        HashBasedTable<String, CustomItem, Integer> items = getItems();
        String title = LegacyComponentSerializer.legacySection().serialize(MiniMessage.miniMessage().deserialize(config.getString("title")));
        int size = config.getInt("size");
        this.fillerMaterial = Material.valueOf(config.getString("filler"));

        this.inventory = new CustomInventory(items, fillerMaterial, name, title, size, holder);
        return inventory;
    }


    private HashBasedTable<String, CustomItem, Integer> getItems() {
        HashBasedTable<String, CustomItem, Integer> items = HashBasedTable.create();
        for(String key : config.getConfigurationSection("items").getKeys(false)) {
            int slot = Integer.parseInt(key); //Throws NumberFormatException if key is not a number
            String action = config.getString("items." + key + ".action");
            items.put(action, CustomItem.fromConfig(config.getConfigurationSection("items." + slot)), slot);
        }
        return items;
    }

    public static String getAction(HashBasedTable<String, CustomItem, Integer> items, int slot) {
        String action = null;
        for (Table.Cell<String, CustomItem, Integer> cell : items.cellSet()) {
            if (cell.getValue().equals(slot)) {
                action = cell.getRowKey();
                break;
            }
        }
        return action;
    }







}
