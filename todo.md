**Please do not spend more than 3 hours on this challenge. Rather than spending more
time, please leave notes in the README as to what you would do if you had more time.**

* Look at Scala config parser
* Start readme:
  * How to run (and test)
  * Approach and alternatives (maybe in adr form)
  * Domain modelling (classes and relationships)
  * Separation of concerns
  * Extensibility (plugin based, flat hierarchy vs nested uzerine calis)

* Tartisma konulari:
 * Big data: splitting file and then shuffle based on key and have multiple workers (hash based sharding), data skew problem olabilir eger bir category altinda cok fazla pair varsa
 * En ufak hatada hata mi verelim yoksa partial parsing mi yapalim? Guzel bir hata mesaji ile beraber. Strict yaptim su su nedenden dolayi (basit code ama farkli caselerde lenient olunabilir)
 * Tek file vs multiple files reading?

Dusuncelerim:

Design Considerations
When implementing your solution, please consider the following:

● loadConfig() will be called at boot time, and thus should be efficient with time. Config
files can get quite lengthy - there can be an arbitrary number of groups and number of
settings within each group.

- fast run first if the config is valid (mumkun mu bu peki???)
- configleri grouplara gore split edip farklli (keye assign edilmis) parserlar olsun (bunlari sonra distributed hale getirebiliriz belki).
- arbitraryi slicing mumkun degil cunku keyi kaybediyoruz. ama once bir validation, sonra slicing ve en son parsing yapilabilir. ben simdilik validation
  ve parseri decoupled yazabilirim. slicingi ortaya ekleyecek sekilde. slicerin gorevi belli bir block siza gore bolmek olabilir 


● The Config object will be queried throughout the program's execution, so each query
should be fast as well.

- yukaridaki ve asagidaki cozumler bu design considerationi address eder

● Certain queries will be made frequently (thousands of times), others pretty rarely.

- propose use of bloom filters
- propose LRU cache (property nodeu icinde olsun)

● If the config file is not well-formed, it is acceptable to print an error and exit from within
loadConfig(). Once the object is returned, however, it is not permissible to exit or
crash no matter what the query is. Returning an empty value is acceptable, however.

- use some

Draft Class Design: 

**Daha iyi oop isimler dusun, her biri icin interface olabilir**
Config Splitter: naive splitter implement et. configi alip bolsun sadece
ConfigNode: tek gorevi bloom filteri kurmak, parseri kullanip parse etmek ve querylere donmek
Query Layer/Config Manager: bu adamin gorevi global bloom filteri maitain etmek ve eger varsa hangi evq config node bu keye sahip onu assign bilmek olmali, gidip ona
sormali...
Validator: line icin validation yapacak (boolean o bu diye ayrilabilir)
Parser: (bunu ayirabilirsin Boolean parser, Int parser o bu diye)

Execution:
(optional) validate
split (returns key -> lines) (ideally should return key -> files)
Maybe balance it here... (use a config balancer) 
QueryLayer 
* creates ConfigNode per key and let ConfigNode (which in return use a parser and maybe validator) load it
* create/merge bloom filter
* initialise LRU cache

Test Cases:
