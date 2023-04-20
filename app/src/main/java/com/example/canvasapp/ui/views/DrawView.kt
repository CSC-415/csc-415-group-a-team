package com.example.canvasapp.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.canvasapp.ui.CanvasMainFragment
import com.example.canvasapp.ui.CanvasMainFragment.Companion.paintBrush
import com.example.canvasapp.ui.CanvasMainFragment.Companion.path
import java.util.*
import kotlin.collections.ArrayList

class DrawView : View {

    var params: ViewGroup.LayoutParams? = null

    var fragment: CanvasMainFragment? = null //will get set by canvasmainfragment

    private lateinit var drawBitmap: Bitmap
    private lateinit var drawCanvas: Canvas


    companion object {
        var pathList = ArrayList<Path>()
        var colorList = ArrayList<Int>()
        var opacityList = ArrayList<Int>()
        var brushSizeList = ArrayList<Float>()

        var undonePathList = ArrayList<Path>()
        var undoneColorList = ArrayList<Int>()
        var undoneOpacityList = ArrayList<Int>()
        var undoneBrushSizeList = ArrayList<Float>()

        var currentBrush = Color.BLACK
        var brushSize = 8f
        var brushOpacity = 255

        enum class Tool {
            BRUSH, ERASER, PICKER, FILLCAN
        }

        var currentTool = Tool.BRUSH
    }

    constructor(context: Context) : this(context, null) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        paintBrush.isAntiAlias = true
        paintBrush.color = currentBrush
        paintBrush.style = Paint.Style.STROKE
        paintBrush.strokeJoin = Paint.Join.ROUND
        paintBrush.strokeWidth = brushSize
        paintBrush.alpha = brushOpacity

        drawBitmap = Bitmap.createBitmap(900, 1100, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(drawBitmap)
        drawCanvas.drawColor(Color.WHITE)

        params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var x = event.x
        var y = event.y
        when (currentTool) {
            Tool.BRUSH ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        path.moveTo(x, y)
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        path.lineTo(x, y)
                    }
                    MotionEvent.ACTION_UP -> {
                        pathList.add(Path(path))
                        colorList.add(currentBrush)
                        brushSizeList.add(brushSize)
                        opacityList.add(brushOpacity)
                        path.reset()
                        undonePathList.clear()
                        fragment?.updateUndoRedoButtons()
                    }
                    else -> return false
                }
            Tool.ERASER ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        path.moveTo(x, y)
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        path.lineTo(x, y)
                    }
                    MotionEvent.ACTION_UP -> {
                        pathList.add(Path(path))
                        colorList.add(Color.WHITE)
                        brushSizeList.add(brushSize)
                        opacityList.add(255)
                        path.reset()
                        undonePathList.clear()
                        fragment?.updateUndoRedoButtons()
                    }
                    else -> return false
                }
            Tool.PICKER -> {
                var tempBitmap = getTempBitmap()
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val pickedColor = getPixelColor(x, y, tempBitmap)
                        if (pickedColor != Color.TRANSPARENT) {
                            currentBrush = pickedColor
                            paintBrush.color = currentBrush
                        }
                        fragment?.buttonBackgroundReset()
                    }
                    MotionEvent.ACTION_UP -> {
                        val pickedColor = getPixelColor(x, y, tempBitmap)
                        if (pickedColor != Color.TRANSPARENT) {
                            currentBrush = pickedColor
                            paintBrush.color = currentBrush
                        }
                        currentTool = Tool.BRUSH
                        fragment?.buttonBackgroundReset()
                    }
                    else -> return false
                }
            }

            Tool.FILLCAN -> {
                TODO()
            }
        }

        //Use postInvalidate after changes on UI
        postInvalidate()
        return false
    }

    fun save(): Bitmap? {
        val savedBitmap =
            Bitmap.createBitmap(drawBitmap.width, drawBitmap.height, Bitmap.Config.ARGB_8888)

        val savedCanvas = Canvas(savedBitmap)

        savedCanvas.drawBitmap(drawBitmap, 0f, 0f, null)
        savedCanvas.drawColor(Color.WHITE)

        drawLines(savedCanvas)

        return savedBitmap
    }

    override fun onDraw(canvas: Canvas) {
        drawLines(canvas)

        //draws the currently being drawn path
        if (currentTool == Tool.BRUSH) {
            paintBrush.color = currentBrush
            paintBrush.alpha = brushOpacity
            paintBrush.strokeWidth = brushSize
            canvas.drawPath(path, paintBrush)
        }
        if (currentTool == Tool.ERASER) {
            paintBrush.color = Color.WHITE
            paintBrush.alpha = 255
            paintBrush.strokeWidth = brushSize
            canvas.drawPath(path, paintBrush)
        }
    }

    private fun drawLines(canvas: Canvas) {
        for (i in pathList.indices) {
            paintBrush.color = colorList[i]
            paintBrush.alpha = opacityList[i]
            paintBrush.strokeWidth = brushSizeList[i]
            canvas.drawPath(pathList[i], paintBrush)
        }
    }

    fun undo() {
        if (pathList.isNotEmpty()) {
            undonePathList.add(pathList.removeAt(pathList.size - 1))
            undoneColorList.add(colorList.removeAt(colorList.size - 1))
            undoneOpacityList.add(opacityList.removeAt(opacityList.size - 1))
            undoneBrushSizeList.add(brushSizeList.removeAt(brushSizeList.size - 1))
            invalidate()
        }
    }

    fun redo() {
        if (undonePathList.isNotEmpty()) {
            pathList.add(undonePathList.removeAt(undonePathList.size - 1))
            colorList.add(undoneColorList.removeAt(undoneColorList.size - 1))
            opacityList.add(undoneOpacityList.removeAt(undoneOpacityList.size - 1))
            brushSizeList.add(undoneBrushSizeList.removeAt(undoneBrushSizeList.size - 1))
            invalidate()
        }
    }

    fun canUndo(): Boolean {
        return pathList.isNotEmpty()
    }

    fun canRedo(): Boolean {
        return undonePathList.isNotEmpty()
    }

    private fun getTempBitmap(): Bitmap {
        val tempBitmap =
            Bitmap.createBitmap(drawBitmap.width, drawBitmap.height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)
        tempCanvas.drawColor(Color.WHITE)
        drawLines(tempCanvas)
        return tempBitmap
    }

    private fun getPixelColor(x: Float, y: Float, tempBitmap: Bitmap): Int {
        //val tempBitmap = getTempBitmap()

        if (x >= 0 && y >= 0 && x < drawBitmap.width && y < drawBitmap.height) {
            return tempBitmap.getPixel(x.toInt(), y.toInt())
        } else {
            return Color.TRANSPARENT
        }
    }
}