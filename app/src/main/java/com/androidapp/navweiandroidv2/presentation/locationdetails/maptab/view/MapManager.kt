package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.androidapp.entity.models.*
import com.androidapp.navweiandroidv2.R
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.dijkstra.DWGraph
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.dijkstra.Dijkstra
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events.MapLoadedEvent
import com.androidapp.navweiandroidv2.presentation.rx.RxBus
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.scand.svg.SVGHelper
import java.io.IOException
import java.net.URL
import javax.inject.Inject


/**
 * Created by S.Nur Uysal on 2019-12-17.
 */

class MapManager @Inject constructor(val mapView: MapView, val context: Context) {

    private var allNodeList: List<NodeModel> = listOf()
    private var allPointNodesList: MutableList<NodeModel> = mutableListOf()
    private var allLinkNodesList: MutableList<NodeModel> = mutableListOf()

    private var currentPointNodes: MutableList<NodeModel> = mutableListOf()
    private var currentLinkNodes: MutableList<NodeModel> = mutableListOf()

    private var allPointNodesMap: HashMap<String, NodeModel> = hashMapOf()
    private var pointAllNodesBitmap: HashMap<String, Bitmap?> = hashMapOf()


    private var currentPathLinkList: MutableList<NodeModel> = mutableListOf()
    private var currentAllPathLinkList: MutableList<NodeModel> = mutableListOf()

    private var pathFloorList = mutableListOf<Floor>()

    private var floorMapMap: HashMap<Floor, FloorMap> = hashMapOf()
    private var floorMapBitmapMap: HashMap<Floor, Bitmap> = hashMapOf()

    private var graph: DWGraph? = null

    private lateinit var selectedFloor: Floor
    private var shortestPath: Iterable<DWGraph.Edge> = listOf()

    var isGoButtonClicked = false

    var sourceLocation: NodeModel? = null
    var destinationLocation: NodeModel? = null
    private var totalTime: Double = 0.toDouble()

    private var pathStepsMap: java.util.HashMap<Floor, MutableList<MapStepsModel>> = hashMapOf()
    private var pathStepsLinksBitmap: java.util.HashMap<String, Bitmap> = hashMapOf()

    fun initMapData(
        floorMapMap: HashMap<Floor, FloorMap>,
        allNodeList: List<NodeModel>,
        selectedFloor: Floor
    ) {
        this.selectedFloor = selectedFloor
        this.allLinkNodesList.clear()
        this.allPointNodesList.clear()
        this.allPointNodesMap.clear()
        this.pointAllNodesBitmap.clear()

        this.allNodeList = allNodeList
        this.floorMapMap = floorMapMap


        downloadMapImages()

        initAllNodeList()
        initAllPointNodesMap()
        initGraph()

        mapView.initView()
        mapView.initBitmaps()
        downloadBitmaps()

    }

    private fun downloadMapImages() {

        if (!floorMapBitmapMap.containsKey(selectedFloor)) {
//            RxBus.publish(MapLoadedEvent())

            val loadFromUrlTask = LoadFromUrlTask()
            loadFromUrlTask.execute(
                MapBitmap(
                    floorMapMap[selectedFloor]!!.picture_url,
                    selectedFloor
                )
            )
        }

    }

    fun setSelectedFloor(selectedFloor: Floor) {
        this.selectedFloor = selectedFloor


        val selectedMapBitmap = floorMapBitmapMap[selectedFloor]
        if (selectedMapBitmap == null) {
            downloadMapImages()
        } else {
            initCurrentNodeListBySelectedFloor()

            mapView.setMapData(
                currentPointNodes,
                currentLinkNodes,
                allPointNodesMap
            )

            mapView.startMap(selectedMapBitmap)
            updateCurrentPathNodesListByFloor(selectedFloor)
            setPathStepsList()
            mapView.showPathOnMap(currentPathLinkList, currentAllPathLinkList)

            if (isGoButtonClicked) {
                mapView.zoomAndRotateMapWithSelectedPath()
            }else{
                mapView.zoomMapWithSelectedPath()
            }
        }
    }

