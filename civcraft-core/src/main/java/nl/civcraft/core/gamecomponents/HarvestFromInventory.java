package nl.civcraft.core.gamecomponents;

import nl.civcraft.core.model.GameObject;
import nl.civcraft.core.model.Item;
import nl.civcraft.core.npc.Civvy;

import java.util.Optional;

/**
 * Created by Bob on 25-3-2016.
 * <p>
 * This is probably not worth documenting
 */
public class HarvestFromInventory extends AbstractGameComponent implements Harvestable, GameComponent {


    @Override
    public Optional<Item> harvest(Civvy civvy) {
        Optional<Inventory> component = gameObject.getComponent(Inventory.class);
        if (!component.isPresent()) {
            throw new IllegalStateException("HarvestFromInventory can only be added to GameObjects with an Inventory component");
        }
        return component.get().getFirstItem();

    }

    @Override
    public void addTo(GameObject gameObject) {
        Optional<Inventory> component = gameObject.getComponent(Inventory.class);
        if (!component.isPresent()) {
            throw new IllegalStateException("HarvestFromInventory can only be added to GameObjects with an Inventory component");
        }
        super.addTo(gameObject);
    }

}
