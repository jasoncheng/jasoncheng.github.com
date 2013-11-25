---
layout: post
title: "善用 HttpResponseCache"
date: 2012-07-17 21:16
comments: true
categories: [Android, Java, HttpURLConnection, HttpResponseCache, NodeJS]
---

之前寫 Android App 都要自己實作 Cache, 不管是圖片或者是API資料; 

比如說: 打開程式後, 如果Local有Cache就先讀取Cache; 然後在暗地裡發送Request去更新圖片/API資料等...
當有新的, 直接複寫本地端的緩存, 然後 notify refresh.  而且還要防止Cache太多, 要定時刪除舊的資料....

*** 太累了 ***


### 使用的好處 ###
    - 節省用戶端的電力 (因為可以少掉很多Internet Connection)
    - 省下龐大的頻寬費用 (因為對Server來說, 當收到 If-Modified-Since, 如果沒更新, Server端只要回應 304即可)
    - 開發者不用自己再做 Cache 機制了.
    - 最好的事!! 如果你本身不是用 HttpClient, HttpDefaultClient..., 而是用 HttpURLConnection的話, 你根本不用改本來的 Code.


接下來, 實作吧!! 其實很簡單, 你不必改寫你的任何Code, 
你只要 Application層, 把它啓用就好了; 剩下的一切 HttpURLConnection 會幫你處理

{% include_code ResponseCache/ResponseApplication.java %}

{% include_code ResponseCache/MainActivity.java %}

``` js 接下來用 NodeJS 寫個簡單的Static File Server
var express = require("express");
app = express.createServer();
app.use(express.logger());
app.use(express.static(__dirname + '/public'));
app.listen(4000);
```

``` sh 每個Request都會產生兩隻檔案, 一個是實體檔案, 一個是 HTTP Header 資料
# cd /data/data/com.example.testresponsecache/cache/http
# ls -l
-rw------- u0_a48   u0_a48        345 2012-07-17 23:07 20ebbdb944f2be7d9ea96466bafe98a5.0
-rw------- u0_a48   u0_a48         42 2012-07-17 23:07 20ebbdb944f2be7d9ea96466bafe98a5.1
-rw------- u0_a48   u0_a48        321 2012-07-17 23:07 7abaca174bffb497cea054db94961804.0
-rw------- u0_a48   u0_a48      11856 2012-07-17 23:07 7abaca174bffb497cea054db94961804.1
-rw------- u0_a48   u0_a48        163 2012-07-17 23:07 journal
```

``` sh 整個的運作的關鍵就在 Last-Modified
# cat 20ebbdb944f2be7d9ea96466bafe98a5.0
http://jasoncheng.tw/1.json
GET
0
HTTP/1.1 200 OK
9
Connection: keep-alive
Content-Encoding: gzip
Content-Type: text/plain; charset=utf-8
Date: Tue, 17 Jul 2012 15:01:31 GMT
Last-Modified: Tue, 17 Jul 2012 13:24:14 GMT
Server: nginx/0.7.65
Transfer-Encoding: chunked
X-Android-Received-Millis: 1342537658233
X-Android-Sent-Millis: 1342537658210
```

``` sh 第1次執行
- [Tue, 17 Jul 2012 15:14:18 GMT] "GET /1.png HTTP/1.1" 200 11856 "-" "Dalvik/1.6.0 (Linux; U; Android 4.1; sdk Build/JRN83C)"
- [Tue, 17 Jul 2012 15:14:20 GMT] "GET /1.json HTTP/1.1" 200 22 "-" "Dalvik/1.6.0 (Linux; U; Android 4.1; sdk Build/JRN83C)"
```

``` sh 第2次執行, Server 好輕鬆, 只要回304就好了, 省下多少頻寬阿
- [Tue, 17 Jul 2012 15:14:36 GMT] "GET /1.png HTTP/1.1" 304 - "-" "Dalvik/1.6.0 (Linux; U; Android 4.1; sdk Build/JRN83C)"
- [Tue, 17 Jul 2012 15:14:36 GMT] "GET /1.json HTTP/1.1" 304 - "-" "Dalvik/1.6.0 (Linux; U; Android 4.1; sdk Build/JRN83C)"
```

``` sh 修改1.json檔案後, Last-modified 改變了, 所以重新抓一次, 所以 Status Code 變回 200 Okay!
root@ubuntu:/var/www/html/jasoncheng/static_test# node web.js | grep -v favicon
- [Tue, 17 Jul 2012 15:17:30 GMT] "GET /1.png HTTP/1.1" 304 - "-" "Dalvik/1.6.0 (Linux; U; Android 4.1; sdk Build/JRN83C)"
- [Tue, 17 Jul 2012 15:17:30 GMT] "GET /1.json HTTP/1.1" 200 17 "-" "Dalvik/1.6.0 (Linux; U; Android 4.1; sdk Build/JRN83C)"
```

### 測試結果 ###
    - Okay: 關閉網路後, 圖片/JSON 資料會自己返回 Local Cache 的資料 (所以用戶不會感覺網路斷線了..嘿)
    - Okay: 資料更新後, JSON會自動更新
    - Okay: 檔案沒改變的條件下, Server 只回應304

### 測試後的問題 ###
    - Client 端必須是 Android 4.0 以後版本才有支援 (真是XD...我的NexusOne 沒辦法升到4.0啦~~~)
    - Server 上的圖片修改後, Client端並不會即時更新; 懶得翻 Android Source Code了..有興趣的朋友, 再去追 (不過也沒差吧, 應該沒有人大頭照常換的吧)
    - 如果遇到特殊的 port number (非80 port), cache不會生效.

### 2012/07/24 更新 ###

``` java Force a network response
  connection.addRequestProperty("Cache-Control", "max-age=0");
```

``` java Force a cache response
  connection.addRequestProperty("Cache-Control", "only-if-cached");
```
