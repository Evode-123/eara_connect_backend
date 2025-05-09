package com.earacg.earaConnect.model;

public enum MemberType {
    HEAD("Head"),
    SECRETARY("Secretary"),
    MEMBER("Member");

    private final String displayName;

    MemberType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}