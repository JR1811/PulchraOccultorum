package net.shirojr.pulchra_occultorum.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.shirojr.pulchra_occultorum.init.Blocks;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {
    public RecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, net.shirojr.pulchra_occultorum.init.Items.UNICYCLE)
                .pattern(" L ")
                .pattern(" B ")
                .pattern("NIN")
                .input('L', Items.LEATHER)
                .input('B', Items.IRON_BARS)
                .input('I', Items.IRON_INGOT)
                .input('N', Items.IRON_NUGGET)
                .criterion(
                        FabricRecipeProvider.hasItem(net.shirojr.pulchra_occultorum.init.Items.UNICYCLE),
                        FabricRecipeProvider.conditionsFromItem(Items.IRON_INGOT)
                )
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.SAFETY_NET)
                .pattern("w w")
                .pattern(" w ")
                .pattern("w w")
                .input('w', ItemTags.WOOL)
                .criterion(
                        FabricRecipeProvider.hasItem(Blocks.SAFETY_NET.asItem()),
                        FabricRecipeProvider.conditionsFromTag(ItemTags.WOOL)
                )
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.MONOLITH)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SSS")
                .input('S', ItemTags.STONE_BRICKS)
                .input('A', Items.AMETHYST_SHARD)
                .criterion(
                        FabricRecipeProvider.hasItem(Blocks.MONOLITH.asItem()),
                        FabricRecipeProvider.conditionsFromItem(Items.AMETHYST_SHARD)
                )
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.ELASTIC_SAND, 2)
                .input(ItemTags.SAND)
                .input(ItemTags.SAND)
                .input(Items.STRING)
                .input(Items.STRING)
                .criterion(
                        FabricRecipeProvider.hasItem(Blocks.ELASTIC_SAND.asItem()),
                        FabricRecipeProvider.conditionsFromTag(ItemTags.SAND)
                )
                .offerTo(exporter);
    }
}
