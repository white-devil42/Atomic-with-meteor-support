package me.zeroX150.atomic.helper.event.events;

import me.zeroX150.atomic.helper.event.events.base.Event;
import net.minecraft.recipe.Recipe;

public class RecipeClickedEvent extends Event {
    int       syncId;
    Recipe<?> recipe;
    boolean   craftAll;

    public RecipeClickedEvent(int syncId, Recipe<?> recipe, boolean craftAll) {
        this.syncId = syncId;
        this.recipe = recipe;
        this.craftAll = craftAll;
    }

    public Recipe<?> getRecipe() {
        return recipe;
    }

    public int getSyncId() {
        return syncId;
    }

    public boolean craftAll() {
        return craftAll;
    }
}
