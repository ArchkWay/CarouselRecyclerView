package supahsoftware.androidexamplecarousel

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.nio.file.Files.size
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class HorizontalCarouselRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        val view = layoutManager?.focusedChild ?: return super.getChildDrawingOrder(childCount, i)
        val position = indexOfChild(view)

        if (position < 0) {
            return super.getChildDrawingOrder(childCount, i)
        }
        if (i == childCount - 1) {
            return position
        }
        return if (i == position) {
            childCount - 1
        } else super.getChildDrawingOrder(childCount, i)
    }

    fun <T : ViewHolder> initialize(newAdapter: Adapter<T>) {
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        (layoutManager as LinearLayoutManager).setStackFromEnd(true)
        newAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                post {
                    val sidePadding = (width / 2) - (getChildAt(0).width / 2)
                    setPadding(sidePadding, 0, sidePadding, 0)
                    scrollToPosition(0)
                    addOnScrollListener(object : OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            onScrollChanged()
                            val firstItemVisible =
                                (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                            if (firstItemVisible != 0 && firstItemVisible % childCount === 0) {
                                recyclerView.layoutManager?.scrollToPosition(0)
                            }
                        }
                    })
                }
            }
        })
        adapter = newAdapter
    }


    private fun onScrollChanged() {
        post {
            (0 until childCount).forEach { position ->
                val child = getChildAt(position)
                val childCenterX = (child.left + child.right) / 2
                val scaleValue = getGaussianScale(childCenterX, 1.5f, 1.5f, 150.toDouble())
                child.scaleX = scaleValue
                child.scaleY = scaleValue
                this.invalidate()
                setChildrenDrawingOrderEnabled(true)
            }
        }
    }


    private fun getGaussianScale(
        childCenterX: Int,
        minScaleOffest: Float,
        scaleFactor: Float,
        spreadFactor: Double
    ): Float {
        val recyclerCenterX = (left + right) / 2
        return (Math.pow(
            Math.E,
            -Math.pow(childCenterX - recyclerCenterX.toDouble(), 2.toDouble()) / (2 * Math.pow(
                spreadFactor,
                2.toDouble()
            ))
        ) * scaleFactor + minScaleOffest).toFloat()
    }
}