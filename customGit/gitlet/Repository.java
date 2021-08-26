
package gitlet;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

import gitlet.*;
import gitlet.Utils.*;


import static gitlet.Utils.*;
import static java.lang.System.exit;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Jae Woo Kim, Minseo Kim
 */

public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

//  public static final File REMOVE_MAP = join(GITLET_DIR, ".remove");

    public static HashMap<String, Blob> ADD_map;
    /** Remove state */
    public static HashMap<String, Blob> REMOVE_map;
    /** Blob */
    public static HashMap<String, Blob> BLOB_map;
    /** Commit */
    public static HashMap<String, Commit> COMMIT;

    public static HashMap<String, String> BRANCH;

    public static String HEAD;

    public static String headBranch;

    public static String currBranch;

    public Repository() {
        try {
            COMMIT = readObject(join(GITLET_DIR, ".commit"), HashMap.class);
        } catch (IllegalArgumentException e) {
            COMMIT = new HashMap<String, Commit>();
        } try {
            ADD_map = readObject(join(GITLET_DIR, ".add"), HashMap.class);
        } catch (IllegalArgumentException e) {
            ADD_map = new HashMap<String, Blob>();
        } try {
            REMOVE_map = readObject(join(GITLET_DIR, ".rm"), HashMap.class);
        } catch (IllegalArgumentException e) {
            REMOVE_map = new HashMap<String, Blob>();
        }
//      try {
//          REMOVE_map = readObject(STAGE_REMOVE_DIR, REMOVE_map.getClass());
//      } catch (IllegalArgumentException e) {
//          REMOVE_map = new HashMap<String, Blob>();
//      }
        try {
            HEAD = readObject(join(GITLET_DIR, ".head"), String.class);
        } catch (IllegalArgumentException e) {
            HEAD = "";
        } try {
            BLOB_map = readObject(join(GITLET_DIR, ".blob"), HashMap.class);
        } catch (IllegalArgumentException e) {
            BLOB_map = new HashMap<String, Blob>();
        } try {
            BRANCH = gitlet.Utils.readObject(join(GITLET_DIR, ".branches"), HashMap.class);
        } catch (IllegalArgumentException e) {
            BRANCH = new HashMap<>();
        } try {
            headBranch = gitlet.Utils.readObject(gitlet.Utils.join(GITLET_DIR, ".headbranch"), String.class);
        } catch (IllegalArgumentException e) {
            headBranch = "";
        } try {
            currBranch = gitlet.Utils.readObject(gitlet.Utils.join(GITLET_DIR, ".currbranch"), String.class);
        } catch (IllegalArgumentException e) {
            currBranch = "";
        }
    }

    public static void saveFiles(){
        writeObject(join(GITLET_DIR, ".blob"), BLOB_map);
        writeObject(join(GITLET_DIR, ".commit"), COMMIT);
        writeObject(join(GITLET_DIR, ".add"), ADD_map);
        writeObject(join(GITLET_DIR, ".rm"), REMOVE_map);
        writeObject(join(GITLET_DIR, ".head"), HEAD);
        gitlet.Utils.writeObject(join(GITLET_DIR, ".branches"), BRANCH);
        gitlet.Utils.writeObject(join(GITLET_DIR, ".headbranch"), headBranch);
        gitlet.Utils.writeObject(join(GITLET_DIR, ".currbranch"), currBranch);
    }

    /** TODO: fill in the rest of this class. */
    public static void init() { //init
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        } else {
            GITLET_DIR.mkdir();
            Commit commit0 = new Commit();
            commit0.setSHA1();
            String commit0_name = commit0.name;
            COMMIT.put(commit0_name, commit0);
            HEAD = commit0_name;
            headBranch = "master";
            currBranch = "master";
            BRANCH.put(headBranch, HEAD);
            saveFiles();
        }
    }

    public static void add(String fileName){
        File addFile = new File(fileName);
        if(!addFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        Blob add_wait = new Blob(addFile, fileName);
        add_wait.setSHA1();
        Commit currCommit = COMMIT.get(HEAD);
        if (REMOVE_map.containsKey(fileName) &&
                REMOVE_map.get(fileName).name.equals(add_wait.name)) {
            currCommit.blob.put(fileName, REMOVE_map.get(fileName));
            REMOVE_map.remove(fileName);
        } else if(!(currCommit.blob.containsKey(fileName)
                && currCommit.blob.get(fileName).name.equals(add_wait.name))) {
            ADD_map.put(fileName, add_wait);
            BLOB_map.put(add_wait.name, add_wait);
        }
        saveFiles();
    }


    public static void log(){
        Commit curr = COMMIT.get(HEAD);
        while(curr != null) {
            printLog(curr);
            curr = COMMIT.get(curr.prevCommit);
        }
    }

    private static void printLog(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.name);
        System.out.println("Date: "+commit.timestamp);
        System.out.println(commit.getLog());
        System.out.println();
    }

    public static void commit(String message) {
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        }
        if(ADD_map.isEmpty() && REMOVE_map.isEmpty()){
            System.out.println("No changes added to the commit.");
            return;
        }
        Commit newCommit = new Commit(message, COMMIT.get(HEAD).blob, ADD_map, HEAD);
        newCommit.setSHA1();
        COMMIT.put(newCommit.name, newCommit);
        HEAD = newCommit.name;
        if (headBranch == currBranch) {
            BRANCH.replace(headBranch, HEAD);
        } else {
            BRANCH.replace(currBranch, HEAD);
        }
        ADD_map = new HashMap<>();
        REMOVE_map = new HashMap<>();
        saveFiles();
    }

    public static void setBranch(String newBranch) {
        if (!BRANCH.containsKey(newBranch)) {
            BRANCH.put(newBranch, HEAD);
        } else {
            System.out.println("A branch with that name already exists.");
            return;
        }
        saveFiles();
    }


    public static void checkout(String sha, String fileName) {

        Commit currCommit = COMMIT.get(sha); //HEAD
        Blob oldFile = currCommit.blob.get(fileName);
        if (oldFile==null) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        gitlet.Utils.writeContents(gitlet.Utils.join(CWD, fileName), oldFile.fileContents);
        saveFiles();
    }

    public static void checkout(String id, String HEAD2, String fileName) {

        if(id.length()<=40) {
            for (String c : COMMIT.keySet()) {
                if (c.contains(id)) {
                    id = c;
                    break;
                }
            }
        }
        Commit want = COMMIT.get(id);
        if (want == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Blob oldBlob = want.blob.get(fileName);
        if (oldBlob == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        Utils.writeContents(Utils.join(CWD, fileName), oldBlob.fileContents);
        saveFiles();
    }

    public static void checkout(String branch) {

        if (!BRANCH.containsKey(branch)) {
            System.out.println("No such branch exists.");
            return;
        } else if (currBranch.equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        List<String> files = gitlet.Utils.plainFilenamesIn(CWD);
        Commit currCommit = COMMIT.get(HEAD);
        Commit nextCommit = COMMIT.get(BRANCH.get(branch));

        for (String f: files) {
            File file = new File(f);
            Blob b = new Blob(file, f);
            b.setSHA1();
            if (currCommit.blob.containsKey(f)) {
                if (!currCommit.blob.get(f).name.equals(b.name)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return;
                }
            } else if (ADD_map.containsKey(f)) {
                if (!ADD_map.get(f).name.equals(b.name)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return;
                }
            } else {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        for (String b: nextCommit.blob.keySet()) {
            Utils.writeContents(join(CWD, b), nextCommit.blob.get(b).fileContents);
        }

        for (String f: files) {
            if (!nextCommit.blob.containsKey(f)) {
                Utils.restrictedDelete(f);
            }
        }

        currBranch = branch;
        HEAD = nextCommit.name;
        saveFiles();
    }

    public static void globalLog(){
        for(Commit commit : COMMIT.values()) {
            printLog(commit);
        }
    }

    public static void find(String message) {
        boolean temp = false;
        for(Commit commit : COMMIT.values()) {
            if(commit.message.equals(message)) {
                System.out.println(commit.name);
                temp = true;
            }
        }
        if(!temp) {
            System.out.println("Found no commit with that message.");
            return;
        }
    }

    public static void rm(String fileName) {
        Blob b = COMMIT.get(HEAD).blob.get(fileName);
        if (COMMIT.get(HEAD).blob.containsKey(fileName)) {
            REMOVE_map.put(fileName, b);
            ADD_map.remove(fileName);
            Utils.restrictedDelete(fileName);
            COMMIT.get(HEAD).blob.remove(fileName);
        } else if (ADD_map.containsKey(fileName)) {
            ADD_map.remove(fileName);
        } else {
            System.out.println("No reason to remove the file.");
        }
        saveFiles();
    }

    public static void rmBranch(String branch) {
        if (currBranch.equals(branch)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        if (!BRANCH.containsKey(branch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        BRANCH.remove(branch);
        saveFiles();
    }

    public static void reset(String id) {

        if (!COMMIT.containsKey(id)) {
            System.out.println("No commit with that id exists.");
            return;
        }

        List<String> files = gitlet.Utils.plainFilenamesIn(CWD);
        Commit currCommit = COMMIT.get(HEAD);
        Commit nextCommit = COMMIT.get(id);

        for (String f: files) {
            File file = new File(f);
            Blob b = new Blob(file, f);
            b.setSHA1();
            if (currCommit.blob.containsKey(f)) {
                if (!currCommit.blob.get(f).name.equals(b.name)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return;
                }
            } else if (ADD_map.containsKey(f)) {
                if (!ADD_map.get(f).name.equals(b.name)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return;
                }
            } else {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }


        for (String b: nextCommit.blob.keySet()) {
            Utils.writeContents(join(CWD, b), nextCommit.blob.get(b).fileContents);
        }

        for (String f: files) {
            if (!nextCommit.blob.containsKey(f)) {
                Utils.restrictedDelete(f);
            }
        }

        BRANCH.replace(currBranch, id);
        HEAD = id;
        ADD_map.clear();
        REMOVE_map.clear();
        saveFiles();
    }

    public static void status() {

        ArrayList<String> branches = new ArrayList<>(BRANCH.keySet());
        Collections.sort(branches);
        System.out.println("=== Branches ===");
        for (String branch : branches) {
            if (headBranch.equals(branch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println("\n=== Staged Files ===");
        for (String files : ADD_map.keySet()) {
            System.out.println(files);
        }
        System.out.println("\n=== Removed Files ===");

        for (String files : REMOVE_map.keySet()) {
            System.out.println(files);

        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        System.out.println("\n=== Untracked Files ===");
        System.out.println();
        saveFiles();
    }


    public void merge(String branchname) {
    }


}


