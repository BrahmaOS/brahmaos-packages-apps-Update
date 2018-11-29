package Update.src.com.brahma.update.utils;

public class VersionTransfer {
    public int versionInt;
    public String versionString;

    public VersionTransfer(int version) {
        this.versionInt = version;
        this.versionString = intversion_to_strversion(version);
    }

    public VersionTransfer(String version) {
        this.versionString = version;
        this.versionInt = strversion_to_intversion(version);
    }

    public int strversion_to_intversion(String version) {
        String[] splitVersions = version.split("\\.", 3);
        int result = Integer.parseInt(splitVersions[0]) * 1000000 + Integer.parseInt(splitVersions[1]) * 1000 + Integer.parseInt(splitVersions[2]);
        return result;
    }

    public String intversion_to_strversion(int version){
        int versionFirst = version/1000000;
        int versionSecond = (version%1000000)/1000;
        int versionThird = version%1000;
        String resultStr = new String();
        resultStr = String.format("%d.%d.%d", versionFirst, versionSecond, versionThird);
        return resultStr;
    }

    @Override
    public String toString() {
        return "Version{" +
                "versionInt=" + versionInt +
                ", versionStr=" + versionString +
                '}';
    }
}

