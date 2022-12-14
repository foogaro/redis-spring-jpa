# Spring Data JPA for persistence<br/>Spring Data Redis for caching<br/>Redis as distributed cache

![Redis and Spring](images/spring-booot-redis.png)

Using Redis to cache entities managed by Spring JPA repository persisted in MySQL.

Nowadays, leveraging a Real-Time Data platform is a must.

You services and applications need to respond as quick as possible with the information requested.

Responding in seconds and milliseconds is not enough anymore, you have to respond within sub-milliseconds. 

Redis provides you the speed of light you need.

# Most common Caching Patterns
However, caching data is not the whole thing. You also need to have the right strategy to access you cache.
That's where caching patterns come in play.
Here is the list of the most common used caching patterns:
- Cache-Aside (Lazy-loading)
- Read-Through
- Write-Through
- Write-Behind (Write-back)

## Cache-Aside (Lazy-loading) read
This pattern describes how the data should be retrieved.
The application layer, the service, first checks if the data is available into the Redis cache.

If the data is available, it gets returned immediately to the requester.

If the data is __not__ available, the service looks for it into the database.
Once the data is retrieved from the database, it gets also set into the cache for later use, and finally returned to the requester.

![Cache-Aside read](images/cache-aside-read.png)

## Cache-Aside (Lazy-loading) write
This pattern describes how the data should be stored.
First, the application layer, the service, writes the data into the cache.

Next, the service writes the data into the database.

![Cache-Aside write](images/cache-aside-write.png)

## Read-Through
This pattern describes how the data should be retrieved.
The application layer, the service, only connects to the Redis cache, without ever connecting to the database.

Thus, the service requests the data from the Redis cache.
The caching layer, Redis, first checks if it has the data.

If the data is available, it gets returned immediately to the service.

If the data is __not__ available, Redis looks for it into the database.
Once the data is retrieved from the database, it gets set into the cache for later use, and finally returned to the service, and then to the requester.

![Read-Through](images/read-through.png)

## Write-Through
This pattern describes how the data should be stored.

The application layer, the service, only connects to the Redis cache, without ever connecting to the database.

Thus, the service sends the data to be written into the Redis cache.
The caching layer, Redis, stores the data, and then sends the data to be written into the database as well.

Then entire chain is synchronous.

![Write-Through](images/write-through.png)

## Write-Behind (Write-back)
This pattern describes how the data should be stored.

The application layer, the service, only connects to the Redis cache, without ever connecting to the database.

Thus, the service sends the data to be written into the Redis cache.
The caching layer, Redis, stores the data, and then sends the data to be written into the database as well.

The last step of the chain is asynchronous, that is Redis calling the persistence layer.

![Write-Behind (Write-back)](images/write-behind.png)

# The Cache provider
How does Spring fit in all this?

Redis provides a way to add custom logic (your code) into its engine .

It supports two programming languages:
- Python
- JVM (Java, Scala)

Redis provides such capabilities leveraging one of its module, RedisGears.

However, if you still want the whole control to be at application layer, Spring and its caching capabilities, that is what you are looking for.

# Caching at application layer
Using Spring is the key point to easy the entire process, without adding any custom logic to Redis.

The integration between the application and Redis is delegated to Spring using its
```@EnableCaching``` annotation,
and by specifying Redis as its cache layer into the ```application.properties```, as follows:

```properties
spring.cache.type=redis
spring.redis.client-type=jedis
spring.redis.cluster.max-redirects= # Maximum number of redirects to follow when executing commands across the cluster.
spring.redis.cluster.nodes= # Comma-separated list of "host:port" pairs to bootstrap from.
spring.redis.database=0 # Database index used by the connection factory.
spring.redis.url= # Connection URL, will override host, port and password (user will be ignored), e.g. redis://user:password@example.com:6379
spring.redis.host=localhost # Redis server host.
spring.redis.password= # Login password of the redis server.
spring.redis.ssl=false # Enable SSL support.
spring.redis.pool.max-active=8 # Max number of connections that can be allocated by the pool at a given time. Use a negative value for no limit.
spring.redis.pool.max-idle=8 # Max number of "idle" connections in the pool. Use a negative value to indicate an unlimited number of idle connections.
spring.redis.pool.max-wait=-1 # Maximum amount of time (in milliseconds) a connection allocation should block before throwing an exception when the pool is exhausted. Use a negative value to block indefinitely.
spring.redis.pool.min-idle=0 # Target for the minimum number of idle connections to maintain in the pool. This setting only has an effect if it is positive.
spring.redis.port=6379 # Redis server port.
spring.redis.sentinel.master= # Name of Redis server.
spring.redis.sentinel.nodes= # Comma-separated list of host:port pairs.
spring.redis.timeout=0 # Connection timeout in milliseconds.
```

At service layer, whenever the persistence layer is invoked, Spring can automatically cache any kind of operation (save, update, delete), thus keeping the caching layer always aligned with the database.

```java
@Service
@CacheConfig(cacheNames={"actors"})
public class ActorService {

    private final ActorRepository repository;

    public ActorService(ActorRepository repository) {
        this.repository = repository;
    }

    @Cacheable(unless="#result == null")
    public Optional<Actor> findById(Long id) {
        return repository.findById(id);
    }

    @Cacheable(unless="#result == null")
    public List<Actor> findAll() {
        return repository.findAll();
    }

    @CachePut(key = "#result.id", unless = "#result == null")
    public Actor save(Actor actor) {
        return repository.save(actor);
    }

    @CacheEvict
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @CacheEvict
    public void deleteAll() {
        repository.deleteAll();
    }
}
```

## @Cacheable
That's the Spring annotation to use when you want to cache whatever you are looking for in your persistence layer.

## @CachePut
That's the Spring annotation to use when you want to update whatever you are updating in your persistence layer.

## @CacheEvict
That's the Spring annotation to use when you want to delete whatever you are deleting from your persistence layer.

