package com.example.todolist

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todolist.DTO.ToDo
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dailog_layout.*
import kotlinx.android.synthetic.main.dailog_layout.view.*

class DashboardActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler
    lateinit var spanText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dbHandler = DBHandler(this)
        rv_dashborad.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)

        ic_fab_btn.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Create Task")
            dialog.setCancelable(false)

            val view = layoutInflater.inflate(R.layout.dailog_layout, null)
            val toDoName= view.findViewById<EditText>(R.id.ev_todo)

            dialog.setView(view)
            dialog.setPositiveButton("Add"){_: DialogInterface, _: Int ->
                if(toDoName.text.isNotEmpty()){
                    val toDo = ToDo()
                    toDo.name = toDoName.text.toString()
                    dbHandler.addToDo(toDo)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel"){_: DialogInterface, _: Int ->
            }
            dialog.show()
        }
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList(){
        spanText = findViewById(R.id.desc)
        spanText.setText("You have " + dbHandler.getToDos().size + " tasks to be done")
        rv_dashborad.adapter = DashBoradAdapter(this, dbHandler.getToDos())
    }

    fun editToDo(toDo: ToDo){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Edit Task")
        dialog.setCancelable(false)
        val view = layoutInflater.inflate(R.layout.dailog_layout, null)
        val toDoName= view.findViewById<EditText>(R.id.ev_todo)
        toDoName.setText(toDo.name)

        dialog.setView(view)
        dialog.setPositiveButton("Update"){_: DialogInterface, _: Int ->
            if(toDoName.text.isNotEmpty()){
                toDo.name = toDoName.text.toString()
                dbHandler.editToDo(toDo)
                refreshList()
            }
        }
        dialog.setNegativeButton("Cancel"){_: DialogInterface, _: Int ->
        }
        dialog.show()
    }

    class DashBoradAdapter(val activity: DashboardActivity, val list: MutableList<ToDo>) : RecyclerView.Adapter<DashBoradAdapter.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard,parent,false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.toDoName.text = list[position].name
            holder.toDoName.setOnClickListener {
                val intent = Intent(activity, ItemActivity::class.java)
                intent.putExtra(INTENT_TODO_ID,list[position].id)
                intent.putExtra(INTENT_TODO_NAME,list[position].name)
                activity.startActivity(intent)
            }
            holder.menu.setOnClickListener {
                val popupMenu = PopupMenu(activity,holder.menu)
                popupMenu.inflate(R.menu.dashboard_child)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_edit ->{
                            activity.editToDo(list[position])
                        }
                        R.id.menu_delete ->{
                            val dialog = AlertDialog.Builder(activity)
                            dialog.setTitle("Are you sure")
                            dialog.setMessage("Do you want to delete?")
                            dialog.setPositiveButton("Continue"){ _: DialogInterface, _: Int->
                                activity.dbHandler.deleteToDo(list[position].id)
                                activity.refreshList()
                            }
                            dialog.setNegativeButton("Cancel"){_: DialogInterface, _: Int->

                            }
                            dialog.setCancelable(false)
                            dialog.show()
                        }
                        R.id.menu_markAsCompleted ->{
                            activity.dbHandler.updateToDoItemStatus(list[position].id, true)
                        }
                        R.id.menu_reset ->{
                            activity.dbHandler.updateToDoItemStatus(list[position].id, false)
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }

        class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
            val toDoName = itemView.findViewById<TextView>(R.id.tv_todoName)
            val menu = itemView.findViewById<ImageView>(R.id.iv_menu)
        }
    }
}