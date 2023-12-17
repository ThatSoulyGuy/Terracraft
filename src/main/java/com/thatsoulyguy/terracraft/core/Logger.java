package com.thatsoulyguy.terracraft.core;

import com.thatsoulyguy.terracraft.util.ANSIFormatter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Logger
{
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:ss:mm");
    public static LocalTime date = null;

    public static void WriteConsole(String message, LogLevel level)
    {
        String name = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(s -> s.skip(1).findFirst()).map(frame -> frame.getDeclaringClass().getSimpleName()).orElse("UnknownClass");
        date = LocalTime.now();

        switch (level)
        {
            case INFO ->
                    System.out.println(ANSIFormatter.Format("&2[%s&2] [Thread/INFO] [%s&2]: %s&2", formatter.format(date), name, message));

            case DEBUG ->
                    System.out.println(ANSIFormatter.Format("&1[%s&1] [Thread/DEBUG] [%s&1]: %s&1", formatter.format(date), name, message));

            case WARNING ->
                    System.out.println(ANSIFormatter.Format("&6[%s&6] [Thread/WARNING] [%s&6]: %s&6", formatter.format(date), name, message));

            case ERROR ->
                    System.err.println(ANSIFormatter.Format("&4[%s&4] [Thread/ERROR] [%s&4]: %s&4", formatter.format(date), name, message));

            case FATAL_ERROR ->
                    System.err.println(ANSIFormatter.Format("&4[%s&4] [Thread/FATAL_ERROR] [%s&4]: %s&4", formatter.format(date), name, message));
        }

        date = null;
    }
}