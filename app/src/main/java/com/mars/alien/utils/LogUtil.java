package com.mars.alien.utils;

import android.util.Log;

public class LogUtil {
    private final static boolean WRITE_LOG_TO_LOCAL_ENABLE = true;
    private final static String TAG = "TAG";

    public static void v(String tag, String format, Object... args) {
        if (WRITE_LOG_TO_LOCAL_ENABLE) {
            String string = format;
            try {
                string = String.format(format, args);
            } catch (Exception ignored) {
            }

            if (string == null) {
                string = "";
            }
            Log.v(tag, string);
        }
    }

    public static void v(String format, Object... args) {
        if (WRITE_LOG_TO_LOCAL_ENABLE) {
            String string = format;
            try {
                string = String.format(format, args);
            } catch (Exception ignored) {
            }

            if (string == null) {
                string = "";
            }
            Log.v(TAG, string);
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (WRITE_LOG_TO_LOCAL_ENABLE) {
            String string = format;
            try {
                string = String.format(format, args);
            } catch (Exception ignored) {
            }

            if (string == null) {
                string = "";
            }
            Log.i(tag, string);
        }
    }

    public static void i(String format, Object... args) {
        if (WRITE_LOG_TO_LOCAL_ENABLE) {
            String string = format;
            try {
                string = String.format(format, args);
            } catch (Exception ignored) {
            }

            if (string == null) {
                string = "";
            }
            Log.i(TAG, string);
        }
    }

    public static void d(String tag, String format, Object... args) {
        if (WRITE_LOG_TO_LOCAL_ENABLE) {
            String string = format;
            try {
                string = String.format(format, args);
            } catch (Exception ignored) {
            }

            if (string == null) {
                string = "";
            }
            Log.d(tag, string);
        }
    }

    public static void d(String format, Object... args) {
        if (WRITE_LOG_TO_LOCAL_ENABLE) {
            String string = format;
            try {
                string = String.format(format, args);
            } catch (Exception ignored) {
            }

            if (string == null) {
                string = "";
            }
            Log.d(TAG, string);
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (WRITE_LOG_TO_LOCAL_ENABLE) {
            String string = format;
            try {
                string = String.format(format, args);
            } catch (Exception ignored) {
            }

            if (string == null) {
                string = "";
            }
            Log.w(tag, string);
        }
    }

    public static void w(String format, Object... args) {
        if (WRITE_LOG_TO_LOCAL_ENABLE) {
            String string = format;
            try {
                string = String.format(format, args);
            } catch (Exception ignored) {
            }

            if (string == null) {
                string = "";
            }
            Log.w(TAG, string);
        }
    }

    public static void e(String tag, String format, Object... args) {
        if (WRITE_LOG_TO_LOCAL_ENABLE) {
            String string = format;
            try {
                string = String.format(format, args);
            } catch (Exception ignored) {
            }

            if (string == null) {
                string = "";
            }
            Log.e(tag, string);
        }
    }

    public static void e(String format, Object... args) {
        if (WRITE_LOG_TO_LOCAL_ENABLE) {
            String string = format;
            try {
                string = String.format(format, args);
            } catch (Exception ignored) {
            }

            if (string == null) {
                string = "";
            }
            Log.e(TAG, string);
        }
    }
}
