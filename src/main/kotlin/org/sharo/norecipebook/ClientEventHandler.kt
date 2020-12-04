package org.sharo.norecipebook

import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraft.client.gui.widget.button.ImageButton
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.gui.screen.inventory.InventoryScreen
import net.minecraft.client.gui.screen.inventory.CraftingScreen
import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.recipebook.RecipeBookGui
import net.minecraftforge.api.distmarker.Dist
import org.apache.logging.log4j.LogManager

@EventBusSubscriber(Dist.CLIENT, modid = Core.MOD_ID)
class ClientEventHandler {
    companion object {
        @JvmStatic
        @SubscribeEvent
        fun onGuiScreenEvent(event: GuiScreenEvent.InitGuiEvent.Post) {
            val widgets = event.getWidgetList()
            for (widget in widgets) {
                if (widget is ImageButton && widget.visible) {
                    if ("minecraft:textures/gui/recipe_button.png" == widget.resourceLocation.toString()) {
                        widget.visible = false
                    }
                }
            }
            val screen = event.gui
            if (screen is ContainerScreen<*>) {
                var widthTooNarrow = when (screen) {
                is InventoryScreen ->
                    screen.widthTooNarrow
                is CraftingScreen ->
                    screen.widthTooNarrow
                is AbstractFurnaceScreen<*> ->
                    screen.widthTooNarrowIn
                else ->
                    false
                }
                var clazz: Class<*> = screen.javaClass
                while (clazz != Screen::class.java) {
                    val fields = clazz.getDeclaredFields()
                    for (f in fields) {
                        if (RecipeBookGui::class.java.isAssignableFrom(f.getType())) {
                            try {
                                f.setAccessible(true)
                                val gui = f.get(screen) as RecipeBookGui?
                                if (
                                    gui != null
                                    && gui.recipeBook != null
                                    && gui.recipeBookPage != null
                                    && gui.recipeBookPage.overlay != null
                                    && gui.isVisible()
                                ) {
                                    gui.toggleVisibility()
                                    screen.guiLeft = gui.updateScreenPosition(widthTooNarrow, screen.width, screen.xSize)
                                }
                            } catch (e: IllegalAccessException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    clazz = clazz.getSuperclass()
                }
            }
        }
    }
}