    fun isShowEscalatorButton(): Boolean {

        if (isGoButtonClicked && destinationLocation != null && sourceLocation != null && selectedFloor.floorId != destinationLocation?.floor?.floorId && pathFloorList.size > 1) {
            return true
        }
        return false
    }

    fun getNextFloorToDestination(): Floor {
        var isDestinationToUp = false
        if (destinationLocation != null && sourceLocation != null && destinationLocation?.floor?.weight!! > sourceLocation?.floor?.weight!!) {
            isDestinationToUp = true
        }
        pathFloorList.forEach {
            if (isDestinationToUp) {
                if (it.weight == selectedFloor.weight + 1) {
                    return it
                }
            } else if (it.weight == selectedFloor.weight - 1) {
                return it
            }

        }
        return selectedFloor
    }

    fun getShowEscalatorButtonResource(): Int {
        if (destinationLocation != null && sourceLocation != null && destinationLocation?.floor?.weight!! > sourceLocation?.floor?.weight!!) {
            return R.drawable.escalator_up
        }

        return R.drawable.escalator_down

    }

    private fun initAllNodeList() {
        allNodeList.forEach {

            if (it.node.type == NodeType.point) {

                this.allPointNodesList.add(it)

                if (it.node.externalLink != null && it.node.externalLink?.isNotEmpty()!!) {
                    val externalLink = it.node.externalLink!![0]
                    val linkNodeModel = NodeModel(
                        floor = null,
                        locations = null,
                        nodeLocation = null,
                        node = Node(
                            id = "linkfrom${externalLink.toId}",
                            type = NodeType.link,
                            to_id = externalLink.slotId,
                            from_id = it.node.id
                        )
                    )
                    val linkNodeModelNext = NodeModel(
                        floor = null,
                        locations = null,
                        nodeLocation = null,
                        node = Node(
                            id = "linkto${externalLink.toId}",
                            type = NodeType.link,
                            to_id = it.node.id!!,
                            from_id = externalLink.slotId
                        )
                    )
                    this.allLinkNodesList.add(linkNodeModel)
                    this.allLinkNodesList.add(linkNodeModelNext)

                }
            } else {
                this.allLinkNodesList.add(it)

                val linkNodeModelNext = NodeModel(
                    floor = it.floor,
                    locations = null,
                    nodeLocation = null,
                    node = Node(
                        id = it.node.id!!,
                        type = NodeType.link,
                        to_id = it.node.from_id!!,
                        from_id = it.node.to_id!!
                    )
                )

                this.allLinkNodesList.add(linkNodeModelNext)


            }
        }
    }

    private fun initCurrentNodeListBySelectedFloor() {
        currentPathLinkList.clear()
        currentPointNodes.clear()
        currentLinkNodes.clear()

        allPointNodesList.forEach {
            if (selectedFloor.floorId == it.floor?.floorId) {
                this.currentPointNodes.add(it)
            }
        }

        allLinkNodesList.forEach {
            if (selectedFloor.floorId == it.floor?.floorId) {
                this.currentLinkNodes.add(it)
            }
        }
    }


    private fun initAllPointNodesMap() {
        allPointNodesList.let {
            it.forEach { node ->
                allPointNodesMap.put(node.node.id!!, node)

            }
        }
    }


    private fun initGraph() {
        graph = DWGraph(allPointNodesList.size)

        allLinkNodesList.forEach { linkNode ->
            val indexNodeTo = allPointNodesList.indexOf(allPointNodesMap[linkNode.node.to_id])
            val indexNodeFrom = allPointNodesList.indexOf(allPointNodesMap[linkNode.node.from_id])

            if (indexNodeFrom != -1 && indexNodeTo != -1) {
                graph?.addEdge(
                    indexNodeFrom,
                    indexNodeTo,
                    linkNode,
                    getDistance(
                        allPointNodesMap[linkNode.node.to_id]?.node!!,
                        allPointNodesMap[linkNode.node.from_id]?.node!!
                    )
                )

            }

        }
    }

