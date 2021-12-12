/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.notifications;

import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.render.Hud;
import me.zeroX150.atomic.helper.font.FontRenderers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Notification {

    public final String   title;
    public final long     creationDate;
    public       String[] contents;
    public       long     duration;
    public       double   posX;
    public       double   posY;
    public       double   renderPosX        = 0;
    public       double   renderPosY        = 0;
    public       double   animationProgress = 0;
    public       double   animationGoal     = 0;
    public       boolean  shouldDoAnimation = false;


    public Notification(long duration, String title, String... contents) {
        this.duration = duration;
        this.creationDate = System.currentTimeMillis();
        this.contents = contents;
        this.title = title;
    }

    /**
     * Creates a new notification rendered on the screen<br> If the duration is below 0, it counts as special. Special codes and their meaning:<br>
     * <ul>
     *  <li>-1: This is an error message (blinks red, doesnt remove itself)</li>
     *  <li>-2: This is a success message (blinks green, doesnt remove itself)</li>
     * </ul><br>
     * Both of these cases make the notification not expire, you have to remove it yourself by setting Notification#duration to 0
     *
     * @param duration How long the notification will stay (special cases are described above
     * @param title    What the title of the notification is (irrelevant when topBar is set)
     * @param topBar   Whether or not to show this notification at the top of the screen instead of the right
     * @param contents What the contents of the notification is
     * @return The newly created notification
     */
    public static Notification create(long duration, String title, boolean topBar, String... contents) {
        Notification n = new Notification(duration, title, contents);
        if (Objects.requireNonNull(ModuleRegistry.getByClass(Hud.class)).isEnabled()) {
            if (topBar) {
                n.posY = n.renderPosY = -69;
                NotificationRenderer.topBarNotifications.add(0, n);
            } else {
                NotificationRenderer.notifications.add(0, n);
            }
        }
        return n;
    }

    public static Notification create(long duration, String title, String... contents) {
        return create(duration, title, false, contents);
    }

    @SuppressWarnings("UnusedReturnValue") public static Notification create(long duration, String title, String split) {
        List<String> splitContent = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String c : split.split(" +")) {
            if (FontRenderers.normal.getStringWidth(line + " " + c) >= 145) {
                splitContent.add(line.toString());
                line = new StringBuilder();
            }
            line.append(c).append(" ");
        }
        splitContent.add(line.toString());
        return create(duration, title, splitContent.toArray(new String[0]));
    }
}
