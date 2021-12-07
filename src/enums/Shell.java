package enums;

public enum Shell {
    POWERSHELL("powershell", "C:\\Windows\\System32\\WindowsPowerShell\\v1.0",'-'),
    CMD("cmd", "C:\\Windows\\system32", '/'),
    SH("sh", "", '-'),
    BUSH("bush", "C:\\Windows\\system32", '-');

    private final String name, path;
    private final char argS; // Argument separator

    private Shell(String name, String path, char argS) {
        this.name = name;
        this.path = path;
        this.argS = argS;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
    public char getArgS() {
        return argS;
    }

    public String getExec() {
        return this.getName() + " " + this.getArgS() + "c ";
    }
}
