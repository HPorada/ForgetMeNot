package com.example.forgetmenot.model

class Note {
    var title: String? = null
    var content: String? = null
    var endDate: String? = null

    constructor() {}
    constructor(title: String?, content: String?, endDate: String?) {
        this.title = title
        this.content = content
        this.endDate = endDate
    }
}