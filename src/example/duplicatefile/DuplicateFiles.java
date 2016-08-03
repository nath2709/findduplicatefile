package example.duplicatefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class DuplicateFiles {

    // private File inputfile;
    private Map<String, List<String>> dups = new HashMap<>();;
    private Map<Long, List<String>> dupsBysize = new HashMap<>();;
    private Map<String, List<String>> dupsByContent = new HashMap<>();;

    /**
     * find duplicate files by file name. It is assume that file with same name
     * are duplicates.
     * 
     * @param filename
     */
    public void findDuplicatesByName(String filename) {
        File inputfile = new File(filename);

        String name = "";
        File[] files = inputfile.listFiles();
        List<String> names = null;
        for (File file : files) {

            if (file.isDirectory()) {

                findDuplicatesByName(file.getAbsolutePath());

            } else {
                name = file.getName();
                if (dups.containsKey(name)) {
                    List<String> values = dups.get(name);
                    values.add(file.getAbsolutePath());
                    dups.put(name, values);
                } else {
                    names = new ArrayList<>();
                    names.add(file.getAbsolutePath());
                    dups.put(name, names);
                }
            }
        }

        // Set<String> fileKeys = dups.keySet();
        // for (String fileKey : fileKeys) {
        // List<String> filesValues = dups.get(fileKey);
        // for (String fileValue : filesValues) {
        // System.out.println(fileKey + " " + fileValue);
        // }
        // System.out.println("========================");
        // }

    }

    /**
     * find duplicates file by file size. It is assume with file with same size
     * are duplicates
     * 
     * @param filename
     * @return
     */
    public Map<Long, List<String>> findDuplicatesBySize(String filename) {
        File inputfile = new File(filename);

        long size = 0L;
        File[] files = inputfile.listFiles();
        List<String> values = null;
        for (File file : files) {

            if (file.isDirectory()) {

                findDuplicatesBySize(file.getAbsolutePath());

            } else {
                size = file.length();
                values = dupsBysize.get(size);
                if (values == null) {
                    values = new ArrayList<>();
                    dupsBysize.put(size, values);
                }
                values.add(file.getAbsolutePath());
            }
        }
        return dupsBysize;
    }

    /**
     * find duplicate files by it's content. file with same content are
     * duplicates. Content of file is read and converted into hash based on MD5
     * hash
     * 
     * @param inputfile
     */

    public void findDuplicatesByContent(File inputfile) {

        String hash = "";
        File[] files = inputfile.listFiles();
        List<String> values = null;
        for (File file : files) {
            // System.out.println(file.getAbsolutePath());
            if (file.isDirectory()) {

                findDuplicatesByContent(file);
            } else {
                // replace generatefilehash method with other generate method.
                hash = generateFileHash(file);
                if (dupsByContent.containsKey(hash)) {
                    values = dupsByContent.get(hash);
                    values.add(file.getAbsolutePath());

                } else {
                    values = new ArrayList<>();
                    values.add(file.getAbsolutePath());
                }
                dupsByContent.put(hash, values);
            }
        }
    }

    private String generateHash(File file) {
        byte[] fileContent;
        String hash = "";
        try {
            // Using Java7 Files Api to read content of file.
            fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(fileContent);
            hash = String.format("%032x", new BigInteger(1, md5.digest()));

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }

    /**
     * Another way of reading file using FileInputStream.
     * 
     * @param file
     * @return
     */
    private String generateFileHash(File file) {
        FileInputStream fis;
        byte content[] = null;
        String hash = "";
        try {
            fis = new FileInputStream(file);
            content = new byte[(int) file.length()];
            fis.read(content, 0, (int) file.length());
            fis.close();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(content);
            hash = String.format("%032x", new BigInteger(1, md5.digest()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

     public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        File file = new File(path);

        if (!file.exists() || !file.isDirectory()) {
            System.out.println("either file doesnot exists of it is not a directory ");
            scanner.close();
            return;
        }

        scanner.close();

        DuplicateFiles df = new DuplicateFiles();

        df.findDuplicatesByContent(file);

        Set<String> fileKeys = df.dupsByContent.keySet();
        for (String fileKey : fileKeys) {
            List<String> filesValues = df.dupsByContent.get(fileKey);
            for (String fileValue : filesValues) {
                System.out.println(fileKey + " " + fileValue);
            }
            System.out.println("========================");
        }

    }

}
