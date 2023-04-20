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
            BRUSH, ERASER, PICKER
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

        drawBitmap = Bitmap.createBitmap(900, 900, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(drawBitmap)
        drawCanvas.drawColor(Color.WHITE)

        params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var x = event.x
        var y = event.y
        if (currentTool == Tool.BRUSH) {
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
                    undonePathList.clear() // Clear the redo list
                    fragment?.updateUndoRedoButtons() // Update the undo and redo buttons
                }
                else -> return false
            }
        }
        if (currentTool == Tool.ERASER) {
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
                    undonePathList.clear() // Clear the redo list
                    fragment?.updateUndoRedoButtons() // Update the undo and redo buttons
                }
                else -> return false
            }
        }
        if (currentTool == Tool.PICKER) {
            //val savedCanvas =
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    return true
                }
                MotionEvent.ACTION_MOVE -> {

                }
                MotionEvent.ACTION_UP -> {
                }
                else -> return false
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

        for (i in pathList.indices) {
            drawLines(savedCanvas, i)
        }

        return savedBitmap
    }

    override fun onDraw(canvas: Canvas) {
        for (i in pathList.indices) {
            drawLines(canvas, i)
            invalidate() //changes done on UI
        }

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

    private fun drawLines(canvas: Canvas, i: Int) {
        paintBrush.color = colorList[i]
        paintBrush.alpha = opacityList[i]
        paintBrush.strokeWidth = brushSizeList[i]
        canvas.drawPath(pathList[i], paintBrush)
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

//    private fun FloodFill(pt: Point, targetColor: Int, replacementColor: Int) {
//        val q: Queue<Point> = LinkedList<Point>()
//        q.add(pt)
//        while (q.size > 0) {
//            val n: Point = q.poll()
//            if (drawBitmap.getPixel(n.x, n.y) !== targetColor) continue
//            val w: Point = n
//            val e = Point(n.x + 1, n.y)
//            while (w.x > 0 && drawBitmap.getPixel(w.x, w.y) === targetColor) {
//                drawBitmap.setPixel(w.x, w.y, replacementColor)
//                if (w.y > 0 && drawBitmap.getPixel(w.x, w.y - 1) === targetColor) q.add(
//                    Point(w.x, w.y - 1)
//                )
//                if (w.y < drawBitmap.getHeight() - 1 && drawBitmap.getPixel(
//                        w.x, w.y + 1
//                    ) === targetColor
//                ) q.add(
//                    Point(w.x, w.y + 1)
//                )
//                w.x--
//            }
//            while (e.x < drawBitmap.getWidth() - 1 && drawBitmap.getPixel(
//                    e.x, e.y
//                ) === targetColor
//            ) {
//                drawBitmap.setPixel(e.x, e.y, replacementColor)
//                if (e.y > 0 && drawBitmap.getPixel(e.x, e.y - 1) === targetColor) q.add(
//                    Point(e.x, e.y - 1)
//                )
//                if (e.y < drawBitmap.getHeight() - 1 && drawBitmap.getPixel(
//                        e.x, e.y + 1
//                    ) === targetColor
//                ) q.add(
//                    Point(e.x, e.y + 1)
//                )
//                e.x++
//            }
//        }
//    }
}