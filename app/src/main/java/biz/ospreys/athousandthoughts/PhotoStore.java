package biz.ospreys.athousandthoughts;

import java.util.*;

public final class PhotoStore {
    private PhotoStore() {}
    private static final Map<String, Set<String>> photos = new LinkedHashMap<>(); // uri -> tags

    public static synchronized void addPhotos(List<String> uris) {
        for (String u : uris) if (!photos.containsKey(u)) photos.put(u, new TreeSet<>());
    }
    public static synchronized int count() { return photos.size(); }

    /** Photos having ALL requiredTags, newest first. */
    public static synchronized List<String> getUris(Set<String> requiredTags) {
        List<String> out = new ArrayList<>();
        for (Map.Entry<String, Set<String>> e : photos.entrySet())
            if (e.getValue().containsAll(requiredTags)) out.add(e.getKey());
        Collections.reverse(out);
        return out;
    }
    public static synchronized List<String> getAllTags() {
        Set<String> all = new TreeSet<>();
        for (Set<String> t : photos.values()) all.addAll(t);
        return new ArrayList<>(all);
    }
    public static synchronized Set<String> getTags(String uri) {
        Set<String> t = photos.get(uri);
        return t == null ? new TreeSet<>() : new TreeSet<>(t);
    }
    public static synchronized void addTag(String uri, String tag) {
        Set<String> t = photos.get(uri);
        if (t != null) t.add(tag);
    }
    public static synchronized void removeTag(String uri, String tag) {
        Set<String> t = photos.get(uri);
        if (t != null) t.remove(tag);
    }
}