    private fun getDistance(fromNode: Node, toNode: Node): Double {
        val weight = Math.abs(toNode.coord?.x!! - fromNode.coord?.x!!.toDouble())
        val height = Math.abs(toNode.coord?.y!! - fromNode.coord?.y!!.toDouble())
        val distance = Math.sqrt(weight * weight + height * height)
//        Log.d("distance", distance.toString())
        return distance
    }

    fun zoomAndRotateMapWithSelectedPath() {
        mapView.zoomAndRotateMapWithSelectedPath()

    }

    fun zoomMapWithSelectedPath() {

        mapView.zoomMapWithSelectedPath()
    }

    fun showPathOnMap(): Floor? {

        if (sourceLocation!!.node.id == destinationLocation!!.node.id) {
            return selectedFloor
        }
        val indexSource = allPointNodesList.indexOf(sourceLocation!!)
        val indexDestination = allPointNodesList.indexOf(destinationLocation!!)

        if (indexSource == -1 || indexDestination == -1)
            return null

        val dijkstra = Dijkstra(graph!!, indexSource)

        val newSelectedFloor: Floor = sourceLocation!!.floor!!


        if (dijkstra.hasPathTo(indexDestination)) {
            shortestPath = listOf()
            val distance = dijkstra.distTo(indexDestination)
            Log.d("showPathOnMap", "distance: $distance")
            shortestPath = dijkstra.pathTo(indexDestination)
            pathFloorList.clear()

            shortestPath.forEach {
                totalTime += it.weight
                Log.d(
                    "showPathOnMap",
                    "x: ${allPointNodesMap[it.node.node.from_id]?.node?.coord?.x} " +
                            "y: ${allPointNodesMap[it.node.node.from_id]?.node?.coord?.y}" +
                            "x: ${allPointNodesMap[it.node.node.to_id]?.node?.coord?.x}" +
                            "x: ${allPointNodesMap[it.node.node.to_id]?.node?.coord?.y}"
                )
                val floor = it.node.floor
                if (floor != null && !pathFloorList.contains(floor)) {
                    pathFloorList.add(floor)
                }
            }
            updateCurrentPathNodesListByFloor(newSelectedFloor)

            if (currentPathLinkList.isEmpty() || currentAllPathLinkList.isEmpty()) {
                return null
            }

            if (newSelectedFloor.floorId == selectedFloor.floorId) {
                mapView.showPathOnMap(currentPathLinkList, currentAllPathLinkList)
            }

        } else {
            Log.d("showPathOnMap", "No Path Found")
            return null
        }

        return newSelectedFloor
    }

    private fun updateCurrentPathNodesListByFloor(newSelectedFloor: Floor) {

        currentPathLinkList.clear()
        currentAllPathLinkList.clear()
        if (shortestPath.count() == 0) return

        shortestPath.forEach {
            val floor = it.node.floor

            if (!currentPathLinkList.contains(it.node) && floor != null && floor.floorId == newSelectedFloor.floorId)
                currentPathLinkList.add(it.node)

            currentAllPathLinkList.add(it.node)

        }

    }

    fun getPathNodesByFloorList(): MutableList<Floor> {
        return pathFloorList
    }

    fun getTotalTime(): Int {
        return Math.round(totalTime * 0.017 / 5).toInt()
    }

    fun clearPathOnMap() {
        isGoButtonClicked = false
        totalTime = 0.toDouble()
        shortestPath = listOf()
        currentPathLinkList.clear()
        currentAllPathLinkList.clear()
        mapView.showPathOnMap(currentPathLinkList, currentAllPathLinkList)
    }

