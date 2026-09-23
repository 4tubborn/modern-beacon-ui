package stubborn.beaconui.util;

public final class RuntimeEnvironment {
    private RuntimeEnvironment() {}

    private static boolean serverInstalled = false;

    public static boolean isServerInstalled() {
        return serverInstalled;
    }

    public static void setServerInstalled(boolean value) {
        serverInstalled = value;
    }
}
