package com.example.todolist.DTO

class ToDo {

    var id: Long = -1
    var name = ""
    var craetedAt = ""
    var items: MutableList<ToDoItem> = ArrayList()
}