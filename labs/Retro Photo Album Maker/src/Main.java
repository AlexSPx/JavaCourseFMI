import bg.sofia.uni.fmi.mjt.photoalbum.MonochromeAlbumCreator;
import bg.sofia.uni.fmi.mjt.photoalbum.ParallelMonochromeAlbumCreator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        MonochromeAlbumCreator albumCreator = new ParallelMonochromeAlbumCreator(7);

        albumCreator.processImages("images", "bwimages");
    }
}
