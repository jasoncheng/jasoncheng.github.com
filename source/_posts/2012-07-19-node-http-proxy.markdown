---
layout: post
title: "NodeJS 好物 node-http-proxy"
date: 2012-07-19 23:50
comments: true
categories: [NodeJS, http-proxy, proxy, server]
---

今天要介紹一個超棒的 node_module, 小弟用了一陣子了, 覺得好用, 推薦給大家; 
如果你的角色是 Server Administrator 的話,
請繼續往下看, 希望這篇簡短地介紹, 會增加你工作上的效率 (我到底在扯什麼...).

### 重點是, 把 port 80, 交給 NodeJS 吧 ####

公司 R&D 狀況: 
    - Team A 寫 Java servlet, 提供一些啥鬼的WebService
    - Team B 負責用 apache + PHP 寫 EndUser 網頁
    - Team C 負責用 NodeJS 寫開放的API
    - Team D 要用 nginx + php5-fpm 來寫後台控管網頁
    - Team D 要用 Perl + Mason 寫統計網頁

Server:
    - Server01: 8.8.8.8 / 192.168.1.1
    - Server02: 192.168.1.2

Assume:
    - 所有的服務都要有專屬網址
    - 所有的服務只能用80 port
    - domain name: jasoncheng.tw

``` js 在 server01 只要寫幾行code, 一切就幫你搞定
var options = {
  hostnameOnly: false,
  router: {
    // Team A
    'jasoncheng.com/servlet':   'server01:8081',

    // Team B
    'jasoncheng.tw':            'server01:8082',

    // Team C
    'api.jasoncheng.tw':        'server02:8083',

    // Team C
    'adm.jasoncheng.tw':        'server02:8084',

    // Team D 
    'adm.jasoncheng.tw/static': 'server02:8085'
  }
};

require("http-proxy").createServer(options).listen(80);
```

當然, 或許你會說 Nginx / Apache 本身也可以做到這些需求, 但不可否認, 人要趕流行不是嗎?
那就用 NodeJS 囉! 這麼多人都說 *** The Next Big Thing is Node *** 了.

今天, 不管開發人員要用哪些技術來寫網頁/API, 要用湯姆貓/阿帕契/引擎X當Server......都沒差; 
難搞的系統管理者, 就可以趁機會跟同事們好好交朋友, 快速地幫程式開發人員, 弄一組專屬的 Demo 網址吧.


``` sh Do It Now!!
$npm install http-proxy
```
