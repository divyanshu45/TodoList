package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.todolist.DTO.ToDo
import com.example.todolist.DTO.ToDoItem

class DBHandler(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){
    override fun onCreate(db: SQLiteDatabase) {

        val createToDoTable = " CREATE TABLE $TABLE_TO_DO(" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_CRAETED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_NAME varchar);"

        val createTodoItemTable = "CREATE TABLE $TABLE_TODO_ITEM(" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT, " +
                "$COL_CRAETED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_TODO_ID integer," +
                "$COL_ITEM_NAME varchar," +
                "$COL_IS_COMPLETE integer);"

        db.execSQL(createToDoTable)
        db.execSQL(createTodoItemTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun addToDo(toDo: ToDo) : Boolean{
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        val result = db.insert(TABLE_TO_DO,null,cv)
        return result != (-1).toLong()
    }

    fun editToDo(toDo: ToDo){
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        db.update(TABLE_TO_DO, cv, "$COL_ID=?", arrayOf(toDo.id.toString()))
    }

    fun getToDos() : MutableList<ToDo>{
        val result: MutableList<ToDo> = ArrayList()
        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TO_DO", null)
        if(queryResult.moveToFirst()){
            do {
                val toDo = ToDo()
                toDo.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                toDo.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
                result.add(toDo)
            } while (queryResult.moveToNext())
        }
        queryResult.close()
        return result
    }

    fun addToDoItems(item: ToDoItem) : Boolean{
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME,item.itemName)
        cv.put(COL_TODO_ID,item.toDoId)
        cv.put(COL_IS_COMPLETE, item.isCompleted)
        val result = db.insert(TABLE_TODO_ITEM, null, cv)
        return result != (-1).toLong()
    }

    fun getToDoItem(toDoId: Long) : MutableList<ToDoItem>{
        val result: MutableList<ToDoItem>  = ArrayList()
        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$toDoId",null)
        if(queryResult.moveToFirst()){
            do {
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.isCompleted = queryResult.getInt(queryResult.getColumnIndex(COL_IS_COMPLETE)) == 1
                result.add(item)

            } while (queryResult.moveToNext())
        }
        queryResult.close()
        return result
    }

    fun updateToDoItems(item: ToDoItem){
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_TODO_ID, item.toDoId)
        cv.put(COL_IS_COMPLETE,item.isCompleted)
        db.update(TABLE_TODO_ITEM,cv, "$COL_ID=?", arrayOf(item.id.toString()))
    }

    fun deleteToDo(toDoId: Long){
        val db = writableDatabase
        db.delete(TABLE_TO_DO, "$COL_ID=?", arrayOf(toDoId.toString()))
        db.delete(TABLE_TODO_ITEM, "$COL_TODO_ID=?", arrayOf(toDoId.toString()))
    }

    fun updateToDoItemStatus(toDoId: Long, isCompleted: Boolean){
        val db = writableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$toDoId",null)
        if(queryResult.moveToFirst()){
            do {
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId = queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.isCompleted = isCompleted
                updateToDoItems(item)
            } while (queryResult.moveToNext())
        }
        queryResult.close()
    }

    fun deleteToDoItem(itemId: Long){
        val db = writableDatabase
        db.delete(TABLE_TODO_ITEM, "$COL_ID=?", arrayOf(itemId.toString()))
    }
}