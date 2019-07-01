package com.danielvilha.kotlinmessengerandroid.views

/**
 * Created by danielvilha on 2019-07-01
 */
class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long) {
    constructor() : this("", "", "", "", -1)
}