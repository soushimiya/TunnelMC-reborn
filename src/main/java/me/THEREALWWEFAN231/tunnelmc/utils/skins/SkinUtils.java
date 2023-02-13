package me.THEREALWWEFAN231.tunnelmc.utils.skins;

import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import lombok.experimental.UtilityClass;
import net.minecraft.client.texture.NativeImage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@UtilityClass
public class SkinUtils {

    public NativeImage toNativeImage(ImageData imageData) {
        NativeImage nativeImage = new NativeImage(imageData.getWidth(), imageData.getHeight(), true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData.getImage());
        for (int y = 0; y < imageData.getHeight(); y++) {
            for (int x = 0; x < imageData.getWidth(); x++) {
                int red = inputStream.read();
                int green = inputStream.read();
                int blue = inputStream.read();
                int alpha = inputStream.read();
                nativeImage.setColor(x, y, NativeImage.packColor(alpha, blue, green, red));
            }
        }
        return nativeImage;
    }

    public ImageData toImageData(BufferedImage bufferedImage) {
        return ImageData.from(bufferedImage);
    }
}