    fun getPathStepsList(): HashMap<Floor, MutableList<MapStepsModel>> {
        return pathStepsMap
    }


    internal inner class LoadFromUrlTask : AsyncTask<MapBitmap, Void, Bitmap?>() {
        private var floor: Floor? = null
        override fun doInBackground(vararg model: MapBitmap): Bitmap? {
            floor = model[0].floor

            val url: URL?
            try {
                url = URL(model[0].imageUrl)
                return SVGHelper.noContext().open(url).checkSVGSize().bitmap
            } catch (e: IOException) {
                Toast.makeText(
                    context,
                    "The Map could not downloaded and rendered for ${floor?.floorName}",
                    Toast.LENGTH_LONG
                )
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(svg: Bitmap?) {
            if (svg == null) return
            floorMapBitmapMap.put(floor!!, svg)
            if (floorMapBitmapMap.containsKey(selectedFloor) && floor!!.floorId == selectedFloor.floorId) {
                setSelectedFloor(selectedFloor)
            }

            if (floorMapBitmapMap.size != floorMapMap.size) {
                for (me in floorMapMap.entries) {
                    if (!floorMapBitmapMap.containsKey(me.key)) {
                        val loadFromUrlTask = LoadFromUrlTask()
                        loadFromUrlTask.execute(
                            MapBitmap(
                                me.value.picture_url,
                                me.key
                            )
                        )
                    }
                }
            } else {
                RxBus.publish(MapLoadedEvent())
            }
        }
    }


    private fun downloadBitmaps() {
        allPointNodesList.forEach {
            val node = it.node
            val location = it.locations

            if (!pointAllNodesBitmap.containsKey(node.id)) {

                when {
                    node.logo_url != null && node.logo_url!!.isNotEmpty() -> {

                        downloadBitmapFromUrl(node.logo_url!!, node.id!!)

                    }
                    location != null -> {
                        downloadBitmapFromUrl(
                            location.location_details?.logo_url!!,
                            node.id!!
                        )

                    }
                }
            }

        }
    }

    private fun downloadBitmapFromUrl(url: String, nodeId: String) {

        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>(100, 100) {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    pointAllNodesBitmap.put(nodeId, resource)
                    mapView.setPointNodesBitmapMap(pointAllNodesBitmap)
                }

            })
    }


    private fun setPathStepsList() {
        pathStepsMap.clear()
        pathStepsLinksBitmap.clear()
        var lastAngle: Float? = null

        currentAllPathLinkList.forEach { nodeModel ->
            if (!(nodeModel.node.id!!.contains("linkfrom") || nodeModel.node.id!!.contains("linkto"))) {

                val toNodeModel = allPointNodesMap[nodeModel.node.to_id]
                Log.d(
                    "serrrr",
                    "nodeModel.node.s:${nodeModel.node.type} "
                )
                val floor = toNodeModel?.floor
                if (floor != null) {
                    val fromNode = allPointNodesMap[nodeModel.node.from_id]?.node!!
                    val toNode = toNodeModel.node

                    var resource = getDefaultBitmap()
                    var bitmap: Bitmap? = null

                    var angle = mapView.getAngleOfLine(fromNode, toNode)

                    if (lastAngle != null) {
                        val tempAngle = angle
                        angle -= lastAngle!!
                        if (angle < 0) {
                            angle += 360
                        }
                        if (angle > 360) {
                            angle -= 360
                        }
                        lastAngle = tempAngle

                    } else {
                        lastAngle = angle
                        angle = 0f
                    }


                    if (toNodeModel.locations != null || (toNode.logo_url != null && toNode.logo_url!!.isNotEmpty())) {
                        bitmap = pointAllNodesBitmap[toNode.id]
                    } else {
                        resource = getDirectionBitmap(angle)
                    }

                    val title = getDirectionTitle(toNode, toNodeModel.locations, angle)

                    if (pathStepsMap.containsKey(floor)) {

                        val pathStepsList = pathStepsMap[floor]!!
                        val newModel =
                            MapStepsModel(title, resource, bitmap, toNodeModel.locations != null)
                        if (pathStepsList[pathStepsList.size - 1].stepName != title)
                            pathStepsList.add(newModel)

                    } else {

                        val pathStepsList = mutableListOf<MapStepsModel>()
                        pathStepsList.add(
                            MapStepsModel(
                                title,
                                resource,
                                bitmap,
                                toNodeModel.locations != null
                            )
                        )
                        pathStepsMap[floor] = pathStepsList
                    }
                }
            }
        }
    }

