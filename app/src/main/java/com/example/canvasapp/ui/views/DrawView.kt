package com.example.canvasapp.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.canvasapp.ui.CanvasMainFragment.Companion.paintBrush
import com.example.canvasapp.ui.CanvasMainFragment.Companion.path

class DrawView : View {

    var params: ViewGroup.LayoutParams? = null

    private lateinit var drawBitmap: Bitmap
    private lateinit var drawCanvas: Canvas


    companion object {
        var pathList = ArrayList<Path>()
        var colorList = ArrayList<Int>()
        var brushSizeList = ArrayList<Float>()
        var currentBrush = Color.BLACK
        var brushSize = 8f
    }

    constructor(context: Context) : this(context, null) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        paintBrush.isAntiAlias = true
        paintBrush.color = currentBrush
        paintBrush.style = Paint.Style.STROKE
        paintBrush.strokeJoin = Paint.Join.ROUND
        paintBrush.strokeWidth = brushSize

        drawBitmap = Bitmap.createBitmap(900, 900, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(drawBitmap)

        params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var x = event.x
        var y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                //pathList.add(path)
                //colorList.add(currentBrush)
                //brushSizeList.add(brushSize)
            }
            MotionEvent.ACTION_UP -> {
                pathList.add(Path(path))
                colorList.add(currentBrush)
                brushSizeList.add(brushSize)
                path.reset()
            }
            else -> return false
        }
        //Use postInvalidate after changes on UI
        postInvalidate()
        return false
    }

    fun save(): Bitmap? {
        // Create a new bitmap with the same dimensions as the drawBitmap
        val savedBitmap =
            Bitmap.createBitmap(drawBitmap.width, drawBitmap.height, Bitmap.Config.ARGB_8888)

        // Create a new canvas with the savedBitmap
        val savedCanvas = Canvas(savedBitmap)

        // Draw the contents of the drawCanvas (paths, colors, etc.) onto the savedCanvas
        savedCanvas.drawBitmap(drawBitmap, 0f, 0f, null)
        for (i in pathList.indices) {
            paintBrush.color = colorList[i]
            paintBrush.strokeWidth = brushSizeList[i]
            savedCanvas.drawPath(pathList[i], paintBrush)
        }

        // Return the updated savedBitmap
        return savedBitmap
    }

    override fun onDraw(canvas: Canvas) {

        for (i in pathList.indices) {
            paintBrush.color = colorList[i]
            paintBrush.strokeWidth = brushSizeList[i]
            canvas.drawPath(pathList[i], paintBrush)
            invalidate() //changes done on UI
        }

        //draws the currently being drawn path
        paintBrush.color = currentBrush
        paintBrush.strokeWidth = brushSize
        canvas.drawPath(path, paintBrush)
    }
}