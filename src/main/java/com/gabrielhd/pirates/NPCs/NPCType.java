package com.gabrielhd.pirates.NPCs;

public enum NPCType {

    SHOP, ARENAS;

    public static NPCType getByName(String str) {
        for (NPCType npcType : values()) {
            if(npcType.name().equalsIgnoreCase(str)) {
                return npcType;
            }
        }
        return null;
    }

}