    private fun getDirectionBitmap(angle: Float): Int {

        if ((angle >= 0 && angle < 30) || (angle < 360 && angle >= 330)) {
            return getGoStraightBitmap()
        }
        if (angle < 60 && angle >= 30) {
            return get45DegreeRightBitmap()

        } else if (angle < 150 && angle >= 60) {
            return getTurnRightBitmap()

        } else if (angle < 300 && angle >= 210) {
            return getTurnLeftBitmap()

        } else if (angle < 330 && angle >= 300) {
            return get45DegreeLeftBitmap()

        } else if (angle < 200 && angle >= 150) {
            return getGoStraightBitmap()

        }
        return R.drawable.shape_white_border_yellow_circle
    }

    private fun getDirectionTitle(
        nextNode: Node,
        toLocation: Locations?,
        angle: Float
    ): String {

        var toTitle = toLocation?.location_details?.name

        if (toTitle == null && nextNode.name != null && nextNode.name?.isNotEmpty()!!) {
            toTitle = nextNode.name
        }

        if ((angle >= 0 && angle < 30) || (angle < 360 && angle >= 330)) {
            if (toTitle != null) {
                return context.getString(R.string.title_map_go_straight_to, toTitle)
            }
            return context.getString(R.string.title_map_go_straight)
        } else if (angle < 60 && angle >= 30) {
            if (toTitle != null) {
                return context.getString(R.string.title_map_slight_right_to, toTitle)
            }
            return context.getString(R.string.title_map_slight_right)

        } else if (angle < 150 && angle >= 60) {
            if (toTitle != null) {
                return context.getString(R.string.title_map_take_right_to, toTitle)
            }
            return context.getString(R.string.title_map_right)

        } else if (angle < 300 && angle >= 210) {
            if (toTitle != null) {
                return context.getString(R.string.title_map_take_left_to, toTitle)
            }
            return context.getString(R.string.title_map_left)

        } else if (angle < 330 && angle >= 300) {
            if (toTitle != null) {
                return context.getString(R.string.title_map_slight_left_to, toTitle)
            }
            return context.getString(R.string.title_map_slight_left)

        } else if (angle < 200 && angle >= 150) {
            if (toTitle != null) {
                return context.getString(R.string.title_map_go_straight_to, toTitle)
            }
            return context.getString(R.string.title_map_go_straight)

        }


        return "Unknown"
    }

    private fun getDefaultBitmap(): Int {
        return R.drawable.ic_arrow_top
    }

    private fun getTurnRightBitmap(): Int {
        return R.drawable.ic_arrow_right

    }

    private fun getTurnLeftBitmap(): Int {
        return R.drawable.ic_arrow_left
    }

    private fun getGoStraightBitmap(): Int {
        return R.drawable.ic_arrow_top
    }

    private fun getGoStraightBackBitmap(): Int {
        return R.drawable.ic_arrow_bottom
    }


    private fun get45DegreeRightBitmap(): Int {
        return R.drawable.ic_direction_45deg_right
    }

    private fun get45DegreeLeftBitmap(): Int {
        return R.drawable.ic_direction_45deg_left
    }

    fun zoomToSelectedStore(nodeModel: NodeModel) {
        mapView.zoomToSelectedStore(nodeModel)

    }


}

data class MapBitmap(val imageUrl: String, val floor: Floor)