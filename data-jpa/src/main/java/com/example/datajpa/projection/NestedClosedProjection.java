package com.example.datajpa.projection;

public interface NestedClosedProjection {
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
