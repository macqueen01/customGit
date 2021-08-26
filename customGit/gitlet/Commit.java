package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** The message of this Commit. */
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
