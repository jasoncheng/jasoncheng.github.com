---
layout: post
title: "octopress + github 學習經驗"
date: 2012-07-17 12:05
comments: true
categories: [Octopress, Dropbox, Github]
---

最近剛在學習使用 Octopress + Github, 把弄過幾次的經驗記錄下來.

因為我會在兩台以上的電腦使用 octopress 寫文章, 但是看過一些文章後,

覺得要在第N台電腦搞 <code>$git clone ..</code>這些設定, 太麻煩了.

### 使用 Dropbox 存放 Octopress 的資料(source..等) ###
當設定好 octopress <code>$rake install</code> 之後, 
就不要再操作 git 了; ex.<code>$git add . </code> or <code>$git add source</code>,

*** 不需要把 source 丟到 github 上面一樣可以運作 ***


{%img images/dropbox_octopress.png %}


{%img images/octopress_github_no_need_source.png %}

### Github上面只放_depoly的資料 ###

``` sh 每次要發佈只要
$rake generate
$rake deploy
```
