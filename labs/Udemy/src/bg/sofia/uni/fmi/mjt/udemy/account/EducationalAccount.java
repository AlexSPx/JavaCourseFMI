package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class EducationalAccount extends AccountBase {
    private int lastDiscountIndex;
    public EducationalAccount(String name, double balance) {
        super(name, balance, AccountType.EDUCATION);
        this.lastDiscountIndex = 0;
    }

    private double lastFiveGradeAverage() {
        int courseCount = super.getCourseCount();
        double[] grades = super.getGrades();
        double gradeSum = 0;
        for(int i = courseCount - 5; i < courseCount; i++) {
           gradeSum += grades[i];
        }

        return gradeSum / 5;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        int courseCount = super.getCourseCount();
        if((courseCount - lastDiscountIndex) >= 5 && lastFiveGradeAverage() >= 4.5) {
            buyCourse(course, course.getPrice() - (course.getPrice() * getAccountType().getDiscount()));
            lastDiscountIndex = super.getCourseCount();
        } else {
            buyCourse(course, course.getPrice());
        }
    }
}
