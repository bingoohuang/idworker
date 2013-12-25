idwoker
=======

an ID generator with un-coordinated, under 64-bits, k-sorted, and deterministically unique.

Any order system need to create an unique order ID.

Traditionally, we use `Oracle sequence` or `mysql auto-increment field`. And so we create sequence or mysql increment id table and prepare SQL to get an available unique id. And every time when we need a new id, we may need re-execute the SQL.

Is there an easy one to provide unique IDs with un-coordinated, [k-sorted](http://ci.nii.ac.jp/naid/110002673489/), under-64bits and even no need of ID server?

Yes, we can make it.

```java
// just get an id of long type
long id = Id.next(); // eg. 149346876358656

// we need an fixed-sized id of all-digits string with prefix of yyMMdd
String sid = Sid.next(); // eg. 131225121466337099776 with fixed size 21.

// or we don's care if all digits or not
String xid = Sid.nextShort(); // eg. 131225BDF6601B55 with fixed size 16.

```

This id of type long is composited by three segments:

+ 42 bits of milli-seconds elapsed from 2013-12-24 20:01:38.127(1387886498127L). With 42 bits, we can have max of 4398046511103 milli-seconds, which is about 139.27years;
+ 10 bits of worker id. With these 10 bits, we can have 1024 workers at max.
+ 11 bits of incremental sequence. Within one milli-second, we can have 2048 sequence number at max. The sequence will be reset everywhile a new milli-second started.

We left one first bit to be zero and keep the generated ids always positive. 

## Rationale

I have to confess that the orignial idea is from [twitter snowflake](https://github.com/twitter/snowflake).

Within one process, we are of course sure it is simple to make all IDs generated unique. But within multiple processes on the one host, or even on multiple hosts, how to guarentee such global uniqueness? 

As you already known, the worker id segment is used to rescue the distributed unique condition. If each process has a unique worker id, all the generated IDs of each process will be globally unique, whenever multiple processes on one host or among multiple distributed hosts.

Snowflake deployed their ID online services, and Zookeeper is used internally to keep every worker id is unique. And when anybody want a new ID, just access to the ID online services by THRIFT.

Here I made this different and try to make more simple.

According to my design, the embedded idworker client will only need to access the idworker server on its first running to get an unique workerid. And then the client will store the worker id at local disk. When the client restarted, it will try to find available worker id from the local disk files. If it got, the local file of worker id will be `locked` excludedly. The lock will be held until the process ends or the client releases it as needed.

If the client can not find available worker id at local disk file and meanwhile the server is gone, the client will try to use the last 10 bits of host ipv4 as to be the worker id. When the worker id of last 10 bits of host is still unvailable, the client will generate a random mask of max worker number and to generate a `dangerous` worker id which is only locally unique at current host rather than globally unique.

## The idworker server
The idworker server is just a simple servlet running in embedded jetty container.
The server is only needed when a new client process can find a pre-allocated workerid. And this situation happens only when a new process is deployed, or the local disk changed, or the local workerid files are cleared.


## Protocol between client and server

> see

+ Request: `http://idworker-server-host:9223/see`
+ Usage: To see the last worker id which maybe serviced to the client, the answer is 4-sized number.
+ Response: `0010`

> list all

+ Request: `http://idworker-server-host:9223/list`
+ Usage: List all the ip-user and the worker ids allocated for the host.
+ Reponse: `10.192.202.115.bingoohuang:0010,0320,0311;10.192.202.125.bingoohuang:0110,0320`

> list of one ip and user

+ Request: `http://idworker-server-host:9223/list?ipu=10.192.202.115.bingoohuang`
+ Usage: List the worker ids allocated for the specified ip and user.
+ Reponse: `0010,0320,0311`

> inc for one ip and user
	
+ Request: `http://idworker-server-host:9223/inc?ipu=10.192.202.115.bingoohuang`
+ Usage: Allocate a new worker id for the specified ip and user.
+ Reponse: `0312`

> sync of one ip and user

+ Request: `http://idworker-server-host:9223/sync?ipu=10.192.202.115.bingoohuang&ids=0010,0320,0311`
+ Usage: To synchronized all the ip and user specific worker ids between client and server.
+ Response: `0010,0320,0311,0313`


