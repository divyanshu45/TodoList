package com.example.todolist

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.DTO.ToDo
import com.example.todolist.DTO.ToDoItem
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler
    var toDoId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(ic_toolbar_item)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_)
        supportActionBar?.title = intent.getStringExtra(INTENT_TODO_NAME)
        toDoId = intent.getLongExtra(INTENT_TODO_ID, -1)
        dbHandler = DBHandler(this)

        rv_item.layoutManager = LinearLayoutManager(this)

        ic_add_item.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Create SubTask")
            val view = layoutInflater.inflate(R.layout.dailog_layout, null)
            val toDoName= view.findViewById<EditText>(R.id.ev_todo)

            dialog.setView(view)
            dialog.setPositiveButton("Add"){_: DialogInterface, _: Int ->
                if(toDoName.text.isNotEmpty()){
                    val item = ToDoItem()
                    item.itemName = toDoName.text.toString()
                    item.toDoId = toDoId
                    item.isCompleted = false
                    dbHandler.addToDoItems(item)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel"){_: DialogInterface, _: Int ->
            }
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList(){
        tasks_no.setText(dbHandler.getToDoItem(toDoId).size.toString() + " ITEMS")
        rv_item.adapter = ItemAdapter(this, dbHandler.getToDoItem(toDoId))
    }

    fun editToDoItem(item :ToDoItem){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Edit SubTask")
        val view = layoutInflater.inflate(R.layout.dailog_layout, null)
        val toDoName= view.findViewById<EditText>(R.id.ev_todo)
        toDoName.setText(item.itemName)

        dialog.setView(view)
        dialog.setPositiveButton("Upadate"){_: DialogInterface, _: Int ->
            if(toDoName.text.isNotEmpty()){
                item.itemName = toDoName.text.toString()
                item.toDoId = toDoId
                item.isCompleted = false
                dbHandler.updateToDoItems(item)
                refreshList()
            }
        }
        dialog.setNegativeButton("Cancel"){_: DialogInterface, _: Int ->
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    class ItemAdapter(val activity: ItemActivity, val list: MutableList<ToDoItem>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_item,parent,false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemName.text = list[position].itemName
            holder.itemName.isChecked = list[position].isCompleted
            holder.itemName.setOnClickListener {
                list[position].isCompleted = !list[position].isCompleted
                activity.dbHandler.updateToDoItems(list[position])
            }
            holder.delete.setOnClickListener {
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("Are you sure")
                dialog.setMessage("Do you want to delete?")
                dialog.setPositiveButton("Continue"){ _: DialogInterface, _: Int->
                    activity.dbHandler.deleteToDoItem(list[position].id)
                    activity.refreshList()
                }
                dialog.setNegativeButton("Cancel"){_: DialogInterface, _: Int->

                }
                dialog.setCancelable(false)
                dialog.show()
            }
            holder.edit.setOnClickListener {
                activity.editToDoItem(list[position])
            }
        }

        class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
            val itemName: CheckBox = itemView.findViewById(R.id.cb_item)
            val edit = itemView.findViewById<ImageView>(R.id.iv_edit)
            val delete = itemView.findViewById<ImageView>(R.id.iv_delete)
        }
    }
}