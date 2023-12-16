package bg.sofia.uni.fmi.mjt.photoalbum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessor implements Runnable {

    private final Image image;
    private final String imagePath;
    public ImageProcessor(Image image, String imagePath) {
        this.image = image;
        this.imagePath = imagePath;
    }

    private Image convertToBlackAndWhite(Image image) {
        BufferedImage processedData = new BufferedImage(
                image.data.getWidth(),
                image.data.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        processedData.getGraphics().drawImage(image.data, 0, 0, null);

        return new Image(image.name, processedData);
    }

    @Override
    public void run() {
        Image bwImage = convertToBlackAndWhite(image);

        try {
            ImageIO.write(bwImage.data, "jpg", new File(imagePath, bwImage.name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
