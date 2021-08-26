package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;
import static gitlet.Utils.serialize;

public class Blob implements Serializable {
    public String name;
    public byte[] fileContents;
    public String fileName;

    public Blob(File file, String f) {
        this.fileContents = Utils.readContents(file);
        this.fileName = f;
    }


    public void setSHA1() {
        byte[] temp = Utils.serialize(this);
        this.name = Utils.sha1(temp);
    }


}
