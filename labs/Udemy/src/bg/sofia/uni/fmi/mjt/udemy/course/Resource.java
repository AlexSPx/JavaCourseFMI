package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;

import java.util.Objects;

public class Resource implements Completable {
    private final String name;
    private final ResourceDuration duration;
    private boolean completed;

    public Resource(String name, ResourceDuration duration) {
        this.name = name;
        this.duration = duration;
        this.completed = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(name, resource.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public int getCompletionPercentage() {
        return completed ? 100 : 0;
    }

    /**
     * Returns the resource name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total duration of the resource.
     */
    public ResourceDuration getDuration() {
        return duration;
    }

    /**
     * Marks the resource as completed.
     */
    public void complete() {
        this.completed = true;
    }
}
