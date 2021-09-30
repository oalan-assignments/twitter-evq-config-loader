# EVQ Config Loader

---
Author:  
[Ozgun Alan](mailto:losthimself@gmail.com)
---

## Getting Started

Dependencies
* AdoptOpenJDK 11.0.9.hs
* Scala 2.13.5
* Sbt 1.4.6

Tested on Linux Ubuntu

Run example:
```
sbt run
```
`EvqConfigTestRun` object will load sample config and values will be printed

Run tests:

```
sbt test
```

## Design 

Considering the indication:
>Please do not spend more than 3 hours on this challenge. Rather than spending more
time, please leave notes in the README as to what you would do if you had more time.

I aimed to implement a system that can address most of the design considerations (at least in a basic way). I discussed missing parts in the last part of this document. Current draft implementation contain contracts, so missing parts could be implemented without too much effort in further iterations.

My approach to requirements/assumptions are below. For the list of missing parts, see the last section

### Assumptions:

* Configs are static and do not change dynamically

* Configs are flat (not hierarchical)

* There is one (potentially big) config file


### Requirements:

#### Group config order should be retained
For able to retain order of configs used LinkedHashMap

#### loadConfig() will be called at boot time, thus should be as fast as possible

This hinted me that load operation should not block and affect the boot time. Therefore, I decided to stream the file and handle loading asynchronously using `Akka` actors. For this purpose I implemented two collaborating objects:
* `Config.Reader` (`LineAwareConfigFileReader`): wraps a file iterator and returns specific line objects (Group, Property, Comment, Blank) which is pattern matched by the loader for further processing
* `Config.Loader` (`InMemoryConfigLoader`): gets type line objects from the reader and per group creates an actor (`InMemoryGroupNode`), then streams config lines to the actor via messages. A group actor would:
  * extract key value pairs and keep them in memory
  * resolve overrides
  * serve the configs for that group once asked

It is aimed tp quickly go through to lines, offload relevant lines to the nodes (actors) so the nodes can perform further processing asynchronously.

Another option could be splitting the file and then let worker nodes work on each file. However, it still requires an initial processing not so different from the current approach. 

The tradeoff:
* Apart from obvious failure scenarios (e.g. file not found) there is not much validation happening at the load time. If the config line is invalid, it is logged as an error. I decided that it is a good tradeoff for the system to return None for invalid properties, rather than coordinating/spending time for validations at the load time. This may not be right choice in some scenarios (e.g. if the client strictly depends on the existence of all properties)


#### Configs should be returned with their types 

In Scala, it is not possible to retain multiple types in a collection (in this case a map). Trying to do it would yield a collection with type `Any` and we would need to cast to the type we expected for.

I decided to keep key and values as strings and provided a typed (generic) method (see `get` in `trait Group`) to get values in the desired type. This method make use of String decoders `ValueDecoders`.

Tradeoffs:
* Initial processing of the configs are fast since the there is no type resolving
* However, that cost is shifted to query time (String => T resolution in each call). One option could be maintaining separate lookups per type.
* Query interface is not as simple as it is given in the assignment document (Python or similar dynamically typed language). This is inherent with statically typed languages. Good if client cares about types (and type safety). Not so good otherwise, since it requires the knowledge (guessing) of type. 


#### Config files can get quite lengthy

This hints me that the data may not fit into memory of one node. Therefore, sharding is needed.

Current implementation does not address this problem completely. Because each actor (`InMemoryConfigGroupNode`) is running locally. However, we could use the current implementation and create a cluster with `akka-cluster` api. So, the system could scale if necessary.

Another option could be backing it up in the disk with an efficient data structure. We could use LRU cache and once the configs do not fit memory, they could be persisted to disk. Again a cluster could be used. This option could be provided by implementing Disk-Backed version of `InMemoryConfigGroupNode`

First option is simpler and will perform better as long as we have sufficient memory (locally or sum of the cluster).
  
#### Arbitrary number of groups and number of settings within each group
    
Hints that if we do sharding by groups, shards can be skewed.

In current implementation each actor is assigned to a group. So there can be data skew. This could be addressed by for a given threshold `Config.Loader` could dispatch lines of multiple groups to one node. It would slightly change (or require another implementation) the constructors `InMemoryConfigs`. So instead of `Map[String, ActorRef]` it would be `Map[String, List[ActorRef]]` since some groups would be shared between the actors. That would also require some Actor coordination (join) at the time of query.
  
#### Config object's query responses should be fast

For able to satisfy this requirement, used `Guava Cache (LRU)` in `InMemoryConfigs`. In current implementation it does not make a lot of difference since all nodes are local. Using cache, only ask/tell overhead is avoided. However, if a cluster is used, it could make difference by avoiding network round trips.

#### If the config file is not well-formed, it is acceptable to print an error and exit from within loadConfig(). Once the object is returned, however, it is not permissible to exit or crash no matter what the query is. Returning an empty value is acceptable, however.

As explained above, loadConfig() offloads the work to nodes/actors as quickly as possible to avoid long boot time. There is sufficient error handling in parsing, value decoding and in case of error None (empty value) would be returned. 
However, since loader returning `InMemoryConfigs` object almost immediately, in case of large config files actors may still be working. At that moment if a query made, it can cause timeout.
This could be quite likely with current implementation because it allows data skew as it is now.
For now, changing timeout values could help. For future iterations, a couple of improvements could be made:
* Distributing data evenly (avoiding data skew)
* Adaptive timeout can be calculated based on load threshold of each node
* I implemented a basic retry mechanism, but it should be extended to cover timeout errors.


### Future Work/Discussion

* No full-fledged dependency injection used. Just created contracts and concrete implementations. Can be easily implemented once there are more than one implementation of each contract. For now just a few factories (just to indicate direction otherwise they are not very meaningful).
* Distributing nodes with actors (akka-remote/akka-cluster)
* Handle duplicate groups and keys: Currently keys are not a problem. Last one would override the previous. However, safeguarding against duplicate groups are not implemented.
* Use bloom filter for faster response. Current LRU cache will hold None values. Using a bloom filter would also let us avoid that.
* Avoid data skew by offloading equal amount of data to nodes
* Make clear separation between IT and UT. `InMemoryConfigLoaderSpec` is actually an integration test covering both `InMemoryConfigs` and `InMemoryConfigLoader` which is not very desired.
* Currently, tests are covering mostly happy paths. More rigorous testing would be required
  * Integration/Performance test with large files
  * Corner cases with files and config lines
* I tried to avoid using Akka Actor's test framework which in return required me to expose some methods with package visibility to be able to test. It is not a good practice but did not want to make tests complicated for now. For future, it is worth investing in Akka's test toolkit
* Used akka classic due to familiarity. If there was more time, I would use Akka's typed api
* Add more value decoders: Array[Int], Array[Double] etc
* Read constants from a config file, e.g. timeout values
* Handle timeouts better

Thanks for reading it!