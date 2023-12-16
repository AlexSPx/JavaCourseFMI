package bg.sofia.uni.fmi.mjt.photoalbum;

import bg.sofia.uni.fmi.mjt.photoalbum.Image;
import bg.sofia.uni.fmi.mjt.photoalbum.ImageQueue;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class ImageLoader implements Runnable {
    private ImageQueue images;
    private Path imagePath;
    public ImageLoader(ImageQueue images, Path imagePath) {
        this.images = images;
        this.imagePath = imagePath;
    }

    public Image loadImage(Path imagePath) {
        try {
            BufferedImage imageData = ImageIO.read(imagePath.toFile());
            return new Image(imagePath.getFileName().toString(), imageData);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath.toString()), e);
        }
    }

    @Override
    public void run() {
        images.addImage(loadImage(imagePath));
    }
}
