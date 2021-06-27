## origami-map-test

This project provides three methods and a constructor

##### contentMap(size)
The max size of the map

##### boolean add(String key, String content)
This adds content to the store with a unique key. If the key exists it return false

#####  String get(String key)
This returns the content added by the add method. Null if not found

##### Integer getSize()
This returns the current size


### About the implementation

There are two maps used; store and tally. Store is a hash map used to contain the keys and MapData. MapData is used to contain the value and the time of entry.
When the map is full and there is a request to add another value the least viewed (by call get()) will be rejected. However if there are multiple viewed entries of the same count it will reject the oldest entry.
 
The maps used in this implementation are concurrent to prevent any multi threading concurrency problems. Also the methods are synchronised so that method can not be accessed by multiple threads. This is tested in the ContentMapTest. 

 