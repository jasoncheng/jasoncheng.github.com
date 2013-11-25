---
layout: post
title: "幫你的 Cacti 加個監控告警機制"
date: 2012-07-24 22:47
comments: true
categories: [NodeJS, cacti, device monitor, npm, node_modules]
---

玩了幾個月的 Node, 而且昨晚睡不著, 裝了 cacti 起來玩玩 (*** 是的, 我很宅 ***), 
今天撞到頭的想把自己寫的爛東西  [cacti-host-updown-monitor](http://search.npmjs.org/#/cacti-host-updown-monitor), 分享一下!

畢竟用的 node_modules 好一陣子了, 從來也沒用Github 來 OpenSource (羞~~這樣也叫 OpenSource... *** 指 ***) . 
趁這個機會, 順便來玩玩 npm 發佈.

``` sh
$ npm adduser (建立你的帳號)
$ npm publish (發佈 Node Module 到 NPM Registry)
```

或許你會說, [cacti](http://www.cacti.net/) 不是已經有很多 plugin 了; 比如說, <code>thold</code> 之類的 Thresholds / Alarms 機制了, 
是的, 沒錯; *** But i don't care ***, 這部分我不想裝 plugin, 我只想用自己寫的...哈!


題外話: 現在回想起來, 比起 cacti 以前在 TTN 弄的那套 Device Monitor 還真好用(誤)....但這都是過去了...過去了...


### 適用對象 ###

    - 白老鼠
    - 不怕死的勇者
    - 有使用 cacti 做 device monitor 的人.

### 適用的cacti版本 ###

    - ALL

### 唯一的用途 ###

    - 當監控的Device Down, 就會發出告警信件通知


``` sh 安裝 
$ npm install cacti-host-updown-monitor 
```

主程式沒有幾行扣, 呵呵...見笑了!

{% gist 3170658 %}
