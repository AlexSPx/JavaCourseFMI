package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotCompletedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.util.Objects;

public abstract class AccountBase implements Account {
    private final String username;
    private final AccountType accountType;
    private double balance;
    private final Course[] courses;
    private final double[] grades;
    private int courseCount;

    public AccountBase(String username, double balance) {
        this(username, balance, AccountType.STANDARD);
    }

    public AccountBase(String username, double balance, AccountType accountType) {
        this.username = username;
        this.balance = balance;
        this.courses = new Course[100];
        this.courseCount = 0;
        this.grades = new double[100];
        this.accountType = accountType;
    }

    private void courseCapacityCheck() throws MaxCourseCapacityReachedException {
        if(courseCount == 100) {
            throw new MaxCourseCapacityReachedException("Max course capacity reached");
        }
    }

    private void purchasePriceCheck(double price) throws InsufficientBalanceException {
        if(price > balance) {
            throw new InsufficientBalanceException("Not enough balance to purchase this course");
        }
    }

    private void alreadyPurchasedCheck(Course course) throws CourseAlreadyPurchasedException {
        for(int i = 0; i < courseCount; i++) {
            if(courses[i].equals(course)) throw new CourseAlreadyPurchasedException("Course already purchased");
        }
    }

    protected void buyCourse(Course course, double price) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        courseCapacityCheck();
        alreadyPurchasedCheck(course);
        purchasePriceCheck(price);

        courses[courseCount++] = course;
        balance -= price;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        buyCourse(course, course.getPrice());
    }

    @Override
    public String getUsername() {
        return username;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    @Override
    public void addToBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }

        this.balance += amount;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException {
        for(int i = 0; i < courseCount; i++) {
            if(courses[i].equals(course)) {
                for(Resource toComplete : resourcesToComplete) {
                    courses[i].completeResource(toComplete);
                }
                return;
            }
        }

        throw new CourseNotPurchasedException("Course not purchased");
    }

    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        for(int i = 0; i < courseCount; i++) {
           if(courses[i].equals(course)) {
                if(!course.isCompleted()) throw new CourseNotCompletedException("Course not completed");
                grades[i] = grade;
               return;
           }
        }

        throw new CourseNotPurchasedException("Course not purchased");

    }

    @Override
    public Course getLeastCompletedCourse() {
        Course leastCompleted = courses[0];
        for (int i = 1; i < courseCount; i++) {
            if(leastCompleted.getCompletionPercentage() > courses[i].getCompletionPercentage()) {
                leastCompleted = courses[i];
            }
        }

        return leastCompleted;
    }

    public Course[] getCourses() {
        return courses;
    }

    public double[] getGrades() {
        return grades;
    }

    public int getCourseCount() {
        return courseCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountBase that = (AccountBase) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
