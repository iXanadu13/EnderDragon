package pers.xanadu.enderdragon.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentSet<T> extends AbstractSet<T> implements Serializable {
    private static final long serialVersionUID = 1072237514L;
    private final ConcurrentHashMap<T, Boolean> map = new ConcurrentHashMap<>();

    public ConcurrentSet() {
    }

    public int size() {
        return this.map.size();
    }

    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    public boolean add(T o) {
        return this.map.putIfAbsent(o, Boolean.TRUE) == null;
    }

    public boolean remove(Object o) {
        return this.map.remove(o) != null;
    }

    public void clear() {
        this.map.clear();
    }

    public Iterator<T> iterator() {
        return this.map.keySet().iterator();
    }

}
