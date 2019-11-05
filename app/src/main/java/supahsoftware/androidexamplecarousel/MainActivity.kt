package supahsoftware.androidexamplecarousel

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.os.HandlerCompat.postDelayed
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private val itemAdapter by lazy {
        ItemAdapter { position: Int, item: Item ->
            Toast.makeText(this@MainActivity, "Pos ${position}", Toast.LENGTH_LONG).show()
            item_list.smoothScrollToPosition(position)
        } }
    private val possibleItems = listOf(
        Item("Airplanes", R.drawable.ic_airplane),
        Item("Cars", R.drawable.ic_car),
        Item("Food", R.drawable.ic_food),
        Item("Gas", R.drawable.ic_gas),
        Item("Home", R.drawable.ic_home)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        item_list.initialize(itemAdapter)
//        item_list.setViewsToChangeColor(listOf(R.id.list_item_background, R.id.list_item_text))
        itemAdapter.setItems(getLargeListOfItems())
        autoScroll(item_list)
    }

    private fun getLargeListOfItems(): List<Item> {
        val items = mutableListOf<Item>()
        (0..40).map { items.add(possibleItems.random()) }
        return items
    }
}

fun autoScroll(recyclerView: RecyclerView) {
    val handler = Handler()
    val runnable = object : Runnable {
        override fun run() {
            recyclerView.scrollBy(2, 0)
            handler.postDelayed(this, 0)
        }
    }
    handler.postDelayed(runnable, 0)
}
data class Item(
    val title: String,
    @DrawableRes val icon: Int
)