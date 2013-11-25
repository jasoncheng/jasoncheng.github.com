---
layout: post
title: "Too many open files"
date: 2012-07-16 22:03
comments: true
categories: [ubuntu, ulimit, nodejs, apache, linux]
---

最近常遇到 Too many open files 的錯誤訊息, 不管是 memcached / nodejs ..
這跟Server上面的ulimit有關. 必須調整1024(預設)值.

顯示目前限制
``` sh
$ulimit -a
```

編輯 /etc/pam.d/common-session, 加上
``` sh
session required pam_limits.so
```

編輯 /etc/security/limits.conf
``` sh
* soft nofile 51200
* hard nofile 51200
```

編輯 /etc/profile
``` sh
ulimit -SHn 51200
```

執行
``` sh
ulimit -SHn 51200 
```
