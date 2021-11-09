package gitlet;


import static gitlet.Repository.HEAD;
import static java.lang.System.exit;
import gitlet.Utils;

/** Driver class for Gitlet, a subset of the Git version-control system.

 *  @author Jae Woo Kim

 */

public class Main {
    public static void main(String[] args) {

        String firstArg;
        try {
            firstArg = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please enter a command.");
            return;
        }
        Repository repo = new Repository();
        switch (firstArg) {

            case "init":

                if (checkArgs(args, 1)) {
                    repo.init();
                }

                break;
            case "add":

                if (checkArgs(args, 2)) {

                    repo.add(args[1]);

                }

                break;

            case "log":

                if (checkArgs(args, 1)) {

                    repo.log();

                }
                break;

            case "commit":
                if (checkArgs(args, 2)) {
                    repo.commit(args[1]);
                }
                break;

            case "checkout":
                if (checkArgs(args, 3)) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                        return;
                    }
                    repo.checkout(HEAD, args[2]);
                }
                if (checkArgs(args, 4)) {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        return;
                    }
                    repo.checkout(args[1], HEAD, args[3]);
                }
                if (checkArgs(args, 2)) {
                    repo.checkout(args[1]);
                }

                break;
            case "global-log":
                if (checkArgs(args, 1)) {
                    repo.globalLog();
                }
                break;
            case "find":
                if (checkArgs(args, 2)) {
                    repo.find(args[1]);
                }
                break;

            case "status":
                if (checkArgs(args,1 )) {
                    if (repo.HEAD == "") {
                        System.out.println("Not in an initialized Gitlet directory.");
                        return;
                    } else {
                        repo.status();
                    }
                }
                break;
            case "branch":
                if (checkArgs(args, 2)) {
                    String branchName = args[1];
                    repo.setBranch(branchName);
                }
                break;

            case "rm-branch":
                if (checkArgs(args, 2)) {
                    String branchName = args[1];
                    repo.rmBranch(branchName);
                }
                break;

            case "reset":
                if (checkArgs(args, 2)) {
                    repo.reset(args[1]);
                }
                break;

            case "rm":
                if (checkArgs(args, 2)){
                    repo.rm(args[1]);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                return;

        }
    }

    public static boolean checkArgs(String[] args, int length){
        if (args.length == length){
            return true;
        }
        return false;

    }
}