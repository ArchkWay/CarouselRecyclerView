package supahsoftware.androidexamplecarousel

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import supahsoftware.androidexamplecarousel.HorizontalCarouselRecyclerView.LoadOnScrollDirection
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager


class HorizontalCarouselRecyclerView(
    context: Context,
    attrs: AttributeSet
) : RecyclerView(context, attrs) {
    // Sets the starting page index
    private val startingPageIndex = 0
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private val visibleThreshold = 5
    // The current offset index of data you have loaded
    private val currentPage = 0
    // The total number of items in the dataset after the last load
    private val previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.
    private val loading = true
    private var mLayoutManager: LayoutManager? = null

    private var mDirection: LoadOnScrollDirection? = null

    fun HorizontalCarouselRecyclerView(
        layoutManager: LinearLayoutManager,
        direction: LoadOnScrollDirection
    ) {
        mLayoutManager = layoutManager
        mDirection = direction
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        val view = layoutManager!!.focusedChild ?: return super.getChildDrawingOrder(childCount, i)
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


                        }
                    })
                }
            }
        })
        adapter = newAdapter
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in 0..lastVisibleItemPositions.size) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    fun getFirstVisibleItem(firstVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in 0..firstVisibleItemPositions.size) {
            if (i == 0) {
                maxSize = firstVisibleItemPositions[i]
            } else if (firstVisibleItemPositions[i] > maxSize) {
                maxSize = firstVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    private fun onScrollChanged() {
        post {
            (0 until childCount).forEach { position ->
                val child = getChildAt(position)
                val childCenterX = (child.left + child.right) / 2
                val scaleValue = getGaussianScale(childCenterX, 1.5f, 1.5f, 150.toDouble())
                child.scaleX = scaleValue
                child.scaleY = scaleValue
//                colorView(child, scaleValue)
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

    enum class LoadOnScrollDirection {
        TOP, BOTTOM
    }
}