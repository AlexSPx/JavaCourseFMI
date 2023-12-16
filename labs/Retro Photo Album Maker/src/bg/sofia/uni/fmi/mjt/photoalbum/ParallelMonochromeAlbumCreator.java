package bg.sofia.uni.fmi.mjt.photoalbum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ParallelMonochromeAlbumCreator implements MonochromeAlbumCreator {
    private final ImageQueue images;
    private final int imageProcessorsCount;

    public ParallelMonochromeAlbumCreator(int imageProcessorsCount) {
        this.images = new ImageQueue();
        this.imageProcessorsCount = imageProcessorsCount;
    }

    private List<Path> getAllImagePaths(String src) throws IOException {
        List<Path> paths = new ArrayList<>();

        return Files.walk(Path.of(src))
                .filter(ParallelMonochromeAlbumCreator::isImageFile)
                .toList();
    }

    private static boolean isImageFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
    }

    @Override
    public void processImages(String sourceDirectory, String outputDirectory) throws InterruptedException, IOException {
        List<Path> imagesToProcess = getAllImagePaths(sourceDirectory);

        for (Path path : imagesToProcess) {
            Thread.ofVirtual()
                    .name("Loading Thread")
                    .start(new ImageLoader(images, path));
        }

        ProcessorManager processorManager = new ProcessorManager(imageProcessorsCount);
        System.out.println(imagesToProcess.size());
        while (!images.isEmpty() || images.getLoaded() != imagesToProcess.size() ) {
            processorManager.submit(new ImageProcessor(images.takeImage(), outputDirectory));
        }

        processorManager.shutdown();
    }
}
