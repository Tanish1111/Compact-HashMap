import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tgupta
 * Date: 7/2/23
 * Time: 10:22 AM
 */

/**
 * The CompactHashMap class is a custom implementation of a hash map that allows for storing multiple values for a
 * single key while minimizing memory usage. It achieves this by creating a map of combinations, where each unique set
 * of values is assigned a unique ID, and then using this ID to store and retrieve the data in the underlying data
 * structure, which is an array list of hash maps
 * <p>
 * For iterating this map please first take all the items of arraylist and then iterate over the maps from each of the elements.
 */
public class CompactHashMap<V> {
    /**
     * The CompactHashMap class has three instance variables: combinationsMap is a BiMap<Integer, Set<V>> that stores the
     * integer IDs and associated Set<V> instances; dataMapList is a ArrayList<Map<Long, Integer>> that stores the K keys and associated
     * integer IDs; and count is an integer that keeps track of the highest ID used so far
     */
    private BiMap<Integer, Set<V>> combinationsMap;
    private ArrayList<Map<Long, Integer>> dataMapList;
    private int count = 0;

    /**
     * The constructor initializes the instance variables: dataMap is initialized with a new Arraylist and putting ten
     * new hashMap in it as modulus cannot exceed the value 9;
     * combinationsMap is initialized with a new HashBiMap from the Guava library; and count is set to zero.
     */
    public CompactHashMap() {
        dataMapList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dataMapList.add(i, new HashMap<>());
        }
        combinationsMap = HashBiMap.create();
        count = 0;
    }

    /**
     * This method inserts a key-value pair into the map. It first calculates the index of
     * the data using the modulo operator and retrieves the corresponding hash map from the array list. If the key
     * already exists in the hash map, it retrieves the ID associated with it from the map of combinations and adds
     * the new value to the set of values associated with that ID. If the new set of values already exists in the map of
     * combinations, it retrieves the ID associated with it, otherwise it creates a new ID and adds the set of values
     * to the map of combinations. It then updates the hash map with the new ID. If the key does not already exist in
     * the hash map, it creates a new set of values and adds it to the map of combinations, retrieves the ID associated
     * with it (or creates a new one if it does not exist), and updates the hash map with the new ID
     */
    public void put(long data, V subData) {
        int index = (int) (data % 10);
        Map<Long, Integer> dataMap = dataMapList.get(index);
        if (dataMap.containsKey(data)) {
            int id = dataMap.get(data);
            Set<V> objectSet = combinationsMap.get(id);
            if (!objectSet.contains(subData)) {
                objectSet = new HashSet<>(combinationsMap.get(id));
                objectSet.add(subData);
            }
            int newid = -1;
            if (combinationsMap.inverse().containsKey(objectSet)) {
                newid = combinationsMap.inverse().get(objectSet);
                if (newid < 0) {
                    throw new RuntimeException("Error occurred");
                }
            }
            if (newid < 0) {
                newid = ++count;
                combinationsMap.put(newid, objectSet);
            }
            dataMap.put(data, newid);
        } else {
            int newId = -1;
            Set<V> combinationsSet = new HashSet<>();
            combinationsSet.add(subData);
            if (combinationsMap.inverse().containsKey(combinationsSet)) {
                newId = combinationsMap.inverse().get(combinationsSet);
            }
            if (newId < 0) {
                newId = ++count;
                combinationsMap.put(newId, combinationsSet);
            }
            dataMap.put(data, newId);
            int percent = (int) (data % 10);
            Map<Long, Integer> map = dataMapList.get(percent);
            map = dataMap;
        }
    }

    /**
     * This method removes the key-value pair with the given key from the map by iterating
     * over all the hash maps in the array list and removing the key if it exists.
     */
    public void delete(java.lang.Long data) {
        for (Map<Long, Integer> map : dataMapList) {
            if (map.containsKey(data)) {
                map.remove(data);
                break;
            }
        }
    }

    /**
     * This method returns the size of the array list
     */
    public int getListSize() {
        return dataMapList.size();
    }

    /**
     * This method returns the hash map at the given index of the array list
     */
    public Map<Long, Integer> get(int index) {
        return dataMapList.get(index);
    }

    /**
     * This method retrieves the set of values associated with the given key by first retrieving the ID
     * associated with the key from the hash map and then retrieving the set of values associated with that ID
     * from the map of combinations
     */
    public Set<V> getValues(Long data) {
        int combinationId = -1;
        int index = (int) (data % 10);
        combinationId = dataMapList.get(index).get(data);
        if (combinationsMap.containsKey(combinationId)) {
            return new HashSet<>(combinationsMap.get(combinationId));
        }
        return null;
    }

    /**
     * This method checks if the map contains the given key by checking if the key exists in the
     * hash map at the index calculated using the modulo operator
     */
    public boolean contains(Long data) {
        int index = (int) (data % 10);
        return dataMapList.get(index).containsKey(data);
    }

    /**
     * This method returns the total number of key-value pairs in the map by iterating over all the hash maps in the array list and summing their sizes
     */
    public int getSize() {
        int size = 0;
        for (Map<Long, Integer> map : dataMapList) {
            if (CollectionUtils.isNotEmpty(Collections.singleton(map))) {
                size = size + map.size();
            }
        }
        return size;
    }

    /**
     * This method clears the map by clearing both the array list and the map of combinations
     */
    public void clear() {
        dataMapList.clear();
        combinationsMap.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompactHashMap<?> that = (CompactHashMap<?>) o;
        return count == that.count && Objects.equals(combinationsMap, that.combinationsMap) && Objects.equals(dataMapList, that.dataMapList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(combinationsMap, dataMapList, count);
    }
}
