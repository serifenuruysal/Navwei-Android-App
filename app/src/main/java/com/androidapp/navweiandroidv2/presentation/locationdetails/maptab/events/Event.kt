package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.events

import com.androidapp.entity.models.Floor
import com.androidapp.entity.models.Locations
import com.androidapp.entity.models.NodeModel
import com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.adapter.StoreAdapterType

/**
 * Created by S.Nur Uysal on 2019-11-07.
 */

data class OnClickLocationSelectedEvent(
    val locations: NodeModel,
    val storeAdapterType: StoreAdapterType
)

data class OnClickFloorMapSelectedEvent(val floor: Floor)

data class OnClickStoreNodeEvent(val nodeModel: NodeModel)

class OnClickStoreCloseEvent

data class OpenMinifiedStoreCardEvent(val nodeModel: NodeModel)

class CloseMinifiedStoreCardEvent

data class OpenStoreCardEvent(val nodeModel: NodeModel)

class CloseStoreCardEvent

data class OpenNavigationCardEvent(val nodeModel: NodeModel)

class CloseNavigationCardEvent

data class OpenStorePageEvent(val location: Locations?)

class SetSourceFocusEvent()

class MapLoadedEvent()

