package bg.sofia.uni.fmi.mjt.photoalbum;

import java.util.LinkedList;
import java.util.Queue;

public class ImageQueue {
    private final Queue<Image> images;
    private int loaded = 0;

    public ImageQueue() {
        this.images = new LinkedList<>();
    }

    public synchronized void addImage(Image img) {
        images.add(img);
        loaded++;
        this.notifyAll();
    }

    public synchronized Image takeImage() {
        while (images.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Image img = images.poll();
        this.notifyAll();
        return img;
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }

    public int getLoaded() {
        return loaded;
    }

    public void resetLoaded() {
        this.loaded = 0;
    }
}
