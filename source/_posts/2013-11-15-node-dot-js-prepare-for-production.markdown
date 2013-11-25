---
layout: post
title: "Node.js ready for production, some tips."
date: 2013-11-15 21:25
comments: true
categories: [NodeJS, npm, node_modules, mongodb, Amazon EC2]
---

This article is just for personal note/log, and don't know if this helpful.
Recently, we finish a small project(news web site) base on NodeJS / MongoDB(Replicaset) / MySQL.

### status ###
**servers:**  use [Amazon EC2 m1.medium](http://aws.amazon.com/ec2/pricing/) * 2 (for we don't use cluster, just 1 core CPU is enough; but why not use small instance, it's network issue); Theoretically, one server can handle all of this, but for more safety reason(LoadBalance), we take two.  

**pageview:** about 5 million pageview per day (i believe it's can handle more request)

**max concurrency user:** 15,000

**nodejs version:** 0.10.21

**jobs: **
    - huge amount news pages(directly query mongodb, and do layout)
    - each page at least require 50 ~ 200 mongodb query(It's a long story, that i don't wanna mention about)
    - each page response time approximate 50~100ms
    - JSONP APIs
    - proxy 80 port request to another port number(maybe mobile web/different domain) which listened by NodeJS too
    - 302+301 redirect

### package.json ###
```
"dependencies": {    
  "express": "3.4.4",
  "http-proxy": "0.10.2", 
  "mongodb": "1.3.19",    
  "sequelize": "1.6.0",   
  "dateformat": "~1.0.4", 
  "flow": "0.2.3",   
  "memcached": "0.1.5",   
  "node-cache": "0.0.4",
  "consolidate": "0.8.0", 
  "swig": "~0.13.5", 
  "sprintf": "0.1.1",
  "request": "2.27.0",    
  "solr": "0.2.2",   
  "MD5": "1.0.3",    
  "xml2js": "0.2.7", 
  "log-prefix": "0.0.0",  
  "sitemap": "0.6.0",
  "mysql": "2.0.0-alpha9",
  "colors": "~0.6.2"
}
```


### cache as possible as you can ###
cache everything, and let it auto expire maybe few mins; for, NodeJS is a daemon,
sometime in-memory cache is better/faster than memcached server, 
and most important things is it no necessary to convert **Object/Array <=> string** each time you get and set.

so, i cache Object/Array use [node-cache](https://github.com/ptarjan/node-cache), but be aware on call by reference.

### keep process run forever ###
Before, i use [forever](https://github.com/nodejitsu/forever) to keep process running, it's very useful module;
but currently i use init script instead. here is my example(/etc/init/web.conf):

```
start on (local-filesystems and net-device-up IFACE=eth0)       
stop on shutdown         

respawn

script      
  sleep 5          
  export NODE_ENV=production    
  cd /var/www/web
  exec /usr/bin/node --nouse-idle-notification www.js 2>&1 >> /var/log/node/www.log 
end script  
```

### prevent use JSON.parse / JSON.stringify ###
In my experience, after your node run about few days(approximate handle 20M requests), 
if you use those two method on frequently request, 
100% i can sure that your node process will reach 100% CPU usage and finally can't handle any request/response.

It's can be trace through [heapdump](https://github.com/bnoordhuis/node-heapdump).

```
var b = JSON.stringify({c:1293, t:'ads'});
```

maybe few days, will become....

```
b = '\\\\\\\\\\\\\\\\\...........';  // endless slash
```

weird, right? i can't explain why? but after use heapdump i found this and fixed.

my another EC2 server(m1.medium x 2) each handle 2.5M request per day, simple respose HTTP Header,
calculator pageview and update mongodb fields (count++); 
all server after run 4 days, it's will no response, and no any error, can't handle any request. all i can do is just restart 
this service.

after remove all JSON.parse/JSON.stringify, its alive form 4 days to 1 month before last time i restart.

### too many open files ###
[configure like this](http://stackoverflow.com/questions/5321432/simple-nodejs-http-proxy-fails-with-too-many-open-files)

### log as more details as you can ###
my expressjs setting.

```
app.configure('development', function(){   
  app.use(express.logger('dev'));
});

app.configure('production', function(){    
  app.use(express.logger('default')); 
});
```

and my logrotate config is.

```
/var/log/node/*.log{  
  rotate 30 
  daily
  missingok 
  notifempty
  delaycompress  
  compress  
  copytruncate   
} 
```

### process.on('uncaughtException') ###
[according to this](http://nodejs.org/api/process.html#process_event_uncaughtexception), don't use it.

### think before use express.static ###
our new web site when lunch, first issue popup is static file server (NodeJS+express.static) not powerful,
can't handle large request on photos/css/images/javascript, 
even all static file send {maxAge:86000}, it's just slowly and finally no any response.

maybe nginx or apache is a good option for static file server.

### Be careful on call by reference ###
Object or Array are call by reference, be careful.

### mongoDB no open connection issue ###
We have 3 mongodb build with replicaset.
This problem make me confuse for a long time, sometimes the mongodb connection reset, sometimes it's show no open connection;
and we tracing the wrong way either.

But all my no open connection issue come from below (i'm 99% sure, not node mongo native module issue).
In my case, those two 
    - prevent recursive call
    - prevent use process.uncaugthException


Below example, will produce "no open connection", after few seconds:

```
process.on('uncaughtException', function(err) {
  console.log('Caught exception: ' + err);  // watch this full code trace, you will figure out why connection drop!
});

flow.exec(
  function(){
    db.find({lastName: 'jason'}).toArray(this);
  },
  function(err, docs){
    var data = [];
    for(var idx in docs){
      docs[idx].ooo = docs[idx].notExists.ooo; // will die here, then caugth by uncaughtException
      data.push(docs[idx]);
    }
    this(data);
  },
  function(data){
    res.render('index.html', data);
  }
);
```

What if, you still have to use process.on('uncaughtException') for some reason ?

try process.nextTick, to escape this exception affect to mongo module, it's useful!


### wrote test case ###
thinking about all situation, and wrote into test case(maybe use [mocha](http://visionmedia.github.io/mocha/)),
for production service when occur unknow bug, this is quick solution to find the issue.

    - such as, testing all recently URL through sitemap.xml ? if HTTP Status 200 Okay
    - such as, detect mognodb status, can be find data, can be use aggregate.....
    - such as, is MySQL/Memcached/Solr server alive?


### NodeJS console ###
use different log level (info/warn/error/log) and maybe add some [color](https://github.com/Marak/colors.js)(red/yellow) for more easier to debug.

console.warn and console.error is stderr, it won't wrote message when NODE_ENV=production,
maybe you should override console object for this.
