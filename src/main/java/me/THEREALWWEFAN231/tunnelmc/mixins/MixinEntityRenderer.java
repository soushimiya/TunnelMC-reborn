package me.THEREALWWEFAN231.tunnelmc.mixins;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Collectors;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

	@Shadow public abstract TextRenderer getTextRenderer();

	@Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
	public int renderLabelIfPresent(TextRenderer instance, Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light) {
        System.out.println(text.copy().getSiblings().stream().map(Text::getString).collect(Collectors.toList()));
        return getTextRenderer().draw(text, x, y, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light);
	}
}
