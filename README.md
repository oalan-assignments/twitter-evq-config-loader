## EVQ Config Loader

### Getting Started

#### How to run the example

#### How to run tests

### Design 

**Please do not spend more than 3 hours on this challenge. Rather than spending more
time, please leave notes in the README as to what you would do if you had more time.**

Key requirements:
* Property types should be retained
* Group property order should be retained (use: Ordered Map)
* loadConfig() should be as fast as possible
* Config files can get quite lengthy
  * May not fit into memory of one node. Therefore sharding is needed. Another option could be backing it up in the disk in an efficient way
* **Arbitrary number of groups and number of settings within each group**
  * Hints that if we shard by groups, shard can be skewed, so we have to handle that (if time permits -- See further discussion)
* Config object's query responses should be fast
  * O(1) lookup via maps
  * Use further caching
* Certain queries will be made frequently (thousands of times), others pretty rarely
  * Use of LRU cache may be useful depending on the design. Let's discuss that...
* If the config file is not well-formed, it is acceptable to print an error and exit from within
  loadConfig(). Once the object is returned, however, it is not permissible to exit or
  crash no matter what the query is. **Returning an empty value is acceptable, however.**
  * We can perform a very simple check and continue, log the errors and return None for errornous lines to avoid load time full parsing and checking to speed up load time.
* Every line can have comment: **Hence it is required to hadle them and get the content properly**

Key assumptions:
* Configs are static and does not change dynamically
* Configs are flat (not hierarchical)
* There is one (potentially big) config file

* Use of config for worker thresholds???

* Main actor just looks at group start and end and if a group starts, spawns a new actor and send lines to it for let it create the index. Main actor just holds a ref to groups and its actors
* Each worker actor (ConfigIndex) gets a line as (key, value, override) and keeps them in a map of (key, List(value, override)) then should map it to final (key, value) pair.

* If all actors sends an ack we can compete the load. However, we still actually did not parse the values. This is a tradeoff to have quick load time with the expense of potentially having none for some keys. We could just log errors and continue. Rather than waiting to see if each line is correct
  * Parsing and binding the right time can be time consuming (particularly considering potential exceptions)

* Actor should return a suggestion when a config is there but requested with wrong type...

* **Main actor can keep a LRU cache** 
  * LRU for keeping results of actual parsing and type binding! Cache for each type or casting? 
  * It is ok because there are only handful types
  * Particularly useful if actors are distributed
  * If main actor can find the property in the cache it can just return it otherwise it would need to reach to worker to get it

* Is there a better data structure than Map to keep the properties?

* Separation of concerns:
  * Main actor only skims through to file to create index nodes by collaborating with a line reader. Then just keep track of which group is in which actor. It responds to queries as well by keeping it in a Config object???
  * Decoupled:
    * Line split (just creates key, value pairs) and checks if they are looking legit. A valid key and non-empty value
    * Value parsing. Performs type binding and reports if failed

* **Execution**
  * (optional) validate
  split (returns key -> lines) (ideally should return key -> files)
  Maybe balance it here... (use a config balancer) 
  QueryLayer 
  * creates ConfigNode per key and let ConfigNode (which in return use a parser and maybe validator) load it
  * initialise LRU cach


#### Domain Model, Contracts and Class Collaborations

**Discuss separations and interfaces**
**Discuss extensibility**

### Future Work/Discussion

* No dependency Injection but created contracts and concrete implementations. Can be easily implemented once there are more than one implementations
* 
* Distributing workers with actors (akka-remote/akka-cluster)
* Handle duplicate groups and keys
* Use bloom filter for faster response 
* What if configs change dynamically?
* What if configs need to become hierarchical? (Did we make design flexible enough to accommodate that)
* What about splitting the file first?
* Need of balancing the data load of nodes:
  * Actor can spawn another once reach a threshold
  * Main actor, instead of spawning a new actor per group can wait until a threshold (given by a config)
* **Worker nodes could potentially continue parsing the values after first split of lines (using = split)** 
  * We can not do arbitrary slicing because we would lose group information
  * So we need to keep track of group info. One option would be prepending the group into property at the time of slicing. We would need to do a profiling though if this approach is better than (because it requires a process to split file still) main actor sending lines to the actors. 
* Make clear separation between IT and UT
* More unhappy path tests. E.g:
  * ???
* Add more value decoders:
  * Array[Int], Array[Double] etc
* Parameterized (reading from configs) constants, e.g. timeout values