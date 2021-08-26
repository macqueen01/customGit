package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import gitlet.Utils.*;

public class Commit implements Serializable {

    public String message;
    public String name;
    public HashMap<String, Blob> blob;
    public String timestamp;
    public String prevCommit;
    public Commit(String message,
                  HashMap<String, Blob> prevFiles,
                  HashMap<String, Blob> addFiles,
                  String prev){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd kk:mm:ss yyyy Z");

        this.timestamp = sdf.format(c.getTime());
        this.message = message;
        this.blob = (HashMap<String, Blob>) prevFiles.clone();
        this.blob.putAll(addFiles);
        this.prevCommit = prev;
    }

    public void setSHA1() {
        byte[] temp = Utils.serialize(this);
        this.name = Utils.sha1(temp);
    }

    public String getLog() {
        return this.message;
    }

    public Commit() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd kk:mm:ss yyyy Z");
        this.timestamp = sdf.format(new Date(0));
        this.blob = new HashMap<String, Blob>();
        this.message = "initial commit";
    }

}
