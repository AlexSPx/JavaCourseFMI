package bg.sofia.uni.fmi.mjt.udemy.course.duration;

public record ResourceDuration(int minutes) {
    public ResourceDuration {
        if(!(minutes >= 0 && minutes <= 60)) {
            throw new IllegalArgumentException("Duration must be between 0 and 60 minutes");
        }
    }
}
