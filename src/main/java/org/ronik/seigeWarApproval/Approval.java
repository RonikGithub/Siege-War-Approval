package org.ronik.seigeWarApproval;

// Approval record class to store the expiration time of the approval.
public class Approval {
    private final long expirationTime;

    public Approval(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}