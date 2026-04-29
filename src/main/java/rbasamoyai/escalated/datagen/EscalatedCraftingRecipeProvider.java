package rbasamoyai.escalated.datagen;

import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.index.EscalatedItems;

import static com.tterrag.registrate.providers.RegistrateRecipeProvider.getHasName;
import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;

public class EscalatedCraftingRecipeProvider {

    public static void register() {
        CreateEscalated.REGISTRATE.addDataGenerator(ProviderType.RECIPE, EscalatedCraftingRecipeProvider::buildCraftingRecipes);
    }

    public static void buildCraftingRecipes(RecipeOutput cons) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EscalatedItems.METAL_WALKWAY_STEPS.get())
                .define('C', Items.CHAIN).define('P', CommonMetal.IRON.plates)
                .pattern("CPC")
                .pattern("CPC")
                .pattern("CPC")
                .unlockedBy(getHasName(Items.CHAIN), has(Items.CHAIN))
                .save(cons);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EscalatedItems.WOODEN_WALKWAY_STEPS.get())
                .define('C', Items.CHAIN).define('P', ItemTags.WOODEN_SLABS)
                .pattern("CPC")
                .pattern("CPC")
                .pattern("CPC")
                .unlockedBy(getHasName(Items.CHAIN), has(Items.CHAIN))
                .save(cons);
    }

    private EscalatedCraftingRecipeProvider() {}

}
