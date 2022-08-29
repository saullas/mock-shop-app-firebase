package  com.myshoppal.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shopappfirebase.R

// TODO Step 1: Add the class to add the feature for Swipe right to edit the address.
// START
// For detail explanation of this class you can look at below link.
// https://medium.com/@kitek/recyclerview-swipe-to-delete-easier-than-you-thought-cff67ff5e5f6
/**
 * A abstract class which we will use for edit feature.
 */
abstract class SwipeToDeleteCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

	private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)
	private val intrinsicWidth = deleteIcon!!.intrinsicWidth
	private val intrinsicHeight = deleteIcon!!.intrinsicHeight
	private val background = ColorDrawable()
	private val backgroundColor = Color.parseColor("#F4442D")
	private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

	override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
		/**
		 * To disable "swipe" for specific item return 0 here.
		 * For example:
		 * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
		 * if (viewHolder?.adapterPosition == 0) return 0
		 */
		if (viewHolder.adapterPosition == 10) return 0
		return super.getMovementFlags(recyclerView, viewHolder)
	}

	override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
		return false
	}

	override fun onChildDraw(
		c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
		dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
	) {

		val itemView = viewHolder.itemView
		val itemHeight = itemView.bottom - itemView.top
		val isCanceled = dX == 0f && !isCurrentlyActive

		if (isCanceled) {
			clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
			super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
			return
		}

		// Draw the edit background
		background.color = backgroundColor
		background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
		background.draw(c)

		// Calculate position of edit icon
		val editIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
		val editIconMargin = (itemHeight - intrinsicHeight)
		val editIconLeft = itemView.right + editIconMargin - intrinsicWidth
		val editIconRight = itemView.right + editIconMargin
		val editIconBottom = editIconTop + intrinsicHeight

		// Draw the delete icon
		deleteIcon!!.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
		deleteIcon.draw(c)

		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
	}

	private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
		c?.drawRect(left, top, right, bottom, clearPaint)
	}
}