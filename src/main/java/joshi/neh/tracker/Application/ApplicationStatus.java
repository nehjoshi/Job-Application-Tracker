package joshi.neh.tracker.Application;

public enum ApplicationStatus {
    APPLIED("Applied"),
    OFFER("Offer"),
    WAITLISTED("Waitlisted"),
    ROUND_1("Round 1"),
    ROUND_2("Round 2"),
    ROUND_3("Round 3"),
    ROUND_4("Round 4"),
    ROUND_5("Round 5"),
    REJECTED("Rejected");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}