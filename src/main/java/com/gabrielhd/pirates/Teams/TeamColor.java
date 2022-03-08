package com.gabrielhd.pirates.Teams;

public enum TeamColor
{
    BLUE, 
    RED;
    
    public static TeamColor getTeamColor(String name) {
        for (TeamColor color : values()) {
            if (color.name().equalsIgnoreCase(name)) {
                return color;
            }
        }
        return TeamColor.BLUE;
    }
}
