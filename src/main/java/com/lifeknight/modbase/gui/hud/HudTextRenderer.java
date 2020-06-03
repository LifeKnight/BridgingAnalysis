package com.lifeknight.modbase.gui.hud;

import java.util.ArrayList;

public class HudTextRenderer {
    public static ArrayList<HudText> textToRender = new ArrayList<>();

    public static void doRender() {
        for (HudText hudText: textToRender) {
            hudText.render();
        }
    }
}
