* Refactor and test (Comment and Blank Line'i irrelavanta cevirmeyi dusun) - Name better!
* InMemoryConfigGroupCoordinator(group: ActorRef) delete!!!
* Finish readme!

* **Query'de timeout yerine mailboxuna bakarak in progress vs diyebilir miyim** (Timeoutu handle et)
* LRU cachei yaz
```
  //TODO: Consider using cache
  //  val lruCache = CacheBuilder.newBuilder()
  //    .recordStats()
  //    .maximumSize(100)
  //    .expireAfterAccess(5, TimeUnit.MINUTES)
  //    .asInstanceOf[CacheBuilder[String, String]]
  //    .build[String, Map[String, String]
  
  
    val groupMap = Map(
    "int_val" -> "1",
    "double_val" -> "1.0",
    "paid_users_size_limit" -> "2147483648",
    "name" -> "hello there, ftp uploading",
    "params" -> "array,of,values",
    "enabled" -> "no",
    "bool_0" -> "0",
    "bool_false" -> "false",
    "bool_yes" -> "yes",
    "bool_1" -> "1",
    "bool_true" -> "true"
  
```
* with size threshold Strategy


* Provide a worksheet?
* **Cumhur/Volkan code review**
* Jessica mail at readme yaziyorum hangi formda yollayayim diye sor
* **git'i silip yolla**

---

Extras:

* Use Using in Reader
* **Property line'da ya da group line da comment olabiliyor. O nedenle comment line cok gerekli bir abstraction degil**
  Retain comments in group and property via a field
* Actor error handlinge iyi calis. Ilk okurken onemli
* Parsing'i pattern matching ile yapabilir miyim bir bak (valueyu bir string array olarak dusunerek)
* Look at Scala config parser

---

To stop an actor, the recommended pattern is to return Behaviors.stopped() inside the actor to stop itself, usually as a
response to some user defined stop message or when the actor is done with its job. Stopping a child actor is technically
possible by calling context.stop(childRef) from the parent, but it’s not possible to stop arbitrary (non-child) actors
this way.

The supervision strategy is typically defined by the parent actor when it spawns a child actor. In this way, parents act
as supervisors for their children. The default supervisor strategy is to stop the child. If you don’t define the
strategy all failures result in a stop.


