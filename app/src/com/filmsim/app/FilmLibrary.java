package com.filmsim.app;

import android.content.res.AssetManager;
import java.io.*;
import java.util.*;

/** One identity look plus validated matrix files from Matrix. */
public class FilmLibrary {
    public static class Film {
        public final String file, name;
        public final File external;
        Film(String file, String name, File external) {
            this.file=file; this.name=name; this.external=external;
        }
        public boolean designed() { return true; }
        public int stars() { return 3; }
    }
    public static class Look {
        public final int[] gamma = new int[1024];
        public final int[] matrix = new int[9];
    }
    private final List<Film> films = new ArrayList<Film>();
    public final List<String> scanLog = new ArrayList<String>();
    public String storageError;
    public FilmLibrary(AssetManager assets, File directory) {
        films.add(new Film("identity", "No Change", null));
        try { scan(assets, directory); }
        catch (IOException e) {
            storageError = e.toString();
            android.util.Log.w("MatrixTest", "Matrix folder unavailable", e);
        }
    }
    private void scan(AssetManager assets, File directory) throws IOException {
        if (!directory.isDirectory() && !directory.mkdirs()) throw new IOException("Cannot create " + directory);
        File seed = new File(directory, "CHEN.BIN");
        // Never overwrite a user's edited file. A temporary file avoids partial seeds.
        if (!seed.exists()) {
            File temp = new File(directory, "CHEN.TMP");
            InputStream in = assets.open("films/matrix.bin");
            try {
                FileOutputStream out = new FileOutputStream(temp);
                try {
                    byte[] bytes = new byte[4096]; int n;
                    while ((n=in.read(bytes))!=-1) out.write(bytes,0,n);
                } finally { out.close(); }
            } finally { in.close(); }
            if (!temp.renameTo(seed)) throw new IOException("Cannot save " + seed);
        }
        scanLog.add("scan directory=" + directory);
        File[] files = directory.listFiles();
        if (files == null) throw new IOException("Cannot read " + directory);
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File a, File b) { return a.getName().compareToIgnoreCase(b.getName()); }
        });
        for (File file : files) {
            scanLog.add("entry name=" + file.getName() + " file=" + file.isFile() + " bytes=" + file.length());
            if (!file.isFile() || file.getName().startsWith(".")) continue;
            try {
                read(file); // Reject corrupt or incompatible files before listing them.
                String name=file.getName();
                String label = name.toUpperCase(Locale.US).endsWith(".BIN") ? name.substring(0,name.length()-4) : name;
                films.add(new Film(name, label, file));
                scanLog.add("accepted " + name);
            } catch (IOException e) {
                scanLog.add("rejected " + file.getName() + ": " + e);
                android.util.Log.w("MatrixTest", "Skipping " + file, e);
            }
        }
    }
    public List<Film> all() { return films; }
    public Film find(String key) {
        for (Film film : films) if (film.file.equalsIgnoreCase(key)) return film;
        return null;
    }
    public static Look load(AssetManager assets, Film film) throws IOException {
        if (film.external != null) return read(film.external);
        Look identity = new Look();
        for (int i=0;i<1024;i++) identity.gamma[i]=i;
        identity.matrix[0]=identity.matrix[4]=identity.matrix[8]=1024;
        return identity;
    }
    private static Look read(File file) throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        try {
            if (in.readInt()!=1) throw new IOException("Unsupported matrix version");
            Look look=new Look();
            for (int i=0;i<1024;i++) {
                int value=in.readInt();
                if (value<0 || value>1023) throw new IOException("Tone entry outside 0..1023");
                look.gamma[i]=value;
            }
            for (int i=0;i<9;i++) look.matrix[i]=in.readInt();
            if (in.read()!=-1) throw new IOException("Trailing bytes after 4136-byte payload");
            return look;
        } finally { in.close(); }
    }
}
