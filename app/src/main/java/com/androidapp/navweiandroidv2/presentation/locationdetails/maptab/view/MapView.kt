package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.androidapp.entity.models.Node
import com.androidapp.entity.models.NodeModel
import com.androidapp.entity.models.NodeType
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.OnClickStoreCloseEvent
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.OnClickStoreNodeEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import it.sephiroth.android.library.uigestures.*
import java.util.*

/**
 * Created by S.Nur Uysal on 2019-12-06.
 */

class MapView : ImageView,
    UIGestureRecognizer.OnActionListener, UIGestureRecognizerDelegate.Callback {
    private val TAG = "MapView"

    private val delegate = UIGestureRecognizerDelegate(null)
    private var pathArrowBitmap: Bitmap? = null

    private var maxScale = 5.5f
    private var minScale = 0.84f
    private var startScale = 0f

    private var bmp: Bitmap? = null
    private var paint: Paint = Paint()
    private var paintWhite: Paint = Paint()
    private var pathPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var matrixM: Matrix = Matrix()

    private var centerX: Double = 0.0
    private var centerY: Double = 0.0
    private var topLeftX = 0f
    private var topLeftY = 0f

    private var leftBottom: PointF = PointF()
    private var topRight: PointF = PointF()
    private var centerPoint: PointF = PointF()

    private var mapPoints = mutableListOf<MapPoi>()

    private var pathNodesLinkList: MutableList<NodeModel> = mutableListOf()

    private var pointNodesList: List<NodeModel> = listOf()
    private var linkNodesList: MutableList<NodeModel> = mutableListOf()
    private var allPointNodesMap: HashMap<String, NodeModel> = hashMapOf()
    private var pointNodesBitmapMap: HashMap<String, Bitmap?> = hashMapOf()

    private var currentAllPathLinkList: MutableList<NodeModel> = mutableListOf()

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    fun setMapData(
        pointNodesList: List<NodeModel>,
        linkNodes: MutableList<NodeModel>,
        allPointNodesMap: HashMap<String, NodeModel>
    ) {
        this.allPointNodesMap = allPointNodesMap
        this.pointNodesList = pointNodesList
        this.linkNodesList = linkNodes


    }


    fun startMap(mapBitmap: Bitmap) {

        bmp = mapBitmap
        Log.d(TAG, "loadMainMap")

//        Log.d(TAG, "data height:${bmp?.height}  width:${bmp?.width} ")

        matrixM = Matrix()
        startScale = width.toFloat() / bmp?.width!!.toFloat()


        minScale = startScale
        maxScale = startScale * 8

        matrixM.postScale(startScale, startScale)
        matrixM.postTranslate(
            ((width - bmp!!.width * startScale) / 2),
            ((height - bmp!!.height * startScale) / 2)
        )

        val data = FloatArray(9)
        matrixM.getValues(data)
        onGestureUpdate(data)

    }


    override fun onGestureRecognized(p0: UIGestureRecognizer) {
        if (bmp == null) return

        val data = FloatArray(9)
        matrixM.getValues(data)

        when (p0) {
            is UIRotateGestureRecognizer -> {
                matrixM.postRotate(-p0.rotationInDegrees, p0.currentLocationX, p0.currentLocationY)
            }
            is UIPinchGestureRecognizer -> {
//                Log.d(TAG, "UIPinchGestureRecognizer")
                var zoom = p0.scaleFactor

                val scaleX = data[Matrix.MSCALE_X]
                val skewY = data[Matrix.MSKEW_Y]
                val realScale = Math.sqrt((scaleX * scaleX + skewY * skewY).toDouble()).toFloat()

                if (realScale >= maxScale) {
                    if (p0.scaleFactor > 1f) {
                        zoom = 1f
                    }
                } else if (realScale <= minScale) {
                    if (p0.scaleFactor < 1f) {
                        zoom = 1f
                    }
                }
                matrixM.postScale(zoom, zoom, p0.currentLocationX, p0.currentLocationY)
            }
            is UIPanGestureRecognizer -> {
//                Log.d(TAG, "UIPanGestureRecognizer")
                val translateX = p0.scrollX
                val translateY = p0.scrollY

                matrixM.postTranslate(translateX, translateY)

            }
            is UITapGestureRecognizer -> {
//                Log.d(TAG, "UITapGestureRecognizer")
                if (p0.tag == "single-tap") {
                    Log.d(TAG, "UITapGestureRecognizer single-tap")
                    mapPoints.forEach { point ->
                        if (point.xMin < p0.currentLocationX && point.xMax > p0.currentLocationX
                            && point.yMin < p0.currentLocationY && point.yMax > p0.currentLocationY
                        ) {
                            zoomToStore(p0.currentLocationX, p0.currentLocationY)
                            if (allPointNodesMap.containsKey(point.node?.id!!))
                                RxBus.publish(OnClickStoreNodeEvent(allPointNodesMap[point.node.id!!]!!))
                            return
                        }
                    }

                    RxBus.publish(OnClickStoreCloseEvent())

                } else {
                    zoomMapDoubleTap(p0.currentLocationX, p0.currentLocationY)
                }

            }

        }

        onGestureUpdate(data)

    }

    private fun onGestureUpdate(data: FloatArray) {
        matrixM.getValues(data)
        val scaleX = data[Matrix.MSCALE_X]
        val skewY = data[Matrix.MSKEW_Y]

        topLeftX = data[Matrix.MTRANS_X]
        topLeftY = data[Matrix.MTRANS_Y]

        val rAngle: Double = 360.0 - Math.round(
            Math.atan2(
                data[Matrix.MSKEW_X].toDouble(),
                data[Matrix.MSCALE_X].toDouble()
            ) * (180f / Math.PI)
        ).toDouble()

        val angle2 = Math.toDegrees(Math.atan(bmp!!.height.toDouble() / bmp!!.width.toDouble()))
        val realScale = Math.sqrt((scaleX * scaleX + skewY * skewY).toDouble()).toFloat()

        val bmpWidth = bmp!!.width * (realScale) / 2
        val bmpHeight = bmp!!.height * (realScale) / 2
        val distance = Math.sqrt((bmpWidth * bmpWidth + bmpHeight * bmpHeight).toDouble())
//        if (centerPoint.x == 0f)
        centerX =
            ((topLeftX + distance * Math.cos(Math.toRadians(rAngle + angle2))))

//        if (centerPoint.y == 0f)
        centerY =
            ((topLeftY + distance * Math.sin(Math.toRadians(rAngle + angle2))))

        //find center x,y of map image
//        centerX = centerPoint.x.toDouble()
//        centerY = centerPoint.y.toDouble()


        var translateX = 0f
        var translateY = 0f
        val centerMatrix = Matrix(matrixM)
        centerMatrix.preTranslate(bmp!!.width * 0.5f, bmp!!.height * 0.5f)


        //below code for map image move inside the screen
        if (centerX < -(bmpWidth - width / 2)) {
            translateX = Math.abs(centerX + (bmpWidth - width / 2)).toFloat()
        } else if (centerX > width + (bmpWidth - width / 2)) {
            translateX = -(centerX - (width + (bmpWidth - width / 2))).toFloat()
        }

        if (centerY < -(bmpHeight - height / 2)) {
            translateY = Math.abs(centerY + (bmpHeight - height / 2)).toFloat()
        } else if (centerY > height + (bmpHeight - height / 2)) {
            translateY = -(centerY - (height + (bmpHeight - height / 2))).toFloat()
        }

        matrixM.postTranslate(translateX, translateY)
        invalidate()

    }


    fun initView() {
        paint.isAntiAlias = true

        paintWhite.isAntiAlias = true
        paintWhite.color = ContextCompat.getColor(context, R.color.white)
//        paintWhite.setShadowLayer(dpToPx(3F).toFloat(), 0.0f, 2.0f, context.resources.getColor(R.color.white))

        pathPaint.isAntiAlias = true
        pathPaint.strokeWidth = dpToPx(4F).toFloat()
        pathPaint.style = Paint.Style.STROKE

        pathPaint.color = ContextCompat.getColor(context, R.color.yellow)
        pathPaint.setShadowLayer(
            dpToPx(3F).toFloat(),
            0.0f,
            2.0f,
            ContextCompat.getColor(context, R.color.dark_yellow)
        )

        pathPaint.isDither = true
        pathPaint.style = Paint.Style.STROKE
        pathPaint.strokeJoin = Paint.Join.ROUND
        pathPaint.strokeCap = Paint.Cap.ROUND
        pathPaint.pathEffect = CornerPathEffect(4F)


        delegate.setCallback(this)

        val singleTapRecognizer = UITapGestureRecognizer(context)
        singleTapRecognizer.setNumberOfTapsRequired(1)
        singleTapRecognizer.setNumberOfTouchesRequired(1)
        singleTapRecognizer.tag = "single-tap"
        singleTapRecognizer.setActionListener(this)

        val doubleTapRecognizer = UITapGestureRecognizer(context)
        doubleTapRecognizer.setNumberOfTapsRequired(2)
        doubleTapRecognizer.setNumberOfTouchesRequired(1)
        doubleTapRecognizer.tag = "double-tap"
        doubleTapRecognizer.setActionListener(this)

        val rotateRecognizer = UIRotateGestureRecognizer(context)
        rotateRecognizer.tag = "rotate"
        rotateRecognizer.setActionListener(this)

        val panRecognizer = UIPanGestureRecognizer(context)
        panRecognizer.tag = "pan"
        panRecognizer.minimumNumberOfTouches = 1
        panRecognizer.maximumNumberOfTouches = 1
        panRecognizer.setActionListener(this)

        val pinchRecognizer = UIPinchGestureRecognizer(context)
        pinchRecognizer.tag = "pinch"
        pinchRecognizer.setActionListener(this)

        singleTapRecognizer.requireFailureOf(doubleTapRecognizer)

        delegate.addGestureRecognizer(singleTapRecognizer)
        delegate.addGestureRecognizer(doubleTapRecognizer)
        delegate.addGestureRecognizer(rotateRecognizer)
        delegate.addGestureRecognizer(panRecognizer)
        delegate.addGestureRecognizer(pinchRecognizer)

    }

    fun setPointNodesBitmapMap(pointNodesBitmap: HashMap<String, Bitmap?>) {
        this.pointNodesBitmapMap = pointNodesBitmap
        invalidate()
    }


    fun initBitmaps() {

        Glide.with(context).asBitmap().load(R.drawable.ic_diagonal_arrow_right)
            .into(object : CustomTarget<Bitmap>(
                dpToPx(14.99f),
                dpToPx(10.79f)
            ) {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    pathArrowBitmap = resource
                }
            })

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (bmp == null || bmp!!.isRecycled) return

        val data = FloatArray(9)

        matrixM.getValues(data)

        val scaleX = data[Matrix.MSCALE_X]
        val skewY = data[Matrix.MSKEW_Y]
        val realScale = Math.sqrt((scaleX * scaleX + skewY * skewY).toDouble()).toFloat()
        Log.d("tatatatarealScale", "realScale:$realScale scaleX:$scaleX skewY:$skewY")
        canvas!!.drawBitmap(bmp!!, matrixM, paint)


        Log.d(TAG, "onDraw  Started")
        mapPoints.clear()

        if (pathNodesLinkList.isNotEmpty()) {
            val drawPath = Path()

            pathNodesLinkList.forEach {

                val startNode = allPointNodesMap[it.node.from_id]
                val endNode = allPointNodesMap[it.node.to_id]

                val underlayMatrixStart = Matrix(matrixM)
                underlayMatrixStart.preTranslate(
                    startNode!!.node.coord!!.x.toFloat(),
                    startNode.node.coord!!.y.toFloat()
                )
                val xMinStart =
                    getXValueFromMatrix(underlayMatrixStart)
                val yMinStart =
                    getYValueFromMatrix(underlayMatrixStart)

                val underlayMatrixEnd = Matrix(matrixM)
                underlayMatrixEnd.preTranslate(
                    endNode!!.node.coord!!.x.toFloat(),
                    endNode.node.coord!!.y.toFloat()
                )
                val xMinEnd =
                    getXValueFromMatrix(underlayMatrixEnd)
                val yMinEnd =
                    getYValueFromMatrix(underlayMatrixEnd)


                drawPath.moveTo(xMinStart, yMinStart)
                drawPath.lineTo(xMinEnd, yMinEnd)
            }

            drawPath.close()
            canvas.drawPath(drawPath, pathPaint)

            for (i in 0 until pathNodesLinkList.size) {
                Log.d(
                    TAG,
                    "s:${allPointNodesMap[pathNodesLinkList[i].node.from_id]!!.locations?.location_details?.name} d:${allPointNodesMap[pathNodesLinkList[i].node.to_id]!!.locations?.location_details?.name}"
                )
                val node = allPointNodesMap[pathNodesLinkList[i].node.from_id]!!.node
                val nextNode = allPointNodesMap[pathNodesLinkList[i].node.to_id]!!.node

                val arX = when {
                    node.coord!!.x.toFloat() > nextNode.coord!!.x.toFloat() -> node.coord!!.x.toFloat() - kotlin.math.abs(
                        nextNode.coord!!.x.toFloat() - node.coord!!.x.toFloat()
                    ) / 2
                    else -> node.coord!!.x.toFloat() + kotlin.math.abs(nextNode.coord!!.x.toFloat() - node.coord!!.x.toFloat()) / 2
                }
                val arY = when {
                    node.coord!!.y.toFloat() > nextNode.coord!!.y.toFloat() -> node.coord!!.y.toFloat() - kotlin.math.abs(
                        nextNode.coord!!.y.toFloat() - node.coord!!.y.toFloat()
                    ) / 2
                    else -> node.coord!!.y.toFloat() + kotlin.math.abs(nextNode.coord!!.y.toFloat() - node.coord!!.y.toFloat()) / 2
                }


                val curEdgeMatrix = Matrix(matrixM)
                curEdgeMatrix.preTranslate(
                    node.coord!!.x.toFloat(),
                    node.coord!!.y.toFloat()
                )
                val xStart = getXValueFromMatrix(curEdgeMatrix)
                val yStart = getYValueFromMatrix(curEdgeMatrix)

                val nextEdgeMatrix = Matrix(matrixM)
                nextEdgeMatrix.preTranslate(
                    nextNode.coord!!.x.toFloat(),
                    nextNode.coord!!.y.toFloat()
                )
                val xEnd = getXValueFromMatrix(nextEdgeMatrix)
                val yEnd = getYValueFromMatrix(nextEdgeMatrix)

                val angle = getAngle(xStart, yStart, xEnd, yEnd)

                val v = FloatArray(9)
                curEdgeMatrix.getValues(v)

                var rAngle = Math.round(
                    Math.atan2(
                        v[Matrix.MSKEW_X].toDouble(),
                        v[Matrix.MSCALE_X].toDouble()
                    ) * (180 / Math.PI)
                ).toFloat()

                if (rAngle > 360) {
                    rAngle %= 360
                }


                val midEdgeMatrix = Matrix(matrixM)
                val data = FloatArray(9)
                midEdgeMatrix.getValues(data)

                midEdgeMatrix.preTranslate(
                    arX,
                    arY
                )


                val scaleZ = data[Matrix.MSCALE_X]
                val skewZ = data[Matrix.MSKEW_Y]
                val realScale = Math.sqrt((scaleZ * scaleZ + skewZ * skewZ).toDouble()).toFloat()

                val scale = 1f / realScale
//                Log.d("serifjdj", "${scale}")
                if (scale < 0.72f)
                    midEdgeMatrix.preScale(scale, scale)

                midEdgeMatrix.preRotate(angle + rAngle)

                midEdgeMatrix.preTranslate(
                    -pathArrowBitmap!!.width / 2f,
                    -pathArrowBitmap!!.height / 2f
                )

                canvas.drawBitmap(
                    pathArrowBitmap!!,
                    midEdgeMatrix, paint
                )
            }

        }
        if (pointNodesList.isNotEmpty()) {
            pointNodesList.forEach {
                if (it.node.type == NodeType.point) {

                    val underlayMatrix = Matrix(matrixM)
                    val data = FloatArray(9)
                    underlayMatrix.getValues(data)

                    underlayMatrix.preTranslate(
                        it.node.coord!!.x.toFloat(),
                        it.node.coord!!.y.toFloat()
                    )

                    val x = getXValueFromMatrix(underlayMatrix)
                    val y = getYValueFromMatrix(underlayMatrix)
                    if (pointNodesBitmapMap.containsKey(it.node.id)) {

                        val xMin =
                            x - pointNodesBitmapMap[it.node.id]!!.width / 2
                        val yMin =
                            y - pointNodesBitmapMap[it.node.id]!!.height / 2

                        val xMax =
                            x + pointNodesBitmapMap[it.node.id]!!.width / 2
                        val yMax =
                            y + pointNodesBitmapMap[it.node.id]!!.height / 2

                        val poi = MapPoi(xMin, xMax, yMin, yMax, it.node)
                        mapPoints.add(poi)

                        canvas.drawCircle(x, y, 50f, paintWhite)
                        canvas.drawBitmap(
                            pointNodesBitmapMap[it.node.id]!!,
                            xMin,
                            yMin,
                            paint
                        )
                        Log.d(
                            TAG,
                            "renderer: id:${it.node.id} x:${x} y:${y}"
                        )
                    }
                }
//                canvas.drawCircle(
//                    leftBottom.x,
//                    leftBottom.y,
//                    10f,
//                    Paint().also { it.color = Color.parseColor("#FF0000") })
//                canvas.drawCircle(
//                    topRight.x,
//                    topRight.y,
//                    10f,
//                    Paint().also { it.color = Color.parseColor("#00FF00") })
//                canvas.drawCircle(
//                    centerX.toFloat(),
//                    centerY.toFloat(),
//                    10f,
//                    Paint().also { it.color = Color.parseColor("#0000FF") })

//                canvas.drawRect(leftBottom.x, leftBottom.x, leftBottom.x, leftBottom.x, )

                val p = Paint()
// smooths
//                p.isAntiAlias = true
//                p.color = Color.RED
//                p.style = Paint.Style.STROKE
//                p.strokeWidth = 4.5f
//                canvas.drawRect(10f, 10f, 30f, 30f, p)
////                Log.d(TAG, "drawCircle: x:$x y:$y")
//                Log.d(TAG, "drawCircle: xMin:$xMin yMin:$yMin")

            }
        }


    }


    fun showPathOnMap(
        pathNodes: MutableList<NodeModel>,
        currentAllPathLinkList: MutableList<NodeModel> = mutableListOf()
    ) {

        this.pathNodesLinkList = pathNodes
        this.currentAllPathLinkList = currentAllPathLinkList
        invalidate()

    }


    fun zoomAndRotateMapWithSelectedPath() {
        if (pathNodesLinkList.isEmpty())
            return

        val startMatrix = Matrix(matrixM)
        val finMatrix = Matrix(matrixM)

        //move to start
        val startNode = allPointNodesMap[pathNodesLinkList[0].node.from_id]
        val endNode = allPointNodesMap[pathNodesLinkList[pathNodesLinkList.size - 1].node.to_id]

        val endPointMatrix = Matrix(finMatrix)
        endPointMatrix.preTranslate(
            endNode?.node?.coord?.x!!.toFloat(),
            endNode.node.coord!!.y.toFloat()
        )
        val xEnd = getXValueFromMatrix(endPointMatrix)
        val yEnd = getYValueFromMatrix(endPointMatrix)

        val startPointMatrix = Matrix(finMatrix)
        startPointMatrix.preTranslate(
            startNode?.node?.coord!!.x.toFloat(),
            startNode.node.coord!!.y.toFloat()
        )
        val xStart = getXValueFromMatrix(startPointMatrix)
        val yStart = getYValueFromMatrix(startPointMatrix)
        Log.d("serife", "xStart:$xStart yStart:$yStart ")
        val angle = getAngle(xStart, yStart, xEnd, yEnd)


        getPoints(startMatrix)

        val matrixWidth = Math.abs(topRight.x - leftBottom.x + dpToPx(40f))
        val matrixHeight = Math.abs(topRight.y - leftBottom.y + dpToPx(60f))

//        Log.d("tatatata", "matrixWidth:$matrixWidth matrixHeight:$matrixHeight")

        var scale = width.toFloat() / matrixWidth.toFloat()
        Log.d("serife", "sclae1:$scale ")
        val scaleH = height.toFloat() / matrixHeight.toFloat()
        if (scaleH < scale) scale = scaleH
        Log.d("serife", "sclae2:$scale ")

        if (scale > maxScale) {
            scale = maxScale - 1.0f
        } else if (scale < minScale) {
            scale = minScale + 0.5f
        }
        Log.d("serife", "sclae3:$scale ")
        finMatrix.postRotate(-angle - 90, centerPoint.x, centerPoint.y)
        finMatrix.postScale(scale, scale, centerPoint.x - dpToPx(20f), centerPoint.y - dpToPx(30f))

        animateMatrix(startMatrix, finMatrix)


    }

    fun zoomMapWithSelectedPath() {
        if (pathNodesLinkList.isEmpty())
            return

        val startMatrix = Matrix(matrixM)
        val finMatrix = Matrix(matrixM)

        //move to start
        val startNode = allPointNodesMap[pathNodesLinkList[0].node.from_id]
        val endNode = allPointNodesMap[pathNodesLinkList[pathNodesLinkList.size - 1].node.to_id]


        val startPointMatrix = Matrix(finMatrix)
        startPointMatrix.preTranslate(
            startNode?.node?.coord!!.x.toFloat(),
            startNode.node.coord!!.y.toFloat()
        )
        val xStart = getXValueFromMatrix(startPointMatrix)
        val yStart = getYValueFromMatrix(startPointMatrix)
        Log.d("serife", "xStart:$xStart yStart:$yStart ")

        getPoints(startMatrix)

        val matrixWidth = Math.abs(topRight.x - leftBottom.x + dpToPx(40f))
        val matrixHeight = Math.abs(topRight.y - leftBottom.y + dpToPx(60f))

//        Log.d("tatatata", "matrixWidth:$matrixWidth matrixHeight:$matrixHeight")

        var scale = width.toFloat() / matrixWidth.toFloat()
        Log.d("serife", "sclae1:$scale ")
        val scaleH = height.toFloat() / matrixHeight.toFloat()
        if (scaleH < scale) scale = scaleH
        Log.d("serife", "sclae2:$scale ")

        if (scale > maxScale) {
            scale = maxScale - 1.0f
        } else if (scale < minScale) {
            scale = minScale + 0.5f
        }
        Log.d("serife", "sclae3:$scale ")
        finMatrix.postScale(scale, scale, centerPoint.x - dpToPx(20f), centerPoint.y - dpToPx(30f))

        animateMatrix(startMatrix, finMatrix)
    }

    private fun getPoints(curMatrix: Matrix?) {
        val points = mutableListOf<PointF>()
//        Log.d(TAG, "size:${pathNodesLinkList.size}")

        pathNodesLinkList.forEach { nodeModel ->
            val startNode = allPointNodesMap[nodeModel.node.from_id]!!.node
            val endNode = allPointNodesMap[nodeModel.node.to_id]!!.node
            val startMatrix: Matrix? =
                when (curMatrix) {
                    null -> Matrix(matrixM)
                    else -> Matrix(curMatrix)
                }

            Log.d("serife", "startNode:${startNode.id}")

            Log.d("serife", "endNode:${endNode.id}")
            startMatrix!!.preTranslate(
                (startNode.coord?.x!!.toFloat()),
                (startNode.coord!!.y.toFloat())
            )
            val xMinStart = getXValueFromMatrix(startMatrix)
            val yMinStart = getYValueFromMatrix(startMatrix)
            Log.d("serife", "xMinStart.x:${xMinStart} yMinStart.y:${yMinStart}")
            points.add(PointF(xMinStart, yMinStart))

            val endMatrix: Matrix? =
                when (curMatrix) {
                    null -> Matrix(matrixM)
                    else -> Matrix(curMatrix)
                }

            endMatrix!!.preTranslate(
                (endNode.coord?.x!!.toFloat()),
                (endNode.coord!!.y.toFloat())
            )

            val xMinEnd = getXValueFromMatrix(endMatrix)
            val yMinEnd = getYValueFromMatrix(endMatrix)
            Log.d("serife", "xMinEnd.x:${xMinEnd} yMinEnd.y:${yMinEnd}")
            points.add(PointF(xMinEnd, yMinEnd))

        }

        leftBottom.x = points[0].x
        leftBottom.y = points[0].y
        topRight.x = points[0].x
        topRight.y = points[0].y

        points.forEach {

            if (it.x < leftBottom.x) {
                leftBottom.x = it.x
            }
            if (it.x > topRight.x) {
                topRight.x = it.x
            }
            if (it.y > leftBottom.y) {
                leftBottom.y = it.y
            }
            if (it.y < topRight.y) {
                topRight.y = it.y
            }
        }
        centerPoint.x = leftBottom.x + Math.abs(topRight.x - leftBottom.x) / 2
        centerPoint.y = topRight.y + Math.abs(leftBottom.y - topRight.y) / 2
        Log.d("serife", "leftBottom.x:${leftBottom.x} leftBottom.y:${leftBottom.y}")
        Log.d("serife", "topRight.x:${topRight.x} topRight.y:${topRight.y}")
        Log.d("serife", "center.x:${centerPoint.x} center.y:${centerPoint.y}")

    }


    private fun animateMatrix(startMatrix: Matrix, finMatrix: Matrix) {
        val startData = FloatArray(9)
        startMatrix.getValues(startData)

        val finData = FloatArray(9)
        finMatrix.getValues(finData)

        val curData = FloatArray(9)
        startMatrix.getValues(curData)

        val animator = ValueAnimator.ofFloat(0.00f, 1.00f)

        animator.addUpdateListener { animation ->

            val curModifier = animation.animatedValue as Float

            for (i in 0 until 9) {
                curData[i] = startData[i] + (finData[i] - startData[i]) * curModifier
            }
            matrixM.setValues(curData)
            invalidate()
        }
        animator.duration = 150
        animator.start()
    }


    private fun getXValueFromMatrix(matrixA: Matrix): Float {

        val values = FloatArray(9)
        matrixA.getValues(values)

        return values[2]
    }

    private fun getAngle(xStart: Float, yStart: Float, xEnd: Float, yEnd: Float): Float {
        var angle =
            Math.toDegrees(Math.atan2((yEnd - yStart).toDouble(), (xEnd - xStart).toDouble()))

        if (angle < 0) {
            angle += 360f
        }

        return angle.toFloat()
    }

    private fun getYValueFromMatrix(matrixA: Matrix): Float {

        val values = FloatArray(9)
        matrixA.getValues(values)

        return values[5]
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return delegate.onTouchEvent(this, event)
    }

    private fun zoomMapDoubleTap(x: Float, y: Float) {

        val startMatrix = Matrix(matrixM)
        val finMatrix = Matrix(matrixM)

        val data = FloatArray(9)
        matrixM.getValues(data)


        val scaleX = data[Matrix.MSCALE_X]
        val skewY = data[Matrix.MSKEW_Y]
        val realScale = Math.sqrt((scaleX * scaleX + skewY * skewY).toDouble()).toFloat()

        var zoom = 0f
        if (realScale == maxScale) {
            zoom = 1f / realScale
        } else {
            zoom = maxScale / realScale
        }

        if (realScale >= maxScale) {
            if (zoom > 1f) {
                zoom = 1f
            }
        } else if (realScale <= minScale) { // 1.2
            if (zoom < 1f) {
                zoom = 1f
            }
        }
        finMatrix.postScale(zoom, zoom, x, y)


        val startData = FloatArray(9)
        startMatrix.getValues(startData)

        val finData = FloatArray(9)
        finMatrix.getValues(finData)

        val curData = FloatArray(9)
        startMatrix.getValues(curData)

        val animator = ValueAnimator.ofFloat(0.00f, 1.00f)

        animator.addUpdateListener { animation ->

            val curModifier = animation.animatedValue as Float

            for (i in 0 until 9) {
                curData[i] = startData[i] + (finData[i] - startData[i]) * curModifier
            }
            matrixM.setValues(curData)
            invalidate()
        }
        animator.duration = 150
        animator.start()
    }

    fun zoomToSelectedStore(nodeModel: NodeModel) {
        val endPointMatrix = Matrix(matrixM)
        endPointMatrix.preTranslate(
            nodeModel.node.coord?.x!!.toFloat(),
            nodeModel.node.coord?.y!!.toFloat()
        )
        val x =
            getXValueFromMatrix(endPointMatrix) - pointNodesBitmapMap[nodeModel.node.id]!!.width / 2
        val y =
            getYValueFromMatrix(endPointMatrix) - -pointNodesBitmapMap[nodeModel.node.id]!!.width / 2
        zoomToStore(x, y)
    }


    private fun zoomToStore(x: Float, y: Float) {
        val startMatrix = Matrix(matrixM)
        val finMatrix = Matrix(matrixM)
        val data = FloatArray(9)
        val zoom = 3.5f

        matrixM.getValues(data)
        finMatrix.postScale(zoom, zoom, x + dpToPx(30f), y - dpToPx(40f))

        val startData = FloatArray(9)
        startMatrix.getValues(startData)

        val finData = FloatArray(9)
        finMatrix.getValues(finData)

        val curData = FloatArray(9)
        startMatrix.getValues(curData)

        val animator = ValueAnimator.ofFloat(0.00f, 1.00f)

        animator.addUpdateListener { animation ->

            val curModifier = animation.animatedValue as Float

            for (i in 0 until 9) {
                curData[i] = startData[i] + (finData[i] - startData[i]) * curModifier
            }
            matrixM.setValues(curData)
            invalidate()
        }
        animator.duration = 100
        animator.start()
    }


    override fun shouldReceiveTouch(p0: UIGestureRecognizer?): Boolean {
        return true
    }

    override fun shouldBegin(p0: UIGestureRecognizer?): Boolean {
        return true
    }

    override fun shouldRecognizeSimultaneouslyWithGestureRecognizer(
        p0: UIGestureRecognizer?,
        p1: UIGestureRecognizer?
    ): Boolean {
        return true
    }


    fun getAngleOfLine(node: Node, nextNode: Node): Float {
        val curEdgeMatrix = Matrix(matrixM)
        curEdgeMatrix.preTranslate(
            node.coord!!.x.toFloat(),
            node.coord!!.y.toFloat()
        )
        val xStart = getXValueFromMatrix(curEdgeMatrix)
        val yStart = getYValueFromMatrix(curEdgeMatrix)

        val nextEdgeMatrix = Matrix(matrixM)
        nextEdgeMatrix.preTranslate(
            nextNode.coord!!.x.toFloat(),
            nextNode.coord!!.y.toFloat()
        )
        val xEnd = getXValueFromMatrix(nextEdgeMatrix)
        val yEnd = getYValueFromMatrix(nextEdgeMatrix)


        val angle = getAngle(xStart, yStart, xEnd, yEnd)

        val v = FloatArray(9)
        curEdgeMatrix.getValues(v)

        val rAngle = Math.round(
            Math.atan2(
                v[Matrix.MSKEW_X].toDouble(),
                v[Matrix.MSCALE_X].toDouble()
            ) * (180 / Math.PI)
        ).toFloat()


        val total = (rAngle + angle)
        if (total < 0) {
            total + 360
        }

        return total % 360
    }


}

fun dpToPx(dp: Float): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

data class MapPoi(
    val xMin: Float,
    val xMax: Float,
    val yMin: Float,
    val yMax: Float,
    val node: Node?
)
